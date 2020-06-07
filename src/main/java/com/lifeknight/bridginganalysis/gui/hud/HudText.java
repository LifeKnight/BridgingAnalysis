package com.lifeknight.bridginganalysis.gui.hud;

import com.lifeknight.bridginganalysis.gui.Manipulable;
import net.minecraft.client.Minecraft;

import static com.lifeknight.bridginganalysis.gui.hud.HudTextRenderer.textToRender;

public abstract class HudText extends Manipulable {
    private boolean dropShadow;

    public HudText(String name, boolean dropShadow) {
        super(name);
        this.dropShadow = dropShadow;
        textToRender.add(this);
    }

    public HudText(String name) {
        this(name, true);
    }

    public boolean isDropShadow() {
        return dropShadow;
    }

    public void setDropShadow(boolean dropShadow) {
        this.dropShadow = dropShadow;
    }

    public void render() {
        if (this.isVisible()) {
            Minecraft.getMinecraft().fontRendererObj.drawString(getDisplayText(), getXCoordinate(), getYCoordinate(), 0xffffffff, dropShadow);
        }
    }

    @Override
    public abstract String getDisplayText();

    public abstract boolean isVisible();
}
