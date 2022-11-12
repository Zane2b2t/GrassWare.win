package me.zane.grassware.features.command.commands;

import me.zane.grassware.Quantum;
import me.zane.grassware.features.command.Command;

public class ReloadCommand
        extends Command {
    public ReloadCommand() {
        super("reload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        Quantum.reload();
    }
}

