package reports;

import db.DBConnection;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 * ReportManager for BrightMart Retail Store
 * Handles JasperReports compilation, data filling, preview viewer, and PDF export.
 */
public class ReportManager {

    private static final String JRXML_PATH = "src/reports/invoice_report.jrxml";
    private static final String JASPER_PATH = "src/reports/invoice_report.jasper";
    private static final String JASPERSOFT_WORKSPACE_DIR = "C:/Users/laksh/JaspersoftWorkspace/MyReports";

    /**
     * Compiles JRXML template to .jasper file.
     * Can be invoked programmatically or during initialization.
     *
     * @return JasperReport object
     * @throws JRException on compilation error
     */
    public static synchronized JasperReport compileInvoiceReport() throws JRException {
        File jrxmlFile = new File(JRXML_PATH);
        File jasperFile = new File(JASPER_PATH);

        if (!jrxmlFile.exists()) {
            // Fallback: look in classpath
            InputStream is = ReportManager.class.getResourceAsStream("/reports/invoice_report.jrxml");
            if (is != null) {
                return JasperCompileManager.compileReport(is);
            }
            throw new JRException("Invoice template JRXML not found at: " + jrxmlFile.getAbsolutePath());
        }

        // Compile to project destination
        JasperCompileManager.compileReportToFile(jrxmlFile.getAbsolutePath(), jasperFile.getAbsolutePath());
        System.out.println("[ReportManager] Compiled: " + jasperFile.getAbsolutePath());

        // Also compile/copy to Jaspersoft Studio workspace if directory exists
        try {
            File jsWorkspace = new File(JASPERSOFT_WORKSPACE_DIR);
            if (jsWorkspace.exists() && jsWorkspace.isDirectory()) {
                File jsJasper = new File(jsWorkspace, "invoice_report.jasper");
                JasperCompileManager.compileReportToFile(jrxmlFile.getAbsolutePath(), jsJasper.getAbsolutePath());
                System.out.println("[ReportManager] Synced to Jaspersoft Studio: " + jsJasper.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("[ReportManager] Notice: Could not sync to Jaspersoft Studio workspace: " + e.getMessage());
        }

        return (JasperReport) JRLoader.loadObject(jasperFile);
    }

    /**
     * Loads the compiled JasperReport. If .jasper does not exist or JRXML is newer,
     * re-compiles automatically.
     *
     * @return JasperReport instance
     * @throws JRException if compilation or loading fails
     */
    public static synchronized JasperReport getInvoiceReport() throws JRException {
        File jrxmlFile = new File(JRXML_PATH);
        File jasperFile = new File(JASPER_PATH);

        if (jasperFile.exists() && (!jrxmlFile.exists() || jasperFile.lastModified() >= jrxmlFile.lastModified())) {
            try {
                return (JasperReport) JRLoader.loadObject(jasperFile);
            } catch (Exception e) {
                System.err.println("[ReportManager] Failed loading existing .jasper, recompiling: " + e.getMessage());
            }
        }

        // Fallback: load precompiled .jasper directly from classpath
        try (InputStream is = ReportManager.class.getResourceAsStream("/reports/invoice_report.jasper")) {
            if (is != null) {
                return (JasperReport) JRLoader.loadObject(is);
            }
        } catch (Exception e) {
            System.err.println("[ReportManager] Could not load .jasper from classpath: " + e.getMessage());
        }

        // Needs compilation
        return compileInvoiceReport();
    }

    /**
     * Fills the invoice report with data from MySQL database.
     *
     * @param invoiceNo    Invoice Number (e.g. "INV-00001")
     * @param cashier      Cashier username or display name
     * @param paymentMode  Payment mode ("Cash" or "Card")
     * @param cashPaid     Amount received from customer
     * @param balance      Change returned to customer
     * @return JasperPrint object ready for viewing/printing
     * @throws Exception on database or report filling error
     */
    public static JasperPrint generateInvoicePrint(String invoiceNo, String cashier, String paymentMode,
                                                   double cashPaid, double balance) throws Exception {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            throw new IllegalStateException("Database connection is not available.");
        }

        JasperReport jasperReport = getInvoiceReport();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("P_INVOICE_NO", invoiceNo);
        parameters.put("P_CASHIER", (cashier != null && !cashier.trim().isEmpty()) ? cashier : "Cashier");
        parameters.put("P_PAYMENT_MODE", (paymentMode != null && !paymentMode.trim().isEmpty()) ? paymentMode : "Cash");
        parameters.put("P_CASH_PAID", cashPaid);
        parameters.put("P_BALANCE", balance);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
        return jasperPrint;
    }

    /**
     * Displays the interactive JasperReports Viewer for an invoice.
     *
     * @param invoiceNo    Invoice Number
     * @param cashier      Cashier Name
     * @param paymentMode  Payment Mode
     * @param cashPaid     Cash paid
     * @param balance      Change/balance
     */
    public static void viewInvoiceReport(String invoiceNo, String cashier, String paymentMode,
                                         double cashPaid, double balance) {
        try {
            JasperPrint jp = generateInvoicePrint(invoiceNo, cashier, paymentMode, cashPaid, balance);
            if (jp.getPages().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "No data found for Invoice: " + invoiceNo + ".\nPlease ensure the invoice items exist in the database.",
                        "Empty Invoice", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setTitle("JasperViewer - Invoice Receipt");
            viewer.setLocationRelativeTo(null);
            viewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to generate Invoice Report: " + e.getMessage(),
                    "JasperReports Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exports the invoice to a PDF file.
     *
     * @param invoiceNo    Invoice Number
     * @param cashier      Cashier
     * @param paymentMode  Payment Mode
     * @param cashPaid     Cash paid
     * @param balance      Balance
     * @param destinationPdf Output file path
     * @return File reference of the exported PDF
     * @throws Exception on export error
     */
    public static File exportInvoiceToPdf(String invoiceNo, String cashier, String paymentMode,
                                          double cashPaid, double balance, String destinationPdf) throws Exception {
        JasperPrint jp = generateInvoicePrint(invoiceNo, cashier, paymentMode, cashPaid, balance);
        File outFile = new File(destinationPdf);
        if (outFile.getParentFile() != null && !outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }
        JasperExportManager.exportReportToPdfFile(jp, outFile.getAbsolutePath());
        return outFile;
    }
}
