package net.morher.house.modes;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.morher.house.api.entity.DeviceId;
import net.morher.house.api.entity.DeviceInfo;
import net.morher.house.api.entity.EntityId;
import net.morher.house.api.entity.EntityManager;
import net.morher.house.api.entity.common.CommandableEntity;
import net.morher.house.api.entity.common.EntityOptions;
import net.morher.house.api.entity.switches.SwitchOptions;
import net.morher.house.api.subscription.Subscription;
import net.morher.house.modes.ModesAdapterConfiguration.ModeDeviceConfiguration;
import net.morher.house.modes.ModesAdapterConfiguration.ModeEntityConfiguration;
import net.morher.house.modes.ModesAdapterConfiguration.ModesConfiguration;

public class ModesController {
  private final EntityManager entityManager;

  public ModesController(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public void configure(ModesConfiguration config) {
    for (ModeDeviceConfiguration deviceConfig : config.getDevices()) {
      configure(deviceConfig);
    }
  }

  private void configure(ModeDeviceConfiguration deviceConfig) {
    DeviceId deviceId = deviceConfig.getDevice().toDeviceId();
    DeviceInfo deviceInfo = new DeviceInfo();
    deviceInfo.setManufacturer(deviceConfig.getManufacturer());
    deviceInfo.setModel(deviceConfig.getModel());
    deviceInfo.setConfigurationUrl(deviceConfig.getConfigurationUrl());
    deviceInfo.setSwVersion(deviceConfig.getSwVersion());

    ModesDevice device = new ModesDevice(deviceId, deviceInfo);

    for (Entry<String, ModeEntityConfiguration> entity : deviceConfig.getEntities().entrySet()) {
      configureEntity(entity.getValue(), device, entity.getKey());
    }
  }

  private void configureEntity(
      ModeEntityConfiguration entityConfig, ModesDevice device, String entityName) {
    device.getEntities().add(createEntity(entityConfig, device, entityName));
  }

  private ModesEntity createEntity(
      ModeEntityConfiguration entityConfig, ModesDevice device, String entityName) {
    switch (entityConfig.getType()) {
      case "switch":
        SwitchOptions options = new SwitchOptions();
        options.setIcon(entityConfig.getIcon());
        return new ModesPassthroughEntity<>(
            entityManager.switchEntity(device.entityId(entityName)),
            device.getDeviceInfo(),
            options);
    }
    throw new IllegalArgumentException("Unknown entity type: " + entityConfig.getType());
  }

  private interface ModesEntity {}

  private static class ModesPassthroughEntity<
          P, O extends EntityOptions, E extends CommandableEntity<P, O, P>>
      implements ModesEntity, Closeable {
    private final Subscription subscription;

    public ModesPassthroughEntity(E entity, DeviceInfo deviceInfo, O options) {
      subscription = entity.command().subscribe(entity.state()::publish);
      entity.setDeviceInfo(deviceInfo);
      entity.setOptions(options);
    }

    @Override
    public void close() throws IOException {
      subscription.unsubscribe();
    }
  }

  @AllArgsConstructor
  @Getter
  public static class ModesDevice {
    private final DeviceId deviceId;
    private final DeviceInfo deviceInfo;
    private final List<ModesEntity> entities = new ArrayList<>();

    public EntityId entityId(String entityName) {
      return new EntityId(deviceId, entityName);
    }
  }
}
