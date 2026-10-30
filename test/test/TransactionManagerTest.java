package test;

import javax.swing.SwingUtilities;
import view.TransactionManager;

public class TransactionManagerTest {
    public static void main(String[] args) {
        System.out.println("=== STARTING TRANSACTION MANAGER TEST ===");
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    System.out.println("Testing default TransactionManager() instantiation...");
                    TransactionManager tmDef = new TransactionManager();
                    tmDef.pack();
                    if (!tmDef.getTitle().contains("Invoice")) {
                        throw new AssertionError("Expected title to contain 'Invoice', but was: " + tmDef.getTitle());
                    }
                    System.out.println("PASS: tmDef instantiated and packed successfully. Title=" + tmDef.getTitle());

                    System.out.println("Testing tab switching in TransactionManager...");
                    tmDef.setSelectedTab(1);
                    System.out.println("PASS: Switched to Tab 1 (Transaction Records): " + tmDef.getTitle());
                    tmDef.setSelectedTab(0);
                    System.out.println("PASS: Switched back to Tab 0 (Invoices): " + tmDef.getTitle());
                    tmDef.dispose();

                    System.out.println("Testing TransactionManager(2) [Records Mode] instantiation...");
                    TransactionManager tmRec = new TransactionManager(2);
                    tmRec.pack();
                    System.out.println("PASS: tmRec instantiated successfully. Title=" + tmRec.getTitle());
                    tmRec.dispose();

                    System.out.println("=== ALL TRANSACTION MANAGER TESTS PASSED ===");
                } catch (Throwable ex) {
                    System.err.println("FAIL during TransactionManager test: " + ex.getMessage());
                    ex.printStackTrace();
                    System.exit(1);
                }
            });
        } catch (Exception e) {
            System.err.println("Execution failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
