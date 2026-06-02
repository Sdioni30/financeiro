package com.dioni.financeiro.base;

import com.dioni.financeiro.base.auth.model.Usuario;
import com.dioni.financeiro.base.transacoes.model.Transacao;
import com.dioni.financeiro.base.transacoes.repository.DeletarTransacaoCommand;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DeletarTransacaoCommandTest extends TestSupport {

    @Mock
    private TransacaoRepository transacaoRepository;

    private DeletarTransacaoCommand deletarTransacaoCommand;
    private Usuario usuario;

    @Override
    public void init() {
        deletarTransacaoCommand = new DeletarTransacaoCommand(transacaoRepository);

        usuario = new Usuario();
        usuario.setId(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuario);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void should_delete_transaction_and_return_no_content_when_owner() {
        Long id = 1L;
        Transacao transacao = new Transacao();
        transacao.setId(id);
        transacao.setUsuario(usuario);

        when(transacaoRepository.findByIdAndUsuario(id, usuario)).thenReturn(Optional.of(transacao));

        ResponseEntity<Void> response = deletarTransacaoCommand.executar(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        InOrder inOrder = this.inOrder(transacaoRepository);
        inOrder.verify(transacaoRepository).findByIdAndUsuario(id, usuario);
        inOrder.verify(transacaoRepository).deleteById(id);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void should_return_not_found_when_transaction_does_not_exist() {
        Long id = 99L;

        when(transacaoRepository.findByIdAndUsuario(id, usuario)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = deletarTransacaoCommand.executar(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        InOrder inOrder = this.inOrder(transacaoRepository);
        inOrder.verify(transacaoRepository).findByIdAndUsuario(id, usuario);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void should_return_not_found_when_transaction_belongs_to_another_user() {
        Long id = 1L;

        when(transacaoRepository.findByIdAndUsuario(id, usuario)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = deletarTransacaoCommand.executar(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        InOrder inOrder = this.inOrder(transacaoRepository);
        inOrder.verify(transacaoRepository).findByIdAndUsuario(id, usuario);
        inOrder.verifyNoMoreInteractions();
    }
}
