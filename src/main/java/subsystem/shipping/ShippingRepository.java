package subsystem.shipping;

import java.sql.*;

public class ShippingRepository {
    private final String dbUrl;

    public ShippingRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        initTable();
    }

    private void initTable() {
        String sql = "CREATE TABLE IF NOT EXISTS shipments (order_id TEXT PRIMARY KEY, tracking_code TEXT, status TEXT)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("DB Init Error C", e);
        }
    }

    public void save(Shipment shipment) {
        String sql = "INSERT OR REPLACE INTO shipments (order_id, tracking_code, status) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shipment.getOrderId());
            pstmt.setString(2, shipment.getTrackingCode());
            pstmt.setString(3, shipment.getStatus().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB Save Error C", e);
        }
    }

    public Shipment findById(String orderId) {
        String sql = "SELECT tracking_code, status FROM shipments WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Shipment shipment = new Shipment(orderId);
                shipment.setTrackingCodeDirectly(rs.getString("tracking_code"));
                shipment.setStatusDirectly(ShippingStatus.valueOf(rs.getString("status")));
                return shipment;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB Find Error C", e);
        }
        return null;
    }
}