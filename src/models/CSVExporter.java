/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import Objects.TicketRecord;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author PC
 */
public class CSVExporter {
     public static boolean exportToCSV(String filePath, int month, int year, 
                                     long uniquePatients, double totalSales, 
                                     List<TicketRecord> tickets) {
        try (FileWriter writer = new FileWriter(filePath)) {
            String[] monthNames = {
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
            };
            writer.append("REPORTE DE VENTAS\n");
            writer.append("================\n");
            writer.append("Periodo:,").append(monthNames[month - 1]).append(" ").append(String.valueOf(year)).append("\n");
            writer.append("Pacientes Atendidos:,").append(String.valueOf(uniquePatients)).append("\n");
            writer.append("Ventas Totales:,$").append(String.format("%.2f", totalSales)).append("\n");
            writer.append("Fecha de Generacion:,").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n");
            writer.append("\n");
            writer.append("DETALLE DE TICKETS\n");
            writer.append("==================\n");
            writer.append("ID Ticket,Fecha,Paciente,Total,Cantidad Items\n");
            
            // Write ticket data
            if (tickets.isEmpty()) {
                writer.append("No hay datos,No se encontraron ventas,para el período seleccionado,,\n");
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                for (TicketRecord ticket : tickets) {
                    Date date = new Date((long) (ticket.getFechaVenta() * 1000));
                    String formattedDate = sdf.format(date);
                    
                    writer.append(String.valueOf(ticket.getIdTicket())).append(",");
                    writer.append("\"").append(formattedDate).append("\",");
                    writer.append("\"").append(ticket.getNombrePaciente()).append("\",");
                    writer.append("$").append(String.format("%.2f", ticket.getTotal())).append(",");
                    writer.append(String.valueOf(ticket.getItemCount())).append("\n");
                }
            }
            
            writer.append("\n");
            writer.append("RESUMEN\n");
            writer.append("=======\n");
            writer.append("Total de Tickets:,").append(String.valueOf(tickets.size())).append("\n");
            writer.append("Pacientes Unicos:,").append(String.valueOf(uniquePatients)).append("\n");
            writer.append("Monto Total:,$").append(String.format("%.2f", totalSales)).append("\n");
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
}
