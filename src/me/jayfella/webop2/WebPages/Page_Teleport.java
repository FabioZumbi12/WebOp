package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import java.util.Map;
import me.jayfella.webop2.Core.LoggedInUser;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Page_Teleport extends WebPage
{
    public Page_Teleport(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return "text/html; charset=utf-8"; }

    @Override
    public byte[] get(HttpExchange he)
    {
        return new byte[0];
    }

    @Override
    public synchronized byte[] post(HttpExchange he)
    {
        if (!this.getContext().getSessionManager().isAuthorised(he))
            return new byte[0];

        Map<String, String> vars = this.parsePostResponse(he);

        int x = Integer.valueOf(vars.get("x"));
        int y = Integer.valueOf(vars.get("y"));
        int z = Integer.valueOf(vars.get("z"));

        String worldName = vars.get("world");
        World world = this.getContext().getPlugin().getServer().getWorld(worldName);

        final Location location = new Location(world, x, y, z);

        Chunk chunk = location.getChunk();

        if (!chunk.isLoaded())
        {
            return "CHUNK_UNLOADED".getBytes();
        }

        final Block block = world.getHighestBlockAt(location);

        LoggedInUser user = this.getContext().getSessionManager().getLoggedInUser(he.getRemoteAddress().getAddress());
        final Player player = this.getContext().getPlugin().getServer().getPlayerExact(user.getUsername());

        if (player == null)
        {
            return "NOT_LOGGED_IN".getBytes();
        }

        this.getContext().getPlugin().getServer().getScheduler().runTask(this.getContext().getPlugin(),
                new Runnable()
                {
                   @Override
                   public synchronized void run()
                   {
                       player.teleport(block.getLocation().add(0.5, 3, 0.5));
                   }
                });


        return "OK".getBytes();
    }

}
