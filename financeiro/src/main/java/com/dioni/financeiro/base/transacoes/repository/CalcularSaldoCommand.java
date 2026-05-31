package com.dioni.financeiro.base.transacoes.repository;

import com.dioni.financeiro.base.auth.model.Usuario;
import com.dioni.financeiro.base.enums.Categoria;
import com.dioni.financeiro.base.enums.TipoTransacao;
import com.dioni.financeiro.base.transacoes.model.Transacao;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@AllArgsConstructor
public class CalcularSaldoCommand {

    private final TransacaoRepository transacaoRepository;
    private final TransacaoQuery transacaoQuery;

    public Double executar(Categoria categoria) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();
        return transacaoQuery.filtrarPorMes(mes, ano, usuario.getId()).stream()
                .filter(t -> t.getCategoria().equals(categoria))
                .mapToDouble(t -> t.getTipo().equals(TipoTransacao.ENTRADA) ? t.getValor() : -t.getValor())
                .sum();
    }
}
