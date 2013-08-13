package me.jayfella.webop2.DataStore;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.jayfella.webop2.PluginContext;
import me.jayfella.webop2.WebOp2Plugin;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;


public class ChunkMonitor
{
    private final PluginContext context;
    private final Deque<Integer> totalEntityCount = new ArrayDeque<>();

    public ChunkMonitor(PluginContext context)
    {
        this.context = context;

        context.getPlugin().getServer().getScheduler().runTaskTimer(context.getPlugin(), new ChunkCounterTask(), 5L, 20L);
    }

    public int getTotalChunkCount()
    {
        int totalChunks = 0;

        for (World world : context.getPlugin().getServer().getWorlds())
        {
            totalChunks += world.getLoadedChunks().length;
        }

        return totalChunks;
    }

    private synchronized Chunk[] getChunks()
    {
        List<Chunk> chunks = new ArrayList<>();

        for (World world : context.getPlugin().getServer().getWorlds())
        {
            chunks.addAll(Arrays.asList(world.getLoadedChunks()));
        }

        return chunks.toArray(new Chunk[chunks.size()]);
    }

    public synchronized Chunk getHighestEntityCountChunk()
    {
        Chunk[] chunks = getChunks();

        Chunk highestEntityCountChunk = null;

        for (Chunk chunk : chunks)
        {
            if (highestEntityCountChunk == null)
            {
                highestEntityCountChunk = chunk;
            }
            else
            {
                if (highestEntityCountChunk.getEntities().length < chunk.getEntities().length)
                {
                    highestEntityCountChunk = chunk;
                }
            }
        }

        return highestEntityCountChunk;
    }

    public String generateEntityCountChunk(Chunk chunk)
    {
        StringBuilder sb = new StringBuilder();

        int xLoc = chunk.getX() << 4;
        int zLoc = chunk.getZ() << 4;

        sb.append("Chunk Located in world ").append("<span style='font-weight: bold; color: blue;'>").append(chunk.getWorld().getName()).append("</span>").append(" : ");
        sb.append("(X: ").append(xLoc).append(" , ").append("Z: ").append(zLoc).append(")");

        sb.append(" contains ").append(chunk.getEntities().length).append(" entities.").append("<br/>").append("<br/>");

        Map<String, Integer> mobcount = new HashMap<>();

        for (Entity entity : chunk.getEntities())
        {
            EntityType type = entity.getType();

            if (type == null)
                continue;

            Integer currentCount = mobcount.get(type.getName());

            if (currentCount == null)
                currentCount = 0;

            currentCount++;

            mobcount.put(type.getName(), currentCount);
        }

        for (Map.Entry<String, Integer> entry : mobcount.entrySet())
        {
            sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append("<br/>");
        }

        return sb.toString();
    }

    private final class ChunkCounterTask implements Runnable
    {
        @Override
        public void run()
        {
            int chunkCount = getTotalChunkCount();
            totalEntityCount.add(chunkCount);

            while (totalEntityCount.size() > WebOp2Plugin.maxHistoryLength)
            {
                totalEntityCount.removeFirst();
            }
        }

    }
}
