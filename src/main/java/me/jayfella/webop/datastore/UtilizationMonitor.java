// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.datastore;

import com.sun.management.OperatingSystemMXBean;
import me.jayfella.webop.PluginContext;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;

public final class UtilizationMonitor {
    private final TickCounter tickCounter;
    private float lastTps;

    public UtilizationMonitor(final PluginContext context) {
        this.lastTps = 20.0f;
        this.tickCounter = new TickCounter();
        context.getPlugin().getServer().getScheduler().runTaskTimer(context.getPlugin(), this.tickCounter, 1L, 20L);
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
        return BigDecimal.valueOf(this.getCpuUseage() * 100.0).setScale(2, 4);
    }

    public float getCurrentTPS() {
        return this.lastTps;
    }

    private final class TickCounter implements Runnable {
        private long lastPoll;

        private TickCounter() {
            this.lastPoll = System.currentTimeMillis();
        }

        @Override
        public void run() {
            final long now = System.currentTimeMillis();
            final long timeSpent = now - this.lastPoll;
            float tps = (float) (timeSpent / 50L);
            if (tps > 20.0f) {
                tps = 20.0f;
            }
            UtilizationMonitor.this.lastTps = tps;
            this.lastPoll = now;
        }
    }
}
