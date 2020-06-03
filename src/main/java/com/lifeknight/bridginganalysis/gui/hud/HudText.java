package com.lifeknight.bridginganalysis.gui.hud;

import net.minecraft.client.Minecraft;

import static com.lifeknight.bridginganalysis.gui.hud.HudTextRenderer.textToRender;

public abstract class HudText {
    private boolean dropShadow;

    public HudText(boolean dropShadow) {
        this.dropShadow = dropShadow;
        textToRender.add(this);
    }

    public HudText() {
        this(true);
    }

    public boolean isDropShadow() {
        return dropShadow;
    }

    public abstract boolean isVisible();

    public void setDropShadow(boolean dropShadow) {
        this.dropShadow = dropShadow;
    }

    public void render() {
        if (this.isVisible()) {
            Minecraft.getMinecraft().fontRendererObj.drawString(getTextToDisplay(), getXCoordinate(), getYCoordinate(), 0xffffffff, dropShadow);
        }
    }

    public abstract String getTextToDisplay();

    public abstract int getXCoordinate();

    public abstract int getYCoordinate();
}
