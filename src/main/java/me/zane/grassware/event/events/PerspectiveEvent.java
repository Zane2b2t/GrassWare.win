package me.zane.grassware.event.events;

import me.zane.grassware.event.EventStage;

public class PerspectiveEvent extends EventStage {
    private float aspect;

    public PerspectiveEvent(float aspect) {
        this.aspect = aspect;
    }

    public float getAspect() {
        return aspect;
    }

    public void setAspect(float aspect) {
        this.aspect = aspect;
    }
}