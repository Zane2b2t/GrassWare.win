package me.derp.quantum.features.modules.misc;

import me.derp.quantum.features.modules.Module;
import me.derp.quantum.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.util.EnumHand;

public class AutoFrameDupe extends Module {
    private final Setting<Boolean> shulkersonly  = this.register(new Setting<Boolean>("ShulkersOnly", true));
    private final Setting<Integer> range  = this.register(new Setting<Integer>("Range", 5, 0, 6));
    private final Setting<Integer> turns  = this.register(new Setting<Integer>("Turns", 1, 0, 5));
    private final Setting<Integer> ticks  = this.register(new Setting<Integer>("Ticks", 10, 1, 20));
    private int timeoutTicks = 0;

    public AutoFrameDupe() {
        super("AutoFrameDupe", "For 6b6t.org and 8b8t.me and more!", Module.Category.MISC, true, false ,false);
    }

    @Override
    public void onUpdate() {
        if (mc.player != null && mc.world != null) {
            if (shulkersonly.getValue()) {
                int shulkerSlot = getShulkerSlot();
                if (shulkerSlot != -1) {
                    mc.player.inventory.currentItem = shulkerSlot;
                }
            }
            for (Entity frame : mc.world.loadedEntityList) {
                if (frame instanceof EntityItemFrame) {
                    if (mc.player.getDistance(frame) <= range.getValue()) {
                        if (timeoutTicks >= ticks.getValue()) {
                            if (((EntityItemFrame) frame).getDisplayedItem().getItem() == Items.AIR && !mc.player.getHeldItemMainhand().isEmpty) {
                                mc.playerController.interactWithEntity(mc.player, frame, EnumHand.MAIN_HAND);
                            }
                            if (((EntityItemFrame) frame).getDisplayedItem().getItem() != Items.AIR) {
                                for (int i = 0; i < turns.getValue(); i++) {
                                    mc.playerController.interactWithEntity(mc.player, frame, EnumHand.MAIN_HAND);
                                }
                                mc.playerController.attackEntity(mc.player, frame);
                                timeoutTicks = 0;
                            }
                        }
                        ++timeoutTicks;
                    }
                }
            }
        }
    }

    private int getShulkerSlot() {
        int shulkerSlot = -1;
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item instanceof ItemShulkerBox) shulkerSlot = i;
        }
        return shulkerSlot;
    }
}