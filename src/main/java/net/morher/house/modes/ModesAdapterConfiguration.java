package net.morher.house.modes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import net.morher.house.api.config.DeviceName;

@Data
public class ModesAdapterConfiguration {
  private ModesConfiguration modes;

  @Data
  public static class ModesConfiguration {
    private final List<ModeDeviceConfiguration> devices = new ArrayList<>();
  }

  @Data
  public static class ModeDeviceConfiguration {
    private DeviceName device;
    private Map<String, ModeEntityConfiguration> entities = new HashMap<>();
    private String manufacturer = "House";
    private String model = "Modes";
    private String configurationUrl;
    private String swVersion;
  }

  @Data
  public static class ModeEntityConfiguration {
    private String type;
    private String icon;
  }
}
