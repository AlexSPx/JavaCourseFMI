package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.MarkdownTablePrinter;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.TablePrinter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MarkdownTablePrinterTest {

    TablePrinter printer = new MarkdownTablePrinter();

    @Test
    void testWithNullTable() {
        assertThrows(IllegalArgumentException.class, () -> printer.printTable(null));
    }
}
