package bg.sofia.uni.fmi.mjt.csvprocessor.table.column;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class BaseColumn implements Column {
    private final Set<String> values;

    public BaseColumn() {
        this(new LinkedHashSet<>());
    }

    public BaseColumn(Set<String> values) {
        this.values = values;
    }

    @Override
    public void addData(String data) {
        if (data == null || data.isBlank() || values.contains(data)) {
            throw new IllegalArgumentException();
        }

        values.add(data);
    }

    @Override
    public Collection<String> getData() {
        return Collections.unmodifiableCollection(values);
    }

    public String getName() {
        return values.iterator().next();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseColumn column = (BaseColumn) o;
        return Objects.equals(values, column.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
