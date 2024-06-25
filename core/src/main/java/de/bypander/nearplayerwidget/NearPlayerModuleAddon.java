package de.bypander.nearplayerwidget;

import de.bypander.nearplayerwidget.config.NearPlayerModuleConfig;
import de.bypander.nearplayerwidget.hudwidgets.NearPlayerWidget;
import de.bypander.nearplayerwidget.listener.ServerListener;
import net.labymod.api.Laby;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class NearPlayerModuleAddon extends LabyAddon<NearPlayerModuleConfig> {

  private boolean allowedOnServer = true;

  @Override
  protected void enable() {
    this.registerSettingCategory();
    Laby.labyAPI().hudWidgetRegistry().register(new NearPlayerWidget("NearPlayerWidget", this));
    this.registerListener(new ServerListener(this));

    this.logger().info("Enabled NearPlayerWidget");
  }

  public boolean isAllowedOnServer() {
    return this.allowedOnServer;
  }

  public void setAllowedOnServer(boolean allowedOnServer) {
    this.allowedOnServer = allowedOnServer;
  }

  @Override
  protected Class<? extends NearPlayerModuleConfig> configurationClass() {
    return NearPlayerModuleConfig.class;
  }
}