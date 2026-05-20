package com.dioni.financeiro.base.transacoes.web;

import com.dioni.financeiro.base.enums.Categoria;
import com.dioni.financeiro.base.transacoes.repository.CalcularSaldoCommand;
import com.dioni.financeiro.base.transacoes.repository.DeletarTransacaoCommand;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dioni.financeiro.base.transacoes.repository.TransacaoRepository;
import com.dioni.financeiro.base.transacoes.model.Transacao;

import java.time.LocalDate;
import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    private final CalcularSaldoCommand calcularSaldoCommand;

    private final TransacaoRepository repository;

    private final DeletarTransacaoCommand deletarTransacaoCommand;

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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        return deletarTransacaoCommand.executar(id);
    }

}
