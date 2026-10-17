package facade;

import subsystem.inventory.InventoryService;
import subsystem.warehouse.WarehouseService;

public class AdminFulfillmentFacade {
    private final InventoryService inventoryService;
    private final WarehouseService warehouseService;
    private final WorkflowRepository workflowRepository;

    public AdminFulfillmentFacade(InventoryService inventoryService,
                                  WarehouseService warehouseService,
                                  WorkflowRepository workflowRepository) {
        this.inventoryService = inventoryService;
        this.warehouseService = warehouseService;
        this.workflowRepository = workflowRepository;
    }

    public String getWorkflowState(String orderId) {
        return workflowRepository.getWorkflowState(orderId);
    }

    public void triggerManualCompensation(String orderId) {
        String currentState = getWorkflowState(orderId);
        System.out.println("[Admin Facade] Compensating for state: " + currentState);

        if ("PENDING_C".equals(currentState)) {
            warehouseService.rollbackPicking(orderId);
            inventoryService.rollbackInventory(orderId);
            workflowRepository.saveWorkflowState(orderId, "COMPENSATED");
        } else if ("PENDING_B".equals(currentState)) {
            inventoryService.rollbackInventory(orderId);
            workflowRepository.saveWorkflowState(orderId, "COMPENSATED");
        }
    }
}
