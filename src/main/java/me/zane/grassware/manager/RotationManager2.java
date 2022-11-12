package me.zane.grassware.manager;

import me.zane.grassware.util.Util;
import net.minecraftforge.common.MinecraftForge;

/*
* this supposed to be new rotaiton manager :)
 */
public class RotationManager2 implements Util {
    boolean rotated = false;
    float yaw = 0,pitch = 0;
    public RotationManager2() {
        MinecraftForge.EVENT_BUS.register(this);
    }
/*
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && yaw != 0 && pitch != 0) {
            ((CPacketPlayer) event.getPacket()).yaw = this.yaw;
            ((CPacketPlayer) event.getPacket()).pitch = this.pitch;
        }
    }
*/
    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float balls) {
        this.pitch = balls;
    }

    public void setYaw(float sex) {
        this.yaw = sex;
    }

    /*
    public boolean getRotated() {
        return this.rotated;
    }

    public void setRotated(boolean gay) {
        this.rotated = gay;
    }
     */
}
