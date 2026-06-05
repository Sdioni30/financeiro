package com.dioni.financeiro.base.auth.web;

import com.dioni.financeiro.base.auth.dto.AtualizarModoMensalRequest;
import com.dioni.financeiro.base.auth.dto.LoginRequest;
import com.dioni.financeiro.base.auth.dto.LoginResponse;
import com.dioni.financeiro.base.auth.dto.RegisterRequest;
import com.dioni.financeiro.base.auth.model.Usuario;
import com.dioni.financeiro.base.auth.repository.UserRepository;
import com.dioni.financeiro.base.security.JwtService;
import com.dioni.financeiro.base.exceptions.web.ErrorResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.contas-liberadas:}")
    private String contasLiberadasRaw;

    private Set<String> contasLiberadas;

    @PostConstruct
    private void init() {
        this.contasLiberadas = Arrays.stream(contasLiberadasRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginRequest request) {
        if (!contasLiberadas.contains(request.email())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Entre em contato com o administrador"));
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        Usuario usuario = userRepository.findByEmail(request.email()).orElseThrow();
        String token = jwtService.generateToken(usuario);
        return ResponseEntity.ok(new LoginResponse(token, usuario.isModoMensal()));
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<?> userRegister(@RequestBody RegisterRequest request) {
        if (!contasLiberadas.contains(request.email())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Entre em contato com o administrador"));
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Usuario usuario = Usuario.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/api/auth/modoMensal")
    public ResponseEntity<Void> atualizarModoMensal(@RequestBody AtualizarModoMensalRequest request){
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        usuario.setModoMensal(request.modoMensal());
        userRepository.save(usuario);
        return ResponseEntity.ok().build();
    }

}
