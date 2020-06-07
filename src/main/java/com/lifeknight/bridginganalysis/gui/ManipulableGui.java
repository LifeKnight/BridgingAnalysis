package com.lifeknight.bridginganalysis.gui;

import com.lifeknight.bridginganalysis.gui.components.LifeKnightButton;
import com.lifeknight.bridginganalysis.gui.components.ManipulableButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

import static com.lifeknight.bridginganalysis.gui.Manipulable.manipulableComponents;


public class ManipulableGui extends GuiScreen {
    @Override
    public void initGui() {
        for (Manipulable manipulable : manipulableComponents) {
            super.buttonList.add(new ManipulableButton(manipulable));
        }
        super.buttonList.add(new LifeKnightButton(super.buttonList.size(), super.width / 2 - 100, super.height - 30, 200, 20, "Reset") {
            @Override
            public void work() {
                for (GuiButton guiButton : ManipulableGui.super.buttonList) {
                    if (guiButton instanceof ManipulableButton) {
                        ((ManipulableButton) guiButton).resetPosition();
                    }
                }
            }
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        for (GuiButton guiButton : super.buttonList) {
            ((LifeKnightButton) guiButton).work();
        }
    }
}
