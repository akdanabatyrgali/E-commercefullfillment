import facade.*;
import subsystem.inventory.*;
import subsystem.warehouse.*;
import subsystem.shipping.*;

import java.util.Scanner;

public class Main {
    private static final String DB_URL = "jdbc:sqlite:fulfillment.db";

    public static void main(String[] args) {
        InventoryService invService = new InventoryService(new InventoryRepository(DB_URL));
        WarehouseService whService = new WarehouseService(new WarehouseRepository(DB_URL));
        ShippingService shipService = new ShippingService(new ShippingRepository(DB_URL));
        WorkflowRepository wfRepo = new WorkflowRepository(DB_URL);

        AdminFulfillmentFacade adminFacade = new AdminFulfillmentFacade(invService, whService, wfRepo);
        ClientOrderFacade clientFacade = new ClientOrderFacade(invService, whService, shipService, wfRepo, adminFacade);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== E-COMMERCE FULFILLMENT DEMO ===");
            System.out.println("1. Process Normal Order (A -> B -> C Success)");
            System.out.println("2. Process Order with Failure at B (Compensates A)");
            System.out.println("3. Process Order with Failure at C (Compensates B then A)");
            System.out.println("4. Check Order Workflow State from SQLite Database");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) break;

            if (choice == 4) {
                System.out.print("Enter Order ID: ");
                String oid = scanner.nextLine();
                String state = adminFacade.getWorkflowState(oid);
                System.out.println("Persisted SQLite State for " + oid + ": " + state);
                continue;
            }

            System.out.print("Enter Order ID (e.g. ORD-1): ");
            String orderId = scanner.nextLine();
            System.out.print("Enter Product ID: ");
            String productId = scanner.nextLine();
            System.out.print("Enter Quantity: ");
            int qty = scanner.nextInt();

            try {
                if (choice == 1) {
                    clientFacade.placeOrder(orderId, productId, qty, false, false);
                } else if (choice == 2) {
                    clientFacade.placeOrder(orderId, productId, qty, true, false);
                } else if (choice == 3) {
                    clientFacade.placeOrder(orderId, productId, qty, false, true);
                }
            } catch (Exception e) {
                System.out.println("Handled exception: " + e.getMessage());
            }
        }
        scanner.close();
    }
}