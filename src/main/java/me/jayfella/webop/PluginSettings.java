// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class PluginSettings {
    private final String ipAddress;
    private final int httpPort;
    private final FileConfiguration fileConfig;
    private final ConfigurationSection allowedLoginPlayers;

    public PluginSettings(final PluginContext context) {
        context.getPlugin().saveDefaultConfig();
        context.getPlugin().reloadConfig();
        this.fileConfig = context.getPlugin().getConfig();
        this.ipAddress = this.fileConfig.getString("settings.ip-address");
        this.httpPort = this.fileConfig.getInt("settings.http-port");
        this.allowedLoginPlayers = fileConfig.getConfigurationSection("allowed-login-players");
    }

    public FileConfiguration getFileConfiguration() {
        return this.fileConfig;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public int getHttpPort() {
        return this.httpPort;
    }

    public ConfigurationSection getAllowedLogin() {
        return this.allowedLoginPlayers;
    }
}
