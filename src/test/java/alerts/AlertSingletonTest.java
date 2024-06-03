package alerts;

import com.cardiogenerator.HealthDataSimulator;
import com.data_management.DataStorage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class AlertSingletonTest {
    @Test
    void testSingletonInstanceDataStorage() {
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();
        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }
    @Test
    void testSingletonInstanceHealthDataSimulator() {
        HealthDataSimulator instance1 = HealthDataSimulator.getInstance();
        HealthDataSimulator instance2 = HealthDataSimulator.getInstance();

        assertNotNull(instance1, "Instance 1 should not be equal to null");
        assertNotNull(instance2, "Instance 2 should not be equal to null");

        assertSame(instance1, instance2, "Both instances should be the same");
    }
}
