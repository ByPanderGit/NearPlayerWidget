package de.bypander.nearplayerwidget.hudwidgets;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
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
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.network.NetworkPlayerInfo;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.util.I18n;

public class NearPlayerWidget extends TextHudWidget<TextHudWidgetConfig> {

  private TextLine textLine;

  private final NearPlayerModuleConfig config;

  public NearPlayerWidget(String id, NearPlayerModuleAddon addon) {
    super(id);
    this.config = addon.configuration();
    this.setIcon(Icon.texture(ResourceLocation.create("nearplayerwidget", "textures/icon.png"))
        .resolution(64, 64));
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
      return Component.newline()
          .append(Component.text(" " + I18n.translate("nearplayerwidget.custom.noplayer.name")));
    }

    int numberOfEntries = config.maxEntries().get();
    DecimalFormat df = createDecimalFormatter();
    TextComponent textComponent = Component.empty();

    for (int i = 0; i < Math.min(numberOfEntries, playerList.size()); i++) {
      RenderedPlayer rp = playerList.get(i);
      String format = config.format().get().replaceAll("&([0-9a-fk-or])", "§$1");
      String[] parts = format.split("\\{");

      textComponent.append(Component.newline()).append(Component.text(" "));
      StringBuilder ifNoColor = new StringBuilder();
      for (String part : parts) {
        if (part.contains("}")) {
          String[] placeholders = part.split("\\}");

          if (placeholders.length == 0) {
            continue;
          }

          switch (placeholders[0]) {
            case "name":
              if (config.removeNameColor().get()) {
                ifNoColor.append(rp.name().getText().replaceAll("§[0-9a-fk-or]", ""));
              } else {
                textComponent.append(rp.name());
              }
              break;
            case "displayname":
              if (config.removeNameColor().get()) {
                ifNoColor.append(getText(rp.displayName()).replaceAll("§[0-9a-fk-or]", ""));
              } else {
                textComponent.append(rp.displayName());
              }
              break;
            case "distance":
              String distance = df.format(Math.sqrt(rp.squaredDistance()));
              if (config.coloredDistance().get()) {
                ArrayList<Colors> colors = colorsList();
                for (Colors color : colors) {
                  if (Math.sqrt(rp.squaredDistance()) < color.distance()) {
                    distance = color.color() + distance + "§r";
                    break;
                  }
                }
              }
              if (config.removeNameColor().get()) {
                ifNoColor.append(distance);
              } else {
                textComponent.append(Component.text(distance));
              }
              break;
            default:
              if (config.removeNameColor().get()) {
                ifNoColor.append(placeholders[0]);
              } else {
                textComponent.append(Component.text(placeholders[0]));
              }
              break;
          }

          if (placeholders.length > 1) {
            if (config.removeNameColor().get()) {
              ifNoColor.append(placeholders[1]);
            } else {
              textComponent.append(Component.text(placeholders[1]));
            }
          }
        } else {
          if (config.removeNameColor().get()) {
            ifNoColor.append(part);
          } else {
            textComponent.append(Component.text(part));
          }
        }
      }
      textComponent.append(Component.text(ifNoColor.toString()));
    }
    return textComponent;
  }

  private ArrayList<RenderedPlayer> orderedPlayerList() {
    Player clientplayer = Laby.labyAPI().minecraft().getClientPlayer();
    List<Entity> entities = Laby.labyAPI().minecraft().clientWorld().getEntities();
    ArrayList<RenderedPlayer> playerList = new ArrayList<>();
    outer:
    for (Entity entity : entities) {
      if (entity instanceof Player) {
        assert clientplayer != null;
        if (!((Player) entity).getName().equals(clientplayer.getName())) {
          if (config.removePlayer().get()) {
            for (String n : config.removePlayerText().get().split(";")) {
              if (((Player) entity).getName().replaceAll("§", "").equals(n)) {
                continue outer;
              }
            }
          }
          Double squaredDistance = entity.getDistanceSquared(clientplayer);
          TextComponent name;
          name = Component.text(((Player) entity).getName());
          TextComponent displayName = name;
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

  private ArrayList<Colors> colorsList() {
    ArrayList<Colors> colors = new ArrayList<>();
    String[] parts = config.coloredDistanceText().get().split(";");
    for (String part : parts) {
      String[] p = part.split(">");
      if (p.length != 2 || !Pattern.compile("&[0-9a-f]").matcher(p[1]).matches()) {
        continue;
      }
      try {
        int d = Integer.parseInt(p[0]);
        colors.add(new Colors(d, p[1].replace("&", "§")));
      } catch (NumberFormatException ignored) {
      }
    }
    colors.sort(Comparator.comparingDouble(Colors::distance));
    return colors;
  }

  private String getText (TextComponent textComponent) {
    StringBuilder sb = new StringBuilder().append(textComponent.getText());
    for (Component component : textComponent.getChildren()) {
      if (component instanceof TextComponent) {
        sb.append(getText((TextComponent) component));
      }
    }
    return sb.toString();
  }

  private record Colors(Integer distance, String color) {

  }

  private record RenderedPlayer(TextComponent name, TextComponent displayName,
                                Double squaredDistance) {

  }
}
