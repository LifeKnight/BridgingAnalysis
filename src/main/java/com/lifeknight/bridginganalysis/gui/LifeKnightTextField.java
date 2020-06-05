package com.lifeknight.bridginganalysis.gui;

import com.lifeknight.bridginganalysis.variables.LifeKnightString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

import static com.lifeknight.bridginganalysis.utilities.Misc.get2ndPanelCenter;
import static net.minecraft.util.EnumChatFormatting.YELLOW;

public abstract class LifeKnightTextField extends GuiTextField {
    private final int width;
    private final int height;
    public String name;
    public String subDisplayMessage;
    public LifeKnightString lifeKnightString = null;
    public String lastInput = "";

    public LifeKnightTextField(int componentId, int x, int y, int par5Width, int par6Height, String name, LifeKnightString lifeKnightString) {
        this(componentId, x, y, par5Width, par6Height, name);
        this.lifeKnightString = lifeKnightString;
    }

    public LifeKnightTextField(int componentId, int x, int y, int par5Width, int par6Height, String name) {
        super(componentId, Minecraft.getMinecraft().fontRendererObj, x, y, par5Width, par6Height);
        this.width = par5Width;
        this.height = par6Height;
        this.name = name;
        super.setMaxStringLength(100);
        this.setFocused(false);
        this.setCanLoseFocus(true);
    }

    public LifeKnightTextField(int componentId, LifeKnightString lifeKnightString) {
        this(componentId, get2ndPanelCenter() - 100,
                (20 + componentId * 30),
                200,
                20,
                lifeKnightString.getName() + ": " + YELLOW + lifeKnightString.getValue(),
                lifeKnightString);
    }

    public void drawTextBoxAndName() {
        super.drawTextBox();
        if (lifeKnightString != null) {
            name = lifeKnightString.getName() + ": " + YELLOW + lifeKnightString.getValue();
        }
        super.drawCenteredString(Minecraft.getMinecraft().fontRendererObj, name, this.xPosition + this.width / 2, this.yPosition - 15, 0xffffffff);
    }

    public void drawStringBelowBox() {
        super.drawCenteredString(Minecraft.getMinecraft().fontRendererObj, getSubDisplayString(), this.xPosition + this.width / 2, this.yPosition + this.height + 10, 0xffffffff);
    }

    public boolean textboxKeyTyped(char p_146201_1_, int p_146201_2_) {
        if (super.textboxKeyTyped(p_146201_1_, p_146201_2_)) {
            return true;
        } else if (this.isFocused() && p_146201_2_ == 0x1C) {
            this.handleInput();
            return true;
        }
        return false;
    }

    public void setSubDisplayMessage(String subDisplayMessage) {
        this.subDisplayMessage = subDisplayMessage;
    }

    public abstract void handleInput();

    public abstract String getSubDisplayString();
}
