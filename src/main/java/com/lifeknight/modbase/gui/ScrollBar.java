package com.lifeknight.modbase.gui;

import com.lifeknight.modbase.utilities.Misc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

public abstract class ScrollBar extends GuiButton {
    public boolean dragging = false;
    public int startY = 0;
    public ScrollBar() {
        super(-1, Misc.width - 7, 0, 5, Misc.height, "");
        this.visible = false;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0xffffffff);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    @Override
    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
    {
        if (super.mousePressed(par1Minecraft, par2, par3)) {
            startY = par3;
            dragging = true;
            return true;
        } else {
            return false;
        }
    }

    public void mouseReleased(int par1, int par2)
    {
        dragging = false;
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {}

    @Override
    public abstract void mouseDragged(Minecraft mc, int mouseX, int mouseY);
}
