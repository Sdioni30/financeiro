package com.dioni.financeiro.base.transacoes.repository;

import com.dioni.financeiro.base.auth.model.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeletarTransacaoCommand {

    private final TransacaoRepository transacaoRepository;

    public ResponseEntity<Void> executar(Long id) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (transacaoRepository.findByIdAndUsuario(id, usuario).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        transacaoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
