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
import java.util.Date;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import model.UserSession;
import reports.ReportManager;

public class TransactionManager extends javax.swing.JFrame {

    private double netTotal = 0.0;

    // History Tab Components
    private JPanel pnlHistoryTab;
    private JTextField txtHistorySearch;
    private JButton btnHistorySearch;
    private JButton btnHistoryRefresh;
    private JTable tblHistory;
    private JScrollPane spHistory;
    private JLabel lblHistoryStatus;
    private JButton btnHistoryViewPrint;
    private JButton btnHistoryDelete;

    // Payment Mode & Receipt Printing Controls
    private JRadioButton rdoCash;
    private JRadioButton rdoCard;
    private ButtonGroup bgPaymentMode;
    private JCheckBox chkPrintReceipt;
    private JPanel pnlPayControls;

    public TransactionManager() {
        this(0);
    }

    public TransactionManager(int tabIndex) {
        initComponents();
        initHistoryTab();
        configureTabs(tabIndex);
        styleComponents();
        loadSampleData();
        setLocationRelativeTo(null);
    }

    private void configureTabs(int tabIndex) {
        tabbedPane.removeAll();
        tabbedPane.addTab("Invoice (Billing)", pnlInvoiceTab);
        tabbedPane.addTab("Transaction Records", pnlHistoryTab);

        tabbedPane.addChangeListener(e -> {
            int selected = tabbedPane.getSelectedIndex();
            if (selected == 0) {
                setTitle("BrightMart - Invoices");
                if (txtBarcodeSearch != null) {
                    txtBarcodeSearch.requestFocusInWindow();
                }
            } else if (selected == 1) {
                setTitle("BrightMart - Transaction Records");
                loadHistoryData();
            }
        });

        if (tabIndex == 1 || tabIndex == 2) {
            tabbedPane.setSelectedIndex(1);
            setTitle("BrightMart - Transaction Records");
        } else {
            tabbedPane.setSelectedIndex(0);
            setTitle("BrightMart - Invoices");
        }
    }

    public void setSelectedTab(int index) {
        if (index >= 0 && index < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(index);
        }
    }

