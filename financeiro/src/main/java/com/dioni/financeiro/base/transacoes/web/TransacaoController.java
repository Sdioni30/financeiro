package com.dioni.financeiro.base.transacoes.web;

import com.dioni.financeiro.base.dto.TransacaoDTO;
import com.dioni.financeiro.base.dto.TransacaoRequest;
import com.dioni.financeiro.base.enums.Categoria;
import com.dioni.financeiro.base.enums.TipoTransacao;
import com.dioni.financeiro.base.transacoes.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    private final CalcularSaldoCommand calcularSaldoCommand;
    private final ExportarRelatorioCommand exportarRelatorioCommand;
    private final DeletarTransacaoCommand deletarTransacaoCommand;
    private final ListarTransacoesCommand listarTransacoesCommand;
    private final CriarTransacaoCommand criarTransacaoCommand;

    @PostMapping
    public ResponseEntity<TransacaoDTO> criar(@RequestBody TransacaoRequest request) {
        return ResponseEntity.ok(criarTransacaoCommand.executar(request));
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<TransacaoDTO>> listarTodas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam Categoria categoria) {
        Pageable pageable = PageRequest.of(page, 10);
        return ResponseEntity.ok(listarTransacoesCommand.executar(pageable, categoria));
    }

    @GetMapping("/saldo/{categoria}")
    public ResponseEntity<Double> obterSaldo(@PathVariable Categoria categoria) {
        return ResponseEntity.ok(calcularSaldoCommand.executar(categoria));
    }

    @GetMapping("/download/relatorio/{categoria}")
    public ResponseEntity<byte[]> baixarRelatorio(@PathVariable Categoria categoria,
                                                  @RequestParam(required = false) TipoTransacao tipo) {
        return exportarRelatorioCommand.executar(categoria, tipo);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return deletarTransacaoCommand.executar(id);
    }
}
