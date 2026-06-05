package com.dioni.financeiro.base.security;

import com.dioni.financeiro.base.auth.model.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final Set<String> contasGratuitas;

    public JwtAuthFilter(JwtService jwtService,
                         UserDetailsService userDetailsService,
                         @Value("${mercadopago.contas-gratuitas:}") String contasGratuitasRaw) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.contasGratuitas = Arrays.stream(contasGratuitasRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtService.isTokenValid(token, userDetails)) {
                String path = request.getRequestURI();
                boolean rotaLivre = path.startsWith("/api/auth/") || path.startsWith("/api/pagamento/");
                boolean contaGratuita = contasGratuitas.contains(userDetails.getUsername());
                if (!rotaLivre && !contaGratuita && userDetails instanceof Usuario usuario && !usuario.isAssinaturaAtiva()) {
                    response.setStatus(402);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Assinatura inativa ou expirada\"}");
                    return;
                }
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}