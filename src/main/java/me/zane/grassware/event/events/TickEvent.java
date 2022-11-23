package me.zane.grassware.event.events;

import me.zane.grassware.event.Event;
import me.zane.grassware.event.Stage;

public class TickEvent extends Event {

    public TickEvent() {
        super(Stage.None, false);
    }
}