package com.dioni.financeiro.base.pagamento.dto;

public record WebhookPayload(String type, String action, WebhookData data) {
    public record WebhookData(String id) {}
}
