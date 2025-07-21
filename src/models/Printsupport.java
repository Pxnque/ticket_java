/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author PC
 */
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * Utility to take JTable data and print a simple receipt.
 * We only print the first 3 data columns: Cantidad, Descripción, Monto.
 */
public class Printsupport {

    /** The JTable used internally for printing (data snapshot). */
    static JTable itemsTable;

    /** Row count used to size the page. */
    public static int total_item_count = 0;

    /** Column titles for the printed receipt (3 columns only). */
    public static final String[] TITLE = new String[]{"CANT", "DESC", "MONTO"};
    
    public static double computeTotal() {
    if (itemsTable == null || itemsTable.getRowCount() == 0) {
        return 0.0;
    }
    double total = 0.0;
    TableModel mod = itemsTable.getModel();
    for (int i = 0; i < mod.getRowCount(); i++) {
        try {
            int cantidad = Integer.parseInt(mod.getValueAt(i, 0).toString());
            double monto = Double.parseDouble(mod.getValueAt(i, 2).toString());
            total += cantidad * monto;
        } catch (NumberFormatException e) {
            // skip rows with invalid data
        }
    }
    return total;
}

    /**
     * Accepts raw data (possibly with 4 columns) and prepares an internal 3‑column
     * JTable snapshot for printing.
     *
     * @param rawItems object[][] from the original JTable model (any col count >= 3)
     */
    public static void setItems(Object[][] rawItems) {
        if (rawItems == null) {
            rawItems = new Object[0][0];
        }

        // Trim to first 3 columns (CANT, DESC, MONTO). If fewer exist, fill blanks.
        Object[][] data = new Object[rawItems.length][TITLE.length];
        for (int r = 0; r < rawItems.length; r++) {
            data[r][0] = getSafe(rawItems, r, 0); // Cantidad
            data[r][1] = getSafe(rawItems, r, 1); // Descripción
            data[r][2] = getSafe(rawItems, r, 2); // Monto
        }

        DefaultTableModel model = new DefaultTableModel(data, TITLE) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class; // Cantidad
                    case 1: return String.class;  // Descripción
                    case 2: return Double.class;  // Monto
                    default: return Object.class;
                }
            }
        };

        total_item_count = data.length;
        itemsTable = new JTable(model);
        System.out.println("Printsupport: received " + total_item_count + " rows for printing.");
    }

    /** Safe lookup helper to avoid index errors when trimming columns. */
    private static Object getSafe(Object[][] src, int row, int col) {
        if (row >= 0 && row < src.length && src[row] != null && col >= 0 && col < src[row].length) {
            return src[row][col];
        }
        return ""; // fallback
    }

    /**
     * Extracts all data from a JTable into a 2D Object array.
     * (This reads all columns; we'll trim to 3 in {@link #setItems(Object[][])}.)
     */
    public Object[][] getTableData(JTable table) {
        int itemcount = table.getRowCount();
        System.out.println("Item Count:" + itemcount);

        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
        Object[][] tableData = new Object[nRow][nCol];

        for (int i = 0; i < nRow; i++) {
            for (int j = 0; j < nCol; j++) {
                tableData[i][j] = dtm.getValueAt(i, j);
            }
        }
        return tableData;
    }

    /**
     * PageFormat sized according to number of rows.
     */
    public static PageFormat getPageFormat(PrinterJob pj) {
        PageFormat pf = pj.defaultPage();
        Paper paper = pf.getPaper();

        double middleHeight = total_item_count * 1.0; // simplistic dynamic height factor
        double headerHeight = 5.0;
        double footerHeight = 5.0;

        double width = convert_CM_To_PPI(7); // receipt width (~7 cm)
        double height = convert_CM_To_PPI(headerHeight + middleHeight + footerHeight);
        paper.setSize(width, height);
        paper.setImageableArea(
                convert_CM_To_PPI(0.25),
                convert_CM_To_PPI(0.5),
                width - convert_CM_To_PPI(0.35),
                height - convert_CM_To_PPI(1));

        pf.setOrientation(PageFormat.PORTRAIT);
        pf.setPaper(paper);
        return pf;
    }

    protected static double convert_CM_To_PPI(double cm) {
        return toPPI(cm * 0.393600787);
    }

    protected static double toPPI(double inch) {
        return inch * 72d;
    }

    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss a";

    /**
     * Printable implementation that prints the 3-column receipt.
     */
    public static class MyPrintable implements Printable {

        // Column X positions (tweak to taste)
        private static final int COL_X_CANT = 10;
        private static final int COL_X_DESC = 60;
        private static final int COL_X_MONTO = 140;

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex != 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
            Font font = new Font("Monospaced", Font.PLAIN, 7);
            g2d.setFont(font);

            int y = 10;

            // Logo (optional)
            try {
                BufferedImage read = ImageIO.read(getClass().getResource("/img/loaiza.png"));
                int imagewidth = 100;
                int imageheight = 50;
                int x = 100;
                g2d.drawImage(read, x, y, imagewidth, imageheight, null);
                g2d.drawLine(10, y + 60, 180, y + 60);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                /* Header text */
                int hdrY = 80;
                g2d.drawString("Consultorio Jimmy", 40, hdrY);
                g2d.drawString("CopyWrite 2009-2014", 50, hdrY + 10);
                g2d.drawString(now(), 10, hdrY + 20);
                g2d.drawString("Cashier : Roy", 10, hdrY + 30);

                /* Column headers */
                g2d.drawLine(10, hdrY + 40, 180, hdrY + 40);
                g2d.drawString(TITLE[0], COL_X_CANT, hdrY + 50);
                g2d.drawString(TITLE[1], COL_X_DESC, hdrY + 50);
                g2d.drawString(TITLE[2], COL_X_MONTO, hdrY + 50);
                g2d.drawLine(10, hdrY + 60, 180, hdrY + 60);

                /* Rows */
                TableModel mod = itemsTable.getModel();
                int cH = hdrY + 70;
                for (int i = 0; i < mod.getRowCount(); i++) {
                    String cant = safeString(mod.getValueAt(i, 0));
                    String desc = safeString(mod.getValueAt(i, 1));
                    String monto = safeString(mod.getValueAt(i, 2));

                    g2d.drawString(cant, COL_X_CANT, cH);
                    g2d.drawString(desc, COL_X_DESC, cH);
                    g2d.drawString(monto, COL_X_MONTO, cH);

                    cH += 10; // next line
                }
                font = new Font("Monospaced", Font.BOLD, 8);
                g2d.setFont(font);
                double total = Printsupport.computeTotal();
                g2d.drawLine(10, cH, 180, cH);  // separator line
                cH += 10;
                g2d.drawString("TOTAL:", COL_X_DESC - 20, cH);
                g2d.drawString(String.format("$%.2f", total), COL_X_MONTO, cH);

                /* Footer */
                /*font = new Font("Arial", Font.BOLD, 6);
                g2d.setFont(font);
                g2d.drawString("Thank You Come Again", 30, cH + 20);*/
                
            } catch (Exception r) {
                r.printStackTrace();
            }

            return PAGE_EXISTS;
        }

        private String safeString(Object o) {
            return o == null ? "" : o.toString();
        }
    }
}