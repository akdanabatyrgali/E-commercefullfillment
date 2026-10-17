package facade;

import subsystem.inventory.InventoryRepository;
import subsystem.inventory.InventoryService;
import subsystem.warehouse.WarehouseRepository;
import subsystem.warehouse.WarehouseService;
import subsystem.shipping.ShippingRepository;
import subsystem.shipping.ShippingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ClientOrderFacadeTest {
    private static final String TEST_DB = "jdbc:sqlite:test_fulfillment.db";
    private ClientOrderFacade clientFacade;
    private AdminFulfillmentFacade adminFacade;

    @BeforeEach
    public void setUp() {
        // Очищаем тестовую базу перед КАЖДЫМ тестом (F.I.R.S.T.: Независимость и Повторяемость)
        File file = new File("test_fulfillment.db");
        if (file.exists()) file.delete();

        InventoryService invService = new InventoryService(new InventoryRepository(TEST_DB));
        WarehouseService whService = new WarehouseService(new WarehouseRepository(TEST_DB));
        ShippingService shipService = new ShippingService(new ShippingRepository(TEST_DB));
        WorkflowRepository wfRepo = new WorkflowRepository(TEST_DB);

        adminFacade = new AdminFulfillmentFacade(invService, whService, wfRepo);
        clientFacade = new ClientOrderFacade(invService, whService, shipService, wfRepo, adminFacade);
    }

    @Test
    public void testHappyPath() {
        String orderId = "ORD-TEST-1";

        // Успешная цепочка A -> B -> C
        clientFacade.placeOrder(orderId, "PROD-1", 2, false, false);

        // Проверяем результат (Self-Validating)
        assertEquals("COMPLETED", adminFacade.getWorkflowState(orderId));
    }

    @Test
    public void testFailureAtStepB_CompensatesA() {
        String orderId = "ORD-TEST-2";

        // Проверяем, что при сбое на B падает исключение
        assertThrows(RuntimeException.class, () -> {
            clientFacade.placeOrder(orderId, "PROD-1", 2, true, false);
        });

        // Проверяем, что состояние воркфлоу откатилось до COMPENSATED
        assertEquals("COMPENSATED", adminFacade.getWorkflowState(orderId));
    }

    @Test
    public void testFailureAtStepC_CompensatesBThenA() {
        String orderId = "ORD-TEST-3";

        // Проверяем, что при сбое на C падает исключение
        assertThrows(RuntimeException.class, () -> {
            clientFacade.placeOrder(orderId, "PROD-1", 2, false, true);
        });

        // Проверяем двухступенчатый откат (COMPENSATED)
        assertEquals("COMPENSATED", adminFacade.getWorkflowState(orderId));
    }

    @Test
    public void testPersistenceReloadSimulatingRestart() {
        String orderId = "ORD-TEST-4";

        // Доходим до сбоя на C (останавливается на PENDING_C)
        try {
            clientFacade.placeOrder(orderId, "PROD-1", 2, false, true);
        } catch (Exception ignored) {}

        // Симулируем перезапуск приложения: создаем фасад заново с тем же файлом БД
        AdminFulfillmentFacade reloadedAdmin = new AdminFulfillmentFacade(
                new InventoryService(new InventoryRepository(TEST_DB)),
                new WarehouseService(new WarehouseRepository(TEST_DB)),
                new WorkflowRepository(TEST_DB)
        );

        // Проверяем, что состояние успешно прочитано с диска после "перезапуска"
        assertEquals("COMPENSATED", reloadedAdmin.getWorkflowState(orderId));
    }
}
