package me.zane.grassware.fire;

import me.zane.grassware.fire.Event;
import me.zane.grassware.fire.Stage;

public class TickEvent extends Event {

    public TickEvent() {
        super(Stage.None, false);
    }
}