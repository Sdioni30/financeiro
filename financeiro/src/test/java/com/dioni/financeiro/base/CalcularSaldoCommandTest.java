package com.dioni.financeiro.base;

import com.dioni.financeiro.base.auth.model.Usuario;
import com.dioni.financeiro.base.enums.Categoria;
import com.dioni.financeiro.base.enums.TipoTransacao;
import com.dioni.financeiro.base.transacoes.model.Transacao;
import com.dioni.financeiro.base.transacoes.repository.CalcularSaldoCommand;
import com.dioni.financeiro.base.transacoes.repository.TransacaoQuery;
import com.dioni.financeiro.base.transacoes.repository.TransacaoRepository;
import com.dioni.financeiro.support.TestSupport;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CalcularSaldoCommandTest extends TestSupport {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private TransacaoQuery transacaoQuery;

    private CalcularSaldoCommand calcularSaldoCommand;

    @Override
    public void init() {
        calcularSaldoCommand = new CalcularSaldoCommand(transacaoRepository, transacaoQuery);
    }

    private Usuario mockUsuario(boolean modoMensal) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setModoMensal(modoMensal);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuario);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        return usuario;
    }

    @Test
    void should_calculate_correct_balance_when_transactions_exist() {
        Usuario usuario = mockUsuario(false);

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

        when(transacaoRepository.findByUsuario(usuario)).thenReturn(List.of(t1, t2, t3));

        Double saldoResult = calcularSaldoCommand.executar(Categoria.PROFISSIONAL);

        assertThat(saldoResult).isEqualTo(100.0);

        InOrder inOrder = this.inOrder(transacaoRepository);
        inOrder.verify(transacaoRepository).findByUsuario(usuario);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void should_return_zero_when_no_transactions_found_for_category() {
        Usuario usuario = mockUsuario(false);

        when(transacaoRepository.findByUsuario(usuario)).thenReturn(List.of());

        Double saldoResult = calcularSaldoCommand.executar(Categoria.PESSOAL);

        assertThat(saldoResult).isEqualTo(0.0);

        InOrder inOrder = this.inOrder(transacaoRepository);
        inOrder.verify(transacaoRepository).findByUsuario(usuario);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void should_calculate_balance_only_for_current_month_when_modo_mensal_is_enabled() {
        Usuario usuario = mockUsuario(true);

        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();

        Transacao t1 = new Transacao();
        t1.setCategoria(Categoria.PROFISSIONAL);
        t1.setTipo(TipoTransacao.ENTRADA);
        t1.setValor(400.0);

        Transacao t2 = new Transacao();
        t2.setCategoria(Categoria.PROFISSIONAL);
        t2.setTipo(TipoTransacao.SAIDA);
        t2.setValor(100.0);

        when(transacaoQuery.filtrarPorMes(mes, ano, usuario.getId())).thenReturn(List.of(t1, t2));

        Double saldoResult = calcularSaldoCommand.executar(Categoria.PROFISSIONAL);

        assertThat(saldoResult).isEqualTo(300.0);

        InOrder inOrder = this.inOrder(transacaoQuery);
        inOrder.verify(transacaoQuery).filtrarPorMes(mes, ano, usuario.getId());
        inOrder.verifyNoMoreInteractions();
    }
}