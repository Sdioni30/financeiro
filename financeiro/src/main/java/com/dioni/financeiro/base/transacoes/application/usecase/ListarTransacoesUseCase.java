package com.dioni.financeiro.base.transacoes.application.usecase;

import com.dioni.financeiro.base.transacoes.model.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarTransacoesUseCase {
    Page<Transacao> execute(Pageable pageable);
}
