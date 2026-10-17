package subsystem.inventory;

public class OrderEntity {
    private final String orderId;
    private final String productId;
    private final int quantity;
    private OrderStatus status;

    public OrderEntity(String orderId, String productId, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = OrderStatus.NEW;
    }

    public void reserve() {
        this.status = OrderStatus.RESERVED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELED;
    }

    public String getOrderId() { return orderId; }
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public OrderStatus getStatus() { return status; }
    public void setStatusDirectly(OrderStatus status) { this.status = status; }
}
