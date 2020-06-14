/*
 * Copyright (c) 2020 - @FabioZumbi12
 * Last Modified: 14/06/2020 00:14.
 *
 * This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
 *  damages arising from the use of this class.
 *
 * Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
 * redistribute it freely, subject to the following restrictions:
 * 1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
 * use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
 * 2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
 * 3 - This notice may not be removed or altered from any source distribution.
 *
 * Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
 * responsabilizados por quaisquer danos decorrentes do uso desta classe.
 *
 * É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
 * alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
 * 1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
 *  classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
 * 2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
 * classe original.
 * 3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
 */

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
