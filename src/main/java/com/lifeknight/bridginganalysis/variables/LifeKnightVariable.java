package com.lifeknight.bridginganalysis.variables;

public abstract class LifeKnightVariable {
    private final String name;
    private final String group;
    private boolean storeValue;

    public LifeKnightVariable(String name, String group, boolean storeValue) {
        this.name = name;
        this.group = group;
        this.storeValue = storeValue;
    }

    public String getName() {
        return name;
    }

    public String getLowerCaseName() {
        return name.toLowerCase();
    }

    public String getGroup() {
        return group;
    }

    public String getLowerCaseGroup() {
        return group.toLowerCase();
    }

    public abstract Object getValue();

    public abstract void reset();

    public boolean isStoreValue() {
        return storeValue;
    }

    public void setStoreValue(boolean storeValue) {
        this.storeValue = storeValue;
    }
}
