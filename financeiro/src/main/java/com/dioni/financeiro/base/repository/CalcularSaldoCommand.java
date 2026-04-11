package com.dioni.financeiro.base.repository;

import com.dioni.financeiro.base.model.Categoria;
import com.dioni.financeiro.base.model.TipoTransacao;
import com.dioni.financeiro.base.model.Transacao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class CalcularSaldoCommand {


    private final TransacaoRepository transacaoRepository;

    public Double executar(Categoria categoria) {
        List<Transacao> transacoes = transacaoRepository.findAll();
        return transacoes.stream()
                .filter(t -> t.getCategoria().equals(categoria))
                .mapToDouble(t -> t.getTipo().equals(TipoTransacao.ENTRADA) ? t.getValor() : -t.getValor())
                .sum();
    }
}
