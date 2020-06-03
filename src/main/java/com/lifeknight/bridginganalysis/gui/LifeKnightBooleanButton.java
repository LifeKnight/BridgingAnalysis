package com.lifeknight.bridginganalysis.gui;

import com.lifeknight.bridginganalysis.variables.LifeKnightBoolean;
import net.minecraft.client.Minecraft;

public class LifeKnightBooleanButton extends LifeKnightButton {
    private final LifeKnightBoolean lifeKnightBoolean;

    public LifeKnightBooleanButton(int componentId, LifeKnightBoolean lifeKnightBoolean, LifeKnightButton connectedButton) {
        super(componentId, lifeKnightBoolean.getAsString());
        this.lifeKnightBoolean = lifeKnightBoolean;
        if (connectedButton != null) {
            connectedButton.xPosition = this.xPosition + this.width + 10;
        }
    }

    @Override
    public void work() {
        lifeKnightBoolean.toggle();
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.displayString = lifeKnightBoolean.getAsString();
        super.drawButton(mc, mouseX, mouseY);
    }
}
