package me.zane.grassware.fire;

import me.zane.grassware.features.modules.Module;

import java.util.function.Predicate;

public class
Setting<T> {
    protected Module module;
    protected String name, panel = "Main";
    protected T value;
    protected Predicate<T> shown;

    public Setting(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public String getPanel() {
        return panel;
    }

    public boolean isVisible() {
        if (shown == null)
            return true;

        return shown.test(getValue());
    }
}