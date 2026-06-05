package com.dioni.financeiro.base.pagamento;

import com.dioni.financeiro.base.auth.model.Usuario;
import com.dioni.financeiro.base.auth.repository.UserRepository;
import com.dioni.financeiro.base.pagamento.dto.CriarPagamentoResponse;
import com.dioni.financeiro.base.pagamento.dto.WebhookPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/pagamento")
@RequiredArgsConstructor
public class PagamentoController {

    private final MercadoPagoClient mercadoPagoClient;
    private final UserRepository userRepository;

    @Value("${mercadopago.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/criar")
    public ResponseEntity<CriarPagamentoResponse> criarPagamento() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String url = mercadoPagoClient.criarPreferencia(usuario.getEmail());
        return ResponseEntity.ok(new CriarPagamentoResponse(url));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> receberWebhook(
            @RequestParam String secret,
            @RequestBody WebhookPayload payload) {

        if (!webhookSecret.equals(secret)) {
            return ResponseEntity.status(401).build();
        }

        if (!"payment".equals(payload.type())) {
            return ResponseEntity.ok().build();
        }

        Map<String, Object> pagamento = mercadoPagoClient.buscarPagamento(payload.data().id());

        if (!"approved".equals(pagamento.get("status"))) {
            return ResponseEntity.ok().build();
        }

        String emailUsuario = (String) pagamento.get("external_reference");
        userRepository.findByEmail(emailUsuario).ifPresentOrElse(usuario -> {
            LocalDateTime base = usuario.getAssinaturaExpiracao() != null
                    && usuario.getAssinaturaExpiracao().isAfter(LocalDateTime.now())
                    ? usuario.getAssinaturaExpiracao()
                    : LocalDateTime.now();
            usuario.setAssinaturaExpiracao(base.plusDays(30));
            userRepository.save(usuario);
            log.info("Assinatura ativada/renovada para {}, expira em {}", emailUsuario, usuario.getAssinaturaExpiracao());
        }, () -> log.warn("Webhook recebido para email desconhecido: {}", emailUsuario));

        return ResponseEntity.ok().build();
    }
}