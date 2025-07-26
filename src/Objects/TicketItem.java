/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

/**
 *
 * @author PC
 */
public class TicketItem {
    private String descripcion;
    private double monto;
    private int cantidad;
    
    public TicketItem(String descripcion, double monto, int cantidad) {
        this.descripcion = descripcion;
        this.monto = monto;
        this.cantidad = cantidad;
    }
    
    // Getters
    public String getDescripcion() {
        return descripcion;
    }
    
    public double getMonto() {
        return monto;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    // Setters
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public void setMonto(double monto) {
        this.monto = monto;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    @Override
    public String toString() {
        return String.format("TicketItem{descripcion='%s', monto=%.2f, cantidad=%d}", 
                           descripcion, monto, cantidad);
    }
}
