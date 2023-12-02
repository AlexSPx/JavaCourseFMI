package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.MarkdownTablePrinter;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.TablePrinter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class CsvProcessor implements CsvProcessorAPI {

    Table table;

    public CsvProcessor() {
        this(new BaseTable());
    }

    public CsvProcessor(Table table) {
        this.table = table;
    }

    @Override
    public void readCsv(Reader reader, String delimiter) throws CsvDataNotCorrectException {
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;

            while ((line = br.readLine()) != null) {
                table.addData(line.split(String.format("\\%s", delimiter)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeTable(Writer writer, ColumnAlignment... alignments) {
        TablePrinter printer = new MarkdownTablePrinter();

        try (BufferedWriter bw = new BufferedWriter(writer)) {
            int index = 0;
            for (String row : printer.printTable(table, alignments)) {
                bw.write(row);
                if (index++ < table.getRowsCount()) {
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
