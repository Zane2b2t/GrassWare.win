package me.zane.grassware.features.command.commands;

import me.zane.grassware.features.command.Command;


public class StartCommand
        extends Command {
    public StartCommand() {
        super("start", new String[]{"<number>"});
    }

    @Override
    public void execute(String[] var1) {

    }
}
