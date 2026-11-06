package com.meubolso.api.security;

import com.meubolso.api.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

/**
 * Gera e valida tokens JWT.
 * O token é como um "crachá" assinado: o app envia em toda requisição e o servidor confere a assinatura.
 */
@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoHoras;

    public JwtService(@Value("${app.jwt.secret}") String segredoBase64,
                      @Value("${app.jwt.expiracao-horas}") long expiracaoHoras) {
        this.chave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(segredoBase64));
        this.expiracaoHoras = expiracaoHoras;
    }

    public String gerarToken(Usuario usuario) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(usuario.getId()))
                .claim("email", usuario.getEmail())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(expiracaoHoras, ChronoUnit.HOURS)))
                .signWith(chave)
                .compact();
    }

    /**
     * @return o usuário do token, ou vazio se o token for inválido ou estiver expirado
     */
    public Optional<UsuarioAutenticado> validarToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(new UsuarioAutenticado(
                    Long.valueOf(claims.getSubject()),
                    claims.get("email", String.class)));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
