package me.zane.grassware.features.modules.movement;


import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.Setting;

public class CityPhase extends Module {

    public Setting<Float> timeout = register(new Setting("timeout", 5, 1, 10));

    public CityPhase() {
        super("CityPhase", "CityPhase", Module.Category.MOVEMENT, true, false, false);
    }

    public CityPhase(String name, String description, Category category, boolean hasListener, boolean hidden, boolean alwaysListening) {
        super(name, description, category, hasListener, hidden, alwaysListening);
    }

    public boolean movingByKeys() {
        return mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown();
    }

    public double roundToClosest(double num, double low, double high) {
        double d1 = num - low;
        double d2 = high - num;
        if (d2 > d1) {
            return low;
        } else {
            return high;
        }
    }
}