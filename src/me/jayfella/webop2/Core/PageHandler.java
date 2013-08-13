package me.jayfella.webop2.Core;

import me.jayfella.webop2.PluginContext;
import me.jayfella.webop2.WebPages.*;

public class PageHandler
{
    private final PluginContext context;

    // static pages - frequently requested and do not change.
    // do not keep lesser requested pages stored like this, its a waste of memory.
    private final Page_StyleSheet page_stylesheet;
    private final Page_Image page_image;
    private final Page_JScript page_jscript;
    private final Page_ServerHealth page_serverhealth;
    private final Page_PlayerInformation page_playerinformation;
    private final Page_PlayerNameSearch page_playernamesearch;
    private final Page_LogSearch page_logsearch;
    private final Page_Console page_console;
    private final Page_Messages page_messages;

    public PageHandler(PluginContext context)
    {
        this.context = context;

        page_stylesheet = new Page_StyleSheet(context);
        page_image = new Page_Image(context);
        page_jscript = new Page_JScript(context);
        page_serverhealth = new Page_ServerHealth(context);
        page_playerinformation = new Page_PlayerInformation(context);
        page_playernamesearch = new Page_PlayerNameSearch(context);
        page_logsearch = new Page_LogSearch(context);
        page_console = new Page_Console(context);
        page_messages = new Page_Messages(context);
    }

    public WebPage getPage(String pageName)
    {
        switch (pageName)
        {
            case "stylesheet.php": return page_stylesheet;
            case "image.php": return page_image;
            case "jscript.php": return page_jscript;
            case "messages.php": return page_messages;
            case "serverhealth.php": return page_serverhealth;
            case "playerinfo.php": return page_playerinformation;
            case "playersearch.php": return page_playernamesearch;
            case "logsearch.php": return page_logsearch;
            case "console.php": return page_console;

            case "login.php": return new Page_Login(context);
            case "index.php": return new Page_Index(context);
            case "logout.php": return new Page_Logout(context);
            case "whitelist.php": return new Page_Whitelist(context);
            case "profileplugins.php": return new Page_ProfilePlugins(context);
            case "profileserver.php": return new Page_ProfileServer(context);
            case "profileentities.php": return new Page_ProfileEntities(context);
            case "teleport.php": return new Page_Teleport(context);

            default: return new Page_404(context);
        }
    }

}
