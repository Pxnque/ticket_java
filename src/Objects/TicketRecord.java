/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author PC
 */
public class TicketRecord {
    private int idTicket;
    private String nombrePaciente;
    private double total;
    private double fechaVenta; // Unix epoch
    private int itemCount;
    
    public TicketRecord(int idTicket, String nombrePaciente, double total, double fechaVenta, int itemCount) {
        this.idTicket = idTicket;
        this.nombrePaciente = nombrePaciente;
        this.total = total;
        this.fechaVenta = fechaVenta;
        this.itemCount = itemCount;
    }
    
  
    public int getIdTicket() {
        return idTicket;
    }
    
    public String getNombrePaciente() {
        return nombrePaciente;
    }
    
    public double getTotal() {
        return total;
    }
    
    public double getFechaVenta() {
        return fechaVenta;
    }
    
    public int getItemCount() {
        return itemCount;
    }
    
    
    public String getFormattedDate() {
        Date date = new Date((long) (fechaVenta * 1000));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(date);
    }
    
    
    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }
    
    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    public void setFechaVenta(double fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
    
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
    
    @Override
    public String toString() {
        return String.format("TicketRecord{id=%d, paciente='%s', total=%.2f, fecha='%s', items=%d}", 
                           idTicket, nombrePaciente, total, getFormattedDate(), itemCount);
    }
}
