package de.bypander.nearplayerwidget.config;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class NearPlayerModuleConfig extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @TextFieldSetting
  private final ConfigProperty<String> format = new ConfigProperty<>("{name} | {distance}m");

  @SliderSetting(steps = 1, min = 1, max = 25)
  private final ConfigProperty<Integer> maxEntries = new ConfigProperty<>(10);

  @SliderSetting(steps = 1, min = 0, max = 5)
  private final ConfigProperty<Integer> maxDecimals = new ConfigProperty<>(2);

  @SwitchSetting
  private final ConfigProperty<Boolean> removeWithoutName = new ConfigProperty<>(true);

  @Override
  public ConfigProperty<Boolean> enabled() {
    return enabled;
  }

  public ConfigProperty<String> format() {
    return  format;
  }

  public ConfigProperty<Integer> maxEntries() {
    return maxEntries;
  }

  public ConfigProperty<Integer> maxDecimals() {
    return maxDecimals;
  }

  public ConfigProperty<Boolean> removeWithoutName() {
    return removeWithoutName;
  }
}
