package com.meubolso.api.service;

import com.meubolso.api.dto.AuthDtos.AuthResponse;
import com.meubolso.api.dto.AuthDtos.CadastroRequest;
import com.meubolso.api.dto.AuthDtos.LoginRequest;
import com.meubolso.api.dto.AuthDtos.UsuarioResponse;
import com.meubolso.api.exception.ConflitoException;
import com.meubolso.api.exception.CredenciaisInvalidasException;
import com.meubolso.api.exception.RecursoNaoEncontradoException;
import com.meubolso.api.model.Usuario;
import com.meubolso.api.repository.UsuarioRepository;
import com.meubolso.api.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Regras de cadastro e login.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaService categoriaService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       CategoriaService categoriaService,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.categoriaService = categoriaService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse cadastrar(CadastroRequest request) {
        String email = normalizarEmail(request.email());
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflitoException("Este e-mail já está cadastrado");
        }

        Usuario usuario = usuarioRepository.save(new Usuario(
                request.nome().trim(),
                email,
                passwordEncoder.encode(request.senha())));

        // Todo usuário novo já começa com categorias prontas para usar
        categoriaService.criarCategoriasPadrao(usuario);

        return gerarResposta(usuario);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(normalizarEmail(request.email()))
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }
        return gerarResposta(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        return paraResponse(usuario);
    }

    private AuthResponse gerarResposta(Usuario usuario) {
        return new AuthResponse(jwtService.gerarToken(usuario), paraResponse(usuario));
    }

    private UsuarioResponse paraResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
