WebOp Reborn 4
===
##### WebConsole for server administration and to share with your server OPs

![Logo](http://i.imgur.com/bn1vMtw.png)  

### Description
WebOp allows server owners and optional others to view their server health real-time, search the server log, edit server properties, modify the whitelist, and use the console - all through your browser!

All users must be white-listed to access the webpage, and require OP to use the console, modify the whitelists or server properties.

Non-OP users are able to view the server health graphs and search the log. This allows staff to find out who banned someone, who muted someone, etc, direct from the source, instead of needing to talk to staff or use a plugin to log all commands. Real-Time graph data displays your current server health (TPS, Available memory, Entities loaded, Chunks loaded) giving you at-a-glance information regarding the state of your server.

### Features
* Built-in whitelist to enable/disable WebOp and WebOp console view/access.
* View and/or use the console.
* Live server health history graph display how well your server is performing at-a-glance.
* Server latest log search function to easily find out who issued commands.
* Essentials user data addon - hover over player names for user information.
* Tried-and-tested session-based logins.

### Adding custom menu links
To add a custom menu, create a text file inside the "/plugins/webop" directory, and name the file whatever you would like the menu header to say. For example, if you create a file called "My Links.txt" - the header of the menu will be "My Links". Edit the text file and add a "Name >> Value" for each line. For example:

Bukkit Plugins >> http://plugins.bukkit.org/  
Google Home >> http://www.google.com  
Server DynMap >> http://play.myserver.com:8123  

You can create as many text files as you need in the same manner.

### Permissions
Theres no permissions nodes to use. In order to login, you need to add players names and respective password on config file, on section "allowed-login-players:" like bellow:

```
allowed-login-players:
  FabioZumbi12: 1234
```
### Installation
Copy the plugin to your "plugins" folder and start the server to create the default config file. Edit the config file and add yourself to the whitelist. Change the port number and IP address as necessary. The default port is 1337. Restart the server and, using your browser, navigate to "http://myserver.com:1337" - substituting "myserver.com" for your server domain name or IP-address, and specifying the port number you chose to use. For example "http://127.0.0.1:6523", or "http://play.myserver.com:6546".

After installation is complete, any OP player can add and remove other players via the webpage.

### Downloads
BukkitDev: https://dev.bukkit.org/projects/webop-reborn  
SpigotMC: https://www.spigotmc.org/resources/webop-1-12-2-1-15.79410/