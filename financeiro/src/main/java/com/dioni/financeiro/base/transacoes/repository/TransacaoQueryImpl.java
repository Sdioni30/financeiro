package com.dioni.financeiro.base.transacoes.repository;

import com.dioni.financeiro.base.transacoes.model.Transacao;
import com.dioni.financeiro.infra.QueryExecutor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TransacaoQueryImpl implements TransacaoQuery {

    private final QueryExecutor queryExecutor;

    @Override
    public List<Transacao> filtrarPorMes(int mes, int ano, Long usuarioId) {
        return queryExecutor.findList(
                TransacaoQueries.FILTRAR_TRANSACAO_POR_MES,
                Transacao.class,
                mes, ano, usuarioId
        );
    }
}
