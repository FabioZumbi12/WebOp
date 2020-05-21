// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.core;

import me.jayfella.webop.PluginContext;
import me.jayfella.webop.serverprofiler.WebOpPluginManager;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ServerProfiler {
    private final PluginContext context;
    private SimplePluginManager simplePluginManager;
    private WebOpPluginManager pluginManager;
    private final PluginManager oldPluginManager;

    public ServerProfiler(final PluginContext context) {
        this.context = context;
        this.oldPluginManager = this.context.getPlugin().getServer().getPluginManager();
    }

    private void initializeEventProfiler() throws NoSuchMethodException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        this.simplePluginManager = (SimplePluginManager) this.context.getPlugin().getServer().getPluginManager();
        final Field cM = SimplePluginManager.class.getDeclaredField("commandMap");
        cM.setAccessible(true);
        this.pluginManager = new WebOpPluginManager(this.context.getPlugin().getServer(), (SimpleCommandMap) cM.get(this.simplePluginManager));
        for (final Field f : this.simplePluginManager.getClass().getDeclaredFields()) {
            final boolean orig_simp_ia = f.isAccessible();
            f.setAccessible(true);
            final Field smF = this.pluginManager.getClass().getField(f.getName());
            final boolean orig_smart_ia = smF.isAccessible();
            smF.setAccessible(true);
            smF.set(this.pluginManager, f.get(this.simplePluginManager));
            f.setAccessible(orig_simp_ia);
            f.setAccessible(orig_smart_ia);
        }
        final Field plugManager = this.context.getPlugin().getServer().getClass().getDeclaredField("pluginManager");
        plugManager.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(plugManager, plugManager.getModifiers() & 0xFFFFFFEF);
        plugManager.set(this.context.getPlugin().getServer(), this.pluginManager);
    }

    private void restoreEventProfiler() throws NoSuchFieldException, IllegalAccessException {
        final Field plugManager = this.context.getPlugin().getServer().getClass().getDeclaredField("pluginManager");
        plugManager.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(plugManager, plugManager.getModifiers() & 0xFFFFFFEF);
        plugManager.set(this.context.getPlugin().getServer(), this.oldPluginManager);
    }

    public boolean isProfiling() {
        return this.pluginManager != null && this.pluginManager.isProfiling();
    }

    public void startProfiling() {
        if (this.isProfiling()) {
            return;
        }
        try {
            this.initializeEventProfiler();
            this.pluginManager.startProfiling(600000L);
        } catch (NoSuchMethodException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException ex) {
            this.context.getPlugin().getLogger().log(Level.SEVERE, "Server Profiling Error (start): ", ex);
        }
    }

    public void stopProfiling() {
        if (!this.isProfiling()) {
            return;
        }
        try {
            this.pluginManager.stopProfiling();
            this.restoreEventProfiler();
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            this.context.getPlugin().getLogger().log(Level.SEVERE, "Server Profiling Error (stop): ", ex);
        }
    }

    public String buildEventProfileResultRaw() {
        final List<Class<? extends Event>> ranEvents = new ArrayList<Class<? extends Event>>();
        for (final Class<? extends Event> key : this.pluginManager.getEventDuration().keySet()) {
            ranEvents.add(key);
        }
        if (ranEvents.isEmpty()) {
            return "NO_EVENTS";
        }
        final List<String> eventData = new ArrayList<String>();
        final List<Long> countData = new ArrayList<Long>();
        final List<Long> durationData = new ArrayList<Long>();
        for (int i = 0; i < ranEvents.size(); ++i) {
            final Class<? extends Event> event = ranEvents.get(i);
            final String eventName = event.getName();
            final long runCount = this.pluginManager.getEventRunCount().get(event);
            final long runDuration = this.pluginManager.getEventDuration().get(event);
            eventData.add(eventName);
            countData.add(runCount);
            durationData.add(runDuration);
        }
        final StringBuilder response = new StringBuilder();
        response.append("eventNames=");
        for (int j = 0; j < eventData.size(); ++j) {
            response.append(eventData.get(j));
            if (j < eventData.size() - 1) {
                response.append(",");
            } else {
                response.append("\n");
            }
        }
        response.append("eventCounts=");
        for (int j = 0; j < countData.size(); ++j) {
            response.append(countData.get(j));
            if (j < countData.size() - 1) {
                response.append(",");
            } else {
                response.append("\n");
            }
        }
        response.append("eventDurations=");
        for (int j = 0; j < durationData.size(); ++j) {
            final BigDecimal num1 = new BigDecimal(durationData.get(j));
            final BigDecimal num2 = new BigDecimal(1000000L);
            final String timeRunningMillis = new DecimalFormat("0.00").format(num1.divide(num2, 100, RoundingMode.HALF_UP));
            response.append(timeRunningMillis);
            if (j < durationData.size() - 1) {
                response.append(",");
            } else {
                response.append("\n");
            }
        }
        return response.toString();
    }

    public String buildRegisteredListenerResult() {
        final StringBuilder sb = new StringBuilder();
        final Map<RegisteredListener, Long> durations = new HashMap<RegisteredListener, Long>(this.pluginManager.getRegisteredListenerDuration());
        final Map<RegisteredListener, Long> counters = new HashMap<RegisteredListener, Long>(this.pluginManager.getRegisteredListenerRunCount());
        for (final Map.Entry<RegisteredListener, Long> entry : durations.entrySet()) {
            sb.append(entry.getKey().getClass().getName()).append(">").append(counters.get(entry.getKey())).append(">").append(entry.getValue()).append(";");
        }
        return sb.toString();
    }

    public void clearProfilingResults() {
        this.pluginManager.clearProfilingResults();
    }
}
