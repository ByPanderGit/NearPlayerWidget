package de.bypander.nearplayerwidget.listener;

import de.bypander.nearplayerwidget.NearPlayerModuleAddon;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.event.client.network.server.ServerJoinEvent;

public class ServerListener {

  private final NearPlayerModuleAddon addon;

  public ServerListener(NearPlayerModuleAddon addon) {
    this.addon = addon;
  }

  @Subscribe
  public void onServerJoin(ServerJoinEvent event) {
    addon.setAllowedOnServer(event.serverData().address().getHost().toLowerCase().contains("griefergames"));
  }

  @Subscribe
  public void onServerDisconnect(ServerDisconnectEvent event) {
    addon.setAllowedOnServer(true);
  }
}
