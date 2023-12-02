package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.BaseColumn;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaseTable implements Table {

    private final Map<String, BaseColumn> columns;

    public BaseTable() {
        this.columns = new LinkedHashMap<>();
    }

    @Override
    public void addData(String[] data) throws CsvDataNotCorrectException {
        if (data == null) {
            throw new IllegalArgumentException();
        }

        if (columns.isEmpty()) {
            for (String column : data) {
                if (columns.containsKey(column)) {
                    throw new CsvDataNotCorrectException("Column already exists");
                }

                columns.put(column, new BaseColumn());
            }
        }

        if (columns.size() != data.length) {
            throw new CsvDataNotCorrectException("Data size is bigger than column size");
        }

        int i = 0;
        for (BaseColumn column : columns.values()) {
            column.addData(data[i++]);
        }

    }

    @Override
    public Collection<String> getColumnNames() {
        return Collections.unmodifiableCollection(columns.keySet());
    }

    @Override
    public Collection<String> getColumnData(String column) {
        if (column == null || column.isBlank() || !columns.containsKey(column)) {
            throw new IllegalArgumentException();
        }

        Collection<String> data = columns.get(column).getData();

        return Collections.unmodifiableCollection(
                List.copyOf(data).subList(1, data.size())
        );
    }

    @Override
    public int getRowsCount() {
        return columns.isEmpty() ? 0 : columns.values().iterator().next().getData().size();
    }
}
