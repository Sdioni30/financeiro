package com.dioni.financeiro.base;

import com.dioni.financeiro.base.auth.model.Usuario;
import com.dioni.financeiro.base.enums.Categoria;
import com.dioni.financeiro.base.enums.TipoTransacao;
import com.dioni.financeiro.base.transacoes.model.Transacao;
import com.dioni.financeiro.base.transacoes.repository.ExportarRelatorioCommand;
import com.dioni.financeiro.base.transacoes.repository.TransacaoQuery;
import com.dioni.financeiro.base.transacoes.repository.TransacaoRepository;
import com.dioni.financeiro.support.TestSupport;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExportarRelatorioCommandTest extends TestSupport {

    @Mock
    private TransacaoRepository repository;

    @Mock
    private TransacaoQuery transacaoQuery;

    private ExportarRelatorioCommand exportarRelatorioCommand;

    @Override
    public void init() {
        exportarRelatorioCommand = new ExportarRelatorioCommand(repository, transacaoQuery);
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

    private Transacao criarTransacao(Categoria categoria, TipoTransacao tipo, Double valor) {
        Transacao t = new Transacao();
        t.setCategoria(categoria);
        t.setTipo(tipo);
        t.setValor(valor);
        t.setData(LocalDate.now());
        t.setDescricao("Teste");
        return t;
    }

    @Test
    void should_generate_excel_with_transactions_when_modo_mensal_is_disabled() {
        Usuario usuario = mockUsuario(false);

        List<Transacao> transacoes = List.of(
                criarTransacao(Categoria.PESSOAL, TipoTransacao.ENTRADA, 200.0),
                criarTransacao(Categoria.PESSOAL, TipoTransacao.SAIDA, 50.0),
                criarTransacao(Categoria.PROFISSIONAL, TipoTransacao.ENTRADA, 500.0)
        );

        when(repository.findByUsuario(usuario)).thenReturn(transacoes);

        ResponseEntity<byte[]> resultado = exportarRelatorioCommand.executar(Categoria.PESSOAL, null);

        assertThat(resultado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultado.getBody()).isNotEmpty();

        InOrder inOrder = this.inOrder(repository);
        inOrder.verify(repository).findByUsuario(usuario);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void should_generate_excel_with_monthly_transactions_when_modo_mensal_is_enabled() {
        Usuario usuario = mockUsuario(true);

        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();

        List<Transacao> transacoes = List.of(
                criarTransacao(Categoria.PESSOAL, TipoTransacao.ENTRADA, 300.0),
                criarTransacao(Categoria.PESSOAL, TipoTransacao.SAIDA, 100.0)
        );

        when(transacaoQuery.filtrarPorMes(mes, ano, usuario.getId())).thenReturn(transacoes);

        ResponseEntity<byte[]> resultado = exportarRelatorioCommand.executar(Categoria.PESSOAL, null);

        assertThat(resultado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultado.getBody()).isNotEmpty();

        InOrder inOrder = this.inOrder(transacaoQuery);
        inOrder.verify(transacaoQuery).filtrarPorMes(mes, ano, usuario.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void should_return_empty_excel_when_no_transactions_found_for_category() {
        Usuario usuario = mockUsuario(false);

        when(repository.findByUsuario(usuario)).thenReturn(List.of());

        ResponseEntity<byte[]> resultado = exportarRelatorioCommand.executar(Categoria.PROFISSIONAL, null);

        assertThat(resultado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultado.getBody()).isNotEmpty();

        InOrder inOrder = this.inOrder(repository);
        inOrder.verify(repository).findByUsuario(usuario);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void should_generate_excel_with_only_entradas_when_tipo_is_entrada() {
        Usuario usuario = mockUsuario(false);

        List<Transacao> transacoes = List.of(
                criarTransacao(Categoria.PESSOAL, TipoTransacao.ENTRADA, 200.0),
                criarTransacao(Categoria.PESSOAL, TipoTransacao.SAIDA, 50.0)
        );

        when(repository.findByUsuario(usuario)).thenReturn(transacoes);

        ResponseEntity<byte[]> resultado = exportarRelatorioCommand.executar(Categoria.PESSOAL, TipoTransacao.ENTRADA);

        assertThat(resultado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultado.getBody()).isNotEmpty();

        InOrder inOrder = this.inOrder(repository);
        inOrder.verify(repository).findByUsuario(usuario);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void should_generate_excel_with_only_saidas_when_tipo_is_saida() {
        Usuario usuario = mockUsuario(false);

        List<Transacao> transacoes = List.of(
                criarTransacao(Categoria.PESSOAL, TipoTransacao.ENTRADA, 200.0),
                criarTransacao(Categoria.PESSOAL, TipoTransacao.SAIDA, 50.0)
        );

        when(repository.findByUsuario(usuario)).thenReturn(transacoes);

        ResponseEntity<byte[]> resultado = exportarRelatorioCommand.executar(Categoria.PESSOAL, TipoTransacao.SAIDA);

        assertThat(resultado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultado.getBody()).isNotEmpty();

        InOrder inOrder = this.inOrder(repository);
        inOrder.verify(repository).findByUsuario(usuario);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void should_generate_excel_with_only_entradas_when_modo_mensal_is_enabled_and_tipo_is_entrada() {
        Usuario usuario = mockUsuario(true);

        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();

        List<Transacao> transacoes = List.of(
                criarTransacao(Categoria.PESSOAL, TipoTransacao.ENTRADA, 300.0),
                criarTransacao(Categoria.PESSOAL, TipoTransacao.SAIDA, 100.0)
        );

        when(transacaoQuery.filtrarPorMes(mes, ano, usuario.getId())).thenReturn(transacoes);

        ResponseEntity<byte[]> resultado = exportarRelatorioCommand.executar(Categoria.PESSOAL, TipoTransacao.ENTRADA);

        assertThat(resultado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultado.getBody()).isNotEmpty();

        InOrder inOrder = this.inOrder(transacaoQuery);
        inOrder.verify(transacaoQuery).filtrarPorMes(mes, ano, usuario.getId());
        inOrder.verifyNoMoreInteractions();
    }
}
