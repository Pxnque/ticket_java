/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import Objects.TicketItem;
import Objects.TicketRecord;
import database.ConnectionDB;
import java.io.File;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import models.CSVExporter;
import models.PDFExporter;
/**
 *
 * @author PC
 */
public class Dashboard extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Dashboard.class.getName());
    private DefaultTableModel tableModel;
    /**
     * Creates new form Dashboard
     */
    public Dashboard() {
        initComponents();
        initializeTableModel();
        setupComboBoxListeners();
        setCurrentMonthAndYear();
        loadDashboardData();
    }
    private void initializeTableModel() {
        tableModel = new DefaultTableModel(
            new Object[][]{}, 
            new String[]{"Fecha", "Paciente", "Items", "Total"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        registroTable.setModel(tableModel);
    }
    
    private void setupComboBoxListeners() {
        MesComboBox.addActionListener(e -> loadDashboardData());
        AñoComboBox.addActionListener(e -> loadDashboardData());
    }
    
    private void setCurrentMonthAndYear() {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH); // 0-based
        int currentYear = cal.get(Calendar.YEAR);
        
        MesComboBox.setSelectedIndex(currentMonth);
        
        // Set current year in combo box
        String currentYearStr = String.valueOf(currentYear);
        for (int i = 0; i < AñoComboBox.getItemCount(); i++) {
            if (AñoComboBox.getItemAt(i).equals(currentYearStr)) {
                AñoComboBox.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private void loadDashboardData() {
        int selectedMonth = MesComboBox.getSelectedIndex() + 1; // Convert to 1-based
        int selectedYear = Integer.parseInt((String) AñoComboBox.getSelectedItem());
        
        // Get all tickets from database
        List<TicketRecord> allTickets = ConnectionDB.getAllTickets();
        
        // Filter tickets by selected month and year
        List<TicketRecord> filteredTickets = filterTicketsByMonthAndYear(allTickets, selectedMonth, selectedYear);
        
        // Update the table
        updateTable(filteredTickets);
        
        // Update statistics
        updateStatistics(filteredTickets);
        
        // Update the month label
        updateMonthLabel(selectedMonth, selectedYear);
    }
    
    private List<TicketRecord> filterTicketsByMonthAndYear(List<TicketRecord> tickets, int month, int year) {
        return tickets.stream()
            .filter(ticket -> {
                Date date = new Date((long) (ticket.getFechaVenta() * 1000));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                
                int ticketMonth = cal.get(Calendar.MONTH) + 1; // Convert to 1-based
                int ticketYear = cal.get(Calendar.YEAR);
                
                return ticketMonth == month && ticketYear == year;
            })
            .toList();
    }
    
    private void updateTable(List<TicketRecord> tickets) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        if (tickets.isEmpty()) {
            // Show message when no data is available
            tableModel.addRow(new Object[]{
                "No hay datos", 
                "No se encontraron ventas", 
                "para el período seleccionado", 
                ""
            });
            return;
        }
        
        // Add ticket data to table
        for (TicketRecord ticket : tickets) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = new Date((long) (ticket.getFechaVenta() * 1000));
            String formattedDate = sdf.format(date);
            
            tableModel.addRow(new Object[]{
                formattedDate,
                ticket.getNombrePaciente(),
                ticket.getItemCount() + " items",
                String.format("$%.2f", ticket.getTotal())
            });
        }
    }
    
    private void updateStatistics(List<TicketRecord> tickets) {
        if (tickets.isEmpty()) {
            cantidadpacienteslabel.setText("0");
            cantidadventaslabel.setText("$0.00");
            return;
        }
        
        // Count unique patients
        long uniquePatients = tickets.stream()
            .map(TicketRecord::getNombrePaciente)
            .distinct()
            .count();
        
        // Calculate total sales
        double totalSales = tickets.stream()
            .mapToDouble(TicketRecord::getTotal)
            .sum();
        
        cantidadpacienteslabel.setText(String.valueOf(uniquePatients));
        cantidadventaslabel.setText(String.format("$%.2f", totalSales));
    }
    
    private void updateMonthLabel(int month, int year) {
        String[] monthNames = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        
        String monthName = monthNames[month - 1];
        ventasmeslabel.setText("Ventas de " + monthName + " " + year);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        labelMes = new javax.swing.JLabel();
        MesComboBox = new javax.swing.JComboBox<>();
        añolabel = new javax.swing.JLabel();
        AñoComboBox = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        pacientelabel = new javax.swing.JLabel();
        cantidadpacienteslabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        ventaslabel = new javax.swing.JLabel();
        cantidadventaslabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        registroTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        csvbutton = new javax.swing.JToggleButton();
        pdfbutton = new javax.swing.JToggleButton();
        ventasmeslabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Historial de ventas");
        setResizable(false);

        labelMes.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        labelMes.setText("Mes:");

        MesComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" }));
        MesComboBox.setToolTipText("");

        añolabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        añolabel.setText("Año:");

        AñoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2025", "2026", "2027", "2028", "2029", "2030" }));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel2.setPreferredSize(new java.awt.Dimension(216, 89));

        pacientelabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pacienteico.png"))); // NOI18N
        pacientelabel.setText("pacientes atendidos");

        cantidadpacienteslabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        cantidadpacienteslabel.setForeground(new java.awt.Color(13, 158, 13));
        cantidadpacienteslabel.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(pacientelabel)
                        .addGap(0, 66, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cantidadpacienteslabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(pacientelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cantidadpacienteslabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        ventaslabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ventasico.png"))); // NOI18N
        ventaslabel.setText("ventas totales");

        cantidadventaslabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        cantidadventaslabel.setForeground(new java.awt.Color(37, 139, 3));
        cantidadventaslabel.setText("0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(ventaslabel)
                        .addGap(0, 94, Short.MAX_VALUE))
                    .addComponent(cantidadventaslabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(ventaslabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cantidadventaslabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        registroTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Fecha", "Paciente", "Descripcion", "Monto"
            }
        ));
        registroTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                registroTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(registroTable);

        csvbutton.setBackground(new java.awt.Color(24, 151, 10));
        csvbutton.setForeground(new java.awt.Color(255, 255, 255));
        csvbutton.setText("Exportar a CSV");
        csvbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                csvbuttonActionPerformed(evt);
            }
        });

        pdfbutton.setBackground(new java.awt.Color(190, 8, 8));
        pdfbutton.setForeground(new java.awt.Color(255, 255, 255));
        pdfbutton.setText("Exportar a PDF");
        pdfbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pdfbuttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(csvbutton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pdfbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(csvbutton, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(pdfbutton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        ventasmeslabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        ventasmeslabel.setText("Ventas del mes ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 539, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ventasmeslabel, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelMes)
                            .addComponent(MesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(AñoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(añolabel)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(añolabel)
                    .addComponent(labelMes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AñoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ventasmeslabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(127, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void csvbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_csvbuttonActionPerformed
        // TODO add your handling code here:
         exportToCSV();
    }//GEN-LAST:event_csvbuttonActionPerformed

    private void registroTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_registroTableMouseClicked
        // TODO add your handling code here:
         if (evt.getClickCount() == 2) {
            viewTicketDetails();
        }
    }//GEN-LAST:event_registroTableMouseClicked

    private void pdfbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pdfbuttonActionPerformed
        // TODO add your handling code here:
        exportToPDF();
    }//GEN-LAST:event_pdfbuttonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Dashboard().setVisible(true));
    }
     private void viewTicketDetails() {
        int selectedRow = registroTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un ticket para ver los detalles.");
            return;
        }
        
        // Check if it's the "no data" message row
        Object dateValue = tableModel.getValueAt(selectedRow, 0);
        if ("No hay datos".equals(dateValue)) {
            return;
        }
        
        // Get ticket information from the selected row
        String fecha = (String) tableModel.getValueAt(selectedRow, 0);
        String paciente = (String) tableModel.getValueAt(selectedRow, 1);
        String totalStr = (String) tableModel.getValueAt(selectedRow, 3);
        
        // Find the corresponding ticket in the database
        int selectedMonth = MesComboBox.getSelectedIndex() + 1;
        int selectedYear = Integer.parseInt((String) AñoComboBox.getSelectedItem());
        List<TicketRecord> filteredTickets = filterTicketsByMonthAndYear(
            ConnectionDB.getAllTickets(), selectedMonth, selectedYear);
        
        // Find the ticket that matches the selected row
        TicketRecord selectedTicket = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (TicketRecord ticket : filteredTickets) {
            Date ticketDate = new Date((long) (ticket.getFechaVenta() * 1000));
            String ticketDateStr = sdf.format(ticketDate);
            
            if (ticketDateStr.equals(fecha) && 
                ticket.getNombrePaciente().equals(paciente) &&
                String.format("$%.2f", ticket.getTotal()).equals(totalStr)) {
                selectedTicket = ticket;
                break;
            }
        }
        
        if (selectedTicket != null) {
            showTicketDetails(selectedTicket);
        }
    }
    
    private void showTicketDetails(TicketRecord ticket) {
        List<TicketItem> items = ConnectionDB.getTicketItems(ticket.getIdTicket());
        
        StringBuilder details = new StringBuilder();
        details.append("DETALLES DEL TICKET #").append(ticket.getIdTicket()).append("\n\n");
        details.append("Paciente: ").append(ticket.getNombrePaciente()).append("\n");
        details.append("Fecha: ").append(ticket.getFormattedDate()).append("\n");
        details.append("Total: $").append(String.format("%.2f", ticket.getTotal())).append("\n\n");
        details.append("ITEMS:\n");
        details.append("─".repeat(50)).append("\n");
        
        for (TicketItem item : items) {
            details.append(String.format("• %dx %s - $%.2f c/u = $%.2f\n", 
                item.getCantidad(), 
                item.getDescripcion(), 
                item.getMonto(),
                item.getCantidad() * item.getMonto()));
        }
        
        details.append("─".repeat(50)).append("\n");
        details.append(String.format("TOTAL: $%.2f", ticket.getTotal()));
        
        JOptionPane.showMessageDialog(this, details.toString(), 
            "Detalles del Ticket", JOptionPane.INFORMATION_MESSAGE);
    }
    private void exportToPDF() {
        
        int selectedMonth = MesComboBox.getSelectedIndex() + 1;
        int selectedYear = Integer.parseInt((String) AñoComboBox.getSelectedItem());
        List<TicketRecord> filteredTickets = filterTicketsByMonthAndYear(
            ConnectionDB.getAllTickets(), selectedMonth, selectedYear);
        
        long uniquePatients = filteredTickets.stream()
            .map(TicketRecord::getNombrePaciente)
            .distinct()
            .count();
        
        double totalSales = filteredTickets.stream()
            .mapToDouble(TicketRecord::getTotal)
            .sum();
        
       
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte PDF");
        
        
        String[] monthNames = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        String defaultFileName = String.format("Reporte_%s_%d.pdf", 
            monthNames[selectedMonth - 1], selectedYear);
        fileChooser.setSelectedFile(new File(defaultFileName));
        
   
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf");
        fileChooser.setFileFilter(filter);
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            
            // Add .pdf extension if not present
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            
            boolean success = PDFExporter.exportToPDF(filePath, selectedMonth, selectedYear, 
                uniquePatients, totalSales, filteredTickets);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Reporte PDF exportado exitosamente a:\n" + filePath,
                    "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al exportar el reporte PDF.\nAsegúrese de tener la librería iText en el classpath.",
                    "Error de Exportación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void exportToCSV() {
        int selectedMonth = MesComboBox.getSelectedIndex() + 1;
        int selectedYear = Integer.parseInt((String) AñoComboBox.getSelectedItem());
        List<TicketRecord> filteredTickets = filterTicketsByMonthAndYear(
            ConnectionDB.getAllTickets(), selectedMonth, selectedYear);
        
        long uniquePatients = filteredTickets.stream()
            .map(TicketRecord::getNombrePaciente)
            .distinct()
            .count();
        
        double totalSales = filteredTickets.stream()
            .mapToDouble(TicketRecord::getTotal)
            .sum();
        
    
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte CSV");
        
        
        String[] monthNames = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        String defaultFileName = String.format("Reporte_%s_%d.csv", 
            monthNames[selectedMonth - 1], selectedYear);
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        // Set file filter
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv");
        fileChooser.setFileFilter(filter);
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            
            // Add .csv extension if not present
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }
            
            boolean success = CSVExporter.exportToCSV(filePath, selectedMonth, selectedYear, 
                uniquePatients, totalSales, filteredTickets);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Reporte CSV exportado exitosamente a:\n" + filePath,
                    "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al exportar el reporte CSV",
                    "Error de Exportación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> AñoComboBox;
    private javax.swing.JComboBox<String> MesComboBox;
    private javax.swing.JLabel añolabel;
    private javax.swing.JLabel cantidadpacienteslabel;
    private javax.swing.JLabel cantidadventaslabel;
    private javax.swing.JToggleButton csvbutton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelMes;
    private javax.swing.JLabel pacientelabel;
    private javax.swing.JToggleButton pdfbutton;
    private javax.swing.JTable registroTable;
    private javax.swing.JLabel ventaslabel;
    private javax.swing.JLabel ventasmeslabel;
    // End of variables declaration//GEN-END:variables
}
