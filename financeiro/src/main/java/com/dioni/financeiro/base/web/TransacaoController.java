package com.dioni.financeiro.base.web;

import com.dioni.financeiro.base.model.Categoria;
import com.dioni.financeiro.base.repository.CalcularSaldoCommand;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dioni.financeiro.base.repository.TransacaoRepository;
import com.dioni.financeiro.base.model.Transacao;

import java.time.LocalDate;
import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    private final CalcularSaldoCommand calcularSaldoCommand;

    private final TransacaoRepository repository;

    @PostMapping
    public Transacao criar(@RequestBody Transacao transacao) {
        transacao.setData(LocalDate.now());
        return repository.save(transacao);
    }

    @GetMapping
    public List<Transacao> listarTodas() {
        return repository.findAll();
    }

    @GetMapping("/saldo/{categoria}")
    public ResponseEntity<Double> obterSaldo(@PathVariable Categoria categoria) {
        return ResponseEntity.ok(calcularSaldoCommand.executar(categoria));
    }

}
