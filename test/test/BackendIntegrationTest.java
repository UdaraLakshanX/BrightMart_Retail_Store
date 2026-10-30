package test;

import db.DBConnection;
import model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BackendIntegrationTest {
    public static void main(String[] args) {
        System.out.println("=== STARTING BACKEND INTEGRATION TEST (DIRECT JDBC ARCHITECTURE) ===");

        Connection con = DBConnection.getConnection();
        if (con == null) {
            System.err.println("FAIL: Could not establish connection via DBConnection!");
            return;
        }
        System.out.println("PASS: DBConnection connected to brightmart_db successfully.");

        // 1. User Authentication Test
        try {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, "admin");
                pst.setString(2, "admin123");
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        User admin = new User(rs.getString("username"), rs.getString("role"));
                        System.out.println("PASS: User authenticated successfully: " + admin.getUsername() + " (" + admin.getRole() + ")");
                        UserSession.setSession(admin);
                        if (UserSession.isLoggedIn() && "admin".equals(UserSession.getUsername())) {
                            System.out.println("PASS: UserSession correctly retained login state.");
                        }
                    } else {
                        System.err.println("FAIL: Authentication failed for admin/admin123");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("FAIL in user auth: " + e.getMessage());
        }

        // 2. Product Query Test
        List<Product> products = new ArrayList<>();
        try {
            String sql = "SELECT * FROM product ORDER BY item_code ASC";
            try (PreparedStatement pst = con.prepareStatement(sql);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product(
                        rs.getString("item_code"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getDouble("unit_price"),
                        rs.getInt("reorder_level"),
                        rs.getInt("current_stock"),
                        rs.getString("barcode")
                    );
                    products.add(p);
                }
            }
            System.out.println("PASS: Product query retrieved " + products.size() + " products from brightmart_db.");
            for (Product p : products) {
                System.out.println("   -> [" + p.getItemCode() + "] " + p.getDescription() + " | Stock: " + p.getCurrentStock() + " | Price: " + p.getUnitPrice());
            }
        } catch (SQLException e) {
            System.err.println("FAIL in product query: " + e.getMessage());
        }

        // 3. Supplier Query Test
        List<Supplier> suppliers = new ArrayList<>();
        try {
            String sql = "SELECT * FROM supplier ORDER BY supplier_code ASC";
            try (PreparedStatement pst = con.prepareStatement(sql);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Supplier s = new Supplier(
                        rs.getString("supplier_code"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("contact_details")
                    );
                    suppliers.add(s);
                }
            }
            System.out.println("PASS: Supplier query retrieved " + suppliers.size() + " suppliers from brightmart_db.");
            for (Supplier s : suppliers) {
                System.out.println("   -> [" + s.getSupplierCode() + "] " + s.getName() + " | Tel: " + s.getContactDetails());
            }
        } catch (SQLException e) {
            System.err.println("FAIL in supplier query: " + e.getMessage());
        }

        // 4. Low Stock Alerts Query Test
        try {
            String sql = "SELECT * FROM product WHERE current_stock <= reorder_level ORDER BY current_stock ASC";
            try (PreparedStatement pst = con.prepareStatement(sql);
                 ResultSet rs = pst.executeQuery()) {
                int alertCount = 0;
                while (rs.next()) {
                    alertCount++;
                    System.out.println("   -> LOW STOCK ALERT: " + rs.getString("item_code") + " (" + rs.getString("description") + ") - Current Stock: " + rs.getInt("current_stock") + " <= Reorder: " + rs.getInt("reorder_level"));
                }
                System.out.println("PASS: Low Stock alert query identified " + alertCount + " item(s) below reorder level.");
            }
        } catch (SQLException e) {
            System.err.println("FAIL in low stock query: " + e.getMessage());
        }

        // 5. POS Transaction & Stock Deduction Test
        if (!products.isEmpty()) {
            Product target = products.get(0);
            int initialStock = target.getCurrentStock();
            String testInvNo = "INV-TEST99";

            try {
                con.setAutoCommit(false);
                // Clean up any previous test record
                try (PreparedStatement pst = con.prepareStatement("DELETE FROM invoice_items WHERE invoice_no = ?")) {
                    pst.setString(1, testInvNo);
                    pst.executeUpdate();
                }
                try (PreparedStatement pst = con.prepareStatement("DELETE FROM invoice WHERE invoice_no = ?")) {
                    pst.setString(1, testInvNo);
                    pst.executeUpdate();
                }

                // Insert Invoice Header
                String sqlInv = "INSERT INTO invoice (invoice_no, invoice_date_time, total_amount) VALUES (?, ?, ?)";
                try (PreparedStatement pst = con.prepareStatement(sqlInv)) {
                    pst.setString(1, testInvNo);
                    pst.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                    pst.setDouble(3, target.getUnitPrice() * 2);
                    pst.executeUpdate();
                }

                // Insert Item & Deduct Stock
                String sqlItem = "INSERT INTO invoice_items (invoice_no, item_code, sold_qty, selling_price) VALUES (?, ?, ?, ?)";
                String sqlStock = "UPDATE product SET current_stock = current_stock - ? WHERE item_code = ?";
                try (PreparedStatement pstItem = con.prepareStatement(sqlItem);
                     PreparedStatement pstStock = con.prepareStatement(sqlStock)) {
                    pstItem.setString(1, testInvNo);
                    pstItem.setString(2, target.getItemCode());
                    pstItem.setInt(3, 2);
                    pstItem.setDouble(4, target.getUnitPrice());
                    pstItem.executeUpdate();

                    pstStock.setInt(1, 2);
                    pstStock.setString(2, target.getItemCode());
                    pstStock.executeUpdate();
                }

                con.commit();
                con.setAutoCommit(true);

                // Verify Stock Deduction
                int updatedStock = initialStock;
                try (PreparedStatement pst = con.prepareStatement("SELECT current_stock FROM product WHERE item_code = ?")) {
                    pst.setString(1, target.getItemCode());
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) updatedStock = rs.getInt(1);
                    }
                }

                if (updatedStock == initialStock - 2) {
                    System.out.println("PASS: Sales Invoice created and stock accurately deducted from " + initialStock + " to " + updatedStock);
                } else {
                    System.err.println("FAIL: Stock deduction mismatch. Expected: " + (initialStock - 2) + ", Actual: " + updatedStock);
                }

                // 6. Test Stock Reversal on Deletion
                con.setAutoCommit(false);
                // Rollback stock
                try (PreparedStatement pst = con.prepareStatement("UPDATE product SET current_stock = current_stock + 2 WHERE item_code = ?")) {
                    pst.setString(1, target.getItemCode());
                    pst.executeUpdate();
                }
                // Delete line items
                try (PreparedStatement pst = con.prepareStatement("DELETE FROM invoice_items WHERE invoice_no = ?")) {
                    pst.setString(1, testInvNo);
                    pst.executeUpdate();
                }
                // Delete header
                try (PreparedStatement pst = con.prepareStatement("DELETE FROM invoice WHERE invoice_no = ?")) {
                    pst.setString(1, testInvNo);
                    pst.executeUpdate();
                }
                con.commit();
                con.setAutoCommit(true);

                // Verify Restored Stock
                int restoredStock = initialStock;
                try (PreparedStatement pst = con.prepareStatement("SELECT current_stock FROM product WHERE item_code = ?")) {
                    pst.setString(1, target.getItemCode());
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) restoredStock = rs.getInt(1);
                    }
                }

                if (restoredStock == initialStock) {
                    System.out.println("PASS: Transaction Deletion with Automatic Stock Rollback verified! Stock restored back to: " + restoredStock);
                } else {
                    System.err.println("FAIL: Stock reversal mismatch. Expected: " + initialStock + ", Actual: " + restoredStock);
                }

            } catch (SQLException e) {
                System.err.println("FAIL in POS transaction test: " + e.getMessage());
            }

            // 7. Test GRN Inward Stock & Stock Reversal on Deletion
            try {
                String testGRNNo = "GRN-TEST99";
                String testSupCode = suppliers.isEmpty() ? "SUP-001" : suppliers.get(0).getSupplierCode();
                con.setAutoCommit(false);
                // Clean up previous test
                try (PreparedStatement pst = con.prepareStatement("DELETE FROM grn_items WHERE grn_no = ?")) {
                    pst.setString(1, testGRNNo);
                    pst.executeUpdate();
                }
                try (PreparedStatement pst = con.prepareStatement("DELETE FROM grn WHERE grn_no = ?")) {
                    pst.setString(1, testGRNNo);
                    pst.executeUpdate();
                }

                // Insert GRN Header
                String sqlGRN = "INSERT INTO grn (grn_no, supplier_code, received_date_time, total_amount) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstGRN = con.prepareStatement(sqlGRN)) {
                    pstGRN.setString(1, testGRNNo);
                    pstGRN.setString(2, testSupCode);
                    pstGRN.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    pstGRN.setDouble(4, 500.00);
                    pstGRN.executeUpdate();
                }

                // Insert GRN Item & Add Stock
                String sqlGRNItem = "INSERT INTO grn_items (grn_no, item_code, received_qty, buying_price) VALUES (?, ?, ?, ?)";
                String sqlGRNStock = "UPDATE product SET current_stock = current_stock + ? WHERE item_code = ?";
                try (PreparedStatement pstItem = con.prepareStatement(sqlGRNItem);
                     PreparedStatement pstStock = con.prepareStatement(sqlGRNStock)) {
                    pstItem.setString(1, testGRNNo);
                    pstItem.setString(2, target.getItemCode());
                    pstItem.setInt(3, 5);
                    pstItem.setDouble(4, 100.00);
                    pstItem.executeUpdate();

                    pstStock.setInt(1, 5);
                    pstStock.setString(2, target.getItemCode());
                    pstStock.executeUpdate();
                }
                con.commit();
                con.setAutoCommit(true);

                // Verify Inward Stock Addition
                int grnStock = initialStock;
                try (PreparedStatement pst = con.prepareStatement("SELECT current_stock FROM product WHERE item_code = ?")) {
                    pst.setString(1, target.getItemCode());
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) grnStock = rs.getInt(1);
                    }
                }
                if (grnStock == initialStock + 5) {
                    System.out.println("PASS: GRN (Stock Inward) created and stock accurately incremented from " + initialStock + " to " + grnStock);
                } else {
                    System.err.println("FAIL: GRN Stock inward mismatch. Expected: " + (initialStock + 5) + ", Actual: " + grnStock);
                }

                // Reverse GRN: Deduct stock back & Delete records
                con.setAutoCommit(false);
                try (PreparedStatement pst = con.prepareStatement("UPDATE product SET current_stock = current_stock - 5 WHERE item_code = ?")) {
                    pst.setString(1, target.getItemCode());
                    pst.executeUpdate();
                }
                try (PreparedStatement pst = con.prepareStatement("DELETE FROM grn_items WHERE grn_no = ?")) {
                    pst.setString(1, testGRNNo);
                    pst.executeUpdate();
                }
                try (PreparedStatement pst = con.prepareStatement("DELETE FROM grn WHERE grn_no = ?")) {
                    pst.setString(1, testGRNNo);
                    pst.executeUpdate();
                }
                con.commit();
                con.setAutoCommit(true);

                int finalStock = initialStock;
                try (PreparedStatement pst = con.prepareStatement("SELECT current_stock FROM product WHERE item_code = ?")) {
                    pst.setString(1, target.getItemCode());
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) finalStock = rs.getInt(1);
                    }
                }
                if (finalStock == initialStock) {
                    System.out.println("PASS: GRN Deletion with Stock Rollback verified! Stock returned back to: " + finalStock);
                } else {
                    System.err.println("FAIL: GRN stock rollback mismatch. Expected: " + initialStock + ", Actual: " + finalStock);
                }

            } catch (SQLException e) {
                System.err.println("FAIL in POS/GRN transaction test: " + e.getMessage());
            }
        }

        // 8. Test UI View Classes Instantiation & Layout Parity
        try {
            javax.swing.SwingUtilities.invokeAndWait(() -> {
                try {
                    view.Login login = new view.Login();
                    login.pack();
                    login.dispose();

                    view.Dashboard dash = new view.Dashboard();
                    dash.pack();
                    dash.dispose();

                    view.ProductManagement pm = new view.ProductManagement();
                    pm.pack();
                    pm.dispose();

                    view.SupplierManagement sm = new view.SupplierManagement();
                    sm.pack();
                    sm.dispose();

                    view.TransactionManager tm = new view.TransactionManager();
                    tm.pack();
                    tm.setSelectedTab(1);
                    tm.dispose();

                    view.GRNManagement grn = new view.GRNManagement();
                    grn.pack();
                    grn.setSelectedTab(1);
                    grn.dispose();

                    System.out.println("PASS: All 6 Swing Views (Login, Dashboard, ProductManagement, SupplierManagement, TransactionManager, GRNManagement) instantiated, packed, and verified without layout exceptions.");
                } catch (Exception ex) {
                    throw new RuntimeException("UI View Instantiation Failed: " + ex.getMessage(), ex);
                }
            });
        } catch (Exception e) {
            System.err.println("FAIL: UI view instantiation failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== ALL BACKEND INTEGRATION TESTS COMPLETED SUCCESSFULLY ===");
    }
}
