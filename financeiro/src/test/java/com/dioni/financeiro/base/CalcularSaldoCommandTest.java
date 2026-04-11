package com.dioni.financeiro.base;

import com.dioni.financeiro.base.model.Categoria;
import com.dioni.financeiro.base.model.TipoTransacao;
import com.dioni.financeiro.base.model.Transacao;
import com.dioni.financeiro.base.repository.CalcularSaldoCommand;
import com.dioni.financeiro.base.repository.TransacaoRepository;
import com.dioni.financeiro.support.TestSupport;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CalcularSaldoCommandTest extends TestSupport {

    @Mock
    private TransacaoRepository transacaoRepository;

    private CalcularSaldoCommand calcularSaldoCommand;

    @Override
    public void init() {
        calcularSaldoCommand = new CalcularSaldoCommand(transacaoRepository);
    }

    @Test
    void should_calculate_correct_balance_when_transactions_exist() {
        Categoria categoriaAlvo = Categoria.PROFISSIONAL;

        Transacao t1 = new Transacao();
        t1.setCategoria(Categoria.PROFISSIONAL);
        t1.setTipo(TipoTransacao.ENTRADA);
        t1.setValor(150.0);

        Transacao t2 = new Transacao();
        t2.setCategoria(Categoria.PROFISSIONAL);
        t2.setTipo(TipoTransacao.SAIDA);
        t2.setValor(50.0);

        Transacao t3 = new Transacao();
        t3.setCategoria(Categoria.PESSOAL);
        t3.setTipo(TipoTransacao.ENTRADA);
        t3.setValor(500.0);

        List<Transacao> transacoesMock = List.of(t1, t2, t3);

        when(transacaoRepository.findAll()).thenReturn(transacoesMock);

        Double saldoResult = calcularSaldoCommand.executar(categoriaAlvo);

        assertThat(saldoResult).isEqualTo(100.0);

        InOrder inOrder = this.inOrder(transacaoRepository);
        inOrder.verify(transacaoRepository).findAll();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void should_return_zero_when_no_transactions_found_for_category() {
        Categoria categoriaAlvo = Categoria.PESSOAL;


        when(transacaoRepository.findAll()).thenReturn(List.of());

        Double saldoResult = calcularSaldoCommand.executar(categoriaAlvo);

        assertThat(saldoResult).isEqualTo(0.0);

        InOrder inOrder = this.inOrder(transacaoRepository);
        inOrder.verify(transacaoRepository).findAll();
        inOrder.verifyNoMoreInteractions();
    }
}