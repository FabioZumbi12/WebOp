// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.website;

import me.jayfella.webop.WebOpPlugin;
import org.bukkit.Bukkit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

public abstract class WebPage {
    private int responseCode;
    private String contentType;

    public abstract byte[] get(final HttpServletRequest p0, final HttpServletResponse p1);

    public abstract byte[] post(final HttpServletRequest p0, final HttpServletResponse p1);

    public int getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(final int value) {
        this.responseCode = value;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(final String value) {
        this.contentType = value;
    }

    public String addSiteTemplate(final String content, final String title, final HttpServletRequest req) {
        String result = this.loadResource("html", "overall_layout.html").replace("{page_body}", content).replace("{title}", title);
        if (WebOpPlugin.PluginContext.getSessionManager().isValidCookie(req)) {
            String username = "";
            for (final Cookie cookie : req.getCookies()) {
                if (cookie.getName().equals("webop_user")) {
                    username = cookie.getValue();
                    break;
                }
            }
            result = result.replace("{main_menu}", this.loadResource("html", "mainmenu.html"));
            result = result.replace("{username}", username);
            result = result.replace("{userlinks}", userLinks());
        } else {
            result = result.replace("{main_menu}", "");
        }
        return result;
    }

    private File getResourceFile(String type, final String resFile) {
        // Check default file and save
        File defaultFolder = new File(WebOpPlugin.PluginContext.getPlugin().getDataFolder(), File.separator + "themes" + File.separator + "default");
        File defaultFile = new File(defaultFolder, File.separator + type + File.separator + resFile);
        if (!defaultFile.exists()) {
            WebOpPlugin.PluginContext.getPlugin().saveResource("themes/default/" + type + "/" + resFile, true);
        }

        // Check for theme file
        File themePath = WebOpPlugin.PluginContext.getPluginSettings().getThemeFolder();
        File file = new File(themePath, File.separator + type + File.separator + resFile);
        if (file.exists()) {
            defaultFile = file;
        }
        return defaultFile;
    }

    public InputStream loadImage(String imageFile) {
        try {
            return new FileInputStream(getResourceFile("images", imageFile));
        } catch (Exception ignored) {
        }
        return null;
    }

    public String loadResource(String type, final String pageFile) {
        StringBuilder output = new StringBuilder();
        try (final InputStream inp = new FileInputStream(getResourceFile(type, pageFile)); final BufferedReader rd = new BufferedReader(new InputStreamReader(inp))) {
            String s;
            while (null != (s = rd.readLine())) {
                output.append(s).append("\n");
            }
        } catch (Exception ex) {
            return "";
        }
        return output.toString();
    }

    public final String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }

    private String userLinks() {
        File pluginDir = WebOpPlugin.PluginContext.getPlugin().getDataFolder();

        FilenameFilter textFilter = (dir, name) -> name.toLowerCase().endsWith(".txt");

        File[] files = pluginDir.listFiles(textFilter);

        if (files.length > 0) {
            StringBuilder sb = new StringBuilder();

            for (File file : files) {
                String content;

                try {
                    content = this.readFile(file.getCanonicalPath(), Charset.availableCharsets().get("UTF-8"));
                } catch (IOException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                    continue;
                }

                String[] lines = content.split("\n");

                sb.append("<li class='has-sub '>")
                        .append("<a href='#'>").append(file.getName().replace(".txt", "")).append("</a>")
                        .append("<ul>");


                for (String line : lines) {
                    String[] values = line.split(">>");

                    if (values.length != 2)
                        continue;

                    sb
                            .append("<li>")
                            .append("<a href='").append(values[1].trim()).append("'>").append(values[0].trim()).append("</a>")
                            .append("</li>");
                }

                sb.append("</ul>").append("</li>");
            }

            return sb.toString();
        } else {
            return "";
        }
    }

}
