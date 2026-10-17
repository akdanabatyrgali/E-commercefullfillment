package subsystem.warehouse;

import java.sql.*;

public class WarehouseRepository {
    private final String dbUrl;

    public WarehouseRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        initTable();
    }

    private void initTable() {
        String sql = "CREATE TABLE IF NOT EXISTS warehouse_picks (order_id TEXT PRIMARY KEY, status TEXT)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("DB Init Error B", e);
        }
    }

    public void save(PickItem item) {
        String sql = "INSERT OR REPLACE INTO warehouse_picks (order_id, status) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getOrderId());
            pstmt.setString(2, item.getStatus().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB Save Error B", e);
        }
    }

    public PickItem findById(String orderId) {
        String sql = "SELECT status FROM warehouse_picks WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                PickItem item = new PickItem(orderId);
                item.setStatusDirectly(WarehouseStatus.valueOf(rs.getString("status")));
                return item;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB Find Error B", e);
        }
        return null;
    }
}
