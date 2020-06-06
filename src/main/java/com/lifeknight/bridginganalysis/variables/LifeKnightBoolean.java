package com.lifeknight.bridginganalysis.variables;


import static com.lifeknight.bridginganalysis.mod.Core.config;
import static com.lifeknight.bridginganalysis.mod.Core.variables;
import static net.minecraft.util.EnumChatFormatting.GREEN;
import static net.minecraft.util.EnumChatFormatting.RED;

public class LifeKnightBoolean extends LifeKnightVariable {
    private final boolean defaultValue;
    private boolean value;
    private LifeKnightStringList lifeKnightStringList;

    public LifeKnightBoolean(String name, String group, boolean value) {
        super(name, group, true);
        this.value = value;
        defaultValue = value;
        variables.add(this);
    }

    public LifeKnightBoolean(String name, String group, boolean value, LifeKnightStringList lifeKnightStringList) {
        this(name, group, value);
        this.lifeKnightStringList = lifeKnightStringList;
    }

    public Boolean getDefaultValue() {
        return defaultValue;
    }

    public boolean hasStringList() {
        return lifeKnightStringList != null;
    }

    public LifeKnightStringList getLifeKnightStringList() {
        return lifeKnightStringList;
    }

    public Boolean getValue() {
        return value;
    }

    public void toggle() {
        value = !value;
        config.updateConfigFromVariables();
        onSetValue();
    }

    public void setValue(boolean newValue) {
        value = newValue;
        if (config != null) {
            config.updateConfigFromVariables();
            onSetValue();
        }
    }

    public String getAsString() {
        if (value) {
            return super.getName() + ": " + GREEN + "ENABLED";
        } else {
            return super.getName() + ": " + RED + "DISABLED";
        }
    }

    public void onSetValue() {}

    @Override
    public void reset() {
        value = defaultValue;
    }
}
