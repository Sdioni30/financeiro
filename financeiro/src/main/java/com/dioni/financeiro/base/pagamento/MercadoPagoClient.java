package com.dioni.financeiro.base.pagamento;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class MercadoPagoClient {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.preco}")
    private double preco;

    @Value("${mercadopago.return-url}")
    private String returnUrl;

    @Value("${mercadopago.webhook-url}")
    private String webhookUrl;

    @Value("${mercadopago.webhook-secret}")
    private String webhookSecret;

    @Value("${mercadopago.sandbox:false}")
    private boolean sandbox;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.mercadopago.com")
            .build();

    @SuppressWarnings("unchecked")
    public String criarPreferencia(String emailUsuario) {
        Map<String, Object> body = Map.of(
                "items", List.of(Map.of(
                        "title", "Controle Financeiro — Assinatura Mensal",
                        "quantity", 1,
                        "unit_price", preco,
                        "currency_id", "BRL"
                )),
                "payer", Map.of("email", emailUsuario),
                "back_urls", Map.of(
                        "success", returnUrl,
                        "failure", returnUrl,
                        "pending", returnUrl
                ),
                "auto_return", "approved",
                "external_reference", emailUsuario,
                "notification_url", webhookUrl + "?secret=" + webhookSecret
        );

        Map<String, Object> resposta = restClient.post()
                .uri("/checkout/preferences")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        String chaveUrl = sandbox ? "sandbox_init_point" : "init_point";
        return (String) resposta.get(chaveUrl);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> buscarPagamento(String paymentId) {
        return restClient.get()
                .uri("/v1/payments/" + paymentId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
