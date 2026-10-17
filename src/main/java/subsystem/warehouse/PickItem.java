package subsystem.warehouse;

public class PickItem {
    private final String orderId;
    private WarehouseStatus status;

    public PickItem(String orderId) {
        this.orderId = orderId;
        this.status = WarehouseStatus.PENDING_PICK;
    }

    public void pick() {
        this.status = WarehouseStatus.PICKED;
    }

    public void fail() {
        this.status = WarehouseStatus.FAILED;
    }

    public String getOrderId() { return orderId; }
    public WarehouseStatus getStatus() { return status; }
    public void setStatusDirectly(WarehouseStatus status) { this.status = status; }
}
