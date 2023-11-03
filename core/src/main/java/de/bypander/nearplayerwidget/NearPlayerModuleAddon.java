package de.bypander.nearplayerwidget;

import de.bypander.nearplayerwidget.config.NearPlayerModuleConfig;
import de.bypander.nearplayerwidget.hudwidgets.NearPlayerWidget;
import net.labymod.api.Laby;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class NearPlayerModuleAddon extends LabyAddon<NearPlayerModuleConfig> {

  @Override
  protected void enable() {
    registerSettingCategory();
    Laby.labyAPI().hudWidgetRegistry().register(new NearPlayerWidget("NearPlayerWidget", this));
  }

  @Override
  protected Class<? extends NearPlayerModuleConfig> configurationClass() {
    return NearPlayerModuleConfig.class;
  }
}