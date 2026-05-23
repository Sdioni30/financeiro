package com.dioni.financeiro.base.transacoes.repository;

import com.dioni.financeiro.base.auth.model.Usuario;
import com.dioni.financeiro.base.enums.Categoria;
import com.dioni.financeiro.base.transacoes.model.Transacao;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class ExportarRelatorioCommand {

    private final TransacaoRepository repository;
    private final TransacaoQuery transacaoQuery;

    public byte[] executar(Categoria categoria) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Transacao> transacoes;
        if (usuario.isModoMensal()) {
            int mes = LocalDate.now().getMonthValue();
            int ano = LocalDate.now().getYear();
            transacoes = transacaoQuery.filtrarPorMes(mes, ano).stream()
                    .filter(t -> t.getCategoria().equals(categoria))
                    .toList();
        } else {
            transacoes = repository.findAll().stream()
                    .filter(t -> t.getCategoria().equals(categoria))
                    .toList();
        }

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Relatorio");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Data");
            header.createCell(1).setCellValue("Tipo");
            header.createCell(2).setCellValue("Valor");

            int rowIdx = 1;
            for (Transacao t : transacoes) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(t.getData().toString());
                row.createCell(1).setCellValue(t.getTipo().toString());
                row.createCell(2).setCellValue(t.getValor());
            }

            double saldo = transacoes.stream()
                    .mapToDouble(t -> t.getTipo().name().equals("ENTRADA") ? t.getValor() : -t.getValor())
                    .sum();

            sheet.createRow(rowIdx);
            Row saldoRow = sheet.createRow(rowIdx + 1);
            saldoRow.createCell(1).setCellValue("Saldo");
            saldoRow.createCell(2).setCellValue(saldo);

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro", e);
        }
    }
}
