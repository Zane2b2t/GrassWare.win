package me.zane.grassware.event;

import me.zane.grassware.Quantum;
import net.minecraft.client.Minecraft;

public abstract class EventListener<E, M> {
    protected final Minecraft mc = Quantum.mc;
    protected final Class<? extends E> listener;
    public M module;

    public EventListener(final Class<? extends E> listener) {
        this.listener = listener;
    }

    public EventListener(final Class<? extends E> listener, M module) {
        this.listener = listener;
        this.module = module;
    }

    public Class<? extends E> getListener() {
        return listener;
    }

    public void invoke(final Object object) {
    }
}