// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.datastore;

import me.jayfella.webop.WebOpPlugin;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.*;

public class EntityMonitor {
    public String findAllHighestEntityCountInChunks(final int amount) {
        final StringBuilder response = new StringBuilder();
        for (final World world : WebOpPlugin.PluginContext.getPlugin().getServer().getWorlds()) {
            final Chunk[] chunksArray = world.getLoadedChunks().clone();
            final List<Chunk> chunks = Arrays.asList(chunksArray);
            Collections.sort(chunks, new ChunkComparator());
            response.append("<div class='containerHead'><h2>World: ").append("<span style='color: darkblue; font-weight: bold;'>").append(world.getName()).append("</span></h2></div>");
            for (int maxChunks = 50, i = 0; i < chunks.size() && i < maxChunks; ++i) {
                final Chunk thisChunk = chunks.get(i);
                if (thisChunk.getEntities().length >= amount) {
                    response.append("<div style='background: #DFDFDF; padding: 4px; border-radius: 3px;'>").append("<button id='").append("'").append("class='btn-green teleportButton' style='margin-right: 5px; padding: 1px !important;'>Teleport</button>").append("Chunk: ").append("(X: ").append("<span class='xCoord'>").append(thisChunk.getX() << 4).append("</span>").append(" - ").append("Z: ").append("<span class='zCoord'>").append(thisChunk.getZ() << 4).append("</span>").append(")").append("<span class='wCoord' style='display: none;'>").append(world.getName()).append("</span>").append(" Total Entities: ").append(thisChunk.getEntities().length).append("<br/>").append("</div>");
                    final Map<EntityType, Integer> entityMap = new HashMap<EntityType, Integer>();
                    for (final Entity entity : thisChunk.getEntities()) {
                        final EntityType entityType = entity.getType();
                        Integer count = entityMap.get(entityType);
                        if (count == null) {
                            count = 0;
                        }
                        ++count;
                        entityMap.put(entityType, count);
                    }
                    for (final Map.Entry<EntityType, Integer> entry : entityMap.entrySet()) {
                        response.append(entry.getKey().name()).append(" : ").append(entry.getValue()).append("<br/>");
                    }
                    response.append("<br/>");
                }
            }
        }
        return response.toString();
    }

    public String findCertainHighestEntityCountInChunks(final String entityTypes, final int amount) {
        final StringBuilder response = new StringBuilder();
        if (entityTypes.isEmpty()) {
            response.append("You must specify at least one entity type!");
            return response.toString();
        }
        final EntityType[] parsedEntityTypes = this.parseGivenEntityTypes(entityTypes);
        for (final World world : WebOpPlugin.PluginContext.getPlugin().getServer().getWorlds()) {
            final Chunk[] chunksArray = world.getLoadedChunks().clone();
            final List<Chunk> chunks = Arrays.asList(chunksArray);
            final Iterator<Chunk> iterator = chunks.iterator();
            final List<Chunk> validChunks = new ArrayList<Chunk>();
            while (iterator.hasNext()) {
                final Chunk chunk = iterator.next();
                final Map<EntityType, Integer> entityMap = new HashMap<EntityType, Integer>();
                for (final Entity entity : chunk.getEntities()) {
                    final EntityType entityType = entity.getType();
                    Integer count = entityMap.get(entityType);
                    if (count == null) {
                        count = 0;
                    }
                    ++count;
                    entityMap.put(entityType, count);
                }
                boolean isValidChunk = true;
                for (final EntityType entity2 : parsedEntityTypes) {
                    final Integer entityCount = entityMap.get(entity2);
                    if (entityCount == null || entityCount < amount) {
                        isValidChunk = false;
                    }
                }
                if (isValidChunk) {
                    validChunks.add(chunk);
                }
            }
            Collections.sort(validChunks, new ChunkComparator());
            response.append("<div class='containerHead'><h2>World: ").append("<span style='color: darkblue; font-weight: bold;'>").append(world.getName()).append("</span></h2></div>");
            for (int maxChunks = 50, i = 0; i < validChunks.size() && i < maxChunks; ++i) {
                final Chunk thisChunk = validChunks.get(i);
                if (thisChunk.getEntities().length >= amount) {
                    response.append("<div style='background: #DFDFDF; padding: 4px; border-radius: 3px;'>").append("<button id='").append("'").append("class='btn-green teleportButton' style='margin-right: 5px; padding: 1px !important;'>Teleport</button>").append("Chunk: ").append("(X: ").append("<span class='X'>").append(thisChunk.getX() << 4).append("</span>").append(" - ").append("Z: ").append("<span class='Z'>").append(thisChunk.getZ() << 4).append("</span>").append(")").append("<span class='world' style='hidden'>").append(world.getName()).append("</span>").append(" Total Entities: ").append(thisChunk.getEntities().length).append("<br/>").append("</div>");
                    final Map<EntityType, Integer> entityMap2 = new HashMap<EntityType, Integer>();
                    for (final Entity entity3 : thisChunk.getEntities()) {
                        final EntityType entityType2 = entity3.getType();
                        Integer count2 = entityMap2.get(entityType2);
                        if (count2 == null) {
                            count2 = 0;
                        }
                        ++count2;
                        entityMap2.put(entityType2, count2);
                    }
                    for (final Map.Entry<EntityType, Integer> entry : entityMap2.entrySet()) {
                        response.append(entry.getKey().name()).append(" : ").append(entry.getValue()).append("<br/>");
                    }
                    response.append("<br/>");
                }
            }
        }
        return response.toString();
    }

    private EntityType getEntityType(final String type) {
        final String correctType = type.toUpperCase().trim().replace(" ", "_");
        for (final EntityType c : EntityType.values()) {
            if (c.name().equals(correctType)) {
                return c;
            }
        }
        return null;
    }

    private EntityType[] parseGivenEntityTypes(final String entityTypes) {
        final String[] givenMobTypes = entityTypes.split(",");
        final List<EntityType> parsedEntityTypes = new ArrayList<EntityType>();
        for (final String str : givenMobTypes) {
            if (!str.isEmpty()) {
                final EntityType type = this.getEntityType(str);
                if (type != null) {
                    parsedEntityTypes.add(type);
                }
            }
        }
        return parsedEntityTypes.toArray(new EntityType[parsedEntityTypes.size()]);
    }

    private class ChunkComparator implements Comparator<Chunk> {
        @Override
        public int compare(final Chunk a, final Chunk b) {
            return (a.getEntities().length < b.getEntities().length) ? 1 : ((a.getEntities().length == b.getEntities().length) ? 0 : -1);
        }
    }
}
