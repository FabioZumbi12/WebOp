/*
 * Copyright (c) 2020 - @FabioZumbi12
 * Last Modified: 14/06/2020 00:14.
 *
 * This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
 *  damages arising from the use of this class.
 *
 * Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
 * redistribute it freely, subject to the following restrictions:
 * 1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
 * use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
 * 2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
 * 3 - This notice may not be removed or altered from any source distribution.
 *
 * Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
 * responsabilizados por quaisquer danos decorrentes do uso desta classe.
 *
 * É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
 * alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
 * 1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
 *  classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
 * 2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
 * classe original.
 * 3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
 */

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
