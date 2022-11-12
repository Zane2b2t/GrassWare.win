package me.zane.grassware.features.modules.troll;

import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.Feature;
import me.zane.grassware.util.Util;
import net.minecraft.network.play.client.CPacketChatMessage;


public class Coordleaker extends Module {
    public Coordleaker() {
        super("Coordleaker", "troll 69", Category.TROLL, true, false, false);

    }

    @Override
    public
    void onEnable() {
        if (Feature.fullNullCheck()) return;
        Util.mc.player.connection.sendPacket(new CPacketChatMessage("lol my coords are: " + Math.floor(Util.mc.player.posX) + ", " + Math.floor(Util.mc.player.posY) + ", " + Math.floor(Util.mc.player.posZ) + "! come and kill me."));
    }
}
