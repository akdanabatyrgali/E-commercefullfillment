package facade;

import java.sql.*;

public class WorkflowRepository {
    private final String dbUrl;

    public WorkflowRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        initTable();
    }

    private void initTable() {
        String sql = "CREATE TABLE IF NOT EXISTS workflow_status (order_id TEXT PRIMARY KEY, workflow_state TEXT)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("DB Init Workflow Error", e);
        }
    }

    public void saveWorkflowState(String orderId, String state) {
        String sql = "INSERT OR REPLACE INTO workflow_status (order_id, workflow_state) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            pstmt.setString(2, state);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB Save Workflow Error", e);
        }
    }

    public String getWorkflowState(String orderId) {
        String sql = "SELECT workflow_state FROM workflow_status WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("workflow_state");
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB Get Workflow Error", e);
        }
        return "UNKNOWN";
    }
}
