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

import com.sun.management.OperatingSystemMXBean;
import me.jayfella.webop.PluginContext;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.util.LinkedList;

public final class UtilizationMonitor {
    private final LinkedList<Double> history = new LinkedList<>();

    public UtilizationMonitor(final PluginContext context) {
        history.add(20d);
        TickCounter tickCounter = new TickCounter();
        context.getPlugin().getServer().getScheduler().runTaskTimer(context.getPlugin(), tickCounter, 1000L, 50L);
    }

    public double getTotalMemory() {
        return (double) (Runtime.getRuntime().maxMemory() / 1048576L);
    }

    public double getAvailableMemory() {
        return this.getTotalMemory() - this.getUsedMemory();
    }

    public double getUsedMemory() {
        return (double) ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L);
    }

    public BigDecimal getUsedMemoryPercent() {
        final double usedPc = 100.0 / this.getTotalMemory() * this.getUsedMemory();
        return BigDecimal.valueOf(usedPc).setScale(2, 4);
    }

    public double getCpuUseage() {
        final java.lang.management.OperatingSystemMXBean o = ManagementFactory.getOperatingSystemMXBean();
        final OperatingSystemMXBean osMxBean = (OperatingSystemMXBean) o;
        return osMxBean.getSystemCpuLoad();
    }

    public BigDecimal getCpuLoadPercent() {
        double load = this.getCpuUseage() * 100.0;
        return BigDecimal.valueOf(load < 0 ? 0 : load).setScale(2, 4);
    }

    public double getCurrentTPS() {
        double avg = 0;
        for (Double f : history) {
            if (f != null)
                avg += f;
        }
        return avg / history.size();
    }

    private final class TickCounter implements Runnable {
        private long lastPoll;

        private TickCounter() {
            this.lastPoll = System.nanoTime();
        }

        @Override
        public void run() {
            final long startTime = System.nanoTime();
            long timeSpent = (startTime - this.lastPoll) / 1000;
            if (timeSpent == 0)
                timeSpent = 1;

            if (history.size() > 10)
                history.remove();

            double tps = 50L * 1000000.0 / timeSpent;
            if (tps <= 21)
                history.add(tps);

            this.lastPoll = startTime;
        }
    }
}
