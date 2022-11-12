package me.zane.grassware.event.events;

import me.zane.grassware.event.EventStage;
import net.minecraft.entity.player.EntityPlayer;

public class DeathEvent
        extends EventStage {
    public EntityPlayer player;

    public DeathEvent(EntityPlayer player) {
        this.player = player;
    }
}

