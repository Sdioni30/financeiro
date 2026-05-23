package com.dioni.financeiro.base.transacoes.repository;

public class TransacaoQueries {

    public static final String FILTRAR_TRANSACAO_POR_MES = """
            SELECT * FROM transacao
            WHERE MONTH(data) = ?
            AND YEAR(data) = ?
            """;

    public static final String FILTRAR_TRANSACAO_POR_MES_PAGINADO =
            "SELECT t FROM Transacao t WHERE MONTH(t.data) = :mes AND YEAR(t.data) = :ano";
}
