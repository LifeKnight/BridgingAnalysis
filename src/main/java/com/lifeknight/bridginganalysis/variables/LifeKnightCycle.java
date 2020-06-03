package com.lifeknight.bridginganalysis.variables;

import java.io.IOException;
import java.util.ArrayList;

import static com.lifeknight.bridginganalysis.mod.Mod.*;

public class LifeKnightCycle extends LifeKnightVariable {
    private final ArrayList<String> defaultValues;
    private final int defaultCurrentValue;
    private ArrayList<String> values;
    private int currentValue;

    public LifeKnightCycle(String name, String group, ArrayList<String> values, int currentValue) {
        super(name, group, true);
        defaultValues = values;
        defaultCurrentValue = currentValue;
        this.values = values;
        this.currentValue = currentValue;
        variables.add(this);
    }

    public LifeKnightCycle(String name, String group, ArrayList<String> values) {
        this(name, group, values, 0);
    }

    public String getCurrentValueString() {
        return currentValue >= 0 && currentValue < values.size() ? values.get(currentValue) : "";
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public String next() {
        currentValue = currentValue == values.size() - 1 ? 0 : currentValue + 1;
        onValueChange();
        return getCurrentValueString();
    }

    public String previous() {
        currentValue = currentValue == 0 ? values.size() - 1 : currentValue - 1;
        onValueChange();
        return getCurrentValueString();
    }

    public String setCurrentValue(int newValue) {
        if (!(newValue > values.size() - 1)) {
            currentValue = newValue;
        }
        if (config != null) {
            config.updateConfigFromVariables();
            onSetCurrentValue();
        }
        return getCurrentValueString();
    }

    public void addToValues(String newValue) throws IOException {
        if (values.contains(newValue)) {
            throw new IOException(super.getName() + " already contains " + newValue + "!");
        } else {
            values.add(newValue);
            onAddValue();
        }
    }

    public void removeFromValues(String valueToRemove) throws IOException {
        if (values.contains(valueToRemove)) {
            values.remove(valueToRemove);
            onRemoveValue();
        } else {
            throw new IOException(super.getName() + " does not contain " + valueToRemove + "!");
        }
    }

    public void clearValues() {
        currentValue = -1;
        values.clear();
        onClearValues();
    }

    public void onValueChange() {}

    public void onSetCurrentValue() {}

    public void onAddValue() {}

    public void onRemoveValue() {}

    public void onClearValues() {}

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void reset() {
        values = defaultValues;
        currentValue = defaultCurrentValue;
    }
}
