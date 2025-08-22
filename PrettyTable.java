import java.sql.*;
import java.util.*;

public class PrettyTable {
    
    public void displayResult(ResultSet rs) {
        displayResult(rs, "auto");
    }

    /**
     * @param rs        the ResultSet to display
     * @param alignment "left"  -> all columns left aligned
     *                  "right" -> all columns right aligned
     *                  "auto"  -> text left, numeric right
     */
    public void displayResult(ResultSet rs, String alignment) {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            List<String> headers = new ArrayList<>();
            for (int i = 1; i <= colCount; i++) {
                headers.add(meta.getColumnLabel(i));
            }

            // Store rows and compute max width per column
            List<List<String>> rows = new ArrayList<>();
            int[] colWidths = new int[colCount];

            // initialize widths with header lengths
            for (int i = 0; i < colCount; i++) {
                colWidths[i] = headers.get(i).length();
            }

            while (rs.next()) {
                List<String> row = new ArrayList<>();
                for (int i = 1; i <= colCount; i++) {
                    String val = rs.getString(i);
                    if (val == null) val = "NULL";
                    row.add(val);
                    colWidths[i - 1] = Math.max(colWidths[i - 1], val.length());
                }
                rows.add(row);
            }

            // Build separator line
            StringBuilder border = new StringBuilder("+");
            for (int w : colWidths) {
                border.append("-".repeat(w + 2)).append("+");
            }
            String separator = border.toString();

            // Print header
            System.out.println(separator);
            StringBuilder headerLine = new StringBuilder("|");
            for (int i = 0; i < colCount; i++) {
                headerLine.append(" ")
                          .append(String.format("%-" + colWidths[i] + "s", headers.get(i)))
                          .append(" |");
            }
            System.out.println(headerLine);
            System.out.println(separator);

            // Print rows
            for (List<String> row : rows) {
                StringBuilder line = new StringBuilder("|");
                for (int i = 0; i < colCount; i++) {
                    boolean rightAlign = false;

                    if ("right".equalsIgnoreCase(alignment)) {
                        rightAlign = true;
                    } else if ("auto".equalsIgnoreCase(alignment)) {
                        int type = meta.getColumnType(i + 1);
                        // numeric SQL types
                        if (type == Types.INTEGER || type == Types.DECIMAL || type == Types.FLOAT
                                || type == Types.DOUBLE || type == Types.BIGINT
                                || type == Types.NUMERIC || type == Types.SMALLINT) {
                            rightAlign = true;
                        }
                    }
                    String format = rightAlign ? "%" + colWidths[i] + "s" : "%-" + colWidths[i] + "s";
                    line.append(" ")
                        .append(String.format(format, row.get(i)))
                        .append(" |");
                }
                System.out.println(line);
            }
            System.out.println(separator);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
