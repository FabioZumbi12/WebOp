package me.jayfella.webop2.DataStore;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import me.jayfella.webop2.PluginContext;

public final class HealthMonitor
{
    private final TickCounter tickCounter;
    private float lastTps = 20L;

    private int megaBytes = 1024*1024;

    public HealthMonitor(PluginContext context)
    {
        this.tickCounter = new TickCounter();
        context.getPlugin().getServer().getScheduler().runTaskTimer(context.getPlugin(), this.tickCounter, 1L, 20L);
    }

    public double getTotalMemory() { return (Runtime.getRuntime().maxMemory() / 1048576L); }
    public double getAvailableMemory() { return (this.getTotalMemory() - this.getUsedMemory()); }
    public double getUsedMemory() { return ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L); }

    public BigDecimal getUsedMemoryPercent()
    {
        double usedPc = (100.0D / this.getTotalMemory() * this.getUsedMemory());
        return BigDecimal.valueOf(usedPc).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public double getCpuUseage()
    {
        java.lang.management.OperatingSystemMXBean o = ManagementFactory.getOperatingSystemMXBean();
        com.sun.management.OperatingSystemMXBean osMxBean = (com.sun.management.OperatingSystemMXBean) o;

        return osMxBean.getSystemCpuLoad();
    }

    public BigDecimal getCpuLoadPercent()
    {
        return BigDecimal.valueOf(this.getCpuUseage() * 100).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public float getCurrentTPS()
    {
        return this.lastTps;
    }

    private final class TickCounter implements Runnable
    {
        private long lastPoll = System.currentTimeMillis();

        @Override
        public void run()
        {
            long now = System.currentTimeMillis();
            long timeSpent = (now - lastPoll);

            float tps = timeSpent / 50L;

            if (tps > 20L) { tps = 20L; }

            lastTps = tps;
            lastPoll = now;
        }
    }

}