    private String generateNextInvoiceNo() {
        String sql = "SELECT invoice_no FROM invoice";
        int maxNum = 0;
        try {
            Connection con = DBConnection.getConnection();
            if (con != null) {
                try (PreparedStatement pst = con.prepareStatement(sql);
                     ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        String inv = rs.getString("invoice_no");
                        if (inv != null && inv.toUpperCase().startsWith("INV-")) {
                            String numPart = inv.substring(4).replaceAll("[^0-9]", "");
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
            System.err.println("Error generating invoice no: " + e.getMessage());
        }
        return String.format("INV-%05d", maxNum + 1);
    }

    private void styleComponents() {
        btnBack.putClientProperty("JButton.buttonType", "roundRect");
        btnSearch.putClientProperty("JButton.buttonType", "roundRect");
        btnAddInvoice.putClientProperty("JButton.buttonType", "roundRect");
        btnPrint.putClientProperty("JButton.buttonType", "roundRect");

        txtBarcodeSearch.putClientProperty("JComponent.roundRect", true);
        txtItemName.putClientProperty("JComponent.roundRect", true);
        txtPrice.putClientProperty("JComponent.roundRect", true);
        txtQty.putClientProperty("JComponent.roundRect", true);
        txtCash.putClientProperty("JComponent.roundRect", true);

        txtBarcodeSearch.putClientProperty("JTextField.placeholderText", "Scan barcode or enter item code...");
        txtItemName.putClientProperty("JTextField.placeholderText", "Item Description");
        txtPrice.putClientProperty("JTextField.placeholderText", "0.00");
        txtQty.putClientProperty("JTextField.placeholderText", "1");
        txtCash.putClientProperty("JTextField.placeholderText", "Amount paid...");

        jScrollPane1.getViewport().setBackground(new Color(21, 33, 49));
        JTableHeader headerInv = tblInvoice.getTableHeader();
        headerInv.setBackground(new Color(27, 42, 62));
        headerInv.setForeground(new Color(220, 230, 242));
        headerInv.setFont(new Font("Segoe UI", Font.BOLD, 13));
        DefaultTableCellRenderer headerRendererInv = (DefaultTableCellRenderer) headerInv.getDefaultRenderer();
        headerRendererInv.setHorizontalAlignment(JLabel.LEFT);

        tblInvoice.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblInvoice.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblInvoice.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblInvoice.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblInvoice.getColumnModel().getColumn(4).setPreferredWidth(90);

        txtCash.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateBalance(); }
            public void removeUpdate(DocumentEvent e) { updateBalance(); }
            public void changedUpdate(DocumentEvent e) { updateBalance(); }
        });

        txtBarcodeSearch.addActionListener(e -> btnSearchActionPerformed(e));
        txtQty.addActionListener(e -> btnAddInvoiceActionPerformed(e));

        java.awt.event.FocusAdapter selectOnFocus = new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (e.getSource() instanceof JTextField) {
                    ((JTextField) e.getSource()).selectAll();
                }
            }
        };
        txtBarcodeSearch.addFocusListener(selectOnFocus);
        txtItemName.addFocusListener(selectOnFocus);
        txtPrice.addFocusListener(selectOnFocus);
        txtQty.addFocusListener(selectOnFocus);
        txtCash.addFocusListener(selectOnFocus);

        JPopupMenu invoicePopup = new JPopupMenu();
        JMenuItem removeItem = new JMenuItem("Remove Selected Item");
        removeItem.addActionListener(e -> {
            int row = tblInvoice.getSelectedRow();
            if (row != -1) {
                ((DefaultTableModel) tblInvoice.getModel()).removeRow(row);
                calculateNetTotal();
            }
        });
        JMenuItem clearAll = new JMenuItem("Clear All Items");
        clearAll.addActionListener(e -> {
            ((DefaultTableModel) tblInvoice.getModel()).setRowCount(0);
            calculateNetTotal();
        });
        invoicePopup.add(removeItem);
        invoicePopup.add(clearAll);
        tblInvoice.setComponentPopupMenu(invoicePopup);

        tblInvoice.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblInvoice.getSelectedRow();
                    if (row != -1) {
                        int opt = JOptionPane.showConfirmDialog(TransactionManager.this,
                            "Remove item '" + tblInvoice.getValueAt(row, 1) + "' from invoice?",
                            "Remove Item", JOptionPane.YES_NO_OPTION);
                        if (opt == JOptionPane.YES_OPTION) {
                            ((DefaultTableModel) tblInvoice.getModel()).removeRow(row);
                            calculateNetTotal();
                        }
                    }
                }
            }
        });

        tblInvoice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {
                    int row = tblInvoice.getSelectedRow();
                    if (row != -1) {
                        ((DefaultTableModel) tblInvoice.getModel()).removeRow(row);
                        calculateNetTotal();
                    }
                }
            }
        });
    }

    private void updateBalance() {
        if (rdoCard != null && rdoCard.isSelected()) {
            lblBalance.setText("LKR 0.00");
            lblBalance.setForeground(new Color(76, 175, 80));
            return;
        }

        String cashText = txtCash.getText().trim();
        if (cashText.isEmpty() || cashText.equalsIgnoreCase("Amount paid...")) {
            lblBalance.setText("LKR 0.00");
            lblBalance.setForeground(new Color(41, 182, 246));
            return;
        }
        try {
            double cash = Double.parseDouble(cashText);
            double balance = cash - netTotal;
            if (balance >= 0) {
                lblBalance.setText("LKR " + String.format("%.2f", balance));
                lblBalance.setForeground(new Color(76, 175, 80));
            } else {
                lblBalance.setText("Short " + String.format("%.2f", Math.abs(balance)));
                lblBalance.setForeground(new Color(235, 87, 87));
            }
        } catch (NumberFormatException ex) {
            lblBalance.setText("Invalid");
            lblBalance.setForeground(new Color(235, 87, 87));
        }
    }

    private void loadSampleData() {
        calculateNetTotal();
    }

    private void calculateNetTotal() {
        DefaultTableModel model = (DefaultTableModel) tblInvoice.getModel();
        netTotal = 0.0;
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                Object val = model.getValueAt(i, 4);
                if (val != null) {
                    netTotal += Double.parseDouble(val.toString());
                }
            } catch (Exception ignored) {}
        }
        lblTotal.setText("Net Total: LKR " + String.format("%.2f", netTotal));
        if (rdoCard != null && rdoCard.isSelected()) {
            txtCash.setText(String.format("%.2f", netTotal));
        }
        updateBalance();
    }

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
        pnlInvoiceTab = new javax.swing.JPanel();
        lblBarcodeSearch = new javax.swing.JLabel();
        txtBarcodeSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        lblItemName = new javax.swing.JLabel();
        txtItemName = new javax.swing.JTextField();
        lblPrice = new javax.swing.JLabel();
        txtPrice = new javax.swing.JTextField();
        lblQty = new javax.swing.JLabel();
        txtQty = new javax.swing.JTextField();
        btnAddInvoice = new javax.swing.JButton();
        lblInvoiceTable = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblInvoice = new javax.swing.JTable();
        lblTotal = new javax.swing.JLabel();
        lblCashReceived = new javax.swing.JLabel();
        txtCash = new javax.swing.JTextField();
        lblBalanceTitle = new javax.swing.JLabel();
        lblBalance = new javax.swing.JLabel();
        btnPrint = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BrightMart - Invoices");
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
        lblSubtitle.setText("Sales Billing & Transaction Records");

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

        pnlInvoiceTab.setBackground(new java.awt.Color(18, 28, 42));

        lblBarcodeSearch.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblBarcodeSearch.setForeground(new java.awt.Color(203, 213, 225));
        lblBarcodeSearch.setText("Barcode / Item Code:");

        txtBarcodeSearch.setBackground(new java.awt.Color(31, 45, 62));
        txtBarcodeSearch.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtBarcodeSearch.setForeground(new java.awt.Color(241, 245, 249));
        txtBarcodeSearch.setCaretColor(new java.awt.Color(41, 182, 246));
        txtBarcodeSearch.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        btnSearch.setBackground(new java.awt.Color(35, 56, 82));
        btnSearch.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setText("Search");
        btnSearch.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        lblItemName.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblItemName.setForeground(new java.awt.Color(203, 213, 225));
        lblItemName.setText("Item Name");

        txtItemName.setBackground(new java.awt.Color(31, 45, 62));
        txtItemName.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtItemName.setForeground(new java.awt.Color(241, 245, 249));
        txtItemName.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblPrice.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblPrice.setForeground(new java.awt.Color(203, 213, 225));
        lblPrice.setText("Price");

        txtPrice.setBackground(new java.awt.Color(31, 45, 62));
        txtPrice.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtPrice.setForeground(new java.awt.Color(241, 245, 249));
        txtPrice.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblQty.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblQty.setForeground(new java.awt.Color(203, 213, 225));
        lblQty.setText("Quantity");

        txtQty.setBackground(new java.awt.Color(31, 45, 62));
        txtQty.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtQty.setForeground(new java.awt.Color(241, 245, 249));
        txtQty.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        btnAddInvoice.setBackground(new java.awt.Color(35, 56, 82));
        btnAddInvoice.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnAddInvoice.setForeground(new java.awt.Color(255, 255, 255));
        btnAddInvoice.setText("Add to Cart");
        btnAddInvoice.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        btnAddInvoice.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddInvoice.setFocusPainted(false);
        btnAddInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddInvoiceActionPerformed(evt);
            }
        });

        lblInvoiceTable.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblInvoiceTable.setForeground(new java.awt.Color(203, 213, 225));
        lblInvoiceTable.setText("Current Invoice Cart:");

        tblInvoice.setBackground(new java.awt.Color(21, 33, 49));
        tblInvoice.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblInvoice.setForeground(new java.awt.Color(226, 232, 240));
        tblInvoice.setModel(new javax.swing.table.DefaultTableModel(
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
        tblInvoice.setGridColor(new java.awt.Color(30, 47, 66));
        tblInvoice.setRowHeight(28);
        tblInvoice.setShowGrid(true);
        jScrollPane1.setViewportView(tblInvoice);

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblTotal.setText("Net Total: LKR 0.00");

        lblCashReceived.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblCashReceived.setForeground(new java.awt.Color(203, 213, 225));
        lblCashReceived.setText("Cash Received:");

        txtCash.setBackground(new java.awt.Color(31, 45, 62));
        txtCash.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtCash.setForeground(new java.awt.Color(241, 245, 249));
        txtCash.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtCash.setCaretColor(new java.awt.Color(41, 182, 246));

        lblBalanceTitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblBalanceTitle.setForeground(new java.awt.Color(203, 213, 225));
        lblBalanceTitle.setText("Balance:");

        lblBalance.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblBalance.setForeground(new java.awt.Color(41, 182, 246));
        lblBalance.setText("LKR 0.00");

        btnPrint.setBackground(new java.awt.Color(0, 168, 132));
        btnPrint.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnPrint.setForeground(new java.awt.Color(255, 255, 255));
        btnPrint.setText("SAVE & COMPLETE SALE");
        btnPrint.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        btnPrint.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPrint.setFocusPainted(false);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        // Payment Mode Radio Buttons & Jasper Receipt Checkbox
        pnlPayControls = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        pnlPayControls.setOpaque(false);

        javax.swing.JLabel lblPayMode = new javax.swing.JLabel("Payment:");
        lblPayMode.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        lblPayMode.setForeground(new java.awt.Color(203, 213, 225));

        rdoCash = new javax.swing.JRadioButton("Cash", true);
        rdoCash.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        rdoCash.setForeground(new java.awt.Color(241, 245, 249));
        rdoCash.setOpaque(false);
        rdoCash.setFocusPainted(false);

        rdoCard = new javax.swing.JRadioButton("Card");
        rdoCard.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        rdoCard.setForeground(new java.awt.Color(241, 245, 249));
        rdoCard.setOpaque(false);
        rdoCard.setFocusPainted(false);

        bgPaymentMode = new javax.swing.ButtonGroup();
        bgPaymentMode.add(rdoCash);
        bgPaymentMode.add(rdoCard);

        rdoCash.addActionListener(e -> {
            if (txtCash != null) {
                txtCash.setEnabled(true);
                txtCash.setText("");
                txtCash.requestFocus();
                updateBalance();
            }
        });

        rdoCard.addActionListener(e -> {
            if (txtCash != null) {
                txtCash.setEnabled(false);
                txtCash.setText(String.format("%.2f", netTotal));
                updateBalance();
            }
        });

        chkPrintReceipt = new javax.swing.JCheckBox("Print Receipt (Jasper)", true);
        chkPrintReceipt.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        chkPrintReceipt.setForeground(new java.awt.Color(203, 213, 225));
        chkPrintReceipt.setOpaque(false);
        chkPrintReceipt.setFocusPainted(false);
        chkPrintReceipt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        pnlPayControls.add(lblPayMode);
        pnlPayControls.add(rdoCash);
        pnlPayControls.add(rdoCard);
        pnlPayControls.add(new javax.swing.JLabel("  "));
        pnlPayControls.add(chkPrintReceipt);

        javax.swing.GroupLayout pnlInvoiceTabLayout = new javax.swing.GroupLayout(pnlInvoiceTab);
        pnlInvoiceTab.setLayout(pnlInvoiceTabLayout);
        pnlInvoiceTabLayout.setHorizontalGroup(
            pnlInvoiceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInvoiceTabLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlInvoiceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblBarcodeSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlInvoiceTabLayout.createSequentialGroup()
                        .addComponent(txtBarcodeSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlInvoiceTabLayout.createSequentialGroup()
                        .addGroup(pnlInvoiceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblItemName, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblQty, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlInvoiceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtQty)
                            .addComponent(txtPrice)
                            .addComponent(txtItemName)))
                    .addComponent(btnAddInvoice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(35, 35, 35)
                .addGroup(pnlInvoiceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblInvoiceTable)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                    .addGroup(pnlInvoiceTabLayout.createSequentialGroup()
                        .addComponent(lblCashReceived)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCash, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(lblBalanceTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                        .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlInvoiceTabLayout.createSequentialGroup()
                        .addComponent(lblTotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlPayControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20))
        );
        pnlInvoiceTabLayout.setVerticalGroup(
            pnlInvoiceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInvoiceTabLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlInvoiceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBarcodeSearch)
                    .addComponent(lblInvoiceTable))
                .addGap(8, 8, 8)
                .addGroup(pnlInvoiceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlInvoiceTabLayout.createSequentialGroup()
                        .addGroup(pnlInvoiceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBarcodeSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(pnlInvoiceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblItemName)
                            .addComponent(txtItemName, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(pnlInvoiceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPrice)
                            .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(pnlInvoiceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblQty)
                            .addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addComponent(btnAddInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
                .addGap(14, 14, 14)
                .addGroup(pnlInvoiceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblTotal)
                    .addComponent(pnlPayControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(pnlInvoiceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCashReceived)
                    .addComponent(txtCash, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBalanceTitle)
                    .addComponent(lblBalance)
                    .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );

        tabbedPane.addTab("Invoice (Billing)", pnlInvoiceTab);

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

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        String query = txtBarcodeSearch.getText().trim();
        if (query.isEmpty() || query.equalsIgnoreCase("Scan barcode or enter item code...")) {
            JOptionPane.showMessageDialog(this, "Please enter a Barcode or Item Code to search.", "Search Item", JOptionPane.WARNING_MESSAGE);
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
                        txtBarcodeSearch.setText(rs.getString("item_code"));
                        txtItemName.setText(rs.getString("description"));
                        txtPrice.setText(String.format("%.2f", rs.getDouble("unit_price")));
                        txtQty.setText("1");
                        txtQty.requestFocus();
                    } else {
                        JOptionPane.showMessageDialog(this, "Product not found for: " + query, "Not Found", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnAddInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddInvoiceActionPerformed
        String itemCode = txtBarcodeSearch.getText().trim();
        if (itemCode.isEmpty() || itemCode.equalsIgnoreCase("Scan barcode or enter item code...")) {
            JOptionPane.showMessageDialog(this, "Please search or enter an Item Code.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = txtItemName.getText().trim();
        if (name.isEmpty() || name.equalsIgnoreCase("Item Description")) {
            JOptionPane.showMessageDialog(this, "Please specify an Item Name.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double price = 0.0;
        int qty = 1;
        try {
            price = Double.parseDouble(txtPrice.getText().trim());
            if (price <= 0.0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Price (> 0).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            qty = Integer.parseInt(txtQty.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Quantity (> 0).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblInvoice.getModel();
        int existingQty = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().equalsIgnoreCase(itemCode)) {
                try {
                    existingQty = Integer.parseInt(model.getValueAt(i, 2).toString());
                } catch (Exception ignored) {}
                break;
            }
        }

        int currentStock = -1;
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT current_stock FROM product WHERE item_code = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, itemCode);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        currentStock = rs.getInt("current_stock");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking stock: " + e.getMessage());
        }

        int totalCartQty = existingQty + qty;
        if (currentStock >= 0 && currentStock < totalCartQty) {
            int resp = JOptionPane.showConfirmDialog(this,
                "Warning: Current in-stock is " + currentStock +
                ", but total cart quantity will be " + totalCartQty + ". Continue adding?",
                "Low Stock Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (resp != JOptionPane.YES_OPTION) {
                return;
            }
        }

        double total = price * qty;
        boolean found = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().equalsIgnoreCase(itemCode)) {
                try {
                    int currentQty = Integer.parseInt(model.getValueAt(i, 2).toString());
                    int newQty = currentQty + qty;
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

        calculateNetTotal();

        txtBarcodeSearch.setText("");
        txtItemName.setText("");
        txtPrice.setText("");
        txtQty.setText("");
        txtBarcodeSearch.requestFocus();
    }//GEN-LAST:event_btnAddInvoiceActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        DefaultTableModel model = (DefaultTableModel) tblInvoice.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cannot complete empty invoice. Add items first.", "Empty Invoice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (netTotal <= 0.0) {
            JOptionPane.showMessageDialog(this, "Cannot complete invoice with zero or negative total amount.", "Invalid Total", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean isCard = (rdoCard != null && rdoCard.isSelected());
        String cashText = txtCash.getText().trim();
        double cash = 0.0;
        if (isCard) {
            cash = netTotal;
        } else {
            if (cashText.isEmpty() || cashText.equalsIgnoreCase("Amount paid...")) {
                cash = netTotal;
            } else {
                try {
                    cash = Double.parseDouble(cashText);
                    if (cash < netTotal) {
                        JOptionPane.showMessageDialog(this, "Cash received is less than Net Total!", "Payment Insufficient", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid numeric cash amount.", "Invalid Cash Amount", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }

        String invNo = generateNextInvoiceNo();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        boolean saved = false;
        Connection con = DBConnection.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(this, "Cannot connect to the database. Transaction aborted.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            con.setAutoCommit(false);

            String sqlInv = "INSERT INTO invoice (invoice_no, invoice_date_time, total_amount) VALUES (?, ?, ?)";
            try (PreparedStatement pstInv = con.prepareStatement(sqlInv)) {
                pstInv.setString(1, invNo);
                pstInv.setTimestamp(2, now);
                pstInv.setDouble(3, netTotal);
                pstInv.executeUpdate();
            }

            String sqlItem = "INSERT INTO invoice_items (invoice_no, item_code, sold_qty, selling_price) VALUES (?, ?, ?, ?)";
            String sqlStock = "UPDATE product SET current_stock = current_stock - ? WHERE item_code = ?";
            try (PreparedStatement pstItem = con.prepareStatement(sqlItem);
                 PreparedStatement pstStock = con.prepareStatement(sqlStock)) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    String itemCode = model.getValueAt(i, 0).toString();
                    int qty = Integer.parseInt(model.getValueAt(i, 2).toString());
                    double price = Double.parseDouble(model.getValueAt(i, 3).toString());

                    pstItem.setString(1, invNo);
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
            JOptionPane.showMessageDialog(this, "Database Transaction Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException ex) {}
            }
        }

        if (saved) {
            double balance = cash - netTotal;
            String payMode = isCard ? "Card" : "Cash";
            String message = String.format("Invoice [%s] Completed Successfully!\n\nNet Total: LKR %.2f\nCash Received: LKR %.2f\nChange/Balance: LKR %.2f\nPayment Method: %s\n\nDatabase updated & stock deducted.",
                    invNo, netTotal, cash, balance, payMode);

            JOptionPane.showMessageDialog(this, message, "Sale Completed", JOptionPane.INFORMATION_MESSAGE);

            if (chkPrintReceipt != null && chkPrintReceipt.isSelected()) {
                String cashier = UserSession.isLoggedIn() ? UserSession.getUsername() : "Cashier";
                ReportManager.viewInvoiceReport(invNo, cashier, payMode, cash, balance);
            }

            model.setRowCount(0);
            calculateNetTotal();
            txtCash.setText("");
            lblBalance.setText("LKR 0.00");
            if (rdoCash != null) {
                rdoCash.setSelected(true);
            }
            if (pnlHistoryTab != null) {
                loadHistoryData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to complete transaction in database!", "Transaction Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnPrintActionPerformed

    public void initHistoryTab() {
        pnlHistoryTab = new JPanel(new java.awt.BorderLayout(12, 12));
        pnlHistoryTab.setBackground(new Color(18, 28, 42));
        pnlHistoryTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 16, 14, 16));

        JPanel pnlTop = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 4));
        pnlTop.setOpaque(false);

        JLabel lblSearch = new JLabel("Search Invoice No:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSearch.setForeground(new Color(203, 213, 225));

        txtHistorySearch = new JTextField(14);
        txtHistorySearch.setBackground(new Color(31, 45, 62));
        txtHistorySearch.setForeground(new Color(241, 245, 249));
        txtHistorySearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtHistorySearch.setBorder(new javax.swing.border.LineBorder(new Color(45, 65, 88), 1, true));
        txtHistorySearch.putClientProperty("JComponent.roundRect", true);
        txtHistorySearch.putClientProperty("JTextField.placeholderText", "Search Invoice No...");

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

        btnHistoryViewPrint = new JButton("View & Print Invoice");
        btnHistoryViewPrint.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnHistoryViewPrint.setBackground(new Color(0, 168, 132));
        btnHistoryViewPrint.setForeground(Color.WHITE);
        btnHistoryViewPrint.setFocusPainted(false);
        btnHistoryViewPrint.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHistoryViewPrint.setPreferredSize(new Dimension(190, 36));
        btnHistoryViewPrint.putClientProperty("JButton.buttonType", "roundRect");

        btnHistoryDelete = new JButton("Delete Record");
        btnHistoryDelete.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnHistoryDelete.setBackground(new Color(185, 28, 28));
        btnHistoryDelete.setForeground(Color.WHITE);
        btnHistoryDelete.setFocusPainted(false);
        btnHistoryDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHistoryDelete.setPreferredSize(new Dimension(150, 36));
        btnHistoryDelete.putClientProperty("JButton.buttonType", "roundRect");

        pnlBottom.add(lblHistoryStatus);
        pnlBottom.add(btnHistoryViewPrint);
        pnlBottom.add(btnHistoryDelete);

        pnlHistoryTab.add(pnlTop, java.awt.BorderLayout.NORTH);
        pnlHistoryTab.add(spHistory, java.awt.BorderLayout.CENTER);
        pnlHistoryTab.add(pnlBottom, java.awt.BorderLayout.SOUTH);

        btnHistorySearch.addActionListener(e -> loadHistoryData());
        txtHistorySearch.addActionListener(e -> loadHistoryData());
        btnHistoryRefresh.addActionListener(e -> {
            txtHistorySearch.setText("");
            loadHistoryData();
        });

        btnHistoryViewPrint.addActionListener(e -> viewPrintHistoryRecord());
        btnHistoryDelete.addActionListener(e -> deleteHistoryRecord());

        tblHistory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewPrintHistoryRecord();
                }
            }
        });

        loadHistoryData();
    }

    public void loadHistoryData() {
        if (tblHistory == null) return;
        String query = (txtHistorySearch != null) ? txtHistorySearch.getText().trim() : "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String[] cols = {"Invoice No", "Date & Time", "Total Amount (LKR)", "Total Items"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        int count = 0;
        try {
            Connection con = DBConnection.getConnection();
            if (con != null) {
                String sql;
                if (query.isEmpty()) {
                    sql = "SELECT i.invoice_no, i.invoice_date_time, i.total_amount, COUNT(ii.item_code) AS item_count " +
                          "FROM invoice i LEFT JOIN invoice_items ii ON i.invoice_no = ii.invoice_no " +
                          "GROUP BY i.invoice_no, i.invoice_date_time, i.total_amount " +
                          "ORDER BY i.invoice_date_time DESC";
                } else {
                    sql = "SELECT i.invoice_no, i.invoice_date_time, i.total_amount, COUNT(ii.item_code) AS item_count " +
                          "FROM invoice i LEFT JOIN invoice_items ii ON i.invoice_no = ii.invoice_no " +
                          "WHERE i.invoice_no LIKE ? " +
                          "GROUP BY i.invoice_no, i.invoice_date_time, i.total_amount " +
                          "ORDER BY i.invoice_date_time DESC";
                }

                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    if (!query.isEmpty()) {
                        pst.setString(1, "%" + query + "%");
                    }
                    try (ResultSet rs = pst.executeQuery()) {
                        while (rs.next()) {
                            String invNo = rs.getString("invoice_no");
                            Timestamp dt = rs.getTimestamp("invoice_date_time");
                            double amt = rs.getDouble("total_amount");
                            int items = rs.getInt("item_count");
                            String dateStr = dt != null ? sdf.format(new Date(dt.getTime())) : "-";

                            model.addRow(new Object[]{
                                invNo,
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
            System.err.println("Error loading invoice history: " + e.getMessage());
        }

        tblHistory.setModel(model);
        if (lblHistoryStatus != null) {
            lblHistoryStatus.setText("Showing " + count + " sales invoice(s)");
        }
    }

    private void viewPrintHistoryRecord() {
        int row = tblHistory.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an invoice from the table to view.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String recordNo = tblHistory.getValueAt(row, 0).toString();
        try {
            Connection con = DBConnection.getConnection();
            if (con != null) {
                String sqlInv = "SELECT * FROM invoice WHERE invoice_no = ?";
                String dtStr = "-";
                double totalAmt = 0.0;
                boolean found = false;

                try (PreparedStatement pstInv = con.prepareStatement(sqlInv)) {
                    pstInv.setString(1, recordNo);
                    try (ResultSet rsInv = pstInv.executeQuery()) {
                        if (rsInv.next()) {
                            found = true;
                            Timestamp dt = rsInv.getTimestamp("invoice_date_time");
                            if (dt != null) {
                                dtStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
                            }
                            totalAmt = rsInv.getDouble("total_amount");
                        }
                    }
                }

                if (found) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("BrightMart Retail Store - Sales Invoice\n");
                    sb.append("========================================\n");
                    sb.append("Invoice No: ").append(recordNo).append("\n");
                    sb.append("Date & Time: ").append(dtStr).append("\n");
                    sb.append("Total Amount: LKR ").append(String.format("%.2f", totalAmt)).append("\n\n");
                    sb.append("Items Sold:\n");
                    sb.append("----------------------------------------\n");

                    String sqlItems = "SELECT ii.*, p.description FROM invoice_items ii LEFT JOIN product p ON ii.item_code = p.item_code WHERE ii.invoice_no = ?";
                    try (PreparedStatement pstItems = con.prepareStatement(sqlItems)) {
                        pstItems.setString(1, recordNo);
                        try (ResultSet rsItems = pstItems.executeQuery()) {
                            while (rsItems.next()) {
                                String itemCode = rsItems.getString("item_code");
                                String desc = rsItems.getString("description");
                                if (desc == null || desc.isEmpty()) desc = itemCode;
                                int qty = rsItems.getInt("sold_qty");
                                double price = rsItems.getDouble("selling_price");
                                double subTotal = qty * price;
                                sb.append(String.format("• %s (%s) - Qty: %d @ LKR %.2f = LKR %.2f\n",
                                        desc, itemCode, qty, price, subTotal));
                            }
                        }
                    }
                    int choice = JOptionPane.showOptionDialog(this,
                            "Invoice [" + recordNo + "] details retrieved.\nWould you like to open the JasperReports Print Preview?",
                            "Sales Invoice: " + recordNo,
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new String[]{"Open Jasper Invoice", "View Text Summary"},
                            "Open Jasper Invoice");

                    if (choice == 0) {
                        String cashier = UserSession.isLoggedIn() ? UserSession.getUsername() : "Cashier";
                        ReportManager.viewInvoiceReport(recordNo, cashier, "Cash", totalAmt, 0.0);
                    } else if (choice == 1) {
                        JOptionPane.showMessageDialog(this, sb.toString(), "Sales Invoice: " + recordNo, JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Could not load details for invoice: " + recordNo, "Not Found", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading invoice record: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteHistoryRecord() {
        int row = tblHistory.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an invoice to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String recordNo = tblHistory.getValueAt(row, 0).toString();
        int opt = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to permanently delete Sales Invoice [" + recordNo + "]?\n\n" +
            "CRITICAL STOCK REVERSAL:\nAll items sold in this invoice will be RESTOCKED back into inventory.\n\nContinue deletion?",
            "Confirm Delete Invoice",
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

                String sqlRestock = "UPDATE product p INNER JOIN invoice_items ii ON p.item_code = ii.item_code " +
                                   "SET p.current_stock = p.current_stock + ii.sold_qty " +
                                   "WHERE ii.invoice_no = ?";
                try (PreparedStatement pstRestock = con.prepareStatement(sqlRestock)) {
                    pstRestock.setString(1, recordNo);
                    pstRestock.executeUpdate();
                }

                try (PreparedStatement pstDelItems = con.prepareStatement("DELETE FROM invoice_items WHERE invoice_no = ?")) {
                    pstDelItems.setString(1, recordNo);
                    pstDelItems.executeUpdate();
                }

                try (PreparedStatement pstDelInv = con.prepareStatement("DELETE FROM invoice WHERE invoice_no = ?")) {
                    pstDelInv.setString(1, recordNo);
                    pstDelInv.executeUpdate();
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
                    "Sales Invoice [" + recordNo + "] was successfully deleted and stock levels were restored!",
                    "Deletion Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                loadHistoryData();
            }
        }
    }

    public static void main(String args[]) {
        try {
            FlatDarkLaf f = new FlatDarkLaf();
            UIManager.setLookAndFeel(f);
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println("UnsupportedLookAndFeelException: " + e.getMessage());
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TransactionManager().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddInvoice;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSearch;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBalance;
    private javax.swing.JLabel lblBalanceTitle;
    private javax.swing.JLabel lblBarcodeSearch;
    private javax.swing.JLabel lblBrand;
    private javax.swing.JLabel lblCashReceived;
    private javax.swing.JLabel lblInvoiceTable;
    private javax.swing.JLabel lblItemName;
    private javax.swing.JLabel lblPrice;
    private javax.swing.JLabel lblQty;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlInvoiceTab;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tblInvoice;
    private javax.swing.JTextField txtBarcodeSearch;
    private javax.swing.JTextField txtCash;
    private javax.swing.JTextField txtItemName;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtQty;
    // End of variables declaration//GEN-END:variables
}
