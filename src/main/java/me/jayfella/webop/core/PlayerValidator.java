package me.jayfella.webop.core;

import me.jayfella.webop.PluginContext;

public class PlayerValidator {
    public static String isValidAccount(PluginContext context, String username, String password) {
        String pass = context.getPluginSettings().getAllowedLogin().getString(username);
        if (pass != null && pass.equals(password)) {
            return username;
        }
        return "";
    }
}
