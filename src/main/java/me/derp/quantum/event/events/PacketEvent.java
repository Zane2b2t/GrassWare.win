package me.derp.quantum.event.events;

import me.derp.quantum.event.EventStage;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class PacketEvent
extends EventStage {
    private final Packet<?> packet;

    public PacketEvent(int n, Packet<?> packet) {
        super(n);
        this.packet = packet;
    }

    public <T extends Packet<?>> T getPacket() {
        return (T)this.packet;
    }

    @Cancelable
    public static class Receive
    extends PacketEvent {
        public Receive(int n, Packet<?> packet) {
            super(n, packet);
        }
    }

    @Cancelable
    public static class Send
    extends PacketEvent {
        public Send(int n, Packet<?> packet) {
            super(n, packet);
        }
    }
}

