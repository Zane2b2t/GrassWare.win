package me.zane.grassware.features.command.commands;

import me.zane.grassware.Quantum;
import me.zane.grassware.features.command.Command;

public class UnloadCommand
        extends Command {
    public UnloadCommand() {
        super("unload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        Quantum.unload(true);
    }
}

