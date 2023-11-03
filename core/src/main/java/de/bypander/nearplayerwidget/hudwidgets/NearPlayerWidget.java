package de.bypander.nearplayerwidget.hudwidgets;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import de.bypander.nearplayerwidget.NearPlayerModuleAddon;
import de.bypander.nearplayerwidget.config.NearPlayerModuleConfig;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.entity.Entity;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.network.NetworkPlayerInfo;
import net.labymod.api.util.I18n;

public class NearPlayerWidget extends TextHudWidget<TextHudWidgetConfig> {

  private TextLine textLine;

  private final NearPlayerModuleConfig config;

  public NearPlayerWidget(String id, NearPlayerModuleAddon addon) {
    super(id);
    this.config = addon.configuration();
  }

  @Override
  public void onTick(boolean isEditorContext) {
    textLine.updateAndFlush(newTextComponent());
  }

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);
    textLine = super.createLine("Nearplayermodul", newTextComponent());
  }

  private TextComponent newTextComponent() {
    ArrayList<RenderedPlayer> playerList = orderedPlayerList();

    if (playerList.isEmpty()) {
      return Component.newline().append(Component.text(I18n.translate("nearplayerwidget.custom.noplayer.name")));
    }

    int numberOfEntries = config.maxEntries().get();
    DecimalFormat df = createDecimalFormatter();
    TextComponent textComponent = Component.empty();

    for (int i = 0; i < Math.min(numberOfEntries, playerList.size()); i++) {
      RenderedPlayer rp = playerList.get(i);
      String format = config.format().get();
      String[] parts = format.split("\\{");

      textComponent.append(Component.newline()).append(Component.text(" "));

      for (String part : parts) {
        if (part.contains("}")) {
          String[] placeholders = part.split("\\}");

          if (placeholders.length == 0) {
            continue;
          }

          switch (placeholders[0]) {
            case "name":
              textComponent.append(rp.name());
              break;
            case "displayName":
              textComponent.append(rp.displayName());
              break;
            case "distance":
              textComponent.append(Component.text(df.format(Math.sqrt(rp.squaredDistance()))));
              break;
            default:
              textComponent.append(Component.text(placeholders[0]));
              break;
          }

          if (placeholders.length > 1) {
            textComponent.append(Component.text(placeholders[1]));
          }
        } else {
          textComponent.append(Component.text(part));
        }
      }
    }
    return textComponent;
  }

  private ArrayList<RenderedPlayer> orderedPlayerList() {
    Player clientplayer = Laby.labyAPI().minecraft().getClientPlayer();
    List<Entity> entities = Laby.labyAPI().minecraft().clientWorld().getEntities();
    ArrayList<RenderedPlayer> playerList = new ArrayList<>();

    for (Entity entity : entities) {
      if (entity instanceof Player) {
        assert clientplayer != null;
        if (!((Player) entity).getName().equals(clientplayer.getName())) {
          Double squaredDistance = entity.getDistanceSquared(clientplayer);

          TextComponent name = Component.text(((Player) entity).getName());
          if (name.getText().trim().isEmpty() && config.removeWithoutName().get()) {
            continue;
          }

          TextComponent displayName = Component.text(((Player) entity).getName());
          NetworkPlayerInfo npi = ((Player) entity).networkPlayerInfo();
          if (npi != null) {
            if (npi.displayName() instanceof TextComponent) {
              displayName = (TextComponent) npi.displayName();
            }
          }

          playerList.add(
              new RenderedPlayer(name, displayName,
                  squaredDistance));
        }
      }
    }
    playerList.sort(Comparator.comparingDouble(RenderedPlayer::squaredDistance));
    return playerList;
  }

  private DecimalFormat createDecimalFormatter() {
    if (config.maxDecimals().get() > 0) {
      return new DecimalFormat("0." + "0".repeat(config.maxDecimals().get()));
    }
    return new DecimalFormat("0");
  }

  private record RenderedPlayer(TextComponent name, TextComponent displayName, Double squaredDistance) {

  }
}
