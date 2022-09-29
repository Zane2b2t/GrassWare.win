package me.derp.quantum.features.modules.movement;

import me.derp.quantum.features.modules.Module;
import me.derp.quantum.features.setting.Setting;
import me.derp.quantum.util.EntityUtil;
import me.derp.quantum.util.PlayerUtil;
import me.derp.quantum.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

public class Strafe extends Module {

    Setting<Double> jumpHeight = this.register(new Setting<>("Jump Height", 0.41, 0.0, 1.0));
    Setting<Double> timerVal = this.register(new Setting<>("Timer Speed", 1.15, 1.0, 1.5));

    private double playerSpeed;
    private final Timer timer = new Timer();

    public Strafe() {
        super("Strafe", "lightspeed", Category.MOVEMENT, true, false, false);
    }
    @Override
    public void onEnable() {
        playerSpeed = EntityUtil.getBaseMoveSpeed();
    }

    @Override
    public void onDisable() {
        resetTimer();
        this.timer.reset();
    }

    public static void resetTimer() {
        mc.timer.tickLength = 50;
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) {
            this.disable();
            return;
        }

        if (mc.player.isInLava() || mc.player.isInWater() || mc.player.isOnLadder() || mc.player.isInWeb) {
            return;
