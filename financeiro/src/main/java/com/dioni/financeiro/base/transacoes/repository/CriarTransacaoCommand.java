package com.dioni.financeiro.base.transacoes.repository;

import com.dioni.financeiro.base.auth.model.Usuario;
import com.dioni.financeiro.base.dto.TransacaoDTO;
import com.dioni.financeiro.base.dto.TransacaoRequest;
import com.dioni.financeiro.base.transacoes.model.Transacao;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class CriarTransacaoCommand {

    private final TransacaoRepository transacaoRepository;

    public TransacaoDTO executar(TransacaoRequest request) {
        Usuario usuario = (Usuario) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Transacao transacao = request.toTransacao();
        transacao.setUsuario(usuario);
        transacao.setData(LocalDate.now());

        return TransacaoDTO.from(transacaoRepository.save(transacao));
    }
}