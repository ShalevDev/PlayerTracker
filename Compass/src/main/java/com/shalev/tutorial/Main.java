package com.shalev.tutorial;


import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;


public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {


        //Initialize a new instance of the listener class in order to register it as a listener and to register the cRefresh command
        MyListener listener = new MyListener();

        //Register listener as a listener class
        getServer().getPluginManager().registerEvents(listener, this);

        //register the cRefresh command
        Objects.requireNonNull(getCommand("cRefresh")).setExecutor(listener);
        Objects.requireNonNull(getCommand("taskStop")).setExecutor(listener);
        Objects.requireNonNull(getCommand("taskStopAll")).setExecutor(listener);
        Objects.requireNonNull(getCommand("taskDebug")).setExecutor(listener);

        MyListener.pluginRefresh();
    }

    @Override
    public void onDisable() {
    }




}
