package com.lifeknight.bridginganalysis.gui.components;

import com.lifeknight.bridginganalysis.gui.Manipulable;
import com.lifeknight.bridginganalysis.utilities.Misc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;

public class ManipulableButton extends LifeKnightButton {
    private final Manipulable manipulable;
    private boolean isSelectedButton = false;
    private boolean dragging = false;
    private int originalXPosition;
    private int originalYPosition;
    private int originalMouseXPosition;
    private int originalMouseYPosition;

    public ManipulableButton(Manipulable manipulable) {
        super(Manipulable.manipulableComponents.indexOf(manipulable),
                Misc.scaleFrom1080pWidth(manipulable.positionX.getValue() - 3),
                Misc.scaleFrom1080pHeight(manipulable.positionY.getValue() - 3),
                Minecraft.getMinecraft().fontRendererObj.getStringWidth(manipulable.getDisplayText() + 2),
                16,
                manipulable.getDisplayText());
        this.manipulable = manipulable;

        if (this.xPosition < 0 || this.xPosition + this.width > Misc.getGameWidth() ||
            this.yPosition < 0 || this.yPosition + this.height > Misc.getGameHeight()) {
            this.xPosition = 3;
            this.yPosition = 3;
        }
    }


    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        this.displayString = manipulable.getDisplayText();
        if (this.visible) {
            if (isSelectedButton) {
                drawEmptyBox(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0xffffffff);
            }
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            this.drawCenteredString(Minecraft.getMinecraft().fontRendererObj, this.displayString, this.xPosition + super.width / 2, this.yPosition + (super.height - 8) / 2, 0xffffffff);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    @Override
    public boolean mousePressed(Minecraft par1Minecraft, int mouseX, int mouseY) {
        if (super.mousePressed(par1Minecraft, mouseX, mouseY)) {
            isSelectedButton = true;
            dragging = true;
            originalMouseXPosition = mouseX;
            originalMouseYPosition = mouseY;
            originalXPosition = this.xPosition;
            originalYPosition = this.yPosition;
            return true;
        } else {
            isSelectedButton = false;
            return false;
        }
    }

    @Override
    public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (super.visible && this.dragging) {
            int newXPosition = originalXPosition + mouseX - originalMouseXPosition;
            int newYPosition =  originalYPosition + mouseY - originalMouseYPosition;
            if (!(newXPosition < 0) && !(newXPosition + this.width > Misc.getGameWidth())) {
                this.xPosition = newXPosition;
            }
            if (!(newYPosition < 0) && !(newYPosition + this.height > Misc.getGameHeight())) {
                this.yPosition = newYPosition;
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        dragging = false;
        manipulable.updatePosition(this.xPosition, this.yPosition);
    }
    @Override
    public void work() {
        isSelectedButton = true;
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {}

    public void drawEmptyBox(int left, int top, int right, int bottom, int color) {
        drawHorizontalLine(left, right, top, color);
        drawHorizontalLine(left, right, bottom, color);

        drawVerticalLine(left, top, bottom, color);
        drawVerticalLine(right, top, bottom, color);
    }

    public void resetPosition() {
        manipulable.resetPosition();
        this.xPosition = Misc.scaleFrom1080pWidth(manipulable.positionX.getValue() - 3);
        this.yPosition = Misc.scaleFrom1080pHeight(manipulable.positionY.getValue() - 3);
    }
}
