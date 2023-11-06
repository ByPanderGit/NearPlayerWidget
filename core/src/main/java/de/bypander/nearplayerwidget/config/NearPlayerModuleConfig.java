package de.bypander.nearplayerwidget.config;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.util.MethodOrder;

public class NearPlayerModuleConfig extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @TextFieldSetting
  private final ConfigProperty<String> format = new ConfigProperty<>("{displayname} &7({distance}&7)");

  @SliderSetting(steps = 1, min = 1, max = 25)
  private final ConfigProperty<Integer> maxEntries = new ConfigProperty<>(10);

  @SliderSetting(steps = 1, min = 0, max = 5)
  private final ConfigProperty<Integer> maxDecimals = new ConfigProperty<>(2);

  @SettingSection("color")
  @SwitchSetting
  private final ConfigProperty<Boolean> removeNameColor = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> coloredDistance = new ConfigProperty<>(true);

  @TextFieldSetting
  private final ConfigProperty<String> coloredDistanceText = new ConfigProperty<>("1024>&a;20>&6;10>&c");

  @SettingSection("removePlayerSection")
  @SwitchSetting
  private final ConfigProperty<Boolean> removePlayer = new ConfigProperty<>(false);

  @TextFieldSetting
  private final ConfigProperty<String> removePlayerText = new ConfigProperty<>("Name1;Name2:Name3");

  public ConfigProperty<Boolean> removePlayer() {
    return removePlayer;
  }

  public ConfigProperty<String> removePlayerText() {
    return removePlayerText;
  }

  @Override
  public ConfigProperty<Boolean> enabled() {
    return enabled;
  }

  public ConfigProperty<String> format() {
    return  format;
  }

  public ConfigProperty<Boolean> coloredDistance() {
    return coloredDistance;
  }

  public ConfigProperty<String> coloredDistanceText() {
    return coloredDistanceText;
  }

  public ConfigProperty<Integer> maxEntries() {
    return maxEntries;
  }

  public ConfigProperty<Integer> maxDecimals() {
    return maxDecimals;
  }

  public ConfigProperty<Boolean> removeNameColor() {
    return removeNameColor;
  }
}
