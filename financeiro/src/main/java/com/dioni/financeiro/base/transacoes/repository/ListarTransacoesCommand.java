package com.dioni.financeiro.base.transacoes.repository;

import com.dioni.financeiro.base.auth.model.Usuario;
import com.dioni.financeiro.base.dto.TransacaoDTO;
import com.dioni.financeiro.base.enums.Categoria;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class ListarTransacoesCommand {

    private final TransacaoRepository transacaoRepository;

    public Page<TransacaoDTO> executar(Pageable pageable, Categoria categoria) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();
        return transacaoRepository.findByMesAndAno(mes, ano, usuario, categoria, pageable)
                .map(TransacaoDTO::from);
    }
}