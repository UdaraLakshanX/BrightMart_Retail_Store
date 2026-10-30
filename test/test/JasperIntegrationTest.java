package test;

import java.io.File;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import reports.ReportManager;

public class JasperIntegrationTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" Starting JasperReports Integration Test for BrightMart");
        System.out.println("==================================================");

        try {
            // 1. Compile JRXML to .jasper
            System.out.println("\n[STEP 1] Compiling JRXML report template...");
            JasperReport report = ReportManager.compileInvoiceReport();
            if (report == null) {
                throw new RuntimeException("Compiled JasperReport is null!");
            }
            File jasperFile = new File("src/reports/invoice_report.jasper");
            if (!jasperFile.exists() || jasperFile.length() == 0) {
                throw new RuntimeException("Jasper file was not created or is empty: " + jasperFile.getAbsolutePath());
            }
            System.out.println("--> SUCCESS: Report compiled successfully. Size: " + jasperFile.length() + " bytes");

            // 2. Test filling report with INV-00001
            System.out.println("\n[STEP 2] Filling invoice INV-00001 from MySQL database...");
            JasperPrint jp = ReportManager.generateInvoicePrint("INV-00001", "Admin", "Cash", 1000.00, 100.00);
            if (jp == null || jp.getPages().isEmpty()) {
                throw new RuntimeException("Generated JasperPrint has no pages!");
            }
            System.out.println("--> SUCCESS: Invoice filled with " + jp.getPages().size() + " page(s). Report Name: " + jp.getName());

            // 3. Test PDF Export
            System.out.println("\n[STEP 3] Exporting invoice to PDF...");
            File pdfFile = ReportManager.exportInvoiceToPdf("INV-00001", "Admin", "Cash", 1000.00, 100.00, "invoices/sample_invoice_INV-00001.pdf");
            if (!pdfFile.exists() || pdfFile.length() == 0) {
                throw new RuntimeException("PDF file was not created or is empty: " + pdfFile.getAbsolutePath());
            }
            System.out.println("--> SUCCESS: PDF generated at: " + pdfFile.getAbsolutePath() + " (" + pdfFile.length() + " bytes)");

            System.out.println("\n==================================================");
            System.out.println(" ALL JASPER INTEGRATION TESTS PASSED 100%!");
            System.out.println("==================================================");
            System.exit(0);
        } catch (Throwable t) {
            System.err.println("\n[FAILED] Jasper Integration Test Failed:");
            t.printStackTrace();
            System.exit(1);
        }
    }
}
