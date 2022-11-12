package me.zane.grassware.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class Wrapper {
    public static final /* synthetic */ Minecraft mc;

    public static int getKey(String string) {
        return Keyboard.getKeyIndex((String)string.toUpperCase());
    }

    public static EntityPlayerSP getPlayer() {
        return Wrapper.getMinecraft().player;
    }

    static {
        mc = Minecraft.getMinecraft();
    }

    public static World getWorld() {
        return Wrapper.getMinecraft().world;
    }

    public static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }
}

