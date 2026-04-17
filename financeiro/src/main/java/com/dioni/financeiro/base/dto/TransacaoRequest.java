package com.dioni.financeiro.base.dto;

import com.dioni.financeiro.base.model.Categoria;
import com.dioni.financeiro.base.model.TipoTransacao;
import com.dioni.financeiro.base.model.Transacao;
import lombok.Data;


@Data
public class TransacaoRequest {

    private String descricao;
    private Double valor;
    private TipoTransacao tipo;
    private Categoria categoria;

    public Transacao toTransacao() {
        Transacao transacao = new Transacao();
        transacao.setDescricao(this.descricao);
        transacao.setValor(this.valor);
        transacao.setTipo(this.tipo);
        transacao.setCategoria(this.categoria);
        return transacao;
    }
}
