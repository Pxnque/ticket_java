/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;
import Objects.TicketItem;
import Objects.TicketRecord;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PC
 */
public class ConnectionDB {
    private static final String DB_URL = "jdbc:sqlite:ticketsl.db";
    private static Connection connection;
    public static void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static void createTables() throws SQLException {
        String createTicketTable = """
            CREATE TABLE IF NOT EXISTS ticket (
                id_ticket INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_paciente TEXT(100) NOT NULL,
                total NUMERIC NOT NULL,
                fecha_venta REAL NOT NULL
            )
        """;
        
        String createItemTable = """
            CREATE TABLE IF NOT EXISTS item (
                id_item INTEGER PRIMARY KEY AUTOINCREMENT,
                descripcion TEXT NOT NULL,
                monto NUMERIC NOT NULL,
                cantidad INTEGER NOT NULL
            )
        """;
        
        String createItemsTable = """
            CREATE TABLE IF NOT EXISTS items_table (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_ticket INTEGER NOT NULL,
                id_item INTEGER NOT NULL,
                FOREIGN KEY (id_ticket) REFERENCES ticket(id_ticket),
                FOREIGN KEY (id_item) REFERENCES item(id_item)
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTicketTable);
            stmt.execute(createItemTable);
            stmt.execute(createItemsTable);
        }
    }
    public static boolean saveTicketWithItems(String nombrePaciente, double total, List<TicketItem> items) {
        try {
            connection.setAutoCommit(false); // Start transaction
            
            // Insert ticket
            String insertTicket = "INSERT INTO Ticket (nombre_paciente, total, fecha_venta) VALUES (?, ?, ?)";
            int ticketId;
            
            try (PreparedStatement ticketStmt = connection.prepareStatement(insertTicket, Statement.RETURN_GENERATED_KEYS)) {
                ticketStmt.setString(1, nombrePaciente);
                ticketStmt.setDouble(2, total);
                ticketStmt.setDouble(3, System.currentTimeMillis() / 1000.0); // Unix timestamp
                
                ticketStmt.executeUpdate();
                
                // Get generated ticket ID
                try (ResultSet rs = ticketStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        ticketId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to get ticket ID");
                    }
                }
            }
            
            // Insert items and link them to the ticket
            for (TicketItem item : items) {
                // First, check if item already exists (based on description, monto, cantidad)
                int itemId = findOrCreateItem(item);
                
                // Link item to ticket
                String insertItemsTable = "INSERT INTO items_table (id_ticket, id_item) VALUES (?, ?)";
                try (PreparedStatement itemsStmt = connection.prepareStatement(insertItemsTable)) {
                    itemsStmt.setInt(1, ticketId);
                    itemsStmt.setInt(2, itemId);
                    itemsStmt.executeUpdate();
                }
            }
            
            connection.commit(); // Commit transaction
            return true;
            
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Error saving ticket: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true); // Reset auto-commit
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }
     private static int findOrCreateItem(TicketItem item) throws SQLException {
        // First try to find existing item
        String findItem = "SELECT id_item FROM item WHERE descripcion = ? AND monto = ? AND cantidad = ?";
        try (PreparedStatement findStmt = connection.prepareStatement(findItem)) {
            findStmt.setString(1, item.getDescripcion());
            findStmt.setDouble(2, item.getMonto());
            findStmt.setInt(3, item.getCantidad());
            
            try (ResultSet rs = findStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_item");
                }
            }
        }
        
        // If not found, create new item
        String insertItem = "INSERT INTO Item (descripcion, monto, cantidad) VALUES (?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertItem, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, item.getDescripcion());
            insertStmt.setDouble(2, item.getMonto());
            insertStmt.setInt(3, item.getCantidad());
            
            insertStmt.executeUpdate();
            
            try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Failed to get item ID");
                }
            }
        }
    }
    
    // Get all tickets for history/dashboard
    public static List<TicketRecord> getAllTickets() {
        List<TicketRecord> tickets = new ArrayList<>();
        String query = """
            SELECT t.id_ticket, t.nombre_paciente, t.total, t.fecha_venta,
                   COUNT(it.id_item) as item_count
            FROM ticket t
            LEFT JOIN items_table it ON t.id_ticket = it.id_ticket
            GROUP BY t.id_ticket, t.nombre_paciente, t.total, t.fecha_venta
            ORDER BY t.fecha_venta DESC
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                TicketRecord ticket = new TicketRecord(
                    rs.getInt("id_ticket"),
                    rs.getString("nombre_paciente"),
                    rs.getDouble("total"),
                    rs.getDouble("fecha_venta"),
                    rs.getInt("item_count")
                );
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving tickets: " + e.getMessage());
            e.printStackTrace();
        }
        
        return tickets;
    }
    
    // Get items for a specific ticket
    public static List<TicketItem> getTicketItems(int ticketId) {
        List<TicketItem> items = new ArrayList<>();
        String query = """
            SELECT i.descripcion, i.monto, i.cantidad
            FROM item i
            JOIN items_table it ON i.id_item = it.id_item
            WHERE it.id_ticket = ?
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, ticketId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TicketItem item = new TicketItem(
                        rs.getString("descripcion"),
                        rs.getDouble("monto"),
                        rs.getInt("cantidad")
                    );
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving ticket items: " + e.getMessage());
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Close database connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
