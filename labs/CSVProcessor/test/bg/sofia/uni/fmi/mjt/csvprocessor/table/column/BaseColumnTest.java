package bg.sofia.uni.fmi.mjt.csvprocessor.table.column;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BaseColumnTest {
    @Test
    void testAddDataTest() {
        Column column = new BaseColumn();
        column.addData("col1");
        column.addData("row1");

        assertEquals(column.getData().size(), 2,
                "Expected size was 2 but was found " + column.getData().size());
    }

    @Test
    void testAddDataNullTest() {
        Column column = new BaseColumn();

        assertThrows(IllegalArgumentException.class,
                () -> column.addData(null),
                "IllegalArgumentException should be thrown with null data");
    }

    @Test
    void testEqualsTest() {
        Column column1 = new BaseColumn();
        Column column2 = new BaseColumn();
        column1.addData("col1");
        column2.addData("col1");

        assertTrue(column1.equals(column2) && column2.equals(column1), "Columns should be equal");
        assertEquals(column1.hashCode(), column2.hashCode(), "hashcode doesnt match");
    }

    @Test
    void testGetColumnNameTest() {
        BaseColumn column1 = new BaseColumn();
        column1.addData("col1");
        column1.addData("row1");

        assertEquals(column1.getName(), "col1", "Name should be col1, but was " + column1.getName());
    }
}
