
package me.derp.quantum.features.modules.render;

import java.awt.Color;
import java.util.Random;
import me.derp.quantum.Quantum;
import me.derp.quantum.event.events.Render3DEvent;
import me.derp.quantum.features.modules.Module;
import me.derp.quantum.features.setting.Setting;
import me.derp.quantum.util.RenderUtil;
import me.derp.quantum.util.RotationUtil;
import net.minecraft.util.math.BlockPos;

public class HoleESP
extends Module {
    private static HoleESP INSTANCE = new HoleESP();
    private final Setting<Integer> holes = this.register(new Setting<Integer>("Holes", 500, 1, 500));
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 255, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 0, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 0, 0, 255));
    private final Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", 255, 0, 255));
    public Setting<Boolean> ownHole = this.register(new Setting<Boolean>("OwnHole", false));
    public Setting<Boolean> box = this.register(new Setting<Boolean>("Box", true));
    private final Setting<Integer> boxAlpha = this.register(new Setting<Object>("BoxAlpha", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue()));
    public Setting<Boolean> gradientBox = this.register(new Setting<Object>("GradientBox", Boolean.valueOf(true), v -> this.box.getValue()));
    public Setting<Boolean> pulseAlpha = this.register(new Setting<Object>("PulseAlpha", Boolean.valueOf(false), v -> this.gradientBox.getValue()));
    private final Setting<Integer> minPulseAlpha = this.register(new Setting<Object>("MinPulse", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(255), v -> this.pulseAlpha.getValue()));
    private final Setting<Integer> maxPulseAlpha = this.register(new Setting<Object>("MaxPulse", Integer.valueOf(40), Integer.valueOf(0), Integer.valueOf(255), v -> this.pulseAlpha.getValue()));
    private final Setting<Integer> pulseSpeed = this.register(new Setting<Object>("PulseSpeed", Integer.valueOf(10), Integer.valueOf(1), Integer.valueOf(50), v -> this.pulseAlpha.getValue()));
    public Setting<Boolean> invertGradientBox = this.register(new Setting<Object>("InvertGradientBox", Boolean.valueOf(false), v -> this.gradientBox.getValue()));
    public Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", true));
    private final Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.outline.getValue()));
    public Setting<Boolean> gradientOutline = this.register(new Setting<Object>("GradientOutline", Boolean.valueOf(true), v -> this.outline.getValue()));
    public Setting<Boolean> invertGradientOutline = this.register(new Setting<Object>("InvertGradientOutline", Boolean.valueOf(false), v -> this.gradientOutline.getValue()));
    public Setting<Double> height = this.register(new Setting<Double>("Height", 0.0, -2.0, 2.0));
    public Setting<Boolean> safeColor = this.register(new Setting<Boolean>("SafeColor", true));
    private final Setting<Integer> safeRed = this.register(new Setting<Object>("SafeRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.safeColor.getValue()));
    private final Setting<Integer> safeGreen = this.register(new Setting<Object>("SafeGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.safeColor.getValue()));
    private final Setting<Integer> safeBlue = this.register(new Setting<Object>("SafeBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.safeColor.getValue()));
    private final Setting<Integer> safeAlpha = this.register(new Setting<Object>("SafeAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.safeColor.getValue()));
    public Setting<Boolean> customOutline = this.register(new Setting<Object>("CustomLine", Boolean.valueOf(false), v -> this.outline.getValue()));
    private final Setting<Integer> cRed = this.register(new Setting<Object>("OL-Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false));
    private final Setting<Integer> cGreen = this.register(new Setting<Object>("OL-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false));
    private final Setting<Integer> cBlue = this.register(new Setting<Object>("OL-Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false));
    private final Setting<Integer> cAlpha = this.register(new Setting<Object>("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false));
    private final Setting<Integer> safecRed = this.register(new Setting<Object>("OL-SafeRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.safeColor.getValue() != false));
    private final Setting<Integer> safecGreen = this.register(new Setting<Object>("OL-SafeGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.safeColor.getValue() != false));
    private final Setting<Integer> safecBlue = this.register(new Setting<Object>("OL-SafeBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.safeColor.getValue() != false));
    private final Setting<Integer> safecAlpha = this.register(new Setting<Object>("OL-SafeAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.safeColor.getValue() != false));
    private boolean pulsing;
    private boolean shouldDecrease;
    private int pulseDelay;
    private int currentPulseAlpha;
    private int currentAlpha;

    public HoleESP() {
        super("HoleESP", "Shows safe spots.", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static HoleESP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HoleESP();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        int drawnHoles = 0;
        if (!this.pulsing && this.pulseAlpha.getValue().booleanValue()) {
            Random rand = new Random();
            this.currentPulseAlpha = rand.nextInt(this.maxPulseAlpha.getValue() - this.minPulseAlpha.getValue() + 1) + this.minPulseAlpha.getValue();
            this.pulsing = true;
            this.shouldDecrease = false;
        }
        if (this.pulseDelay == 0) {
            if (this.pulsing && this.pulseAlpha.getValue().booleanValue() && !this.shouldDecrease) {
                ++this.currentAlpha;
                if (this.currentAlpha >= this.currentPulseAlpha) {
                    this.shouldDecrease = true;
                }
            }
            if (this.pulsing && this.pulseAlpha.getValue().booleanValue() && this.shouldDecrease) {
                --this.currentAlpha;
            }
            if (this.currentAlpha <= 0) {
                this.pulsing = false;
                this.shouldDecrease = false;
            }
            ++this.pulseDelay;
        } else {
            ++this.pulseDelay;
            if (this.pulseDelay == 51 - this.pulseSpeed.getValue()) {
                this.pulseDelay = 0;
            }
        }
        if (!this.pulseAlpha.getValue().booleanValue() || !this.pulsing) {
            this.currentAlpha = 0;
        }
        if (HoleESP.fullNullCheck()) {
            return;
        }
        for (BlockPos pos : Quantum.holeManager.getSortedHoles()) {
            if (drawnHoles >= this.holes.getValue()) break;
            if (pos.equals((Object)new BlockPos(HoleESP.mc.player.posX, HoleESP.mc.player.posY, HoleESP.mc.player.posZ)) && !this.ownHole.getValue().booleanValue() || RotationUtil.isInFov(pos)) continue;
            if (this.safeColor.getValue().booleanValue() && Quantum.holeManager.isSafe(pos)) {
                RenderUtil.drawBoxESP(pos, new Color(this.safeRed.getValue(), this.safeGreen.getValue(), this.safeBlue.getValue(), this.safeAlpha.getValue()), this.customOutline.getValue(), new Color(this.safecRed.getValue(), this.safecGreen.getValue(), this.safecBlue.getValue(), this.safecAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue(), this.gradientBox.getValue(), this.gradientOutline.getValue(), this.invertGradientBox.getValue(), this.invertGradientOutline.getValue(), this.currentAlpha);
            } else {
                RenderUtil.drawBoxESP(pos, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.customOutline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue(), this.gradientBox.getValue(), this.gradientOutline.getValue(), this.invertGradientBox.getValue(), this.invertGradientOutline.getValue(), this.currentAlpha);
            }
            ++drawnHoles;
        }
    }
}
