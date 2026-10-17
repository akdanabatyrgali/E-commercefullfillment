package subsystem.inventory;

public class InventoryService {
    private final InventoryRepository repository;

    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    public void reserveInventory(String orderId, String productId, int quantity) {
        OrderEntity order = new OrderEntity(orderId, productId, quantity);
        order.reserve();
        repository.save(order);
    }

    public void rollbackInventory(String orderId) {
        OrderEntity order = repository.findById(orderId);
        if (order != null) {
            order.cancel();
            repository.save(order);
        }
    }

    public OrderEntity getOrder(String orderId) {
        return repository.findById(orderId);
    }
}
