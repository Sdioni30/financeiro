package com.dioni.financeiro.base.auth.web;

import com.dioni.financeiro.base.auth.dto.AtualizarModoMensalRequest;
import com.dioni.financeiro.base.auth.dto.LoginRequest;
import com.dioni.financeiro.base.auth.dto.LoginResponse;
import com.dioni.financeiro.base.auth.dto.RegisterRequest;
import com.dioni.financeiro.base.auth.model.Usuario;
import com.dioni.financeiro.base.auth.repository.UserRepository;
import com.dioni.financeiro.base.security.JwtService;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponse> userLogin(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        Usuario usuario = userRepository.findByEmail(request.email()).orElseThrow();
        String token = jwtService.generateToken(usuario);
        return ResponseEntity.ok(new LoginResponse(token, usuario.isModoMensal()));
    }
    @PostMapping("/api/auth/register")
    public ResponseEntity<Void> userRegister(@RequestBody RegisterRequest request){
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
