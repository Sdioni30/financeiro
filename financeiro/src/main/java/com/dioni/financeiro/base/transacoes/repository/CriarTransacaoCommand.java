package com.dioni.financeiro.base.transacoes.repository;

import com.dioni.financeiro.base.auth.model.Usuario;
import com.dioni.financeiro.base.transacoes.model.Transacao;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class CriarTransacaoCommand {

    private final TransacaoRepository transacaoRepository;

    public Transacao executar(Transacao transacao) {

        Usuario usuario = (Usuario) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        transacao.setUsuario(usuario);
        transacao.setData(LocalDate.now());

        return transacaoRepository.save(transacao);
    }

    public List<Transacao> transacaoPorUsuario(){
        Usuario usuario = (Usuario) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return transacaoRepository.findByUsuario(usuario);
    }
}