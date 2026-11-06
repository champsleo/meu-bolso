package com.meubolso.app.data.remote;

import com.meubolso.app.data.model.AuthResponse;
import com.meubolso.app.data.model.CadastroRequest;
import com.meubolso.app.data.model.Categoria;
import com.meubolso.app.data.model.CategoriaRequest;
import com.meubolso.app.data.model.LoginRequest;
import com.meubolso.app.data.model.ResumoMensal;
import com.meubolso.app.data.model.TipoTransacao;
import com.meubolso.app.data.model.Transacao;
import com.meubolso.app.data.model.TransacaoRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Descreve cada endpoint da API REST. O Retrofit gera a implementação automaticamente.
 * Parâmetros @Query nulos são simplesmente omitidos da URL.
 */
public interface ApiService {

    // ---------- Autenticação ----------
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/auth/cadastro")
    Call<AuthResponse> cadastrar(@Body CadastroRequest request);

    // ---------- Categorias ----------
    @GET("api/categorias")
    Call<List<Categoria>> listarCategorias(@Query("tipo") TipoTransacao tipo);

    @POST("api/categorias")
    Call<Categoria> criarCategoria(@Body CategoriaRequest request);

    @PUT("api/categorias/{id}")
    Call<Categoria> atualizarCategoria(@Path("id") long id, @Body CategoriaRequest request);

    @DELETE("api/categorias/{id}")
    Call<Void> excluirCategoria(@Path("id") long id);

    // ---------- Lançamentos ----------
    @GET("api/transacoes")
    Call<List<Transacao>> listarTransacoes(@Query("mes") String mes, @Query("tipo") TipoTransacao tipo);

    @GET("api/transacoes/{id}")
    Call<Transacao> buscarTransacao(@Path("id") long id);

    @POST("api/transacoes")
    Call<Transacao> criarTransacao(@Body TransacaoRequest request);

    @PUT("api/transacoes/{id}")
    Call<Transacao> atualizarTransacao(@Path("id") long id, @Body TransacaoRequest request);

    @DELETE("api/transacoes/{id}")
    Call<Void> excluirTransacao(@Path("id") long id);

    // ---------- Resumo ----------
    @GET("api/resumo")
    Call<ResumoMensal> buscarResumo(@Query("mes") String mes);
}
