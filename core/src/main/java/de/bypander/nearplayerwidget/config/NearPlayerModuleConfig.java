package de.bypander.nearplayerwidget.config;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.annotation.SpriteTexture;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;

@ConfigName("settings")
@SpriteTexture("settings.png")
public class NearPlayerModuleConfig extends AddonConfig {

  @SpriteSlot(x = 0, y = 0)
  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @SpriteSlot(x = 1, y = 0)
  @TextFieldSetting
  private final ConfigProperty<String> format = new ConfigProperty<>("{displayname} &7({distance}&7m)");

  @SpriteSlot(x = 2, y = 0)
  @SliderSetting(steps = 1, min = 1, max = 25)
  private final ConfigProperty<Integer> maxEntries = new ConfigProperty<>(10);

  @SpriteSlot(x = 3, y = 0)
  @SliderSetting(steps = 1, min = 0, max = 5)
  private final ConfigProperty<Integer> maxDecimals = new ConfigProperty<>(2);

  @SettingSection("color")
  @SpriteSlot(x = 4, y = 0)
  @SwitchSetting
  private final ConfigProperty<Boolean> removeNameColor = new ConfigProperty<>(false);

  @SpriteSlot(x = 5, y = 0)
  @SwitchSetting
  private final ConfigProperty<Boolean> coloredDistance = new ConfigProperty<>(true);

  @SpriteSlot(x = 5, y = 0)
  @TextFieldSetting
  private final ConfigProperty<String> coloredDistanceText = new ConfigProperty<>("1024>&a;20>&6;10>&c");

  @SettingSection("removePlayerSection")
  @SpriteSlot(x = 6, y = 0)
  @SwitchSetting
  private final ConfigProperty<Boolean> removePlayer = new ConfigProperty<>(false);

  @SpriteSlot(x = 7, y = 0)
  @TextFieldSetting
  private final ConfigProperty<String> removePlayerText = new ConfigProperty<>("Name1;Name2;Name3");

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
    return format;
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
