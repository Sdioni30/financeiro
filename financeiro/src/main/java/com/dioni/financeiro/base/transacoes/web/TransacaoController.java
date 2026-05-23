package com.dioni.financeiro.base.transacoes.web;

import com.dioni.financeiro.base.enums.Categoria;
import com.dioni.financeiro.base.transacoes.repository.CalcularSaldoCommand;
import com.dioni.financeiro.base.transacoes.repository.DeletarTransacaoCommand;
import com.dioni.financeiro.base.transacoes.repository.ExportarRelatorioCommand;
import com.dioni.financeiro.base.transacoes.repository.ListarTransacoesCommand;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dioni.financeiro.base.transacoes.repository.TransacaoRepository;
import com.dioni.financeiro.base.transacoes.model.Transacao;

import java.time.LocalDate;


@AllArgsConstructor
@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    private final CalcularSaldoCommand calcularSaldoCommand;
    private final ExportarRelatorioCommand exportarRelatorioCommand;
    private final TransacaoRepository repository;
    private final DeletarTransacaoCommand deletarTransacaoCommand;
    private final ListarTransacoesCommand listarTransacoesCommand;

    @PostMapping
    public Transacao criar(@RequestBody Transacao transacao) {
        transacao.setData(LocalDate.now());
        return repository.save(transacao);
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<Transacao>> listarTodas(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return ResponseEntity.ok(listarTransacoesCommand.executar(pageable));
    }

    @GetMapping("/saldo/{categoria}")
    public ResponseEntity<Double> obterSaldo(@PathVariable Categoria categoria) {
        return ResponseEntity.ok(calcularSaldoCommand.executar(categoria));
    }

    @GetMapping("/download/relatorio/{categoria}")
    public ResponseEntity<byte[]> baixarRelatorio(@PathVariable Categoria categoria) {
        byte[] relatorio = exportarRelatorioCommand.executar(categoria);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "relatorio_" + categoria + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(relatorio);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return deletarTransacaoCommand.executar(id);
    }
}
