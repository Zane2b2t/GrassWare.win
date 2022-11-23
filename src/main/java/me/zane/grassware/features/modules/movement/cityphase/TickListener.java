package me.zane.grassware.features.modules.movement.cityphase;

import me.zane.grassware.event.EventListener;
import me.zane.grassware.event.events.TickEvent;
import me.zane.grassware.features.modules.movement.CityPhase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;

public class TickListener extends EventListener<TickEvent, CityPhase> {

    public TickListener(final CityPhase cityPhase) {
        super(TickEvent.class, cityPhase);
    }

    @Override
    public void invoke(final Object object) {
        if (module.movingByKeys()) {
            module.toggle();
            return;
        }
        if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().grow(0.01, 0, 0.01)).size() < 2) {
            mc.player.setPosition(module.roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.301, Math.floor(mc.player.posX) + 0.699), mc.player.posY, module.roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.301, Math.floor(mc.player.posZ) + 0.699));
        } else if (mc.player.ticksExisted % module.timeout.getValue() == 0) {
            mc.player.setPosition(mc.player.posX + MathHelper.clamp(module.roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.241, Math.floor(mc.player.posX) + 0.759) - mc.player.posX, -0.03, 0.03), mc.player.posY, mc.player.posZ + MathHelper.clamp(module.roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.241, Math.floor(mc.player.posZ) + 0.759) - mc.player.posZ, -0.03, 0.03));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(module.roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.23, Math.floor(mc.player.posX) + 0.77), mc.player.posY, module.roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.23, Math.floor(mc.player.posZ) + 0.77), true));
        }
    }
}