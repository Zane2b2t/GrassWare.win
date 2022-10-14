
package me.derp.quantum.manager;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import me.derp.quantum.features.Feature;
import me.derp.quantum.features.modules.client.Management;
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
import me.derp.quantum.features.modules.combat.AutoCrystal;
=======
>>>>>>> parent of e1545ed (More)
=======
>>>>>>> parent of 509ddc3 (Revert "Removed .github stuff")
=======
import me.derp.quantum.features.modules.combat.AutoCrystal;
>>>>>>> parent of 0c0a21a (Merge remote-tracking branch 'origin/main')
=======
>>>>>>> parent of 509ddc3 (Revert "Removed .github stuff")
=======
import me.derp.quantum.features.modules.combat.AutoCrystal;
>>>>>>> parent of 0c0a21a (Merge remote-tracking branch 'origin/main')
=======
import me.derp.quantum.features.modules.combat.AutoCrystal;
>>>>>>> parent of 0c0a21a (Merge remote-tracking branch 'origin/main')
import me.derp.quantum.util.BlockUtil;
import me.derp.quantum.util.DamageUtil;
import me.derp.quantum.util.EntityUtil;
import me.derp.quantum.util.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class SafetyManager
extends Feature
implements Runnable {
    private final TimerUtil syncTimer = new TimerUtil();
    private final AtomicBoolean SAFE = new AtomicBoolean(false);
    private ScheduledExecutorService service;

    @Override
    public void run() {
        if (AutoCrystal.getInstance().isOff() || AutoCrystal.getInstance().threadMode.getValue() == AutoCrystal.ThreadMode.NONE) {
            this.doSafetyCheck();
        }
    }

    public void doSafetyCheck() {
        if (!SafetyManager.fullNullCheck()) {
            EntityPlayer closest;
            boolean safe = true;
            EntityPlayer entityPlayer = closest = Management.getInstance().safety.getValue() != false ? EntityUtil.getClosestEnemy(18.0) : null;
            if (Management.getInstance().safety.getValue().booleanValue() && closest == null) {
                this.SAFE.set(true);
                return;
            }
            ArrayList<Entity> crystals = new ArrayList(SafetyManager.mc.world.loadedEntityList);
            for (Entity crystal : crystals) {
                if (!(crystal instanceof EntityEnderCrystal) || !((double)DamageUtil.calculateDamage(crystal, (Entity)SafetyManager.mc.player) > 4.0) || closest != null && !(closest.getDistanceSq(crystal) < 40.0)) continue;
                safe = false;
                break;
            }
            if (safe) {
                for (BlockPos pos : BlockUtil.possiblePlacePositions(4.0f, false, Management.getInstance().oneDot15.getValue(), false)) {
                    if (!((double)DamageUtil.calculateDamage(pos, (Entity)SafetyManager.mc.player) > 4.0) || closest != null && !(closest.getDistanceSq(pos) < 40.0)) continue;
                    safe = false;
                    break;
                }
            }
            this.SAFE.set(safe);
        }
    }

    public void onUpdate() {
        this.run();
    }

    public String getSafetyString() {
        if (this.SAFE.get()) {
            return "\u00a7aSecure";
        }
        return "\u00a7cUnsafe";
    }

    public boolean isSafe() {
        return this.SAFE.get();
    }

    public ScheduledExecutorService getService() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this, 0L, Management.getInstance().safetyCheck.getValue().intValue(), TimeUnit.MILLISECONDS);
        return service;
    }
}
