package com.dioni.financeiro.base.transacoes.repository;

import com.dioni.financeiro.base.transacoes.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepositoryQueries extends JpaRepository<Transacao, Long> {

    public static final String FILTRAR_TRANSACAO_POR_MODO_MENSAL = """
            SELECT t FROM Transacao t WHERE MONTH(t.data) = :mes AND YEAR(t.data
            """;
}
