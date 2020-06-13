// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class PluginSettings {
    private final File themeFolder;
    private final int httpPort;
    private final FileConfiguration fileConfig;
    private final ConfigurationSection allowedLoginPlayers;

    public PluginSettings(final PluginContext context) {
        context.getPlugin().saveDefaultConfig();
        context.getPlugin().reloadConfig();

        this.fileConfig = context.getPlugin().getConfig();
        this.httpPort = this.fileConfig.getInt("settings.http-port", 1337);
        this.allowedLoginPlayers = fileConfig.getConfigurationSection("allowed-login-players");

        // Create default theme folders
        File mainFolder = new File(context.getPlugin().getDataFolder(), File.separator + "themes" + File.separator + "default");
        if (!mainFolder.exists()) {
            mainFolder.mkdirs();
            new File(mainFolder, File.separator + "css").mkdirs();
            new File(mainFolder, File.separator + "html").mkdirs();
            new File(mainFolder, File.separator + "images").mkdirs();
            new File(mainFolder, File.separator + "javascript").mkdirs();
        }

        // Create theme folders
        String theme = this.fileConfig.getString("settings.html-theme", "default");
        this.themeFolder = new File(context.getPlugin().getDataFolder(), File.separator + "themes" + File.separator + theme);
        if (!this.themeFolder.exists()) this.themeFolder.mkdirs();
    }

    public FileConfiguration getFileConfiguration() {
        return this.fileConfig;
    }

    public File getThemeFolder() {
        return this.themeFolder;
    }

    public int getHttpPort() {
        return this.httpPort;
    }

    public ConfigurationSection getAllowedLogin() {
        return this.allowedLoginPlayers;
    }
}
