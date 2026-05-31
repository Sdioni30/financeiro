package com.dioni.financeiro.base;

import com.dioni.financeiro.base.transacoes.repository.DeletarTransacaoCommand;
import com.dioni.financeiro.base.transacoes.repository.TransacaoRepository;
import com.dioni.financeiro.support.TestSupport;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class DeletarTransacaoCommandTest extends TestSupport {

    @Mock
    private TransacaoRepository transacaoRepository;

    private DeletarTransacaoCommand deletarTransacaoCommand;

    @Override
    public void init() {
        deletarTransacaoCommand = new DeletarTransacaoCommand(transacaoRepository);
    }

    @Test
    void should_delete_transaction_and_return_no_content_when_id_exists() {
        Long id = 1L;

        when(transacaoRepository.existsById(id)).thenReturn(true);

        ResponseEntity<Void> response = deletarTransacaoCommand.executar(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        InOrder inOrder = this.inOrder(transacaoRepository);
        inOrder.verify(transacaoRepository).existsById(id);
        inOrder.verify(transacaoRepository).deleteById(id);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void should_return_not_found_when_id_does_not_exist() {
        Long id = 99L;

        when(transacaoRepository.existsById(id)).thenReturn(false);

        ResponseEntity<Void> response = deletarTransacaoCommand.executar(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        InOrder inOrder = this.inOrder(transacaoRepository);
        inOrder.verify(transacaoRepository).existsById(id);
        inOrder.verifyNoMoreInteractions();
    }
}
