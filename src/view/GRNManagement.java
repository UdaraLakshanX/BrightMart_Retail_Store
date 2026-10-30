package view;

import com.formdev.flatlaf.FlatDarkLaf;
import db.DBConnection;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * GRNManagement - Dedicated Management Window for Purchase Transactions &
 * Goods Received Notes (Stock Inward Operations).
 */
public class GRNManagement extends javax.swing.JFrame {

    private double grnNetTotal = 0.0;

    // History Tab Components
    private JPanel pnlHistoryTab;
    private JTextField txtHistorySearch;
    private JButton btnHistorySearch;
    private JButton btnHistoryRefresh;
    private JTable tblHistory;
    private JScrollPane spHistory;
    private JLabel lblHistoryStatus;
    private JButton btnHistoryViewDetails;
    private JButton btnHistoryDelete;

    /**
     * Creates new form GRNManagement
     */
    public GRNManagement() {
        initComponents();
        initHistoryTab();
        loadSuppliers();
        styleComponents();
        setLocationRelativeTo(null);
        txtBillNoGRN.setText(generateNextGRNNo());
    }

    public void setSelectedTab(int index) {
        if (index >= 0 && index < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(index);
        }
    }

    public String generateNextGRNNo() {
        String sql = "SELECT grn_no FROM grn";
        int maxNum = 0;
        try {
            Connection con = DBConnection.getConnection();
            if (con != null) {
                try (PreparedStatement pst = con.prepareStatement(sql);
                     ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        String grn = rs.getString("grn_no");
                        if (grn != null && grn.toUpperCase().startsWith("GRN-")) {
                            String numPart = grn.substring(4).replaceAll("[^0-9]", "");
                            if (!numPart.isEmpty()) {
                                try {
                                    int num = Integer.parseInt(numPart);
                                    if (num > maxNum) {
                                        maxNum = num;
                                    }
                                } catch (NumberFormatException ignored) {}
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error generating GRN no: " + e.getMessage());
        }
        return String.format("GRN-%05d", maxNum + 1);
    }

    private void loadSuppliers() {
        cmbSupplierGRN.removeAllItems();
        boolean hasSuppliers = false;
        try {
            Connection con = DBConnection.getConnection();
            if (con != null) {
                String sql = "SELECT supplier_code, name FROM supplier ORDER BY supplier_code ASC";
                try (PreparedStatement pst = con.prepareStatement(sql);
                     ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        cmbSupplierGRN.addItem(rs.getString("supplier_code") + " - " + rs.getString("name"));
                        hasSuppliers = true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading suppliers: " + e.getMessage());
        }
        if (!hasSuppliers) {
            cmbSupplierGRN.addItem("SUP001 - Default Supplier");
        }
    }

    private void styleComponents() {
        btnBack.putClientProperty("JButton.buttonType", "roundRect");
        btnSearchGRN.putClientProperty("JButton.buttonType", "roundRect");
        btnAddGRN.putClientProperty("JButton.buttonType", "roundRect");
        btnSaveGRN.putClientProperty("JButton.buttonType", "roundRect");

        txtBarcodeSearchGRN.putClientProperty("JComponent.roundRect", true);
        txtItemNameGRN.putClientProperty("JComponent.roundRect", true);
        txtPriceGRN.putClientProperty("JComponent.roundRect", true);
        txtQtyGRN.putClientProperty("JComponent.roundRect", true);
        txtBillNoGRN.putClientProperty("JComponent.roundRect", true);
        cmbSupplierGRN.putClientProperty("JComponent.roundRect", true);

        txtBarcodeSearchGRN.putClientProperty("JTextField.placeholderText", "Scan barcode or enter item code...");
        txtItemNameGRN.putClientProperty("JTextField.placeholderText", "Item Description");
        txtPriceGRN.putClientProperty("JTextField.placeholderText", "0.00");
        txtQtyGRN.putClientProperty("JTextField.placeholderText", "Qty");
        txtBillNoGRN.putClientProperty("JTextField.placeholderText", "GRN Number");

        jScrollPane2.getViewport().setBackground(new Color(21, 33, 49));
        JTableHeader headerGRN = tblGRN.getTableHeader();
        headerGRN.setBackground(new Color(27, 42, 62));
        headerGRN.setForeground(new Color(220, 230, 242));
        headerGRN.setFont(new Font("Segoe UI", Font.BOLD, 13));
        DefaultTableCellRenderer headerRendererGRN = (DefaultTableCellRenderer) headerGRN.getDefaultRenderer();
        headerRendererGRN.setHorizontalAlignment(JLabel.LEFT);

        tblGRN.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblGRN.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblGRN.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblGRN.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblGRN.getColumnModel().getColumn(4).setPreferredWidth(90);

        txtBarcodeSearchGRN.addActionListener(e -> btnSearchGRNActionPerformed(e));
        txtQtyGRN.addActionListener(e -> btnAddGRNActionPerformed(e));

        java.awt.event.FocusAdapter selectOnFocus = new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (e.getSource() instanceof javax.swing.JTextField) {
                    ((javax.swing.JTextField) e.getSource()).selectAll();
                }
            }
        };
        txtBarcodeSearchGRN.addFocusListener(selectOnFocus);
        txtItemNameGRN.addFocusListener(selectOnFocus);
        txtPriceGRN.addFocusListener(selectOnFocus);
        txtQtyGRN.addFocusListener(selectOnFocus);
        txtBillNoGRN.addFocusListener(selectOnFocus);

        JPopupMenu grnPopup = new JPopupMenu();
        JMenuItem removeGRNItem = new JMenuItem("Remove Selected Item");
        removeGRNItem.addActionListener(e -> {
            int row = tblGRN.getSelectedRow();
            if (row != -1) {
                ((DefaultTableModel) tblGRN.getModel()).removeRow(row);
                calculateGRNTotal();
            }
        });
        JMenuItem clearGRNAll = new JMenuItem("Clear All Items");
        clearGRNAll.addActionListener(e -> {
            ((DefaultTableModel) tblGRN.getModel()).setRowCount(0);
            calculateGRNTotal();
        });
        grnPopup.add(removeGRNItem);
        grnPopup.add(clearGRNAll);
        tblGRN.setComponentPopupMenu(grnPopup);

        tblGRN.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblGRN.getSelectedRow();
                    if (row != -1) {
                        int opt = JOptionPane.showConfirmDialog(GRNManagement.this,
                            "Remove item '" + tblGRN.getValueAt(row, 1) + "' from GRN list?",
                            "Remove Item", JOptionPane.YES_NO_OPTION);
                        if (opt == JOptionPane.YES_OPTION) {
                            ((DefaultTableModel) tblGRN.getModel()).removeRow(row);
                            calculateGRNTotal();
                        }
                    }
                }
            }
        });

        tblGRN.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {
                    int row = tblGRN.getSelectedRow();
                    if (row != -1) {
                        ((DefaultTableModel) tblGRN.getModel()).removeRow(row);
                        calculateGRNTotal();
                    }
                }
            }
        });
    }

    private void calculateGRNTotal() {
        DefaultTableModel model = (DefaultTableModel) tblGRN.getModel();
        grnNetTotal = 0.0;
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                Object val = model.getValueAt(i, 4);
                if (val != null) {
                    grnNetTotal += Double.parseDouble(val.toString());
                }
            } catch (Exception ignored) {}
        }
        lblTotalGRN.setText("Net Total: LKR " + String.format("%.2f", grnNetTotal));
    }

    public void initHistoryTab() {
        pnlHistoryTab = new JPanel(new java.awt.BorderLayout(12, 12));
        pnlHistoryTab.setBackground(new Color(18, 28, 42));
        pnlHistoryTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 16, 14, 16));

        JPanel pnlTop = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 4));
        pnlTop.setOpaque(false);

        JLabel lblSearch = new JLabel("Search GRN / Supplier:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSearch.setForeground(new Color(203, 213, 225));

        txtHistorySearch = new JTextField(16);
        txtHistorySearch.setBackground(new Color(31, 45, 62));
        txtHistorySearch.setForeground(new Color(241, 245, 249));
        txtHistorySearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtHistorySearch.setBorder(new javax.swing.border.LineBorder(new Color(45, 65, 88), 1, true));
        txtHistorySearch.putClientProperty("JComponent.roundRect", true);
        txtHistorySearch.putClientProperty("JTextField.placeholderText", "Search GRN No / Supplier...");

        btnHistorySearch = new JButton("Search");
        btnHistorySearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnHistorySearch.setBackground(new Color(35, 56, 82));
        btnHistorySearch.setForeground(Color.WHITE);
        btnHistorySearch.setBorder(new javax.swing.border.LineBorder(new Color(45, 65, 88), 1, true));
        btnHistorySearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHistorySearch.setFocusPainted(false);
        btnHistorySearch.setPreferredSize(new Dimension(80, 30));
        btnHistorySearch.putClientProperty("JButton.buttonType", "roundRect");

        btnHistoryRefresh = new JButton("Refresh");
        btnHistoryRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnHistoryRefresh.setBackground(new Color(35, 56, 82));
        btnHistoryRefresh.setForeground(Color.WHITE);
        btnHistoryRefresh.setBorder(new javax.swing.border.LineBorder(new Color(45, 65, 88), 1, true));
        btnHistoryRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHistoryRefresh.setFocusPainted(false);
        btnHistoryRefresh.setPreferredSize(new Dimension(80, 30));
        btnHistoryRefresh.putClientProperty("JButton.buttonType", "roundRect");

        pnlTop.add(lblSearch);
        pnlTop.add(txtHistorySearch);
        pnlTop.add(btnHistorySearch);
        pnlTop.add(btnHistoryRefresh);

        tblHistory = new JTable();
        tblHistory.setBackground(new Color(21, 33, 49));
        tblHistory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblHistory.setForeground(new Color(226, 232, 240));
        tblHistory.setGridColor(new Color(30, 47, 66));
        tblHistory.setRowHeight(28);
        tblHistory.setShowGrid(true);

        spHistory = new JScrollPane(tblHistory);
        spHistory.setBorder(new javax.swing.border.LineBorder(new Color(32, 48, 70), 1, true));
        spHistory.getViewport().setBackground(new Color(21, 33, 49));

        JTableHeader headerHist = tblHistory.getTableHeader();
        headerHist.setBackground(new Color(27, 42, 62));
        headerHist.setForeground(new Color(220, 230, 242));
        headerHist.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel pnlBottom = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 14, 8));
        pnlBottom.setOpaque(false);

        lblHistoryStatus = new JLabel("Showing 0 records");
        lblHistoryStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblHistoryStatus.setForeground(new Color(142, 158, 176));

        btnHistoryViewDetails = new JButton("View GRN Details");
        btnHistoryViewDetails.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnHistoryViewDetails.setBackground(new Color(0, 168, 132));
        btnHistoryViewDetails.setForeground(Color.WHITE);
        btnHistoryViewDetails.setFocusPainted(false);
        btnHistoryViewDetails.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHistoryViewDetails.setPreferredSize(new Dimension(170, 36));
        btnHistoryViewDetails.putClientProperty("JButton.buttonType", "roundRect");

        btnHistoryDelete = new JButton("Delete Record");
        btnHistoryDelete.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnHistoryDelete.setBackground(new Color(185, 28, 28));
        btnHistoryDelete.setForeground(Color.WHITE);
        btnHistoryDelete.setFocusPainted(false);
        btnHistoryDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHistoryDelete.setPreferredSize(new Dimension(150, 36));
        btnHistoryDelete.putClientProperty("JButton.buttonType", "roundRect");

        pnlBottom.add(lblHistoryStatus);
        pnlBottom.add(btnHistoryViewDetails);
        pnlBottom.add(btnHistoryDelete);

        pnlHistoryTab.add(pnlTop, java.awt.BorderLayout.NORTH);
        pnlHistoryTab.add(spHistory, java.awt.BorderLayout.CENTER);
        pnlHistoryTab.add(pnlBottom, java.awt.BorderLayout.SOUTH);

        tabbedPane.addTab("GRN History & Records", pnlHistoryTab);

        btnHistorySearch.addActionListener(e -> loadHistoryData());
        txtHistorySearch.addActionListener(e -> loadHistoryData());
        btnHistoryRefresh.addActionListener(e -> {
            txtHistorySearch.setText("");
            loadHistoryData();
        });

        btnHistoryViewDetails.addActionListener(e -> viewGRNDetails());
        btnHistoryDelete.addActionListener(e -> deleteGRNRecord());

        tblHistory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewGRNDetails();
                }
            }
        });

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                loadHistoryData();
            } else {
                txtBarcodeSearchGRN.requestFocusInWindow();
            }
        });

        loadHistoryData();
    }

    public void loadHistoryData() {
        if (tblHistory == null) return;
        String query = (txtHistorySearch != null) ? txtHistorySearch.getText().trim() : "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String[] cols = {"GRN No", "Supplier Code", "Date & Time", "Total Amount (LKR)", "Total Items"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        int count = 0;
        try {
            Connection con = DBConnection.getConnection();
            if (con != null) {
                String sql;
                if (query.isEmpty()) {
                    sql = "SELECT g.grn_no, g.supplier_code, g.received_date_time, g.total_amount, COUNT(gi.item_code) AS item_count " +
                          "FROM grn g LEFT JOIN grn_items gi ON g.grn_no = gi.grn_no " +
                          "GROUP BY g.grn_no, g.supplier_code, g.received_date_time, g.total_amount " +
                          "ORDER BY g.received_date_time DESC";
                } else {
                    sql = "SELECT g.grn_no, g.supplier_code, g.received_date_time, g.total_amount, COUNT(gi.item_code) AS item_count " +
                          "FROM grn g LEFT JOIN grn_items gi ON g.grn_no = gi.grn_no " +
                          "WHERE g.grn_no LIKE ? OR g.supplier_code LIKE ? " +
                          "GROUP BY g.grn_no, g.supplier_code, g.received_date_time, g.total_amount " +
                          "ORDER BY g.received_date_time DESC";
                }

                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    if (!query.isEmpty()) {
                        pst.setString(1, "%" + query + "%");
                        pst.setString(2, "%" + query + "%");
                    }
                    try (ResultSet rs = pst.executeQuery()) {
                        while (rs.next()) {
                            String grnNo = rs.getString("grn_no");
                            String supCode = rs.getString("supplier_code");
                            Timestamp dt = rs.getTimestamp("received_date_time");
                            double amt = rs.getDouble("total_amount");
                            int items = rs.getInt("item_count");
                            String dateStr = dt != null ? sdf.format(new Date(dt.getTime())) : "-";

                            model.addRow(new Object[]{
                                grnNo,
                                supCode,
                                dateStr,
                                String.format("%.2f", amt),
                                items
                            });
                            count++;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading GRN history: " + e.getMessage());
        }

        tblHistory.setModel(model);
        if (lblHistoryStatus != null) {
            lblHistoryStatus.setText("Showing " + count + " GRN record(s)");
        }
    }

    private void viewGRNDetails() {
        int row = tblHistory.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a GRN record from the table to view.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String recordNo = tblHistory.getValueAt(row, 0).toString();
        try {
            Connection con = DBConnection.getConnection();
            if (con != null) {
                String sqlGRN = "SELECT * FROM grn WHERE grn_no = ?";
                String supCode = "-";
                String dtStr = "-";
                double totalAmt = 0.0;
                boolean found = false;

                try (PreparedStatement pstGRN = con.prepareStatement(sqlGRN)) {
                    pstGRN.setString(1, recordNo);
                    try (ResultSet rsGRN = pstGRN.executeQuery()) {
                        if (rsGRN.next()) {
                            found = true;
                            supCode = rsGRN.getString("supplier_code");
                            Timestamp dt = rsGRN.getTimestamp("received_date_time");
                            if (dt != null) {
                                dtStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
                            }
                            totalAmt = rsGRN.getDouble("total_amount");
                        }
                    }
                }

                if (found) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("BrightMart Retail Store - Goods Received Note (GRN)\n");
                    sb.append("========================================\n");
                    sb.append("GRN Number: ").append(recordNo).append("\n");
                    sb.append("Supplier: ").append(supCode).append("\n");
                    sb.append("Date & Time: ").append(dtStr).append("\n");
                    sb.append("Total Inward Value: LKR ").append(String.format("%.2f", totalAmt)).append("\n\n");
                    sb.append("Items Inward Received:\n");
                    sb.append("----------------------------------------\n");

                    String sqlItems = "SELECT gi.*, p.description FROM grn_items gi LEFT JOIN product p ON gi.item_code = p.item_code WHERE gi.grn_no = ?";
                    try (PreparedStatement pstItems = con.prepareStatement(sqlItems)) {
                        pstItems.setString(1, recordNo);
                        try (ResultSet rsItems = pstItems.executeQuery()) {
                            while (rsItems.next()) {
                                String itemCode = rsItems.getString("item_code");
                                String desc = rsItems.getString("description");
                                if (desc == null || desc.isEmpty()) desc = itemCode;
                                int qty = rsItems.getInt("received_qty");
                                double price = rsItems.getDouble("buying_price");
                                double subTotal = qty * price;
                                sb.append(String.format("• %s (%s) - Qty: %d @ LKR %.2f = LKR %.2f\n",
                                        desc, itemCode, qty, price, subTotal));
                            }
                        }
                    }
                    JOptionPane.showMessageDialog(this, sb.toString(), "Goods Received Note: " + recordNo, JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Could not load details for GRN: " + recordNo, "Not Found", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading GRN record: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteGRNRecord() {
        int row = tblHistory.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a GRN record to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String recordNo = tblHistory.getValueAt(row, 0).toString();
        int opt = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to permanently delete Goods Received Note [" + recordNo + "]?\n\n" +
            "CRITICAL STOCK REVERSAL:\nAll items received in this GRN will be DEDUCTED back from inventory stock.\n\nContinue deletion?",
            "Confirm Delete GRN",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            boolean success = false;
            Connection con = DBConnection.getConnection();
            if (con == null) {
                JOptionPane.showMessageDialog(this, "Cannot connect to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                con.setAutoCommit(false);

                List<Object[]> itemsToDeduct = new ArrayList<>();
                try (PreparedStatement pstGet = con.prepareStatement("SELECT item_code, received_qty FROM grn_items WHERE grn_no = ?")) {
                    pstGet.setString(1, recordNo);
                    try (ResultSet rsGet = pstGet.executeQuery()) {
                        while (rsGet.next()) {
                            itemsToDeduct.add(new Object[]{rsGet.getString("item_code"), rsGet.getInt("received_qty")});
                        }
                    }
                }

                // Verify stock deduction safety
                for (Object[] item : itemsToDeduct) {
                    String itemCode = (String) item[0];
                    int qtyToDeduct = (Integer) item[1];
                    try (PreparedStatement pstChk = con.prepareStatement("SELECT current_stock FROM product WHERE item_code = ?")) {
                        pstChk.setString(1, itemCode);
                        try (ResultSet rsChk = pstChk.executeQuery()) {
                            if (rsChk.next()) {
                                int curr = rsChk.getInt("current_stock");
                                if (curr < qtyToDeduct) {
                                    int proceed = JOptionPane.showConfirmDialog(this,
                                        "Warning: Product [" + itemCode + "] currently has only " + curr +
                                        " units in stock.\nDeducting " + qtyToDeduct + " units will result in negative stock (" +
                                        (curr - qtyToDeduct) + ").\n\nDo you still wish to proceed?",
                                        "Negative Stock Warning",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.WARNING_MESSAGE);
                                    if (proceed != JOptionPane.YES_OPTION) {
                                        con.rollback();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }

                String sqlDeduct = "UPDATE product SET current_stock = current_stock - ? WHERE item_code = ?";
                try (PreparedStatement pstDeduct = con.prepareStatement(sqlDeduct)) {
                    for (Object[] item : itemsToDeduct) {
                        pstDeduct.setInt(1, (Integer) item[1]);
                        pstDeduct.setString(2, (String) item[0]);
                        pstDeduct.executeUpdate();
                    }
                }

                try (PreparedStatement pstDelItems = con.prepareStatement("DELETE FROM grn_items WHERE grn_no = ?")) {
                    pstDelItems.setString(1, recordNo);
                    pstDelItems.executeUpdate();
                }

                try (PreparedStatement pstDelGRN = con.prepareStatement("DELETE FROM grn WHERE grn_no = ?")) {
                    pstDelGRN.setString(1, recordNo);
                    pstDelGRN.executeUpdate();
                }

                con.commit();
                success = true;
            } catch (SQLException e) {
                if (con != null) {
                    try { con.rollback(); } catch (SQLException ex) {}
                }
                JOptionPane.showMessageDialog(this, "Database Error during deletion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (con != null) {
                    try { con.setAutoCommit(true); } catch (SQLException ex) {}
                }
            }

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "GRN [" + recordNo + "] was successfully deleted and stock levels were accurately reversed!",
                    "Deletion Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                loadHistoryData();
            }
        }
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
        pnlHeader = new javax.swing.JPanel();
        lblBrand = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        tabbedPane = new javax.swing.JTabbedPane();
        pnlGRNTab = new javax.swing.JPanel();
        lblBarcodeSearchGRN = new javax.swing.JLabel();
        txtBarcodeSearchGRN = new javax.swing.JTextField();
        btnSearchGRN = new javax.swing.JButton();
        lblItemNameGRN = new javax.swing.JLabel();
        txtItemNameGRN = new javax.swing.JTextField();
        lblPriceGRN = new javax.swing.JLabel();
        txtPriceGRN = new javax.swing.JTextField();
        lblQtyGRN = new javax.swing.JLabel();
        txtQtyGRN = new javax.swing.JTextField();
        btnAddGRN = new javax.swing.JButton();
        lblGRNTable = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblGRN = new javax.swing.JTable();
        lblTotalGRN = new javax.swing.JLabel();
        lblBillNoTitle = new javax.swing.JLabel();
        txtBillNoGRN = new javax.swing.JTextField();
        lblStatusTitleGRN = new javax.swing.JLabel();
        cmbSupplierGRN = new javax.swing.JComboBox<>();
        btnSaveGRN = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BrightMart - GRN Management");
        setBackground(new java.awt.Color(18, 28, 42));

        jPanel1.setBackground(new java.awt.Color(18, 28, 42));
        jPanel1.setPreferredSize(new java.awt.Dimension(1060, 620));

        pnlHeader.setBackground(new java.awt.Color(18, 28, 42));

        lblBrand.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblBrand.setForeground(new java.awt.Color(41, 182, 246));
        lblBrand.setText("BRIGHTMART");

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("RETAIL STORE");

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(142, 158, 176));
        lblSubtitle.setText("Goods Received Note (GRN) & Stock Inward Operations");

        btnBack.setBackground(new java.awt.Color(31, 45, 62));
        btnBack.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnBack.setForeground(new java.awt.Color(203, 213, 225));
        try {
            java.net.URL backIconUrl = getClass().getResource("/assest/back.png");
            if (backIconUrl != null) {
                btnBack.setIcon(new javax.swing.ImageIcon(backIconUrl));
            }
        } catch (Exception ignored) {}
        btnBack.setText(" Back to Dashboard");
        btnBack.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        btnBack.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBack.setFocusPainted(false);
        btnBack.setIconTextGap(8);
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addComponent(lblBrand)
                        .addGap(8, 8, 8)
                        .addComponent(lblTitle))
                    .addComponent(lblSubtitle))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblBrand)
                            .addComponent(lblTitle))
                        .addGap(2, 2, 2)
                        .addComponent(lblSubtitle))
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        tabbedPane.setBackground(new java.awt.Color(24, 37, 54));
        tabbedPane.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        pnlGRNTab.setBackground(new java.awt.Color(18, 28, 42));

        lblBarcodeSearchGRN.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblBarcodeSearchGRN.setForeground(new java.awt.Color(203, 213, 225));
        lblBarcodeSearchGRN.setText("Barcode / Item Code:");

        txtBarcodeSearchGRN.setBackground(new java.awt.Color(31, 45, 62));
        txtBarcodeSearchGRN.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtBarcodeSearchGRN.setForeground(new java.awt.Color(241, 245, 249));
        txtBarcodeSearchGRN.setCaretColor(new java.awt.Color(41, 182, 246));
        txtBarcodeSearchGRN.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        btnSearchGRN.setBackground(new java.awt.Color(35, 56, 82));
        btnSearchGRN.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnSearchGRN.setForeground(new java.awt.Color(255, 255, 255));
        btnSearchGRN.setText("Search");
        btnSearchGRN.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        btnSearchGRN.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearchGRN.setFocusPainted(false);
        btnSearchGRN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchGRNActionPerformed(evt);
            }
        });

        lblItemNameGRN.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblItemNameGRN.setForeground(new java.awt.Color(203, 213, 225));
        lblItemNameGRN.setText("Item Name");

        txtItemNameGRN.setBackground(new java.awt.Color(31, 45, 62));
        txtItemNameGRN.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtItemNameGRN.setForeground(new java.awt.Color(241, 245, 249));
        txtItemNameGRN.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblPriceGRN.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblPriceGRN.setForeground(new java.awt.Color(203, 213, 225));
        lblPriceGRN.setText("Price");

        txtPriceGRN.setBackground(new java.awt.Color(31, 45, 62));
        txtPriceGRN.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtPriceGRN.setForeground(new java.awt.Color(241, 245, 249));
        txtPriceGRN.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblQtyGRN.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblQtyGRN.setForeground(new java.awt.Color(203, 213, 225));
        lblQtyGRN.setText("Quantity");

        txtQtyGRN.setBackground(new java.awt.Color(31, 45, 62));
        txtQtyGRN.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtQtyGRN.setForeground(new java.awt.Color(241, 245, 249));
        txtQtyGRN.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        btnAddGRN.setBackground(new java.awt.Color(35, 56, 82));
        btnAddGRN.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnAddGRN.setForeground(new java.awt.Color(255, 255, 255));
        btnAddGRN.setText("Add to GRN List");
        btnAddGRN.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        btnAddGRN.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddGRN.setFocusPainted(false);
        btnAddGRN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddGRNActionPerformed(evt);
            }
        });

        lblGRNTable.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGRNTable.setForeground(new java.awt.Color(203, 213, 225));
        lblGRNTable.setText("Inward Items Batch List:");

        tblGRN.setBackground(new java.awt.Color(21, 33, 49));
        tblGRN.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblGRN.setForeground(new java.awt.Color(226, 232, 240));
        tblGRN.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Item Code", "Description", "Qty", "Price", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblGRN.setGridColor(new java.awt.Color(30, 47, 66));
        tblGRN.setRowHeight(28);
        tblGRN.setShowGrid(true);
        jScrollPane2.setViewportView(tblGRN);

        lblTotalGRN.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTotalGRN.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalGRN.setText("Net Total: LKR 0.00");

        lblBillNoTitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblBillNoTitle.setForeground(new java.awt.Color(203, 213, 225));
        lblBillNoTitle.setText("GRN No:");

        txtBillNoGRN.setBackground(new java.awt.Color(31, 45, 62));
        txtBillNoGRN.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtBillNoGRN.setForeground(new java.awt.Color(241, 245, 249));
        txtBillNoGRN.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblStatusTitleGRN.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblStatusTitleGRN.setForeground(new java.awt.Color(203, 213, 225));
        lblStatusTitleGRN.setText("Supplier:");

        cmbSupplierGRN.setBackground(new java.awt.Color(31, 45, 62));
        cmbSupplierGRN.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbSupplierGRN.setForeground(new java.awt.Color(241, 245, 249));

        btnSaveGRN.setBackground(new java.awt.Color(0, 168, 132));
        btnSaveGRN.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSaveGRN.setForeground(new java.awt.Color(255, 255, 255));
        btnSaveGRN.setText("COMPLETE & RECEIVE");
        btnSaveGRN.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        btnSaveGRN.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSaveGRN.setFocusPainted(false);
        btnSaveGRN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveGRNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlGRNTabLayout = new javax.swing.GroupLayout(pnlGRNTab);
        pnlGRNTab.setLayout(pnlGRNTabLayout);
        pnlGRNTabLayout.setHorizontalGroup(
            pnlGRNTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGRNTabLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlGRNTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblBarcodeSearchGRN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlGRNTabLayout.createSequentialGroup()
                        .addComponent(txtBarcodeSearchGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearchGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlGRNTabLayout.createSequentialGroup()
                        .addGroup(pnlGRNTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblItemNameGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPriceGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblQtyGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlGRNTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtQtyGRN)
                            .addComponent(txtPriceGRN)
                            .addComponent(txtItemNameGRN)))
                    .addComponent(btnAddGRN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(35, 35, 35)
                .addGroup(pnlGRNTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(pnlGRNTabLayout.createSequentialGroup()
                        .addComponent(lblBillNoTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBillNoGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(lblStatusTitleGRN)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbSupplierGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSaveGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlGRNTabLayout.createSequentialGroup()
                        .addGroup(pnlGRNTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblGRNTable)
                            .addComponent(lblTotalGRN))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(20, 20, 20))
        );
        pnlGRNTabLayout.setVerticalGroup(
            pnlGRNTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGRNTabLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlGRNTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBarcodeSearchGRN)
                    .addComponent(lblGRNTable))
                .addGap(8, 8, 8)
                .addGroup(pnlGRNTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlGRNTabLayout.createSequentialGroup()
                        .addGroup(pnlGRNTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBarcodeSearchGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSearchGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(pnlGRNTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblItemNameGRN)
                            .addComponent(txtItemNameGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(pnlGRNTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPriceGRN)
                            .addComponent(txtPriceGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(pnlGRNTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblQtyGRN)
                            .addComponent(txtQtyGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addComponent(btnAddGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
                .addGap(14, 14, 14)
                .addComponent(lblTotalGRN)
                .addGap(14, 14, 14)
                .addGroup(pnlGRNTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBillNoTitle)
                    .addComponent(txtBillNoGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatusTitleGRN)
                    .addComponent(cmbSupplierGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSaveGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );

        tabbedPane.addTab("New GRN (Stock In)", pnlGRNTab);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabbedPane))
                .addGap(24, 24, 24))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1062, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        new Dashboard().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnSearchGRNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchGRNActionPerformed
        String query = txtBarcodeSearchGRN.getText().trim();
        if (query.isEmpty() || query.equalsIgnoreCase("Scan barcode or enter item code...")) {
            JOptionPane.showMessageDialog(this, "Please enter Barcode / Item Code.", "Search Item", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection con = DBConnection.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(this, "Cannot connect to the database. Please check MySQL service.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String sql = "SELECT item_code, description, unit_price FROM product WHERE barcode = ? OR item_code = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, query);
                pst.setString(2, query);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        txtBarcodeSearchGRN.setText(rs.getString("item_code"));
                        txtItemNameGRN.setText(rs.getString("description"));
                        txtPriceGRN.setText(String.format("%.2f", rs.getDouble("unit_price")));
                        txtQtyGRN.setText("10");
                        txtQtyGRN.requestFocus();
                    } else {
                        JOptionPane.showMessageDialog(this, "Product not found for: " + query, "Not Found", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSearchGRNActionPerformed

    private void btnAddGRNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddGRNActionPerformed
        String itemCode = txtBarcodeSearchGRN.getText().trim();
        if (itemCode.isEmpty() || itemCode.equalsIgnoreCase("Scan barcode or enter item code...")) {
            JOptionPane.showMessageDialog(this, "Please search or enter an Item Code.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = txtItemNameGRN.getText().trim();
        if (name.isEmpty() || name.equalsIgnoreCase("Item Description")) {
            JOptionPane.showMessageDialog(this, "Please enter an Item Name.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double price = 0.0;
        int qty = 1;
        try {
            price = Double.parseDouble(txtPriceGRN.getText().trim());
            qty = Integer.parseInt(txtQtyGRN.getText().trim());
            if (qty <= 0 || price <= 0.0) throw new NumberFormatException();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Price and Quantity must be valid numbers greater than 0.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double total = price * qty;
        DefaultTableModel model = (DefaultTableModel) tblGRN.getModel();
        boolean found = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().equalsIgnoreCase(itemCode)) {
                try {
                    int existingQty = Integer.parseInt(model.getValueAt(i, 2).toString());
                    int newQty = existingQty + qty;
                    double newTotal = price * newQty;
                    model.setValueAt(String.valueOf(newQty), i, 2);
                    model.setValueAt(String.format("%.2f", newTotal), i, 4);
                    found = true;
                    break;
                } catch (Exception ignored) {}
            }
        }
        if (!found) {
            model.addRow(new Object[]{
                itemCode,
                name,
                String.valueOf(qty),
                String.format("%.2f", price),
                String.format("%.2f", total)
            });
        }

        calculateGRNTotal();
        txtBarcodeSearchGRN.setText("");
        txtItemNameGRN.setText("");
        txtPriceGRN.setText("");
        txtQtyGRN.setText("");
        txtBarcodeSearchGRN.requestFocus();
    }//GEN-LAST:event_btnAddGRNActionPerformed

    private void btnSaveGRNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveGRNActionPerformed
        DefaultTableModel model = (DefaultTableModel) tblGRN.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No stock items in GRN list.", "Empty GRN", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (grnNetTotal <= 0.0) {
            JOptionPane.showMessageDialog(this, "GRN total amount must be greater than zero.", "Invalid Total", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String grnNo = txtBillNoGRN.getText().trim();
        if (grnNo.isEmpty()) {
            grnNo = generateNextGRNNo();
        }

        String supplierCode = "SUP001";
        if (cmbSupplierGRN.getSelectedItem() != null) {
            String selected = cmbSupplierGRN.getSelectedItem().toString();
            if (selected.contains(" - ")) {
                supplierCode = selected.split(" - ")[0].trim();
            } else {
                supplierCode = selected.trim();
            }
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());

        boolean saved = false;
        Connection con = DBConnection.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(this, "Cannot connect to the database. GRN aborted.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            con.setAutoCommit(false);

            String checkSql = "SELECT grn_no FROM grn WHERE grn_no = ?";
            try (PreparedStatement pstCheck = con.prepareStatement(checkSql)) {
                pstCheck.setString(1, grnNo);
                try (ResultSet rsCheck = pstCheck.executeQuery()) {
                    if (rsCheck.next()) {
                        grnNo = generateNextGRNNo();
                    }
                }
            }

            String sqlGRN = "INSERT INTO grn (grn_no, supplier_code, received_date_time, total_amount) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstGRN = con.prepareStatement(sqlGRN)) {
                pstGRN.setString(1, grnNo);
                pstGRN.setString(2, supplierCode);
                pstGRN.setTimestamp(3, now);
                pstGRN.setDouble(4, grnNetTotal);
                pstGRN.executeUpdate();
            }

            String sqlItem = "INSERT INTO grn_items (grn_no, item_code, received_qty, buying_price) VALUES (?, ?, ?, ?)";
            String sqlStock = "UPDATE product SET current_stock = current_stock + ? WHERE item_code = ?";
            try (PreparedStatement pstItem = con.prepareStatement(sqlItem);
                 PreparedStatement pstStock = con.prepareStatement(sqlStock)) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    String itemCode = model.getValueAt(i, 0).toString();
                    int qty = Integer.parseInt(model.getValueAt(i, 2).toString());
                    double price = Double.parseDouble(model.getValueAt(i, 3).toString());

                    pstItem.setString(1, grnNo);
                    pstItem.setString(2, itemCode);
                    pstItem.setInt(3, qty);
                    pstItem.setDouble(4, price);
                    pstItem.executeUpdate();

                    pstStock.setInt(1, qty);
                    pstStock.setString(2, itemCode);
                    pstStock.executeUpdate();
                }
            }

            con.commit();
            saved = true;
        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) {}
            }
            JOptionPane.showMessageDialog(this, "GRN Save Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException ex) {}
            }
        }

        if (saved) {
            String message = String.format("GRN [%s] Saved Successfully!\n\nSupplier: %s\nTotal Amount: LKR %.2f\nItems Inward: %d\n\nInventory stock levels replenished.",
                    grnNo, supplierCode, grnNetTotal, model.getRowCount());
            JOptionPane.showMessageDialog(this, message, "Stock In Success", JOptionPane.INFORMATION_MESSAGE);

            model.setRowCount(0);
            calculateGRNTotal();
            txtBillNoGRN.setText(generateNextGRNNo());
            loadHistoryData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save GRN to database!", "Transaction Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveGRNActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the FlatDarkLaf look and feel */
        try {
            FlatDarkLaf f = new FlatDarkLaf();
            UIManager.setLookAndFeel(f);
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println("UnsupportedLookAndFeelException: " + e.getMessage());
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GRNManagement().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddGRN;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnSaveGRN;
    private javax.swing.JButton btnSearchGRN;
    private javax.swing.JComboBox<String> cmbSupplierGRN;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblBarcodeSearchGRN;
    private javax.swing.JLabel lblBillNoTitle;
    private javax.swing.JLabel lblBrand;
    private javax.swing.JLabel lblGRNTable;
    private javax.swing.JLabel lblItemNameGRN;
    private javax.swing.JLabel lblPriceGRN;
    private javax.swing.JLabel lblQtyGRN;
    private javax.swing.JLabel lblStatusTitleGRN;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalGRN;
    private javax.swing.JPanel pnlGRNTab;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tblGRN;
    private javax.swing.JTextField txtBarcodeSearchGRN;
    private javax.swing.JTextField txtBillNoGRN;
    private javax.swing.JTextField txtItemNameGRN;
    private javax.swing.JTextField txtPriceGRN;
    private javax.swing.JTextField txtQtyGRN;
    // End of variables declaration//GEN-END:variables
}
