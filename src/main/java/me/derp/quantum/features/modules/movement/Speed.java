package me.derp.quantum.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.muffin.oyveyplus.api.event.events.EventMove;
import me.muffin.oyveyplus.api.event.events.EventPacket;
import me.muffin.oyveyplus.api.module.Module;
import me.muffin.oyveyplus.api.settings.Setting;

public class Speed extends Module {
    public Speed() {
        super("Speed", "Move faster!", Category.Movement);
    }

    public Setting<String> mode = register("Mode", "StrictStrafe", "Strafe", "StrictStrafe", "YPort");
    public Setting<Double> speed = register("Speed", 2.6D, 1, 10, 1);
    public Setting<Boolean> liquids = register("Liquids", true);

    private double moveSpeed = 0;
    private double lastDist = 0;
    private int stage = 4;

    public void onUpdate() {
        if (!liquids.getValue() && (mc.player.isInLava() || mc.player.isInWater())) return;
        lastDist = Math.sqrt(Math.pow(mc.player.posX - mc.player.prevPosX, 2) + Math.pow(mc.player.posZ - mc.player.prevPosZ, 2));
    }

    @Subscribe
    public void onMove(EventMove event) {
        if (mode.getValue().equalsIgnoreCase("YPort")) mc.player.motionY = -0.4D;

        if (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) {
            if (mc.player.onGround) stage = 2;
        }

        if (stage == 1) {
            if (mode.getValue().equalsIgnoreCase("StrictStrafe")) {
                moveSpeed = 1.38 * (2.4D / 10);
            } else moveSpeed = 1.38 * (speed.getValue() / 10);

            if (mc.player.isPotionActive(MobEffects.SPEED)) {
                final int amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
                moveSpeed *= 1.0 + 0.2 * (amplifier + 1);
            }

            stage++;
        } else if (stage == 2) {
            if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                mc.player.motionY = 0.3995f;
                event.motionY = 0.3995f;
            }

            moveSpeed *= 2.149;
            if (mc.player.isPotionActive(MobEffects.SPEED)) {
                final int amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
                moveSpeed *= 1.0 + 0.2 * (amplifier + 1);
            }

            stage++;
        } else if (stage == 3) {
            if (mode.getValue().equalsIgnoreCase("StrictStrafe")) moveSpeed = lastDist - (0.66 * (lastDist - (2.4D / 10)));
            else moveSpeed = lastDist - (0.66 * (lastDist - (speed.getValue() / 10)));

            if (mc.player.isPotionActive(MobEffects.SPEED)) {
                final int amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
                moveSpeed *= 1.0 + 0.2 * (amplifier + 1);
            }

            stage++;
        } else {
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) stage = 1;
            moveSpeed = lastDist - (lastDist / 159.0);
        }

        if (mode.getValue().equalsIgnoreCase("StrictStrafe")) moveSpeed = Math.min(Math.max(moveSpeed, (2.4D / 10)), 0.551);
        else moveSpeed = Math.min(Math.max(moveSpeed, (speed.getValue() / 10)), 0.551);

        float forward = mc.player.movementInput.moveForward;
        float strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;

        if (!(mc.player.moveForward != 0.0D || mc.player.moveStrafing != 0.0D)) {
            event.motionX = 0;
            event.motionZ = 0;
        } else if (forward != 0.0f) {
            if (strafe >= 1.0f) {
                yaw += (float)(forward > 0.0f ? -45 : 45);
                strafe = 0.0f;
            } else if (strafe <= -1.0f) {
                yaw += (float)(forward > 0.0f ? 45 : -45);
                strafe = 0.0f;
            }

            if (forward > 0.0f) forward = 1.0f;
            else if (forward < 0.0f) forward = -1.0f;
        }

        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));

        event.motionX = ((double) forward * moveSpeed * cos + (double) strafe * moveSpeed * sin);
        event.motionZ = ((double) forward * moveSpeed * sin - (double) strafe * moveSpeed * cos);

        if (!(mc.player.moveForward != 0.0D || mc.player.moveStrafing != 0.0D)) {
            event.motionX = 0;
            event.motionZ = 0;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(EventPacket.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            moveSpeed = 0;
            lastDist = 0;
            stage = 4;
        }
    }
}
