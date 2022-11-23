package me.zane.grassware.features.modules.movement.CityPhase;


import me.zane.grassware.features.setting.Setting;
import me.zane.grassware.fire.Descriptor;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.fire.EventListener;

@Descriptor(description = "Phases slightly into the wall to prevent crystal damage")
public class CityPhase extends Module {

    public Setting<Float> timeout = this.register(new Setting<Integer>("Timeout", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(10)));

    public CityPhase(String name, String description, Category category, boolean hasListener, boolean hidden, boolean alwaysListening) {
        super(name, description, category, hasListener, hidden, alwaysListening);
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