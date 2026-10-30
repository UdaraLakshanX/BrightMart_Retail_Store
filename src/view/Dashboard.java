package view;

import com.formdev.flatlaf.FlatDarkLaf;
import db.DBConnection;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class Dashboard extends javax.swing.JFrame {

    private String currentUser = "admin";
    private String currentRole = "Admin";

    /**
     * Creates new form Dashboard
     */
    public Dashboard() {
        initComponents();
        styleComponents();
        if (model.UserSession.isLoggedIn()) {
            setUserRole(model.UserSession.getUsername(), model.UserSession.getRole());
        }
        loadDashboardData();
        setLocationRelativeTo(null);
    }

    public Dashboard(String username, String role) {
        this();
        setUserRole(username, role);
    }

    public void setUserRole(String username, String role) {
        this.currentUser = username;
        this.currentRole = role;
        lblUserRole.setText("Logged in as: " + username + " (" + role + ")");
    }

    private void styleComponents() {
        btnProducts.putClientProperty("JButton.buttonType", "roundRect");
        btnSuppliers.putClientProperty("JButton.buttonType", "roundRect");
        btnGRN.putClientProperty("JButton.buttonType", "roundRect");
        btnInvoice.putClientProperty("JButton.buttonType", "roundRect");
        btnLogout.putClientProperty("JButton.buttonType", "roundRect");

        jScrollPane1.getViewport().setBackground(new Color(21, 33, 49));

        JTableHeader header = tblReorderAlerts.getTableHeader();
        header.setBackground(new Color(27, 42, 62));
        header.setForeground(new Color(220, 230, 242));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.LEFT);

        tblReorderAlerts.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblReorderAlerts.getColumnModel().getColumn(1).setPreferredWidth(220);
        tblReorderAlerts.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblReorderAlerts.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblReorderAlerts.getColumnModel().getColumn(4).setPreferredWidth(100);

        pnlCardSales.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pnlCardSales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new TransactionManager(1).setVisible(true);
                Dashboard.this.dispose();
            }
        });

        pnlCardProducts.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pnlCardProducts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new ProductManagement(0).setVisible(true);
                Dashboard.this.dispose();
            }
        });

        pnlCardLowStock.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pnlCardLowStock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new ProductManagement(0).setVisible(true);
                Dashboard.this.dispose();
            }
        });

        tblReorderAlerts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    new ProductManagement(0).setVisible(true);
                    Dashboard.this.dispose();
                }
            }
        });
    }

    public void loadDashboardData() {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return;

            try (PreparedStatement pst = con.prepareStatement("SELECT COUNT(*) FROM product");
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    lblProductsValue.setText(String.valueOf(rs.getInt(1)));
                }
            }

            try (PreparedStatement pst = con.prepareStatement("SELECT COUNT(*) FROM product WHERE current_stock <= reorder_level");
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    lblLowStockValue.setText(String.valueOf(rs.getInt(1)));
                }
            }

            try (PreparedStatement pst = con.prepareStatement("SELECT COALESCE(SUM(total_amount), 0) FROM invoice WHERE DATE(invoice_date_time) = CURRENT_DATE");
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    lblSalesValue.setText(String.format("LKR %,.2f", rs.getDouble(1)));
                }
            }

            DefaultTableModel model = (DefaultTableModel) tblReorderAlerts.getModel();
            model.setRowCount(0);
            try (PreparedStatement pst = con.prepareStatement("SELECT item_code, description, category, current_stock, reorder_level FROM product WHERE current_stock <= reorder_level ORDER BY current_stock ASC");
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("item_code"),
                        rs.getString("description"),
                        rs.getString("category"),
                        String.valueOf(rs.getInt("current_stock")),
                        String.valueOf(rs.getInt("reorder_level"))
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
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
        lblDashboard = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        lblUserRole = new javax.swing.JLabel();
        pnlSidebar = new javax.swing.JPanel();
        btnProducts = new javax.swing.JButton();
        btnSuppliers = new javax.swing.JButton();
        btnGRN = new javax.swing.JButton();
        btnInvoice = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        pnlContent = new javax.swing.JPanel();
        pnlCardSales = new javax.swing.JPanel();
        lblSalesTitle = new javax.swing.JLabel();
        lblSalesValue = new javax.swing.JLabel();
        pnlCardProducts = new javax.swing.JPanel();
        lblProductsTitle = new javax.swing.JLabel();
        lblProductsValue = new javax.swing.JLabel();
        pnlCardLowStock = new javax.swing.JPanel();
        lblLowStockTitle = new javax.swing.JLabel();
        lblLowStockValue = new javax.swing.JLabel();
        pnlTableCard = new javax.swing.JPanel();
        lblTableTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReorderAlerts = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BrightMart - Dashboard");
        setBackground(new java.awt.Color(18, 28, 42));

        jPanel1.setBackground(new java.awt.Color(18, 28, 42));
        jPanel1.setPreferredSize(new java.awt.Dimension(1060, 620));

        pnlHeader.setBackground(new java.awt.Color(18, 28, 42));

        lblBrand.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblBrand.setForeground(new java.awt.Color(41, 182, 246));
        lblBrand.setText("BRIGHTMART");

        lblDashboard.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblDashboard.setForeground(new java.awt.Color(255, 255, 255));
        lblDashboard.setText("RETAIL STORE");

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(142, 158, 176));
        lblSubtitle.setText("DashBoard");

        lblUserRole.setBackground(new java.awt.Color(31, 45, 62));
        lblUserRole.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblUserRole.setForeground(new java.awt.Color(203, 213, 225));
        lblUserRole.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUserRole.setText("Logged in as: admin");
        lblUserRole.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblUserRole.setOpaque(true);

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
                        .addComponent(lblDashboard))
                    .addComponent(lblSubtitle))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblUserRole, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblBrand)
                            .addComponent(lblDashboard))
                        .addGap(2, 2, 2)
                        .addComponent(lblSubtitle))
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(lblUserRole, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnlSidebar.setBackground(new java.awt.Color(24, 37, 54));
        pnlSidebar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        btnProducts.setBackground(new java.awt.Color(35, 56, 82));
        btnProducts.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        btnProducts.setForeground(new java.awt.Color(255, 255, 255));
        btnProducts.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assest/products.png"))); // NOI18N
        btnProducts.setText(" Products");
        btnProducts.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnProducts.setFocusPainted(false);
        btnProducts.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnProducts.setIconTextGap(10);
        btnProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductsActionPerformed(evt);
            }
        });

        btnSuppliers.setBackground(new java.awt.Color(35, 56, 82));
        btnSuppliers.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        btnSuppliers.setForeground(new java.awt.Color(255, 255, 255));
        btnSuppliers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assest/suppliers.png"))); // NOI18N
        btnSuppliers.setText(" Suppliers");
        btnSuppliers.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSuppliers.setFocusPainted(false);
        btnSuppliers.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSuppliers.setIconTextGap(10);
        btnSuppliers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuppliersActionPerformed(evt);
            }
        });

        btnGRN.setBackground(new java.awt.Color(35, 56, 82));
        btnGRN.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        btnGRN.setForeground(new java.awt.Color(255, 255, 255));
        btnGRN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assest/grn.png"))); // NOI18N
        btnGRN.setText(" GRN");
        btnGRN.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGRN.setFocusPainted(false);
        btnGRN.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGRN.setIconTextGap(10);
        btnGRN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGRNActionPerformed(evt);
            }
        });

        btnInvoice.setBackground(new java.awt.Color(35, 56, 82));
        btnInvoice.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        btnInvoice.setForeground(new java.awt.Color(255, 255, 255));
        btnInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assest/invoice.png"))); // NOI18N
        btnInvoice.setText(" Invoice");
        btnInvoice.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnInvoice.setFocusPainted(false);
        btnInvoice.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnInvoice.setIconTextGap(10);
        btnInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInvoiceActionPerformed(evt);
            }
        });

        btnLogout.setBackground(new java.awt.Color(208, 74, 54));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assest/logout.png"))); // NOI18N
        btnLogout.setText(" Logout");
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.setFocusPainted(false);
        btnLogout.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLogout.setIconTextGap(10);
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSidebarLayout = new javax.swing.GroupLayout(pnlSidebar);
        pnlSidebar.setLayout(pnlSidebarLayout);
        pnlSidebarLayout.setHorizontalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLogout, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .addComponent(btnInvoice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGRN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSuppliers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );
        pnlSidebarLayout.setVerticalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(btnSuppliers, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(btnGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(btnInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 182, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        pnlContent.setBackground(new java.awt.Color(18, 28, 42));

        pnlCardSales.setBackground(new java.awt.Color(21, 33, 49));
        pnlCardSales.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        lblSalesTitle.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblSalesTitle.setForeground(new java.awt.Color(148, 163, 184));
        lblSalesTitle.setText("Today's Sales");

        lblSalesValue.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblSalesValue.setForeground(new java.awt.Color(255, 255, 255));
        lblSalesValue.setText("LKR 0.00");

        javax.swing.GroupLayout pnlCardSalesLayout = new javax.swing.GroupLayout(pnlCardSales);
        pnlCardSales.setLayout(pnlCardSalesLayout);
        pnlCardSalesLayout.setHorizontalGroup(
            pnlCardSalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardSalesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCardSalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSalesTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .addComponent(lblSalesValue, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnlCardSalesLayout.setVerticalGroup(
            pnlCardSalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardSalesLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblSalesTitle)
                .addGap(4, 4, 4)
                .addComponent(lblSalesValue)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pnlCardProducts.setBackground(new java.awt.Color(21, 33, 49));
        pnlCardProducts.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        lblProductsTitle.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblProductsTitle.setForeground(new java.awt.Color(148, 163, 184));
        lblProductsTitle.setText("Total Products");

        lblProductsValue.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblProductsValue.setForeground(new java.awt.Color(255, 255, 255));
        lblProductsValue.setText("0");

        javax.swing.GroupLayout pnlCardProductsLayout = new javax.swing.GroupLayout(pnlCardProducts);
        pnlCardProducts.setLayout(pnlCardProductsLayout);
        pnlCardProductsLayout.setHorizontalGroup(
            pnlCardProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardProductsLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCardProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblProductsTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .addComponent(lblProductsValue, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnlCardProductsLayout.setVerticalGroup(
            pnlCardProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardProductsLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblProductsTitle)
                .addGap(4, 4, 4)
                .addComponent(lblProductsValue)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pnlCardLowStock.setBackground(new java.awt.Color(21, 33, 49));
        pnlCardLowStock.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        lblLowStockTitle.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblLowStockTitle.setForeground(new java.awt.Color(148, 163, 184));
        lblLowStockTitle.setText("Low Stock Items");

        lblLowStockValue.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblLowStockValue.setForeground(new java.awt.Color(255, 255, 255));
        lblLowStockValue.setText("0");

        javax.swing.GroupLayout pnlCardLowStockLayout = new javax.swing.GroupLayout(pnlCardLowStock);
        pnlCardLowStock.setLayout(pnlCardLowStockLayout);
        pnlCardLowStockLayout.setHorizontalGroup(
            pnlCardLowStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardLowStockLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCardLowStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLowStockTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .addComponent(lblLowStockValue, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnlCardLowStockLayout.setVerticalGroup(
            pnlCardLowStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardLowStockLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblLowStockTitle)
                .addGap(4, 4, 4)
                .addComponent(lblLowStockValue)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pnlTableCard.setBackground(new java.awt.Color(21, 33, 49));
        pnlTableCard.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblTableTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTableTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTableTitle.setText("REORDER LEVEL ALERTS (Low Stock)");

        jScrollPane1.setBackground(new java.awt.Color(21, 33, 49));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        tblReorderAlerts.setBackground(new java.awt.Color(21, 33, 49));
        tblReorderAlerts.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblReorderAlerts.setForeground(new java.awt.Color(226, 232, 240));
        tblReorderAlerts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item Code", "Description", "Category", "Current Stock", "Reorder Level"
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
        tblReorderAlerts.setGridColor(new java.awt.Color(30, 47, 66));
        tblReorderAlerts.setRowHeight(28);
        tblReorderAlerts.setSelectionBackground(new java.awt.Color(35, 60, 95));
        tblReorderAlerts.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblReorderAlerts.setShowGrid(true);
        jScrollPane1.setViewportView(tblReorderAlerts);

        javax.swing.GroupLayout pnlTableCardLayout = new javax.swing.GroupLayout(pnlTableCard);
        pnlTableCard.setLayout(pnlTableCardLayout);
        pnlTableCardLayout.setHorizontalGroup(
            pnlTableCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTableCardLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlTableCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTableTitle)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 763, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnlTableCardLayout.setVerticalGroup(
            pnlTableCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTableCardLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblTableTitle)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout pnlContentLayout = new javax.swing.GroupLayout(pnlContent);
        pnlContent.setLayout(pnlContentLayout);
        pnlContentLayout.setHorizontalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentLayout.createSequentialGroup()
                .addComponent(pnlCardSales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(14, 14, 14)
                .addComponent(pnlCardProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(14, 14, 14)
                .addComponent(pnlCardLowStock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(pnlTableCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlContentLayout.setVerticalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentLayout.createSequentialGroup()
                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlCardLowStock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlCardProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlCardSales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16)
                .addComponent(pnlTableCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(pnlSidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(pnlContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(24, 24, 24))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlSidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(48, 48, 48))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductsActionPerformed
        new ProductManagement(0).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnProductsActionPerformed

    private void btnSuppliersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuppliersActionPerformed
        new SupplierManagement().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnSuppliersActionPerformed

    private void btnGRNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGRNActionPerformed
        new GRNManagement().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnGRNActionPerformed

    private void btnInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvoiceActionPerformed
        new TransactionManager().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnInvoiceActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        model.UserSession.clear();
        new Login().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnLogoutActionPerformed

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
                new Dashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGRN;
    private javax.swing.JButton btnInvoice;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnSuppliers;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBrand;
    private javax.swing.JLabel lblDashboard;
    private javax.swing.JLabel lblLowStockTitle;
    private javax.swing.JLabel lblLowStockValue;
    private javax.swing.JLabel lblProductsTitle;
    private javax.swing.JLabel lblProductsValue;
    private javax.swing.JLabel lblSalesTitle;
    private javax.swing.JLabel lblSalesValue;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTableTitle;
    private javax.swing.JLabel lblUserRole;
    private javax.swing.JPanel pnlCardLowStock;
    private javax.swing.JPanel pnlCardProducts;
    private javax.swing.JPanel pnlCardSales;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlSidebar;
    private javax.swing.JPanel pnlTableCard;
    private javax.swing.JTable tblReorderAlerts;
    // End of variables declaration//GEN-END:variables
}
