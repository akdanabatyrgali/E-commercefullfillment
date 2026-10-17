package subsystem.warehouse;

public class WarehouseService {
    private final WarehouseRepository repository;

    public WarehouseService(WarehouseRepository repository) {
        this.repository = repository;
    }

    public void processPicking(String orderId, boolean triggerFailure) {
        PickItem item = new PickItem(orderId);
        if (triggerFailure) {
            item.fail();
            repository.save(item);
            throw new RuntimeException("Warehouse picking failed (Item out of stock)!");
        }
        item.pick();
        repository.save(item);
    }

    public void rollbackPicking(String orderId) {
        PickItem item = repository.findById(orderId);
        if (item != null) {
            item.fail();
            repository.save(item);
        }
    }

    public PickItem getItem(String orderId) {
        return repository.findById(orderId);
    }
}
