package test;

import javax.swing.SwingUtilities;
import view.GRNManagement;

public class GRNManagementTest {
    public static void main(String[] args) {
        System.out.println("=== STARTING GRN MANAGEMENT TEST ===");
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    System.out.println("Testing GRNManagement instantiation...");
                    GRNManagement grn = new GRNManagement();
                    grn.pack();
                    if (!grn.getTitle().contains("GRN")) {
                        throw new AssertionError("Expected title to contain 'GRN', but was: " + grn.getTitle());
                    }
                    System.out.println("PASS: grn instantiated and packed successfully. Title=" + grn.getTitle());

                    String nextNo = grn.generateNextGRNNo();
                    System.out.println("PASS: Next GRN No generated: " + nextNo);
                    if (nextNo == null || !nextNo.startsWith("GRN-")) {
                        throw new AssertionError("Invalid GRN number generated: " + nextNo);
                    }

                    System.out.println("Testing tab switching in GRNManagement...");
                    grn.setSelectedTab(1); // GRN History & Records
                    System.out.println("PASS: Switched to Tab 1 (GRN History & Records)");
                    grn.setSelectedTab(0); // New GRN (Stock In)
                    System.out.println("PASS: Switched back to Tab 0 (New GRN)");

                    grn.dispose();
                    System.out.println("=== ALL GRN MANAGEMENT TESTS PASSED ===");
                } catch (Throwable ex) {
                    System.err.println("FAIL during GRNManagement test: " + ex.getMessage());
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
