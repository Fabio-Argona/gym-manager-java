package com.treino_abc_backend.security;

import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.repository.AlunoRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AlunoRepository alunoRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            email = jwtUtil.extractEmail(jwt);
            System.out.println("[JWT FILTER] Token detectado: " + jwt);
            System.out.println("[JWT FILTER] Email extraído do token: " + email);
        } else {
            System.out.println("[JWT FILTER] Nenhum token encontrado no header Authorization.");
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<Aluno> alunoOpt = alunoRepository.findByEmail(email);

            if (alunoOpt.isPresent()) {
                Aluno aluno = alunoOpt.get();
                System.out.println("[JWT FILTER] Aluno encontrado: " + aluno.getEmail());

                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(aluno.getEmail())
                        .password(aluno.getPassword())
                        .roles(aluno.getRole().replace("ROLE_", ""))
                        .build();

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    System.out.println("[JWT FILTER] Token válido, autenticando...");
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("[JWT FILTER] Token inválido!");
                }
            } else {
                System.out.println("[JWT FILTER] Nenhum aluno encontrado para este email.");
            }
        }

        filterChain.doFilter(request, response);
    }
}
