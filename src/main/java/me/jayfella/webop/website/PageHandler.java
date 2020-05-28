// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.website;

import me.jayfella.webop.website.pages.*;

public class PageHandler {
    private final Stylesheet stylesheet;
    private final Javascript javascript;
    private final Image image;

    public PageHandler() {
        this.stylesheet = new Stylesheet();
        this.javascript = new Javascript();
        this.image = new Image();
    }

    public WebPage getWebPage(final String pageName) {
        switch (pageName) {
            case "stylesheet.php": {
                return this.stylesheet;
            }
            case "jscript.php": {
                return this.javascript;
            }
            case "image.php": {
                return this.image;
            }
            case "login.php": {
                return new Login();
            }
            case "index.php": {
                return new Index();
            }
            case "data.php": {
                return new Data();
            }
            case "permissions.php": {
                return new Permissions();
            }
            case "logout.php": {
                return new Logout();
            }
            case "badlogin.php": {
                return new BadLogin();
            }
            default: {
                return new Error404();
            }
        }
    }
}
