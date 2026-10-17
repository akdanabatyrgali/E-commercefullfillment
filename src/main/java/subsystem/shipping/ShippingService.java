package subsystem.shipping;

import java.util.UUID;

public class ShippingService {
    private final ShippingRepository repository;

    public ShippingService(ShippingRepository repository) {
        this.repository = repository;
    }

    public void processShipping(String orderId, boolean triggerFailure) {
        Shipment shipment = new Shipment(orderId);
        if (triggerFailure) {
            shipment.fail();
            repository.save(shipment);
            throw new RuntimeException("Shipping Carrier API error!");
        }
        String trackingCode = "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        shipment.ship(trackingCode);
        repository.save(shipment);
    }

    public Shipment getShipment(String orderId) {
        return repository.findById(orderId);
    }
}