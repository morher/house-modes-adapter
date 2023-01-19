package net.morher.house.modes;

import net.morher.house.api.context.HouseAdapter;
import net.morher.house.api.context.HouseMqttContext;

public class ModesAdapter implements HouseAdapter {

  public static void main(String[] args) throws Exception {
    new ModesAdapter().run(new HouseMqttContext("modes-adapter"));
  }

  @Override
  public void run(HouseMqttContext ctx) {
    new ModesController(ctx.entityManager())
        .configure(ctx.loadAdapterConfig(ModesAdapterConfiguration.class).getModes());
  }
}
