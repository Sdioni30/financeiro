package com.dioni.financeiro.base.transacoes.repository;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeletarTransacaoCommand {

    private final TransacaoRepository transacaoRepository;

    public ResponseEntity<Void> executar(Long id){
        if(!transacaoRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        transacaoRepository.deleteById(id);
            return ResponseEntity.noContent().build();

    }

}
