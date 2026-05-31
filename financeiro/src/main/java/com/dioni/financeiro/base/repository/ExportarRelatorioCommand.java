package com.dioni.financeiro.base.repository;

import com.dioni.financeiro.base.model.Categoria;
import com.dioni.financeiro.base.model.Transacao;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class ExportarRelatorioCommand {

    private final TransacaoRepository repository;


    public byte[] executar(Categoria categoria) {
        List<Transacao> transacoes = repository.findAll().stream()
                .filter(t -> t.getCategoria().equals(categoria))
                .toList();

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

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro", e);
        }
    }
}