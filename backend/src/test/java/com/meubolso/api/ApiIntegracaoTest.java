package com.meubolso.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.YearMonth;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa a API de ponta a ponta: requisição HTTP → controller → service → banco (H2 em memória).
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegracaoTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRecusarRequisicaoSemToken() throws Exception {
        mvc.perform(get("/api/transacoes"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    void deveCadastrarComCategoriasPadrao() throws Exception {
        String token = cadastrar(emailAleatorio());

        mvc.perform(get("/api/categorias").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10));
    }

    @Test
    void naoDevePermitirEmailDuplicado() throws Exception {
        String email = emailAleatorio();
        cadastrar(email);

        mvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "Outro", "email", email, "senha", "123456"))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRecusarLoginComSenhaErrada() throws Exception {
        String email = emailAleatorio();
        cadastrar(email);

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "senha", "senhaErrada"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("E-mail ou senha incorretos"));
    }

    @Test
    void deveValidarDadosDoLancamento() throws Exception {
        String token = cadastrar(emailAleatorio());

        mvc.perform(post("/api/transacoes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("descricao", "", "valor", -10, "data", "2026-11-01", "categoriaId", 1))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalhes.length()").value(2));
    }

    @Test
    void fluxoCompletoDeLancamentosEResumo() throws Exception {
        String token = cadastrar(emailAleatorio());
        long alimentacao = idDaCategoria(token, "Alimentação");
        long salario = idDaCategoria(token, "Salário");
        String mes = YearMonth.now().toString();
        String data = YearMonth.now().atDay(1).toString();

        // Cria uma receita e uma despesa
        criarLancamento(token, "Salário", "3000.00", data, salario);
        long idMercado = criarLancamento(token, "Mercado", "250.00", data, alimentacao);

        // Edita a despesa
        mvc.perform(put("/api/transacoes/" + idMercado)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("descricao", "Mercado do mês", "valor", "300.00",
                                "data", data, "categoriaId", alimentacao))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Mercado do mês"))
                .andExpect(jsonPath("$.tipo").value("DESPESA"));

        // Lista o mês
        mvc.perform(get("/api/transacoes").param("mes", mes).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // Confere o resumo
        mvc.perform(get("/api/resumo").param("mes", mes).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReceitas").value(3000.0))
                .andExpect(jsonPath("$.totalDespesas").value(300.0))
                .andExpect(jsonPath("$.saldo").value(2700.0))
                .andExpect(jsonPath("$.despesasPorCategoria[0].nome").value("Alimentação"))
                .andExpect(jsonPath("$.despesasPorCategoria[0].percentualDoTotal").value(100.0));

        // Não deixa excluir categoria que tem lançamentos
        mvc.perform(delete("/api/categorias/" + alimentacao).header("Authorization", "Bearer " + token))
                .andExpect(status().isUnprocessableEntity());

        // Exclui o lançamento
        mvc.perform(delete("/api/transacoes/" + idMercado).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveAvisarQuandoLimiteForExcedido() throws Exception {
        String token = cadastrar(emailAleatorio());
        String data = YearMonth.now().atDay(1).toString();

        String resposta = mvc.perform(post("/api/categorias")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "Delivery", "tipo", "DESPESA", "cor", "#FF0000", "limiteMensal", "100.00"))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        long delivery = objectMapper.readTree(resposta).get("id").asLong();

        criarLancamento(token, "Pizza", "120.00", data, delivery);

        mvc.perform(get("/api/resumo").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.despesasPorCategoria[0].limiteExcedido").value(true))
                .andExpect(jsonPath("$.despesasPorCategoria[0].percentualDoLimite").value(120.0));
    }

    @Test
    void usuarioNaoAcessaDadosDeOutroUsuario() throws Exception {
        String tokenA = cadastrar(emailAleatorio());
        String tokenB = cadastrar(emailAleatorio());
        long categoriaA = idDaCategoria(tokenA, "Lazer");
        long lancamentoA = criarLancamento(tokenA, "Cinema", "40.00", YearMonth.now().atDay(1).toString(), categoriaA);

        mvc.perform(get("/api/transacoes/" + lancamentoA).header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }

    // ---------- métodos auxiliares ----------

    private String cadastrar(String email) throws Exception {
        String resposta = mvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "Pessoa Teste", "email", email, "senha", "123456"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resposta).get("token").asText();
    }

    private long idDaCategoria(String token, String nome) throws Exception {
        String resposta = mvc.perform(get("/api/categorias").header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();
        for (JsonNode categoria : objectMapper.readTree(resposta)) {
            if (categoria.get("nome").asText().equals(nome)) {
                return categoria.get("id").asLong();
            }
        }
        throw new IllegalStateException("Categoria não encontrada: " + nome);
    }

    private long criarLancamento(String token, String descricao, String valor, String data, long categoriaId)
            throws Exception {
        String resposta = mvc.perform(post("/api/transacoes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("descricao", descricao, "valor", valor,
                                "data", data, "categoriaId", categoriaId))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resposta).get("id").asLong();
    }

    private String json(Object objeto) throws Exception {
        return objectMapper.writeValueAsString(objeto);
    }

    private String emailAleatorio() {
        return "teste-" + UUID.randomUUID() + "@email.com";
    }
}
