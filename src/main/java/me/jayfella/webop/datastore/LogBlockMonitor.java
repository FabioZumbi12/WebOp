// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.datastore;

import java.util.ArrayList;
import java.util.List;

public class LogBlockMonitor {
    public List<String> LookupGeneralDestroyedOre(final String player, final String block, final String sinceMins) {
        final List<String> results = new ArrayList<String>();
        /*final LogBlock lbPlugin = (LogBlock)WebOp3Plugin.PluginContext.getPlugin().getServer().getPluginManager().getPlugin("LogBlock");
        final QueryParams lbQuery = new QueryParams(lbPlugin);
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final int sinceVal = Integer.valueOf(sinceMins);
        if (!player.isEmpty()) {
            lbQuery.setPlayer(player);
        }
        lbQuery.needPlayer = true;
        lbQuery.bct = QueryParams.BlockChangeType.DESTROYED;
        lbQuery.types = Arrays.asList(new Block(Material.getMaterial(block).getId(), 0));
        lbQuery.limit = -1;
        lbQuery.needDate = true;
        lbQuery.needType = true;
        lbQuery.needData = true;
        lbQuery.needCoords = true;
        lbQuery.since = sinceVal;
        final List<String> loggedWorlds = (List<String>)lbPlugin.getConfig().getStringList("loggedWorlds");
        for (final World world : WebOp3Plugin.PluginContext.getPlugin().getServer().getWorlds()) {
            if (!loggedWorlds.contains(world.getName())) {
                continue;
            }
            lbQuery.world = world;
            try {
                final List<BlockChange> lbResults = (List<BlockChange>)lbPlugin.getBlockChanges(lbQuery);
                for (final BlockChange change : lbResults) {
                    final StringBuilder result = new StringBuilder().append("<button class=\"btn-green teleportButton\" style=\"padding: 1px 4px !important\">Teleport</button>&nbsp;").append(change.playerName).append(" destroyed ").append(Material.getMaterial(change.replaced).name()).append(" on ").append(df.format(new Date(change.date))).append(" X:<span class='xCoord'>").append(change.loc.getBlockX()).append("</span> Y:<span class='yCoord'>").append(change.loc.getBlockY()).append("</span> Z:<span class='zCoord'>").append(change.loc.getBlockZ()).append("</span> in world:<span class='wCoord'>").append(change.loc.getWorld().getName()).append("</span><br/>");
                    results.add(result.toString());
                }
            }
            catch (SQLException ex) {}
        }*/
        return results;
    }
}
