package subsystem.shipping;

public class Shipment {
    private final String orderId;
    private String trackingCode;
    private ShippingStatus status;

    public Shipment(String orderId) {
        this.orderId = orderId;
        this.status = ShippingStatus.PENDING_SHIPPING;
    }

    public void ship(String trackingCode) {
        this.trackingCode = trackingCode;
        this.status = ShippingStatus.SHIPPED;
    }

    public void fail() {
        this.status = ShippingStatus.FAILED;
    }

    public String getOrderId() { return orderId; }
    public String getTrackingCode() { return trackingCode; }
    public ShippingStatus getStatus() { return status; }
    public void setStatusDirectly(ShippingStatus status) { this.status = status; }
    public void setTrackingCodeDirectly(String code) { this.trackingCode = code; }
}
