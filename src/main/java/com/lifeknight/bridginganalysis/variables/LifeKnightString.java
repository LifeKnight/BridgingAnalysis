package com.lifeknight.bridginganalysis.variables;

import static com.lifeknight.bridginganalysis.mod.Core.config;
import static com.lifeknight.bridginganalysis.mod.Core.variables;

public class LifeKnightString extends LifeKnightVariable {
    private final String defaultValue;
    private String value;

    public LifeKnightString(String name, String group, String value) {
        super(name, group, true);
        defaultValue = value;
        this.value = value;
        variables.add(this);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        if (config != null) {
            config.updateConfigFromVariables();
            onSetValue();
        }
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void clear() {
        this.value = "";
        onClear();
    }

    public void onSetValue() {}

    public void onClear() {}

    @Override
    public void reset() {
        value = defaultValue;
    }
}
