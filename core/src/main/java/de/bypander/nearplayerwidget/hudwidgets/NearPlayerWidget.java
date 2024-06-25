package de.bypander.nearplayerwidget.hudwidgets;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

import de.bypander.nearplayerwidget.NearPlayerModuleAddon;
import de.bypander.nearplayerwidget.config.NearPlayerModuleConfig;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.util.I18n;

public class NearPlayerWidget extends TextHudWidget<TextHudWidgetConfig> {

  private TextLine textLine;

  private final NearPlayerModuleConfig config;

  private final NearPlayerModuleAddon addon;

  public NearPlayerWidget(String id, NearPlayerModuleAddon addon) {
    super(id);
    this.addon = addon;
    this.config = addon.configuration();
    this.setIcon(Icon.texture(ResourceLocation.create("nearplayerwidget", "textures/icon.png"))
      .resolution(64, 64));
  }

  @Override
  public boolean isEnabled() {
    return addon.isAllowedOnServer() && super.isEnabled();
  }

  @Override
  public void onTick(boolean isEditorContext) {
    if (!isEnabled())
      return;
    textLine.updateAndFlush(newTextComponent());
  }

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);
    textLine = super.createLine("Nearplayermodul", newTextComponent());
  }

  private TextComponent newTextComponent() {
    Player clientPlayer = Laby.labyAPI().minecraft().getClientPlayer();
    List<Player> playerList = Laby.labyAPI().minecraft().clientWorld().getPlayers();
    if (playerList == null || playerList.isEmpty() || clientPlayer == null) {
      return Component.newline()
        .append(Component.text(" " + I18n.translate("nearplayerwidget.custom.noplayer.name")));
    }
    List<Player> sortedList = new ArrayList<>(playerList);
    sortedList.sort(Comparator.comparingDouble(p -> p.getDistanceSquared(clientPlayer)));

    DecimalFormat df = createDecimalFormatter();
    TextComponent textComponent = Component.empty();
    List<String> format = format();
    List<Colors> colors = new ArrayList<>();
    if (config.coloredDistance().get())
       colors = colorsList();
    int added = 0;

    String[] removePlayer = config.removePlayerText().get().split(";");
    HashMap<String, String> map = new HashMap<>();
    for (String s : removePlayer) {
      map.put(s, null);
    }

    //loop through all rendered player until no player is left, or the maximum number of visible player in the widget is reached
    for (Player p : sortedList) {
      if (added >= config.maxEntries().get())
        break;

      if (p == null || p.getNetworkPlayerInfo() == null || p.getName() == clientPlayer.getName())
        continue;

      if (config.removePlayer().get() && map.containsKey(p.getName().replace("§", "")))
        continue;

      //add the player in a new line
      textComponent.append(Component.newline().append(Component.text(" ")));
      StringBuilder sb = new StringBuilder();
      outer:
      for (String s : format) {
        switch (s) {
          case "§displayname":
            if (config.removeNameColor().get()) {
              sb.append(getPlainText(p.getNetworkPlayerInfo().displayName()));
            } else {
              textComponent.append(p.getNetworkPlayerInfo().displayName());
            }
            break;
          case "§name":
            if (config.removeNameColor().get()) {
              sb.append(p.getName().replaceAll("§[0-9a-fk-or]", ""));
            } else {
              textComponent.append(Component.text(p.getName()));
            }
            break;
          case "§distance":
            double distanceSquared = p.getDistanceSquared(clientPlayer);
            double distance = Math.sqrt(distanceSquared);
            String formattedDistance = df.format(distance);
            if (!config.coloredDistance().get()) {
              if (config.removeNameColor().get()) {
                sb.append(formattedDistance);
              } else {
                textComponent.append(Component.text(formattedDistance));
              }
              continue;
            }

            for (Colors color : colors) {
              if (distanceSquared < color.distanceSquared()) {
                if (config.removeNameColor().get()) {
                  sb.append(color.color + formattedDistance + "§r");
                } else {
                  textComponent.append(Component.text(color.color + formattedDistance + "§r"));
                }
                continue  outer;
              }
            }
            if (config.removeNameColor().get()) {
              sb.append(formattedDistance);
            } else {
              textComponent.append(Component.text(formattedDistance));
            }
            break;
          default:
            if (config.removeNameColor().get()) {
              sb.append(s);
            } else {
              textComponent.append(Component.text(s));
            }
        }
      }
      added += 1;
      textComponent.append(Component.text(sb.toString()));
    }
    if (added == 0)
      return Component.newline()
        .append(Component.text(" " + I18n.translate("nearplayerwidget.custom.noplayer.name")));
    return textComponent;
  }

  private List<String> format() {
    String format = config.format().get().replaceAll("&([0-9a-fk-orA-FK-OR])", "§$1");
    format = format.replaceAll("\\{(displayname|name|distance)\\}", "§ §$1§ ");
    String[] front = format.split("§ ");
    return Arrays.asList(front);
  }

  private DecimalFormat createDecimalFormatter() {
    if (config.maxDecimals().get() > 0) {
      return new DecimalFormat("0." + "0".repeat(config.maxDecimals().get()));
    }
    return new DecimalFormat("0");
  }

  private List<Colors> colorsList() {
    List<Colors> colors = new ArrayList<>();
    String[] parts = config.coloredDistanceText().get().split(";");
    for (String part : parts) {
      String[] p = part.split(">");
      if (p.length != 2 || !Pattern.compile("&[0-9a-f]").matcher(p[1]).matches()) {
        continue;
      }
      try {
        int d = Integer.parseInt(p[0]);
        colors.add(new Colors(d * d, p[1].replace("&", "§")));
      } catch (NumberFormatException ignored) {
      }
    }
    colors.sort(Comparator.comparingDouble(Colors::distanceSquared));
    return colors;
  }

  private String getPlainText(Component textComponent) {
    StringBuilder builder = new StringBuilder();
    Laby.references().componentRenderer().getColorStrippingFlattener().flatten(textComponent, builder::append);
    return builder.toString();
  }

  private record Colors(Integer distanceSquared, String color) {
  }
}