package com.dioni.financeiro.base.transacoes.repository;

import com.dioni.financeiro.base.transacoes.model.Transacao;

import java.util.List;

public interface TransacaoQuery {
    List<Transacao> filtrarPorMes(int mes, int ano);
}
