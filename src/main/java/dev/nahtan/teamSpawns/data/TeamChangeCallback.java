package dev.nahtan.teamSpawns.data;

import org.bukkit.entity.Player;

public interface TeamChangeCallback {

    void call(Player player, String teamName);
}
