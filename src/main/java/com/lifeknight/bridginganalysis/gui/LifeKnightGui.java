package com.lifeknight.bridginganalysis.gui;

import com.lifeknight.bridginganalysis.utilities.Misc;
import com.lifeknight.bridginganalysis.variables.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.lifeknight.bridginganalysis.utilities.Misc.*;
import static com.lifeknight.bridginganalysis.mod.Mod.*;
import static net.minecraft.util.EnumChatFormatting.*;

public class LifeKnightGui extends GuiScreen {
    private final String name;
    private final ArrayList<LifeKnightVariable> guiVariables = new ArrayList<>();
    private final ArrayList<LifeKnightTextField> textFields = new ArrayList<>();
    private int panelHeight = 0;
    private final ArrayList<GuiButton> variableButtons = new ArrayList<>();
    private final ArrayList<LifeKnightButton> extraButtons = new ArrayList<>();
    private ScrollBar scrollBar;
    private LifeKnightTextField searchField;
    private String searchInput = "";
    private String listMessage = "";
    private final ArrayList<String> groupNames = new ArrayList<>(Collections.singletonList("All"));
    public String selectedGroup = "All";
    public int toScroll = 0;

    public LifeKnightGui(String name, ArrayList<LifeKnightVariable> lifeKnightVariables) {
        this.name = name;
        guiVariables.addAll(lifeKnightVariables);
        for (LifeKnightVariable lifeKnightVariable: guiVariables) {
            if (!groupNames.contains(lifeKnightVariable.getGroup())) {
                groupNames.add(lifeKnightVariable.getGroup());
            }
        }
    }

    public LifeKnightGui(String name, LifeKnightVariable... lifeKnightVariables) {
        this(name, new ArrayList<>(Arrays.asList(lifeKnightVariables)));
    }

