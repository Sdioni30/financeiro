package com.dioni.financeiro.base.dto;

import com.dioni.financeiro.base.model.Categoria;
import com.dioni.financeiro.base.model.TipoTransacao;
import com.dioni.financeiro.base.model.Transacao;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransacaoDTO {

    private Long id;
    private String descricao;
    private Double valor;
    private TipoTransacao tipo;
    private Categoria categoria;
    private LocalDate data;

    public static TransacaoDTO from(Transacao transacao){
        TransacaoDTO transacaoDTO = new TransacaoDTO();
        transacaoDTO.setId(transacao.getId());
        transacaoDTO.setDescricao(transacao.getDescricao());
        transacaoDTO.setValor(transacao.getValor());
        transacaoDTO.setTipo(transacao.getTipo());
        transacaoDTO.setCategoria(transacao.getCategoria());
        transacaoDTO.setData(transacao.getData());
        return transacaoDTO;
    }

}
