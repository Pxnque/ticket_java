/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import Objects.TicketRecord;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author PC
 */
public class PDFExporter {
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
    
    public static boolean exportToPDF(String filePath, int month, int year, 
                                     long uniquePatients, double totalSales, 
                                     List<TicketRecord> tickets) {
        
        Document document = new Document(PageSize.A4);
        
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            addHeader(document, month, year, uniquePatients, totalSales);
            document.add(new Paragraph(" "));
            addTicketsTable(document, tickets);
            addSummary(document, tickets.size(), uniquePatients, totalSales);
            
            return true;
            
        } catch (DocumentException | IOException e) {
            System.err.println("Error creating PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            document.close();
        }
    }
    
    private static void addHeader(Document document, int month, int year, 
                                 long uniquePatients, double totalSales) throws DocumentException {
        
        String[] monthNames = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        
        
        Paragraph title = new Paragraph("REPORTE DE VENTAS", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        Paragraph subtitle = new Paragraph(monthNames[month - 1] + " " + year, HEADER_FONT);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20f);
        document.add(subtitle);
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20f);
        PdfPCell headerCell1 = new PdfPCell(new Phrase("INFORMACIÓN GENERAL", HEADER_FONT));
        headerCell1.setColspan(2);
        headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerCell1.setPadding(10f);
        infoTable.addCell(headerCell1);
        addInfoRow(infoTable, "Período:", monthNames[month - 1] + " " + year);
        addInfoRow(infoTable, "Pacientes Atendidos:", String.valueOf(uniquePatients));
        addInfoRow(infoTable, "Ventas Totales:", "$" + String.format("%.2f", totalSales));
        addInfoRow(infoTable, "Fecha de Generación:", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        
        document.add(infoTable);
    }
    
    private static void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, NORMAL_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5f);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5f);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    private static void addTicketsTable(Document document, List<TicketRecord> tickets) throws DocumentException {
        
        // Table title
        Paragraph tableTitle = new Paragraph("DETALLE DE TICKETS", HEADER_FONT);
        tableTitle.setSpacingBefore(10f);
        tableTitle.setSpacingAfter(10f);
        document.add(tableTitle);
        
        if (tickets.isEmpty()) {
            Paragraph noData = new Paragraph("No se encontraron ventas para el período seleccionado.", NORMAL_FONT);
            noData.setAlignment(Element.ALIGN_CENTER);
            noData.setSpacingAfter(20f);
            document.add(noData);
            return;
        }
        
        // Create table
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20f);
        
        // Set column widths
        float[] columnWidths = {1f, 2.5f, 3f, 1.5f, 1f};
        table.setWidths(columnWidths);
        
        // Add headers
        addTableHeader(table, "ID");
        addTableHeader(table, "Fecha");
        addTableHeader(table, "Paciente");
        addTableHeader(table, "Total");
        addTableHeader(table, "Items");
        
        // Add data rows
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (TicketRecord ticket : tickets) {
            Date date = new Date((long) (ticket.getFechaVenta() * 1000));
            String formattedDate = sdf.format(date);
            
            addTableCell(table, String.valueOf(ticket.getIdTicket()));
            addTableCell(table, formattedDate);
            addTableCell(table, ticket.getNombrePaciente());
            addTableCell(table, "$" + String.format("%.2f", ticket.getTotal()));
            addTableCell(table, String.valueOf(ticket.getItemCount()));
        }
        
        document.add(table);
    }
    
    private static void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(8f);
        table.addCell(cell);
    }
    
    private static void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, SMALL_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5f);
        table.addCell(cell);
    }
    
    private static void addSummary(Document document, int totalTickets, 
                                  long uniquePatients, double totalSales) throws DocumentException {
        
        
        Paragraph summaryTitle = new Paragraph("RESUMEN", HEADER_FONT);
        summaryTitle.setSpacingBefore(10f);
        summaryTitle.setSpacingAfter(10f);
        document.add(summaryTitle);
        
        
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(60);
        summaryTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        
        addInfoRow(summaryTable, "Total de Tickets:", String.valueOf(totalTickets));
        addInfoRow(summaryTable, "Pacientes Únicos:", String.valueOf(uniquePatients));
        addInfoRow(summaryTable, "Monto Total:", "$" + String.format("%.2f", totalSales));
        
        document.add(summaryTable);
        
        
        Paragraph footer = new Paragraph("Reporte generado automáticamente por TicketSL", SMALL_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30f);
        document.add(footer);
    }
}
