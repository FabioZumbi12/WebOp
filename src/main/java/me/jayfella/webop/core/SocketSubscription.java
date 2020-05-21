// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class SocketSubscription {
    private final Set<String> subscribers;

    public SocketSubscription() {
        this.subscribers = new HashSet<String>();
    }

    public void addSubscriber(final String playername) {
        this.subscribers.add(playername);
    }

    public void removeSubscriber(final String playername) {
        this.subscribers.remove(playername);
    }

    public String[] getSubscribers() {
        final List<String> names = new ArrayList<String>();
        for (final String name : this.subscribers) {
            names.add(name);
        }
        return names.toArray(new String[names.size()]);
    }

    public boolean isSubscriber(final String playername) {
        return this.subscribers.contains(playername);
    }
}
