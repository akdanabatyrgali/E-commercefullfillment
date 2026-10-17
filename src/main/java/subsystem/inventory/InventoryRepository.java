package subsystem.inventory;

import java.sql.*;

public class InventoryRepository {
    private final String dbUrl;

    public InventoryRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        initTable();
    }

    private void initTable() {
        String sql = "CREATE TABLE IF NOT EXISTS inventory_orders (order_id TEXT PRIMARY KEY, product_id TEXT, quantity INTEGER, status TEXT)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("DB Init Error A", e);
        }
    }

    public void save(OrderEntity order) {
        String sql = "INSERT OR REPLACE INTO inventory_orders (order_id, product_id, quantity, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, order.getOrderId());
            pstmt.setString(2, order.getProductId());
            pstmt.setInt(3, order.getQuantity());
            pstmt.setString(4, order.getStatus().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB Save Error A", e);
        }
    }

    public OrderEntity findById(String orderId) {
        String sql = "SELECT product_id, quantity, status FROM inventory_orders WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                OrderEntity order = new OrderEntity(orderId, rs.getString("product_id"), rs.getInt("quantity"));
                order.setStatusDirectly(OrderStatus.valueOf(rs.getString("status")));
                return order;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB Find Error A", e);
        }
        return null;
    }
}