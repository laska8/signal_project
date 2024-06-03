package alerts;

import com.alerts.Alert;
import com.alerts.factory.AlertFactory;
import com.alerts.factory.BloodOxygenAlertFactory;
import com.alerts.factory.BloodPressureAlertFactory;
import com.alerts.factory.ECGAlertFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertFactoryTest {

    @Test
    public void testBloodPressureAlarmFactory() {
        AlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("100", "SystolicPressure", 1714376789050L);

        assertEquals("100", alert.getPatientId());
        assertEquals("SystolicPressure", alert.getCondition());
        assertEquals(1714376789050L, alert.getTimestamp());
    }

    @Test
    public void testECGAlarmFactory() {
        AlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert("100", "ECG", 1714376789050L);

        assertEquals("100", alert.getPatientId());
        assertEquals("ECG", alert.getCondition());
        assertEquals(1714376789050L, alert.getTimestamp());
    }

    @Test
    public void testBloodOxygenAlarmFactory() {
        AlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert("100", "Saturation", 1714376789050L);

        assertEquals("100", alert.getPatientId());
        assertEquals("Saturation", alert.getCondition());
        assertEquals(1714376789050L, alert.getTimestamp());
    }
}