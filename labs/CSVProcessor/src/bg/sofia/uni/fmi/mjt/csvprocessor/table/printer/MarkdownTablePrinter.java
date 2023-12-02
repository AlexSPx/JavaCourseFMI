package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MarkdownTablePrinter implements TablePrinter {
    private final int minRowSize = 3;

    private List<StringBuilder> buildRows(List<Integer> rowSizes, List<Collection<String>> columns) {
        List<StringBuilder> rowsBuilder = new ArrayList<>(columns.get(0).size());
        for (int i = 0; i < columns.get(0).size(); i++) {
            rowsBuilder.add(new StringBuilder("|"));
        }

        for (int i = 0; i < columns.size(); i++) {
            int currentRow = 0;
            for (String rowData : columns.get(i)) {
                rowsBuilder.get(currentRow++)
                        .append(" ")
                        .append(rowData)
                        .append(" ".repeat(rowSizes.get(i) - rowData.length() + 1))
                        .append("|");
            }
        }

        return rowsBuilder;
    }

    private String getFormatingRow(List<Integer> rowSizes,
                                   List<Collection<String>> columns,
                                   ColumnAlignment... alignments) {
        StringBuilder formatingRow = new StringBuilder("|");
        for (int i = 0; i < columns.size(); i++) {
            if (i >= alignments.length) {
                formatingRow.append(" ").append("-".repeat(rowSizes.get(i))).append(" ");
            } else {
                switch (alignments[i]) {
                    case LEFT -> formatingRow.append(" :").append("-".repeat(
                            rowSizes.get(i) - ColumnAlignment.LEFT.getAlignmentCharactersCount())
                    ).append(" ");
                    case CENTER -> {
                        formatingRow.append(" :").append(
                                rowSizes.get(i) > 1 ? "-".repeat(
                                        rowSizes.get(i) - ColumnAlignment.CENTER.getAlignmentCharactersCount()
                                ) : "-"
                        ).append(": ");
                    }
                    case RIGHT -> formatingRow.append(" ").append("-".repeat(
                            rowSizes.get(i) - ColumnAlignment.RIGHT.getAlignmentCharactersCount())
                    ).append(": ");
                    case NOALIGNMENT -> formatingRow.append(" ").append("-".repeat(rowSizes.get(i))).append(" ");
                }
            }

            formatingRow.append("|");
        }

        return formatingRow.toString();
    }

    private Collection<String> formatData(String headerRow,
                                          List<Integer> rowSizes,
                                          List<Collection<String>> columns,
                                          ColumnAlignment... alignments) {
        List<String> data = new LinkedList<>();

        List<StringBuilder> rowsBuilder = buildRows(rowSizes, columns);
        String formatingRow = getFormatingRow(rowSizes, columns, alignments);

        data.add(headerRow);

        for (StringBuilder bd : rowsBuilder) {
            data.add(bd.toString());
        }

        if (data.size() > 1) {
            data.add(1, formatingRow);
        } else {
            data.add(formatingRow);
        }

        return data;
    }

    private String buildHeaderRow(Collection<String> names, List<Integer> rowSizes) {
        StringBuilder header = new StringBuilder("|");
        int index = 0;
        for (String name : names) {
            header
                    .append(" ")
                    .append(name)
                    .append(" ".repeat(rowSizes.get(index++) - name.length() + 1))
                    .append("|");
        }

        return header.toString();
    }

    @Override
    public Collection<String> printTable(Table table, ColumnAlignment... alignments) {
        if (table == null || alignments == null) {
            throw new IllegalArgumentException();
        }

        List<Integer> rowSizes = new ArrayList<>(table.getRowsCount());
        List<Collection<String>> columns = new ArrayList<>(table.getRowsCount());

        for (String name : table.getColumnNames()) {
            Collection<String> column = table.getColumnData(name);
            columns.add(column);

            int rowSzie = minRowSize;
            if (name.length() > rowSzie) {
                rowSzie = name.length();
            }

            for (String colValue : column) {
                if (colValue.length() > rowSzie) {
                    rowSzie = colValue.length();
                }
            }

            rowSizes.add(rowSzie);
        }

        String header = buildHeaderRow(table.getColumnNames(), rowSizes);

        return formatData(header, rowSizes, columns, alignments);
    }
}
