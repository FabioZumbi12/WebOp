package me.jayfella.webop2;

import org.bukkit.configuration.file.FileConfiguration;

public class PluginSettings
{
    private final String ipAddress;

    private final int httpPort;
    private final FileConfiguration fileConfig;

    public PluginSettings(PluginContext context)
    {
        context.getPlugin().saveDefaultConfig();
        fileConfig = context.getPlugin().getConfig();

        this.ipAddress = fileConfig.getString("settings.ip-address");
        this.httpPort = fileConfig.getInt("settings.http-port");
    }

    public FileConfiguration getFileConfiguration() { return this.fileConfig; }

    public String getIpAddress() { return this.ipAddress; }
    public int getHttpPort() { return this.httpPort; }
}
