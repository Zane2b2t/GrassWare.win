package me.zane.grassware.features.modules.movement.CityPhase;


import me.zane.grassware.fire.EventListener;
import me.zane.grassware.fire.Descriptor;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.fire.Slider;

/**
 * Geen probleem wallhacks :^)
 */
@Descriptor(description = "Phases slightly into the wall to prevent crystal damage")
public class CityPhase extends Module {
    public final Slider timeout = Menu.Slider("Timeout", 5, 1, 10);

    public CityPhase() {
        eventListeners = new EventListener[]{
                new TickListener(this)
        };
    }

    protected boolean movingByKeys() {
        return mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown();
    }

    protected double roundToClosest(double num, double low, double high) {
        double d1 = num - low;
        double d2 = high - num;
        if (d2 > d1) {
            return low;
        } else {
            return high;
        }
    }
}