    public LifeKnightGui(String name, ArrayList<LifeKnightVariable> lifeKnightVariables, ArrayList<LifeKnightButton> extraButtons) {
        this(name, lifeKnightVariables);
        this.extraButtons.addAll(extraButtons);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        super.drawCenteredString(fontRendererObj, modColor + name, getScaledWidth(150), getScaledHeight(60), 0xffffffff);
        super.drawCenteredString(fontRendererObj, listMessage, get2ndPanelCenter(), super.height / 2, 0xffffffff);
        super.drawVerticalLine(getScaledWidth(300), 0, super.height, 0xffffffff);
        searchField.drawTextBoxAndName();

        for (int i = 0; i < groupNames.size() - 1; i++) {
            drawHorizontalLine(getScaledWidth(100), getScaledWidth(200), getScaledHeight(150) + 25 * i + 22, 0xffffffff);
        }

        if (variableButtons.size() != 0) {
            scrollBar.height = (int) (super.height * (super.height / (double) panelHeight));
            int j = Mouse.getDWheel() / 7;
            scrollBar.visible = !(scrollBar.height >= super.height);
            if (((j > 0 || toScroll > 0) && variableButtons.get(0).yPosition < 20) || ((j < 0 || toScroll < 0) && variableButtons.get(variableButtons.size() - 1).yPosition + 60 > super.height)) {
                for (GuiButton guiButton: variableButtons) {
                    if (toScroll == 0) {
                        guiButton.yPosition += j;
                    } else {
                        guiButton.yPosition += Math.ceil(toScroll * ((double) Misc.height / getSupposedHeight()));
                    }
                }
                for (LifeKnightTextField lifeKnightTextField: textFields) {
                    if (toScroll == 0) {
                        lifeKnightTextField.yPosition += j;
                    } else {
                        lifeKnightTextField.yPosition += Math.ceil(0.8 * toScroll * ((double) Misc.height / getSupposedHeight()));
                    }
                }
                if (toScroll == 0) {
                    scrollBar.yPosition -= Math.ceil(0.8 * j * ((double) Misc.height / getSupposedHeight()));
                } else {
                    scrollBar.yPosition -= Math.ceil(0.8 * toScroll * ((double) Misc.height / getSupposedHeight()));
                }
            }
            toScroll = 0;
            if (scrollBar.yPosition + scrollBar.height > super.height) {
                scrollBar.yPosition = super.height - scrollBar.height;
            }
            if (variableButtons.get(0).yPosition > 20 && scrollBar.yPosition != 0) {
                listItems();
            }
            if (scrollBar.yPosition < 0) {
                scrollBar.yPosition = 0;
            }
        } else {
            scrollBar.visible = false;
        }

        for (LifeKnightTextField lifeKnightTextField: textFields) {
            if (((selectedGroup.equals("All") || selectedGroup.equals(lifeKnightTextField.lifeKnightString.getGroup())) && (searchInput.isEmpty() || lifeKnightTextField.lifeKnightString.getLowerCaseName().contains(searchInput.toLowerCase())))) {
                lifeKnightTextField.drawTextBoxAndName();
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void initGui() {
        Misc.height = super.height;
        Misc.width = super.width;
        searchField = new LifeKnightTextField(0, getScaledWidth(75), this.height - 40, getScaledWidth(150), 20, "Search") {
            @Override
            public String getSubDisplayString() {
                return null;
            }

            @Override
            public boolean textboxKeyTyped(char p_146201_1_, int p_146201_2_) {
                if (super.textboxKeyTyped(p_146201_1_, p_146201_2_)) {
                    this.handleInput();
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void handleInput() {
                searchInput = this.getText();
                listItems();
            }
        };

        listItems();
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        for (GuiButton guiButton: super.buttonList) {
            if (guiButton instanceof LifeKnightButton) {
                if (button == guiButton) {
                    ((LifeKnightButton) guiButton).work();
                    break;
                }
            }
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode != 1) {
            searchField.textboxKeyTyped(typedChar, keyCode);
            for (LifeKnightTextField lifeKnightTextField: textFields) {
                lifeKnightTextField.textboxKeyTyped(typedChar, keyCode);
            }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        for (LifeKnightTextField lifeKnightTextField: textFields) {
            lifeKnightTextField.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void listItems() {
        textFields.clear();
        super.buttonList.clear();
        variableButtons.clear();
        panelHeight = 5;
        boolean variablesFound = false;
        int componentId = 0;

        for (LifeKnightVariable lifeKnightVariable: guiVariables) {
            if (((selectedGroup.equals("All") || selectedGroup.equals(lifeKnightVariable.getGroup())) && (searchInput.isEmpty() || lifeKnightVariable.getLowerCaseName().contains(searchInput.toLowerCase())))) {
                variablesFound = true;
                if (lifeKnightVariable instanceof LifeKnightBoolean) {
                    if (((LifeKnightBoolean) lifeKnightVariable).hasStringList()) {
                        LifeKnightGui copy = this;
                        LifeKnightButton open;
                        variableButtons.add(open = new LifeKnightButton(componentId,
                                Misc.get2ndPanelCenter() + 110,
                                (20 + (componentId * 30)),
                                20,
                                20, ">") {
                            @Override
                            public void work() {
                                openGui(new ListGui(((LifeKnightBoolean) lifeKnightVariable).getLifeKnightStringList(), copy));
                            }
                        });
                        variableButtons.add(new LifeKnightBooleanButton(componentId, (LifeKnightBoolean) lifeKnightVariable, open));
                    } else {
                        variableButtons.add(new LifeKnightBooleanButton(componentId, (LifeKnightBoolean) lifeKnightVariable, null));
                    }
                    panelHeight += 30;
                    componentId++;
                } else if (lifeKnightVariable instanceof LifeKnightInteger) {
                    variableButtons.add(new LifeKnightSlider(componentId, false, (LifeKnightInteger) lifeKnightVariable));
                    panelHeight += 30;
                    componentId++;
                } else if (lifeKnightVariable instanceof LifeKnightDouble) {
                    variableButtons.add(new LifeKnightSlider(componentId, true, (LifeKnightDouble) lifeKnightVariable));
                    panelHeight += 30;
                    componentId++;
                } else if (lifeKnightVariable instanceof LifeKnightString) {
                    int i = textFields.size();
                    textFields.add(new LifeKnightTextField(componentId + 1, (LifeKnightString) lifeKnightVariable) {
                        @Override
                        public void handleInput() {
                            if (!this.getText().isEmpty()) {
                                this.lastInput = this.getText();
                                this.setText("");
                                this.lifeKnightString.setValue(this.lastInput);
                                this.name = this.lifeKnightString.getName() + ": " + YELLOW + this.lifeKnightString.getValue();
                            }
                        }

                        @Override
                        public String getSubDisplayString() {
                            return null;
                        }
                    });
                    variableButtons.add(new LifeKnightButton(componentId, get2ndPanelCenter() + 110,
                            (20 + ((componentId + 1) * 30)),
                            20,
                            20, ">") {
                        @Override
                        public void work() {
                            textFields.get(i).handleInput();
                        }
                    });
                    panelHeight += 60;
                    componentId += 2;
                } else if (lifeKnightVariable instanceof LifeKnightCycle) {
                    variableButtons.add(new LifeKnightButton(componentId, lifeKnightVariable.getName() + ": " + YELLOW + ((LifeKnightCycle) lifeKnightVariable).getCurrentValueString()) {
                        @Override
                        public void work() {
                            this.displayString = lifeKnightVariable.getName() + ": " + YELLOW + ((LifeKnightCycle) lifeKnightVariable).next();
                        }
                    });
                    panelHeight += 30;
                    componentId++;
                }
            }
        }

        super.buttonList.addAll(variableButtons);

        for (LifeKnightButton lifeKnightButton: extraButtons) {
            if ((selectedGroup.equals("All") && (searchInput.isEmpty() || lifeKnightButton.displayString.toLowerCase().contains(searchInput.toLowerCase())))) {

                lifeKnightButton.xPosition = get2ndPanelCenter() - 100;
                lifeKnightButton.yPosition = componentId * 30 + 20;
                lifeKnightButton.id = componentId;

                super.buttonList.add(lifeKnightButton);

                panelHeight += 30;
                componentId++;
            }
        }

        for (int i = 0; i < groupNames.size(); i++) {
            int finalI = i;
            super.buttonList.add(new LifeKnightButton(super.buttonList.size() - 1, getScaledWidth(100), getScaledHeight(150) + 25 * i, getScaledWidth(100), 20, groupNames.get(i)) {
                final String name = groupNames.get(finalI);

                @Override
                public void work() {
                    selectedGroup = name;
                    listItems();
                }

                @Override
                public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                    if (this.visible) {
                        FontRenderer fontrenderer = mc.fontRendererObj;
                        this.displayString = selectedGroup.equals(name) ? modColor + "" + BOLD + selectedGroup : name;
                        this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, 0xffffffff);
                    }
                }
            });
        }
        listMessage = variablesFound ? "" : GRAY + "No settings found";

        super.buttonList.add(scrollBar = new ScrollBar() {
            @Override
            public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
                if (super.visible && this.dragging) {
                    toScroll = this.startY - mouseY;
                    this.startY = mouseY;
                }
            }
        });
    }
}
