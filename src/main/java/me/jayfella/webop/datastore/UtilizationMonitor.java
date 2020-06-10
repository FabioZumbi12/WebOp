// 
// Decompiled by Procyon v0.5.36
// 

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
