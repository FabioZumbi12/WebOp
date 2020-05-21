// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.core;

public final class WebOpMessage {
    private final int id;
    private final String user;
    private final String timeStamp;
    private final MessagePriority priority;
    private final String message;

    public WebOpMessage(final int id, final String user, final String timeStamp, final MessagePriority priority, final String message) {
        this.id = id;
        this.user = user;
        this.timeStamp = timeStamp;
        this.priority = priority;
        this.message = message;
    }

    public int getId() {
        return this.id;
    }

    public String getUser() {
        return this.user;
    }

    public String getTimeStamp() {
        return this.timeStamp;
    }

    public MessagePriority getPriority() {
        return this.priority;
    }

    public String getMessage() {
        return this.message;
    }
}
