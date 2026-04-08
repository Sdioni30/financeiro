package com.dioni.financeiro.base.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.dioni.financeiro.base.repository.TransacaoRepository;
import com.dioni.financeiro.base.model.Transacao;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoRepository repository;

    @PostMapping
    public Transacao criar(@RequestBody Transacao transacao) {
        transacao.setData(LocalDate.now());
        return repository.save(transacao);
    }

    @GetMapping
    public List<Transacao> listarTodas() {
        return repository.findAll();
    }
}
