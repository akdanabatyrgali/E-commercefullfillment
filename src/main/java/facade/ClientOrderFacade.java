package facade;

import subsystem.inventory.InventoryService;
import subsystem.warehouse.WarehouseService;
import subsystem.shipping.ShippingService;

public class ClientOrderFacade {
    private final InventoryService inventoryService;
    private final WarehouseService warehouseService;
    private final ShippingService shippingService;
    private final WorkflowRepository workflowRepository;
    private final AdminFulfillmentFacade adminFacade;

    public ClientOrderFacade(InventoryService inventoryService,
                             WarehouseService warehouseService,
                             ShippingService shippingService,
                             WorkflowRepository workflowRepository,
                             AdminFulfillmentFacade adminFacade) {
        this.inventoryService = inventoryService;
        this.warehouseService = warehouseService;
        this.shippingService = shippingService;
        this.workflowRepository = workflowRepository;
        this.adminFacade = adminFacade;
    }

    public void placeOrder(String orderId, String productId, int quantity, boolean failAtB, boolean failAtC) {
        try {
            System.out.println("[Step A] Reserving inventory...");
            inventoryService.reserveInventory(orderId, productId, quantity);
            workflowRepository.saveWorkflowState(orderId, "PENDING_B");

            System.out.println("[Step B] Warehouse picking...");
            warehouseService.processPicking(orderId, failAtB);
            workflowRepository.saveWorkflowState(orderId, "PENDING_C");

            System.out.println("[Step C] Shipping...");
            shippingService.processShipping(orderId, failAtC);

            workflowRepository.saveWorkflowState(orderId, "COMPLETED");
            System.out.println("Order completed successfully!");

        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            adminFacade.triggerManualCompensation(orderId);
            throw e;
        }
    }
}