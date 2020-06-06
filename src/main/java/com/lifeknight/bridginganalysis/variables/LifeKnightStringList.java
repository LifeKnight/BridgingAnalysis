package com.lifeknight.bridginganalysis.variables;

import com.lifeknight.bridginganalysis.mod.Utilities;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.lifeknight.bridginganalysis.mod.Core.config;
import static com.lifeknight.bridginganalysis.mod.Core.variables;

public class LifeKnightStringList extends LifeKnightVariable {
    private final ArrayList<String> defaultValue;
    private ArrayList<String> value;

    public LifeKnightStringList(String name, String group, ArrayList<String> value) {
        super(name, group, true);
        defaultValue = value;
        this.value = value;
        variables.add(this);
    }

    public LifeKnightStringList(String name, String group) {
        this(name, group, new ArrayList<>());
    }

    public ArrayList<String> getValue() {
        return value;
    }


    public void setValue(ArrayList<String> newValue) {
        value = newValue;
        config.updateConfigFromVariables();
        onSetValue();
    }

    public void addElement(String element) throws IOException {
        if (!value.contains(element)) {
            value.add(element);
            config.updateConfigFromVariables();
            onAddElement();
        } else {
            throw new IOException(super.getName() + " already contains " + element + "!");
        }
    }

    public void removeElement(String element) throws IOException {
        if (value.contains(element)) {
            value.remove(element);
            config.updateConfigFromVariables();
            onRemoveElement();
        } else {
            throw new IOException(super.getName() + " does not contain " + element + "!");
        }
    }

    public void clear() {
        value.clear();
        config.updateConfigFromVariables();
        onClear();
    }

    public void setValueFromCSV(String CSV) {
        if (CSV.contains(",")) {
            try {
                value = new ArrayList<>(Arrays.asList(CSV.split(",")));
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.queueChatMessageForConnection(EnumChatFormatting.RED + "An error occurred while extracting the value of \"" + super.getName() + "\" from the config; the value will be interpreted as " + value + ".");
            }
        } else {
            value = new ArrayList<>();
        }
    }

    public String toCSV() {
        StringBuilder result = new StringBuilder();

        for (String element: value) {
            result.append(element).append(",");
        }

        return result.toString();
    }

    public void onAddElement() {}

    public void onRemoveElement() {}

    public void onClear() {}

    public void onSetValue() {}

    @Override
    public void reset() {
        value = defaultValue;
    }
}
