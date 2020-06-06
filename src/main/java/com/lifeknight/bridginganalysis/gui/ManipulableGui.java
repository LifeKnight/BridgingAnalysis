package com.lifeknight.bridginganalysis.gui;

import com.lifeknight.bridginganalysis.gui.components.ManipulableButton;
import net.minecraft.client.gui.GuiScreen;

import static com.lifeknight.bridginganalysis.gui.Manipulable.manipulableComponents;

public class ManipulableGui extends GuiScreen {
    @Override
    public void initGui() {
        for (Manipulable manipulable : manipulableComponents) {
            super.buttonList.add(new ManipulableButton(manipulable));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
