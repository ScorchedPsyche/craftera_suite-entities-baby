package com.github.scorchedpsyche.craftera_suite.entities.baby;

import com.github.scorchedpsyche.craftera_suite.entities.baby.listeners.EntityNamingListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    // Plugin startup logic
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EntityNamingListener(), this);
    }

    // Plugin shutdown logic
    @Override
    public void onDisable() {
    }
}
