package net.morher.house.modes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import net.morher.house.api.entity.DeviceId;
import net.morher.house.api.entity.EntityId;
import net.morher.house.api.entity.EntityManager;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.modes.ModesAdapterConfiguration.ModesConfiguration;
import net.morher.house.test.client.DefaultMqttStub;
import net.morher.house.test.client.TestHouseMqttClient;
import net.morher.house.test.config.TestConfigLoader;
import org.junit.Test;

public class ModesControllerTest {

  private static final DeviceId DEVICE_ID = new DeviceId("room", "device");
  private static final EntityId ENTITY_ID_ENABLE = new EntityId(DEVICE_ID, "enable");

  @Test
  public void testCreateDeviceWithSwitchAsMain() {
    DefaultMqttStub mqttStub = new DefaultMqttStub().loopback(true);
    HouseMqttClient client = new TestHouseMqttClient(mqttStub);
    EntityManager entityManager = spy(new EntityManager(client));

    ModesConfiguration config =
        new TestConfigLoader<>(ModesConfiguration.class)
            .fromYaml(
                """
                devices:
                  - device:
                      room: room
                      name: device
                    entities:
                      enable:
                        type: switch
                """);

    ModesController controller = new ModesController(entityManager);
    controller.configure(config);

    verify(entityManager).switchEntity(eq(ENTITY_ID_ENABLE));
    assertFalse(
        "Should not be subscribed to state topic",
        mqttStub.isSubscribedTo("house/room/device/enable"));
    assertTrue(
        "Should be subscribed to command topic",
        mqttStub.isSubscribedTo("house/room/device/enable/command"));
  }
}
