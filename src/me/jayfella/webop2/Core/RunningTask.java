package me.jayfella.webop2.Core;

public class RunningTask implements Comparable<RunningTask>
{
    private final String pluginName;
    private final String className;
    private final int id;
    private final boolean isRecurring;
    private final long recurringPeriod;
    private final boolean isAsync;

    public RunningTask(String pluginName, String className, int id, long recurringPeriod, boolean isAsync)
    {
        this.pluginName = pluginName;
        this.className = className;
        this.id = id;
        this.recurringPeriod = recurringPeriod;
        this.isAsync = isAsync;

        this.isRecurring = this.recurringPeriod > 0;
    }

    public String getPluginName() { return this.pluginName; }
    public String getClassName() { return this.className; }
    public int getId() { return this.id; }
    public boolean isRecurring() { return this.isRecurring; }
    public long getRecurringPeriod() { return this.recurringPeriod; }
    public boolean isAsync() { return this.isAsync; }

    @Override
    public int compareTo(RunningTask t)
    {
        // return owner.getName().compareTo(t.getOwner().getName());
        return this.pluginName.compareTo(t.pluginName);
    }

}
