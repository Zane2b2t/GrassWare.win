package me.zane.grassware.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.features.command.Command;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.Setting;

public class NickHider extends Module {
    public final Setting<String> NameString = register(new Setting<Object>("Name", "New Name Here..."));

    private static NickHider instance;

    public NickHider() {
        super("Media", "Changes name", Module.Category.CLIENT, false, false, false);
        instance = this;
    }

    @Override
    public void onEnable() {
        Command.sendMessage(ChatFormatting.GRAY + "Success! Name succesfully changed to " + ChatFormatting.GREEN + NameString.getValue());
    }

    public static NickHider getInstance() {
        if (instance == null) {
            instance = new NickHider();
        }
        return instance;
    }
}