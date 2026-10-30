package view;

import com.formdev.flatlaf.FlatDarkLaf;
import db.DBConnection;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

public class ProductManagement extends javax.swing.JFrame {

    /**
     * Creates new form ProductManagement
     */
    public ProductManagement() {
        initComponents();
        styleComponents();
        loadProductsData();
        setLocationRelativeTo(null);
    }

    public ProductManagement(int mode) {
        this();
    }

    public void setSelectedTab(int index) {
        if (index >= 0 && index < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(index);
        }
    }

    private void styleComponents() {
        btnBack.putClientProperty("JButton.buttonType", "roundRect");
        btnAdd.putClientProperty("JButton.buttonType", "roundRect");
        btnUpdate.putClientProperty("JButton.buttonType", "roundRect");
        btnRefresh.putClientProperty("JButton.buttonType", "roundRect");
        btnDelete.putClientProperty("JButton.buttonType", "roundRect");
        btnClear.putClientProperty("JButton.buttonType", "roundRect");

        txtItemCode.putClientProperty("JComponent.roundRect", true);
        txtDesc.putClientProperty("JComponent.roundRect", true);
        txtCategory.putClientProperty("JComponent.roundRect", true);
        txtPrice.putClientProperty("JComponent.roundRect", true);
        txtReorder.putClientProperty("JComponent.roundRect", true);
        txtBarcode.putClientProperty("JComponent.roundRect", true);
        txtSearch.putClientProperty("JComponent.roundRect", true);
        txtSearch.putClientProperty("JTextField.placeholderText", "Search products by code, description, category, barcode...");

        jScrollPane1.getViewport().setBackground(new Color(21, 33, 49));
        JTableHeader headerProd = tblProducts.getTableHeader();
        headerProd.setBackground(new Color(27, 42, 62));
        headerProd.setForeground(new Color(220, 230, 242));
        headerProd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        DefaultTableCellRenderer headerRendererProd = (DefaultTableCellRenderer) headerProd.getDefaultRenderer();
        headerRendererProd.setHorizontalAlignment(JLabel.LEFT);

        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblProducts.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblProducts.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblProducts.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblProducts.getColumnModel().getColumn(4).setPreferredWidth(100);

        TableRowSorter<DefaultTableModel> sorterProd = new TableRowSorter<>((DefaultTableModel) tblProducts.getModel());
        tblProducts.setRowSorter(sorterProd);
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText().trim();
                if (text.isEmpty()) {
                    sorterProd.setRowFilter(null);
                } else {
                    sorterProd.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
                }
            }
        });
    }

    public void loadProductsData() {
        DefaultTableModel model = (DefaultTableModel) tblProducts.getModel();
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return;
            String sql = "SELECT item_code, description, category, unit_price, reorder_level FROM product ORDER BY item_code ASC";
            try (PreparedStatement pst = con.prepareStatement(sql);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("item_code"),
                        rs.getString("description"),
                        rs.getString("category"),
                        String.format("%.2f", rs.getDouble("unit_price")),
                        String.valueOf(rs.getInt("reorder_level"))
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
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
        pnlProductTab = new javax.swing.JPanel();
        pnlForm = new javax.swing.JPanel();
        lblItemCode = new javax.swing.JLabel();
        txtItemCode = new javax.swing.JTextField();
        lblDesc = new javax.swing.JLabel();
        txtDesc = new javax.swing.JTextField();
        lblCategory = new javax.swing.JLabel();
        txtCategory = new javax.swing.JTextField();
        lblPrice = new javax.swing.JLabel();
        txtPrice = new javax.swing.JTextField();
        lblReorder = new javax.swing.JLabel();
        txtReorder = new javax.swing.JTextField();
        lblBarcode = new javax.swing.JLabel();
        txtBarcode = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        pnlTableSection = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProducts = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BrightMart - Master Data Management");
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
        lblSubtitle.setText("Product Management");

        btnBack.setBackground(new java.awt.Color(31, 45, 62));
        btnBack.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnBack.setForeground(new java.awt.Color(203, 213, 225));
        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assest/back.png"))); // NOI18N
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

        pnlProductTab.setBackground(new java.awt.Color(18, 28, 42));

        pnlForm.setBackground(new java.awt.Color(24, 37, 54));
        pnlForm.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblItemCode.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblItemCode.setForeground(new java.awt.Color(226, 232, 240));
        lblItemCode.setText("Item Code");

        txtItemCode.setBackground(new java.awt.Color(31, 45, 62));
        txtItemCode.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtItemCode.setForeground(new java.awt.Color(241, 245, 249));
        txtItemCode.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtItemCode.setCaretColor(new java.awt.Color(41, 182, 246));

        lblDesc.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblDesc.setForeground(new java.awt.Color(226, 232, 240));
        lblDesc.setText("Description");

        txtDesc.setBackground(new java.awt.Color(31, 45, 62));
        txtDesc.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtDesc.setForeground(new java.awt.Color(241, 245, 249));
        txtDesc.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtDesc.setCaretColor(new java.awt.Color(41, 182, 246));

        lblCategory.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblCategory.setForeground(new java.awt.Color(226, 232, 240));
        lblCategory.setText("Category");

        txtCategory.setBackground(new java.awt.Color(31, 45, 62));
        txtCategory.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtCategory.setForeground(new java.awt.Color(241, 245, 249));
        txtCategory.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtCategory.setCaretColor(new java.awt.Color(41, 182, 246));

        lblPrice.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblPrice.setForeground(new java.awt.Color(226, 232, 240));
        lblPrice.setText("Unit Price");

        txtPrice.setBackground(new java.awt.Color(31, 45, 62));
        txtPrice.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtPrice.setForeground(new java.awt.Color(241, 245, 249));
        txtPrice.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtPrice.setCaretColor(new java.awt.Color(41, 182, 246));

        lblReorder.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblReorder.setForeground(new java.awt.Color(226, 232, 240));
        lblReorder.setText("Reorder Level");

        txtReorder.setBackground(new java.awt.Color(31, 45, 62));
        txtReorder.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtReorder.setForeground(new java.awt.Color(241, 245, 249));
        txtReorder.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtReorder.setCaretColor(new java.awt.Color(41, 182, 246));

        lblBarcode.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblBarcode.setForeground(new java.awt.Color(226, 232, 240));
        lblBarcode.setText("Barcode");

        txtBarcode.setBackground(new java.awt.Color(31, 45, 62));
        txtBarcode.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtBarcode.setForeground(new java.awt.Color(241, 245, 249));
        txtBarcode.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtBarcode.setCaretColor(new java.awt.Color(41, 182, 246));

        btnAdd.setBackground(new java.awt.Color(35, 56, 82));
        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assest/add.png"))); // NOI18N
        btnAdd.setText(" Add Product");
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.setFocusPainted(false);
        btnAdd.setIconTextGap(8);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(35, 56, 82));
        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assest/edit.png"))); // NOI18N
        btnUpdate.setText(" Update");
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.setFocusPainted(false);
        btnUpdate.setIconTextGap(8);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnRefresh.setBackground(new java.awt.Color(35, 56, 82));
        btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnRefresh.setForeground(new java.awt.Color(255, 255, 255));
        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assest/refresh.png"))); // NOI18N
        btnRefresh.setText(" Refresh");
        btnRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setIconTextGap(8);
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(208, 74, 54));
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assest/delete.png"))); // NOI18N
        btnDelete.setText(" Delete");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.setFocusPainted(false);
        btnDelete.setIconTextGap(8);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnClear.setBackground(new java.awt.Color(35, 56, 82));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assest/clear.png"))); // NOI18N
        btnClear.setText(" Clear");
        btnClear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClear.setFocusPainted(false);
        btnClear.setIconTextGap(8);
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFormLayout = new javax.swing.GroupLayout(pnlForm);
        pnlForm.setLayout(pnlFormLayout);
        pnlFormLayout.setHorizontalGroup(
            pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblItemCode)
                            .addComponent(lblDesc)
                            .addComponent(lblCategory)
                            .addComponent(lblPrice)
                            .addComponent(lblReorder)
                            .addComponent(lblBarcode))
                        .addGap(14, 14, 14)
                        .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtItemCode)
                            .addComponent(txtDesc)
                            .addComponent(txtCategory)
                            .addComponent(txtPrice)
                            .addComponent(txtReorder)
                            .addComponent(txtBarcode))))
                .addGap(16, 16, 16))
        );
        pnlFormLayout.setVerticalGroup(
            pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblItemCode)
                    .addComponent(txtItemCode, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDesc)
                    .addComponent(txtDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCategory)
                    .addComponent(txtCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPrice)
                    .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblReorder)
                    .addComponent(txtReorder, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBarcode)
                    .addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pnlTableSection.setBackground(new java.awt.Color(18, 28, 42));

        lblSearch.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSearch.setForeground(new java.awt.Color(203, 213, 225));
        lblSearch.setText("Search Products:");

        txtSearch.setBackground(new java.awt.Color(31, 45, 62));
        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtSearch.setForeground(new java.awt.Color(241, 245, 249));
        txtSearch.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSearch.setCaretColor(new java.awt.Color(41, 182, 246));

        jScrollPane1.setBackground(new java.awt.Color(21, 33, 49));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        tblProducts.setBackground(new java.awt.Color(21, 33, 49));
        tblProducts.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblProducts.setForeground(new java.awt.Color(226, 232, 240));
        tblProducts.setGridColor(new java.awt.Color(30, 47, 66));
        tblProducts.setRowHeight(28);
        tblProducts.setSelectionBackground(new java.awt.Color(35, 60, 95));
        tblProducts.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblProducts.setShowGrid(true);
        tblProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item Code", "Description", "Category", "Unit Price", "Reorder Level"
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
        jScrollPane1.setViewportView(tblProducts);

        javax.swing.GroupLayout pnlTableSectionLayout = new javax.swing.GroupLayout(pnlTableSection);
        pnlTableSection.setLayout(pnlTableSectionLayout);
        pnlTableSectionLayout.setHorizontalGroup(
            pnlTableSectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTableSectionLayout.createSequentialGroup()
                .addComponent(lblSearch)
                .addGap(12, 12, 12)
                .addComponent(txtSearch))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
        );
        pnlTableSectionLayout.setVerticalGroup(
            pnlTableSectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTableSectionLayout.createSequentialGroup()
                .addGroup(pnlTableSectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlProductTabLayout = new javax.swing.GroupLayout(pnlProductTab);
        pnlProductTab.setLayout(pnlProductTabLayout);
        pnlProductTabLayout.setHorizontalGroup(
            pnlProductTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProductTabLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(pnlForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(pnlTableSection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );
        pnlProductTabLayout.setVerticalGroup(
            pnlProductTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProductTabLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnlProductTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlTableSection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );

        tabbedPane.addTab("Products Management", pnlProductTab);

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
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                .addGap(16, 16, 16))
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

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        new Dashboard().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        String code = txtItemCode.getText().trim();
        String desc = txtDesc.getText().trim();
        String cat = txtCategory.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String reorderStr = txtReorder.getText().trim();
        String barcode = txtBarcode.getText().trim();

        if (code.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Item Code and Description are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price = 0.0;
        int reorder = 0;
        try {
            if (!priceStr.isEmpty()) price = Double.parseDouble(priceStr);
            if (!reorderStr.isEmpty()) reorder = Integer.parseInt(reorderStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price and Reorder Level must be valid numeric values!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (price < 0.0 || reorder < 0) {
            JOptionPane.showMessageDialog(this, "Price and Reorder Level cannot be negative!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String effectiveBarcode = barcode.isEmpty() ? code : barcode;

        try {
            Connection con = DBConnection.getConnection();
            if (con == null) {
                JOptionPane.showMessageDialog(this, "Cannot connect to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String checkSql = "SELECT item_code FROM product WHERE item_code = ?";
            try (PreparedStatement pstCheck = con.prepareStatement(checkSql)) {
                pstCheck.setString(1, code);
                try (ResultSet rs = pstCheck.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Product with Item Code '" + code + "' already exists! Use Update to modify existing products.", "Duplicate Item Code", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            String checkBarcodeSql = "SELECT item_code FROM product WHERE barcode = ?";
            try (PreparedStatement pstBc = con.prepareStatement(checkBarcodeSql)) {
                pstBc.setString(1, effectiveBarcode);
                try (ResultSet rsBc = pstBc.executeQuery()) {
                    if (rsBc.next()) {
                        JOptionPane.showMessageDialog(this, "A product with barcode '" + effectiveBarcode + "' already exists (Item Code: " + rsBc.getString("item_code") + ")!", "Duplicate Barcode", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            String insertSql = "INSERT INTO product (item_code, description, category, unit_price, reorder_level, current_stock, barcode) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pst = con.prepareStatement(insertSql)) {
                pst.setString(1, code);
                pst.setString(2, desc);
                pst.setString(3, cat);
                pst.setDouble(4, price);
                pst.setInt(5, reorder);
                pst.setInt(6, 0);
                pst.setString(7, effectiveBarcode);
                pst.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadProductsData();
            btnClearActionPerformed(evt);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(this, "Duplicate entry! Either Item Code or Barcode already exists in database.", "Duplicate Error", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        String code = txtItemCode.getText().trim();
        String desc = txtDesc.getText().trim();
        String cat = txtCategory.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String reorderStr = txtReorder.getText().trim();
        String barcode = txtBarcode.getText().trim();

        if (code.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select or specify a product to update.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price = 0.0;
        int reorder = 0;
        try {
            if (!priceStr.isEmpty()) price = Double.parseDouble(priceStr);
            if (!reorderStr.isEmpty()) reorder = Integer.parseInt(reorderStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price and Reorder Level must be valid numeric values!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (price < 0.0 || reorder < 0) {
            JOptionPane.showMessageDialog(this, "Price and Reorder Level cannot be negative!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String effectiveBarcode = barcode.isEmpty() ? code : barcode;

        try {
            Connection con = DBConnection.getConnection();
            if (con == null) {
                JOptionPane.showMessageDialog(this, "Cannot connect to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String checkBarcodeSql = "SELECT item_code FROM product WHERE barcode = ? AND item_code != ?";
            try (PreparedStatement pstBc = con.prepareStatement(checkBarcodeSql)) {
                pstBc.setString(1, effectiveBarcode);
                pstBc.setString(2, code);
                try (ResultSet rsBc = pstBc.executeQuery()) {
                    if (rsBc.next()) {
                        JOptionPane.showMessageDialog(this, "Another product with barcode '" + effectiveBarcode + "' already exists (Item Code: " + rsBc.getString("item_code") + ")!", "Duplicate Barcode", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            String updateSql = "UPDATE product SET description = ?, category = ?, unit_price = ?, reorder_level = ?, barcode = ? WHERE item_code = ?";
            try (PreparedStatement pst = con.prepareStatement(updateSql)) {
                pst.setString(1, desc);
                pst.setString(2, cat);
                pst.setDouble(3, price);
                pst.setInt(4, reorder);
                pst.setString(5, effectiveBarcode);
                pst.setString(6, code);
                int rows = pst.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProductsData();
                } else {
                    JOptionPane.showMessageDialog(this, "Product not found: " + code, "Not Found", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(this, "Duplicate entry! Either Item Code or Barcode already exists in database.", "Duplicate Error", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loadProductsData();
        JOptionPane.showMessageDialog(this, "Products refreshed from database.", "Refreshed", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        String code = txtItemCode.getText().trim();
        if (code.isEmpty()) {
            int viewRow = tblProducts.getSelectedRow();
            if (viewRow != -1) {
                int row = tblProducts.convertRowIndexToModel(viewRow);
                code = tblProducts.getModel().getValueAt(row, 0).toString();
            }
        }
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete product: " + code + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection con = DBConnection.getConnection();
                if (con == null) {
                    JOptionPane.showMessageDialog(this, "Cannot connect to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String deleteSql = "DELETE FROM product WHERE item_code = ?";
                try (PreparedStatement pst = con.prepareStatement(deleteSql)) {
                    pst.setString(1, code);
                    int rows = pst.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadProductsData();
                        btnClearActionPerformed(evt);
                    } else {
                        JOptionPane.showMessageDialog(this, "Product not found: " + code, "Not Found", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                if (e.getErrorCode() == 1451 || (e.getMessage() != null && e.getMessage().toLowerCase().contains("foreign key"))) {
                    JOptionPane.showMessageDialog(this,
                        "Cannot delete product '" + code + "' because it is linked to existing Sales Invoices or GRN records.\n" +
                        "To delete this product, remove or reverse the associated transactions first.",
                        "Referential Integrity Constraint", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        txtItemCode.setText("");
        txtDesc.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtReorder.setText("");
        txtBarcode.setText("");
        tblProducts.clearSelection();
    }//GEN-LAST:event_btnClearActionPerformed

    private void tblProductsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductsMouseClicked
        int viewRow = tblProducts.getSelectedRow();
        if (viewRow != -1) {
            int row = tblProducts.convertRowIndexToModel(viewRow);
            DefaultTableModel model = (DefaultTableModel) tblProducts.getModel();
            String code = model.getValueAt(row, 0).toString();
            try {
                Connection con = DBConnection.getConnection();
                String sql = "SELECT * FROM product WHERE item_code = ?";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, code);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            txtItemCode.setText(rs.getString("item_code"));
                            txtDesc.setText(rs.getString("description"));
                            txtCategory.setText(rs.getString("category"));
                            txtPrice.setText(String.format("%.2f", rs.getDouble("unit_price")));
                            txtReorder.setText(String.valueOf(rs.getInt("reorder_level")));
                            txtBarcode.setText(rs.getString("barcode") != null ? rs.getString("barcode") : "");
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error fetching product: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_tblProductsMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            FlatDarkLaf f = new FlatDarkLaf();
            UIManager.setLookAndFeel(f);
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println("UnsupportedLookAndFeelException: " + e.getMessage());
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProductManagement().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBarcode;
    private javax.swing.JLabel lblBrand;
    private javax.swing.JLabel lblCategory;
    private javax.swing.JLabel lblDesc;
    private javax.swing.JLabel lblItemCode;
    private javax.swing.JLabel lblPrice;
    private javax.swing.JLabel lblReorder;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlForm;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlProductTab;
    private javax.swing.JPanel pnlTableSection;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tblProducts;
    private javax.swing.JTextField txtBarcode;
    private javax.swing.JTextField txtCategory;
    private javax.swing.JTextField txtDesc;
    private javax.swing.JTextField txtItemCode;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtReorder;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
