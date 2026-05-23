package com.dioni.financeiro.base.transacoes.application.usecase;

import com.dioni.financeiro.base.auth.model.Usuario;
import com.dioni.financeiro.base.transacoes.model.Transacao;
import com.dioni.financeiro.base.transacoes.repository.TransacaoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class ListarTransacoesUseCaseImpl implements ListarTransacoesUseCase {

    private final TransacaoRepository transacaoRepository;

    @Override
    public Page<Transacao> execute(Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (usuario.isModoMensal()) {
            int mes = LocalDate.now().getMonthValue();
            int ano = LocalDate.now().getYear();
            return transacaoRepository.findByMesAndAno(mes, ano, pageable);
        }

        return transacaoRepository.findAll(pageable);
    }
}
