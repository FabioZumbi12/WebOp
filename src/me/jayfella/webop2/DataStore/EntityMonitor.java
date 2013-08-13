
package me.jayfella.webop2.DataStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.jayfella.webop2.PluginContext;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public final class EntityMonitor
{
    private final PluginContext context;

    public EntityMonitor(final PluginContext context)
    {
        this.context = context;
    }

    public synchronized int getTotalEntityCount()
    {
        int totalEntities = 0;

        for (World world : context.getPlugin().getServer().getWorlds())
        {
            totalEntities += world.getEntities().size();
        }

        return totalEntities;
    }

    public synchronized List<Entity> getAllEntities()
    {
        List<Entity> entities = new ArrayList<>();

        for (World world : context.getPlugin().getServer().getWorlds())
        {
            entities.addAll(world.getEntities());
        }

        return entities;
    }

    public synchronized String generateSocketEntityCount()
    {
        Map<EntityType, Integer> entityMap = new HashMap<>();

        StringBuilder sb = new StringBuilder().append("ENTITIES : ");

        for (Entity entity : getAllEntities())
        {
            EntityType type = entity.getType();

            Integer count = entityMap.get(type);

            if (count == null) count = 0;

            count++;

            entityMap.put(type, count);
        }

        EntityType[] eTypes = EntityType.values();

        // for (EntityType type : EntityType.values())
        for (int i = 0; i < eTypes.length; i++)
        {
            Integer count = entityMap.get(eTypes[i]);

            if (count == null) count = 0;

            String eTypeName = eTypes[i].name();
            String eCount = String.valueOf(count);

            sb.append(eTypeName).append("=").append(eCount).append(",");
        }

        return sb.toString();
    }





}
