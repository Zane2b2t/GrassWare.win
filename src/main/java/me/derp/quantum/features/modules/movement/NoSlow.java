package me.derp.quantum.features.modules.movement;

import me.derp.quantum.event.events.KeyPressedEvent;
import me.derp.quantum.event.events.PacketEvent;
import me.derp.quantum.features.modules.Module;
import me.derp.quantum.features.setting.Setting;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoSlow
        extends Module {
    private static final KeyBinding[] keys = new KeyBinding[]{NoSlow.mc.gameSettings.keyBindForward, NoSlow.mc.gameSettings.keyBindBack, NoSlow.mc.gameSettings.keyBindLeft, NoSlow.mc.gameSettings.keyBindRight, NoSlow.mc.gameSettings.keyBindJump, NoSlow.mc.gameSettings.keyBindSprint};
    private static NoSlow INSTANCE = new NoSlow();
    public Setting<Boolean> guiMove = this.register(new Setting<Boolean>("GuiMove", true));
    public Setting<Boolean> noSlow = this.register(new Setting<Boolean>("NoSlow", true));
    public Setting<Boolean> soulSand = this.register(new Setting<Boolean>("SoulSand", false));
    public Setting<Boolean> strict = this.register(new Setting<Boolean>("Strict", false));
    public Setting<Boolean> sneakPacket = this.register(new Setting<Boolean>("SneakPacket", false));
    public Setting<Boolean> endPortal = this.register(new Setting<Boolean>("EndPortal", false));
    private boolean sneaking = false;

    public NoSlow() {
        super("NoSlow", "Prevents you from getting slowed down.", Module.Category.MOVEMENT, true, false, false);
        this.setInstance();
    }

    public static NoSlow getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoSlow();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.guiMove.getValue().booleanValue()) {
            if (NoSlow.mc.currentScreen instanceof GuiOptions || NoSlow.mc.currentScreen instanceof GuiVideoSettings || NoSlow.mc.currentScreen instanceof GuiScreenOptionsSounds || NoSlow.mc.currentScreen instanceof GuiContainer || NoSlow.mc.currentScreen instanceof GuiIngameMenu) {
                for (KeyBinding bind : keys) {
                    KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
                }
            } else if (NoSlow.mc.currentScreen == null) {
                for (KeyBinding bind : keys) {
                    if (Keyboard.isKeyDown(bind.getKeyCode())) continue;
                    KeyBinding.setKeyBindState(bind.getKeyCode(), false);
                }
            }
        }
        Item item = NoSlow.mc.player.getActiveItemStack().getItem();
        if (this.sneaking && !NoSlow.mc.player.isHandActive() && this.sneakPacket.getValue().booleanValue()) {
            NoSlow.mc.player.connection.sendPacket(new CPacketEntityAction(NoSlow.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.sneaking = false;
        }
    }

    @SubscribeEvent
    public void onUseItem(PlayerInteractEvent.RightClickItem event) {
        Item item = NoSlow.mc.player.getHeldItem(event.getHand()).getItem();
        if ((item instanceof ItemFood || item instanceof ItemBow || item instanceof ItemPotion && this.sneakPacket.getValue().booleanValue()) && !this.sneaking) {
            NoSlow.mc.player.connection.sendPacket(new CPacketEntityAction(NoSlow.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.sneaking = true;
        }
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        if (this.noSlow.getValue().booleanValue() && NoSlow.mc.player.isHandActive() && !NoSlow.mc.player.isRiding()) {
            event.getMovementInput().moveStrafe *= 5.0f;
            event.getMovementInput().moveForward *= 5.0f;
        }
    }

    @SubscribeEvent
    public void onKeyPressedEvent(KeyPressedEvent event) {
        if (this.guiMove.getValue().booleanValue() && event.getStage() == 0 && !(NoSlow.mc.currentScreen instanceof GuiChat)) {
            event.info = event.pressed;
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && this.strict.getValue().booleanValue() && this.noSlow.getValue().booleanValue() && NoSlow.mc.player.isHandActive() && !NoSlow.mc.player.isRiding()) {
            NoSlow.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(NoSlow.mc.player.posX), Math.floor(NoSlow.mc.player.posY), Math.floor(NoSlow.mc.player.posZ)), EnumFacing.DOWN));
        }
    }
}

