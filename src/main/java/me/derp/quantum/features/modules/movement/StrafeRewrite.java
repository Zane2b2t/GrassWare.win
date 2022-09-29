package me.derp.quantum..features.modules.movement;

import me.derp.quantum..Quantum;
import me.derp.quantum..event.events.MoveEvent;
import me.derp.quantum..event.events.PacketEvent;
import me.derp.quantum..event.events.UpdateWalkingPlayerEvent;
import me.derp.quantum..features.modules.Module;
import me.derp.quantum..features.setting.Setting;
import me.derp.quantum..util.MovementUtil;
import me.derp.quantum..util.Timer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class StrafeRewrite extends Module
{
    private static StrafeRewrite INSTANCE;
    private final Setting<Mode> mode;
    private final Setting<Boolean> useTimer;
    private final Setting<Float> timerFactor;
    private final Setting<Boolean> boost;
    private final Setting<Boolean> hypixel;
    private final Setting<Boolean> strict;
    private final Setting<Boolean> disableOnSneak;
    private int strafeStage;
    public int hopStage;
    private double horizontal;
    private double currentSpeed;
    private double prevMotion;
    private boolean oddStage;
    private int state;
    private double aacSpeed;
    private int aacCounter;
    private int aacState;
    private int ticksPassed;
    private double maxVelocity;
    private final Timer velocityTimer;
    private final Timer setbackTimer;
    private int lowHopStage;
    private double lowHopSpeed;
    private boolean even;
    private boolean forceGround;

    public StrafeRewrite() {
        super("StrafeRewrite", "Kokas.", Category.MOVEMENT, true, false, false);
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", Mode.STRAFE));
        this.useTimer = (Setting<Boolean>)this.register(new Setting("UseTimer", true));
        this.timerFactor = (Setting<Float>)this.register(new Setting("Factor", 1.0f, 0.1f, 10.0f));
        this.boost = (Setting<Boolean>)this.register(new Setting("Boost", false, v -> this.mode.getValue() == Mode.STRAFE || this.mode.getValue() == Mode.STRAFESTRICT));
        this.hypixel = (Setting<Boolean>)this.register(new Setting("Hypixel", false));
        this.strict = (Setting<Boolean>)this.register(new Setting("Strict", false, v -> this.mode.getValue() == Mode.STRAFE));
        this.disableOnSneak = (Setting<Boolean>)this.register(new Setting("DisableOnSneak", false));
        this.strafeStage = 1;
        this.currentSpeed = 0.0;
        this.prevMotion = 0.0;
        this.oddStage = false;
        this.state = 4;
        this.aacSpeed = 0.2873;
        this.aacState = 4;
        this.ticksPassed = 0;
        this.maxVelocity = 0.0;
        this.velocityTimer = new Timer();
        this.setbackTimer = new Timer();
        StrafeRewrite.INSTANCE = this;
    }

    public static StrafeRewrite getInstance() {
        if (StrafeRewrite.INSTANCE == null) {
            StrafeRewrite.INSTANCE = new StrafeRewrite();
        }
        return StrafeRewrite.INSTANCE;
    }

    @Override
    public void onUpdate() {
        if (StrafeRewrite.mc.player == null || StrafeRewrite.mc.world == null) {
            return;
        }
        if (this.disableOnSneak.getValue() && StrafeRewrite.mc.player.isSneaking()) {
            return;
        }
        if (this.mode.getValue() == Mode.STRAFE || (this.mode.getValue() == Mode.LOWHOP && this.useTimer.getValue())) {
            JorgitoHack.timerManager.setTimer(1.08f + 0.008f * this.timerFactor.getValue());
        }
        else if (this.mode.getValue() != Mode.STRAFESTRICT) {
            JorgitoHack.timerManager.reset();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (this.mode.getValue() == Mode.STRAFE || this.mode.getValue() == Mode.STRAFESTRICT || this.mode.getValue() == Mode.LOWHOP) {
            final double dX = StrafeRewrite.mc.player.posX - StrafeRewrite.mc.player.prevPosX;
            final double dZ = StrafeRewrite.mc.player.posZ - StrafeRewrite.mc.player.prevPosZ;
            this.prevMotion = Math.sqrt(dX * dX + dZ * dZ);
        }
    }

    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && this.forceGround) {
            this.forceGround = false;
            ((CPacketPlayer) event.getPacket()).onGround = true;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            JorgitoHack.timerManager.reset();
            this.currentSpeed = 0.0;
            this.state = 4;
            this.aacSpeed = 0.2873;
            this.aacState = 4;
            this.prevMotion = 0.0;
            this.aacCounter = 0;
            this.maxVelocity = 0.0;
            this.setbackTimer.reset();
            this.lowHopStage = 4;
        }
        else if (event.getPacket() instanceof SPacketExplosion) {
            final SPacketExplosion velocity = event.getPacket();
            this.maxVelocity = Math.sqrt(velocity.getMotionX() * velocity.getMotionX() + velocity.getMotionZ() * velocity.getMotionZ());
            this.velocityTimer.reset();
        }
    }

    @SubscribeEvent
    public void onMove(final MoveEvent event) {
        if (StrafeRewrite.mc.player == null || StrafeRewrite.mc.world == null) {
            return;
        }
        if (this.disableOnSneak.getValue() && StrafeRewrite.mc.player.isSneaking()) {
            return;
        }
        switch (this.mode.getValue()) {
            case STRAFE: {
                if (this.state != 1 || StrafeRewrite.mc.player.moveForward == 0.0f || StrafeRewrite.mc.player.moveStrafing == 0.0f) {
                    if (this.state == 2 && (StrafeRewrite.mc.player.moveForward != 0.0f || StrafeRewrite.mc.player.moveStrafing != 0.0f)) {
                        double jumpSpeed = 0.0;
                        if (StrafeRewrite.mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                            jumpSpeed += (StrafeRewrite.mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
                        }
                        event.setY(StrafeRewrite.mc.player.motionY = (this.hypixel.getValue() ? 0.3999999463558197 : 0.3999) + jumpSpeed);
                        this.currentSpeed *= (this.oddStage ? 1.6835 : 1.395);
                    }
                    else if (this.state == 3) {
                        final double adjustedMotion = 0.66 * (this.prevMotion - this.getBaseMotionSpeed());
                        this.currentSpeed = this.prevMotion - adjustedMotion;
                        this.oddStage = !this.oddStage;
                    }
                    else {
                        final List<AxisAlignedBB> collisionBoxes = (List<AxisAlignedBB>)StrafeRewrite.mc.world.getCollisionBoxes(StrafeRewrite.mc.player, StrafeRewrite.mc.player.getEntityBoundingBox().offset(0.0, StrafeRewrite.mc.player.motionY, 0.0));
                        if ((collisionBoxes.size() > 0 || StrafeRewrite.mc.player.collidedVertically) && this.state > 0) {
                            this.state = ((StrafeRewrite.mc.player.moveForward != 0.0f || StrafeRewrite.mc.player.moveStrafing != 0.0f) ? 1 : 0);
                        }
                        this.currentSpeed = this.prevMotion - this.prevMotion / 159.0;
                    }
                }
                else {
                    this.currentSpeed = 1.35 * this.getBaseMotionSpeed() - 0.01;
                }
                this.currentSpeed = Math.max(this.currentSpeed, this.getBaseMotionSpeed());
                if (this.maxVelocity > 0.0 && this.boost.getValue() && !this.velocityTimer.hasPassed(75.0) && !StrafeRewrite.mc.player.collidedHorizontally) {
                    this.currentSpeed = Math.max(this.currentSpeed, this.maxVelocity);
                }
                else if (this.strict.getValue()) {
                    this.currentSpeed = Math.min(this.currentSpeed, 0.433);
                }
                double forward = StrafeRewrite.mc.player.movementInput.moveForward;
                double strafe = StrafeRewrite.mc.player.movementInput.moveStrafe;
                float yaw = StrafeRewrite.mc.player.rotationYaw;
                if (forward == 0.0 && strafe == 0.0) {
                    event.setX(0.0);
                    event.setZ(0.0);
                }
                else {
                    if (forward != 0.0) {
                        if (strafe > 0.0) {
                            yaw += ((forward > 0.0) ? -45 : 45);
                        }
                        else if (strafe < 0.0) {
                            yaw += ((forward > 0.0) ? 45 : -45);
                        }
                        strafe = 0.0;
                        if (forward > 0.0) {
                            forward = 1.0;
                        }
                        else if (forward < 0.0) {
                            forward = -1.0;
                        }
                    }
                    event.setX(forward * this.currentSpeed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * this.currentSpeed * Math.sin(Math.toRadians(yaw + 90.0f)));
                    event.setZ(forward * this.currentSpeed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * this.currentSpeed * Math.cos(Math.toRadians(yaw + 90.0f)));
                }
                if (StrafeRewrite.mc.player.moveForward == 0.0f && StrafeRewrite.mc.player.moveStrafing == 0.0f) {
                    return;
                }
                ++this.state;
                break;
            }
            case STRAFESTRICT: {
                ++this.aacCounter;
                this.aacCounter %= 5;
                if (this.aacCounter != 0) {
                    JorgitoHack.timerManager.reset();
                }
                else if (MovementUtil.isPlayerMoving()) {
                    JorgitoHack.timerManager.setTimer(1.3f);
                    final EntityPlayerSP player = StrafeRewrite.mc.player;
                    player.motionX *= 1.0199999809265137;
                    final EntityPlayerSP player2 = StrafeRewrite.mc.player;
                    player2.motionZ *= 1.0199999809265137;
                }
                if (StrafeRewrite.mc.player.onGround && MovementUtil.isPlayerMoving()) {
                    this.aacState = 2;
                }
                if (this.round(StrafeRewrite.mc.player.posY - (int)StrafeRewrite.mc.player.posY, 3) == this.round(0.138, 3)) {
                    final EntityPlayerSP player3 = StrafeRewrite.mc.player;
                    player3.motionY -= 0.08;
                    event.setY(event.getY() - 0.09316090325960147);
                    final EntityPlayerSP player4 = StrafeRewrite.mc.player;
                    player4.posY -= 0.09316090325960147;
                }
                if (this.aacState == 1 && (StrafeRewrite.mc.player.moveForward != 0.0f || StrafeRewrite.mc.player.moveStrafing != 0.0f)) {
                    this.aacState = 2;
                    this.aacSpeed = 1.38 * this.getBaseMotionSpeed() - 0.01;
                }
                else if (this.aacState == 2) {
                    this.aacState = 3;
                    event.setY(StrafeRewrite.mc.player.motionY = 0.399399995803833);
                    this.aacSpeed *= 2.149;
                }
                else if (this.aacState == 3) {
                    this.aacState = 4;
                    final double adjustedMotion = 0.66 * (this.prevMotion - this.getBaseMotionSpeed());
                    this.aacSpeed = this.prevMotion - adjustedMotion;
                }
                else {
                    if (StrafeRewrite.mc.world.getCollisionBoxes((Entity)StrafeRewrite.mc.player, StrafeRewrite.mc.player.getEntityBoundingBox().offset(0.0, StrafeRewrite.mc.player.motionY, 0.0)).size() > 0 || StrafeRewrite.mc.player.collidedVertically) {
                        this.aacState = 1;
                    }
                    this.aacSpeed = this.prevMotion - this.prevMotion / 159.0;
                }
                this.aacSpeed = Math.max(this.aacSpeed, this.getBaseMotionSpeed());
                if (this.maxVelocity > 0.0 && this.boost.getValue() && !this.velocityTimer.hasPassed(75.0) && !StrafeRewrite.mc.player.collidedHorizontally) {
                    this.aacSpeed = Math.max(this.aacSpeed, this.maxVelocity);
                }
                else {
                    this.aacSpeed = Math.min(this.aacSpeed, (this.ticksPassed > 25) ? 0.449 : 0.433);
                }
                float forward2 = StrafeRewrite.mc.player.movementInput.moveForward;
                float strafe2 = StrafeRewrite.mc.player.movementInput.moveStrafe;
                float yaw2 = StrafeRewrite.mc.player.rotationYaw;
                ++this.ticksPassed;
                if (this.ticksPassed > 50) {
                    this.ticksPassed = 0;
                }
                if (forward2 == 0.0f && strafe2 == 0.0f) {
                    event.setX(0.0);
                    event.setZ(0.0);
                }
                else if (forward2 != 0.0f) {
                    if (strafe2 >= 1.0f) {
                        yaw2 += ((forward2 > 0.0f) ? -45 : 45);
                        strafe2 = 0.0f;
                    }
                    else if (strafe2 <= -1.0f) {
                        yaw2 += ((forward2 > 0.0f) ? 45 : -45);
                        strafe2 = 0.0f;
                    }
                    if (forward2 > 0.0f) {
                        forward2 = 1.0f;
                    }
                    else if (forward2 < 0.0f) {
                        forward2 = -1.0f;
                    }
                }
                final double cos = Math.cos(Math.toRadians(yaw2 + 90.0f));
                final double sin = Math.sin(Math.toRadians(yaw2 + 90.0f));
                event.setX(forward2 * this.aacSpeed * cos + strafe2 * this.aacSpeed * sin);
                event.setZ(forward2 * this.aacSpeed * sin - strafe2 * this.aacSpeed * cos);
                if (forward2 == 0.0f && strafe2 == 0.0f) {
                    event.setX(0.0);
                    event.setZ(0.0);
                    break;
                }
                break;
            }
            case LOWHOP: {
                if (!this.setbackTimer.hasPassed(100.0)) {
                    return;
                }
                double jumpSpeed = 0.0;
                if (StrafeRewrite.mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                    jumpSpeed += (StrafeRewrite.mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
                }
                if (this.round(StrafeRewrite.mc.player.posY - (int)StrafeRewrite.mc.player.posY, 3) == this.round(0.4, 3)) {
                    event.setY(StrafeRewrite.mc.player.motionY = 0.31 + jumpSpeed);
                }
                else if (this.round(StrafeRewrite.mc.player.posY - (int)StrafeRewrite.mc.player.posY, 3) == this.round(0.71, 3)) {
                    event.setY(StrafeRewrite.mc.player.motionY = 0.04 + jumpSpeed);
                }
                else if (this.round(StrafeRewrite.mc.player.posY - (int)StrafeRewrite.mc.player.posY, 3) == this.round(0.75, 3)) {
                    event.setY(StrafeRewrite.mc.player.motionY = -0.2 - jumpSpeed);
                }
                else if (this.round(StrafeRewrite.mc.player.posY - (int)StrafeRewrite.mc.player.posY, 3) == this.round(0.55, 3)) {
                    event.setY(StrafeRewrite.mc.player.motionY = -0.14 + jumpSpeed);
                }
                else if (this.round(StrafeRewrite.mc.player.posY - (int)StrafeRewrite.mc.player.posY, 3) == this.round(0.41, 3)) {
                    event.setY(StrafeRewrite.mc.player.motionY = -0.2 + jumpSpeed);
                }
                if (this.lowHopStage == 1 && (StrafeRewrite.mc.player.moveForward != 0.0f || StrafeRewrite.mc.player.moveStrafing != 0.0f)) {
                    this.lowHopSpeed = 1.35 * this.getBaseMotionSpeed() - 0.01;
                }
                else if (this.lowHopStage == 2 && (StrafeRewrite.mc.player.moveForward != 0.0f || StrafeRewrite.mc.player.moveStrafing != 0.0f)) {
                    event.setY(StrafeRewrite.mc.player.motionY = (this.checkHeadspace() ? 0.2 : 0.3999) + jumpSpeed);
                    this.lowHopSpeed *= (this.even ? 1.5685 : 1.3445);
                }
                else if (this.lowHopStage == 3) {
                    final double dV = 0.66 * (this.prevMotion - this.getBaseMotionSpeed());
                    this.lowHopSpeed = this.prevMotion - dV;
                    this.even = !this.even;
                }
                else {
                    if (StrafeRewrite.mc.player.onGround && this.lowHopStage > 0) {
                        this.lowHopStage = ((StrafeRewrite.mc.player.moveForward != 0.0f || StrafeRewrite.mc.player.moveStrafing != 0.0f) ? 1 : 0);
                    }
                    this.lowHopSpeed = this.prevMotion - this.prevMotion / 159.0;
                }
                this.lowHopSpeed = Math.max(this.lowHopSpeed, this.getBaseMotionSpeed());
                float forward3 = StrafeRewrite.mc.player.movementInput.moveForward;
                float strafe3 = StrafeRewrite.mc.player.movementInput.moveStrafe;
                if (forward3 == 0.0f && strafe3 == 0.0f) {
                    event.setX(0.0);
                    event.setZ(0.0);
                }
                else if (forward3 != 0.0 && strafe3 != 0.0) {
                    forward3 *= (float)Math.sin(0.7853981633974483);
                    strafe3 *= (float)Math.cos(0.7853981633974483);
                }
                event.setX(forward3 * this.lowHopSpeed * -Math.sin(Math.toRadians(StrafeRewrite.mc.player.rotationYaw)) + strafe3 * this.lowHopSpeed * Math.cos(Math.toRadians(StrafeRewrite.mc.player.rotationYaw)));
                event.setZ(forward3 * this.lowHopSpeed * Math.cos(Math.toRadians(StrafeRewrite.mc.player.rotationYaw)) - strafe3 * this.lowHopSpeed * -Math.sin(Math.toRadians(StrafeRewrite.mc.player.rotationYaw)));
                if (StrafeRewrite.mc.player.moveForward == 0.0f && StrafeRewrite.mc.player.moveStrafing == 0.0f) {
                    return;
                }
                ++this.lowHopStage;
                break;
            }
        }
    }

    private boolean checkHeadspace() {
        return StrafeRewrite.mc.world.getCollisionBoxes((Entity)StrafeRewrite.mc.player, StrafeRewrite.mc.player.getEntityBoundingBox().offset(0.0, 0.21, 0.0)).size() > 0;
    }

    @Override
    public void onEnable() {
        if (StrafeRewrite.mc.player == null || StrafeRewrite.mc.world == null) {
            this.toggle();
            return;
        }
        this.maxVelocity = 0.0;
        this.hopStage = 1;
        this.lowHopStage = 4;
        if (this.mode.getValue() == Mode.STRAFE) {
            this.state = 4;
            this.currentSpeed = this.getBaseMotionSpeed();
            this.prevMotion = 0.0;
        }
    }

    @Override
    public void onDisable() {
        if (StrafeRewrite.mc.player == null || StrafeRewrite.mc.world == null) {
            return;
        }
        JorgitoHack.timerManager.reset();
    }

    private double getBaseMotionSpeed() {
        double baseSpeed = (this.mode.getValue() == Mode.STRAFE || this.mode.getValue() == Mode.STRAFESTRICT || this.mode.getValue() == Mode.LOWHOP) ? 0.2873 : 0.272;
        if (StrafeRewrite.mc.player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = StrafeRewrite.mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1.0);
        }
        return baseSpeed;
    }

    private double round(final double value, final int places) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public String getDisplayInfo() {
        if (this.mode.getValue() != Mode.NONE) {
            return this.mode.currentEnumName();
        }
        return null;
    }

    static {
        StrafeRewrite.INSTANCE = new StrafeRewrite();
    }

    public enum Mode
    {
        NONE,
        STRAFE,
        STRAFESTRICT,
        LOWHOP;
    }
}
