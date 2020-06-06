package com.lifeknight.bridginganalysis.gui;

import com.lifeknight.bridginganalysis.gui.components.*;
import com.lifeknight.bridginganalysis.utilities.Misc;
import com.lifeknight.bridginganalysis.variables.LifeKnightStringList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

import static com.lifeknight.bridginganalysis.mod.Core.openGui;
import static com.lifeknight.bridginganalysis.utilities.Misc.*;
import static net.minecraft.util.EnumChatFormatting.GRAY;
import static net.minecraft.util.EnumChatFormatting.RED;

public class ListGui extends GuiScreen {
    private final ArrayList<ListItemButton> listItemButtons = new ArrayList<>();
    private final ArrayList<LifeKnightButton> lifeKnightButtons = new ArrayList<>();
    private final LifeKnightStringList lifeKnightStringList;
    private ConfirmButton clearButton;
    private ScrollBar scrollBar;
    private LifeKnightTextField addField, searchField;
    public static ListItemButton selectedItem;
    public LifeKnightButton addButton, removeButton;
    private String textFieldSubMessage = "", searchInput = "", listMessage = "";
    public LifeKnightGui lastGui;
    public int toScroll = 0;

    public ListGui(LifeKnightStringList lifeKnightStringList) {
        this.lifeKnightStringList = lifeKnightStringList;
        lastGui = null;
    }

    public ListGui(LifeKnightStringList lifeKnightStringList, LifeKnightGui lifeKnightGui) {
        this(lifeKnightStringList);
        lastGui = lifeKnightGui;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawCenteredString(fontRendererObj, listMessage, get2ndPanelCenter(), super.height / 2, 0xffffffff);
        drawVerticalLine(getScaledWidth(300), 0, super.height, 0xffffffff);
        searchField.drawTextBoxAndName();
        addField.drawTextBoxAndName();
        addField.drawStringBelowBox();

        if (listItemButtons.size() != 0) {
            int panelHeight = listItemButtons.size() * 30 + 20;

            scrollBar.height = (int) (super.height * (super.height / (double) panelHeight));
            int j = Mouse.getDWheel() / 7;
            scrollBar.visible = !(scrollBar.height >= super.height);
            if (((j > 0 || toScroll > 0) && listItemButtons.get(0).yPosition < 5) || ((j < 0 || toScroll < 0) && listItemButtons.get(listItemButtons.size() - 1).yPosition + 60 > super.height)) {
                for (ListItemButton listItemButton: listItemButtons) {
                    if (toScroll == 0) {
                        listItemButton.yPosition += j;
                    } else {
                        listItemButton.yPosition += Math.ceil(toScroll * ((double) Misc.height / getSupposedHeight()));
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
            if (listItemButtons.get(0).yPosition > 0 && scrollBar.yPosition != 0) {
                listItems();
            }
            if (scrollBar.yPosition < 0) {
                scrollBar.yPosition = 0;
            }
            if ((listItemButtons.get(listItemButtons.size() - 1).yPosition + j < super.height - 50) || (listItemButtons.get(listItemButtons.size() - 1).yPosition + toScroll < super.height - 50)) {
                scrollBar.yPosition = super.height - scrollBar.height;
            }
        } else {
            scrollBar.visible = false;
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

        addField = new LifeKnightTextField(1, getScaledWidth(75), getScaledHeight(115), getScaledWidth(150), 20, lifeKnightStringList.getName()) {
            @Override
            public void handleInput() {
                this.lastInput = this.getText();
                this.setText("");
                try {
                    if (!lastInput.isEmpty()) {
                        lifeKnightStringList.addElement(lastInput);
                        textFieldSubMessage = "";
                        listItems();
                    }
                } catch (IOException ioException) {
                    textFieldSubMessage = RED + ioException.getMessage();
                }
            }

            @Override
            public String getSubDisplayString() {
                return textFieldSubMessage;
            }
        };

        super.buttonList.add(addButton = new LifeKnightButton("Add", 2, getScaledWidth(75), getScaledHeight(195), getScaledWidth(150)) {
            @Override
            public void work() {
                addField.handleInput();
                selectedItem = null;
            }
        });

        super.buttonList.add(removeButton = new LifeKnightButton("Remove",3, getScaledWidth(75), getScaledHeight(260), getScaledWidth(150)) {
            @Override
            public void work() {
                removeSelectedButton();
            }
        });
        removeButton.visible = false;

        super.buttonList.add(clearButton = new ConfirmButton(4, getScaledWidth(75), getScaledHeight(325), getScaledWidth(150), "Clear", RED + "Confirm") {
            @Override
            public void onConfirm() {
                lifeKnightStringList.clear();
                listItems();
            }
        });
        clearButton.visible = false;

        super.buttonList.add(scrollBar = new ScrollBar() {
            @Override
            public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
                if (super.visible && this.dragging) {
                    toScroll = this.startY - mouseY;
                    this.startY = mouseY;
                }
            }
        });

        lifeKnightButtons.add(addButton);
        lifeKnightButtons.add(removeButton);
        lifeKnightButtons.add(clearButton);
        if (lastGui != null) {
            LifeKnightButton backButton;
            super.buttonList.add(backButton = new LifeKnightButton("Back", 5, getScaledHeight(10), getScaledHeight(10), 50) {
                @Override
                public void work() {
                    openGui(lastGui);
                }
            });
            lifeKnightButtons.add(backButton);
        }
        listItems();
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof ListItemButton) {
            for (ListItemButton listItemButton: listItemButtons) {
                if (button == listItemButton) {
                    listItemButton.work();
                    break;
                }
            }
        } else if (button instanceof LifeKnightButton) {
            for (LifeKnightButton lifeKnightButton: lifeKnightButtons) {
                if (button == lifeKnightButton) {
                    lifeKnightButton.work();
                    break;
                }
            }
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 0xD3 && selectedItem != null) {
            removeSelectedButton();
        } else {
            addField.textboxKeyTyped(typedChar, keyCode);
            searchField.textboxKeyTyped(typedChar, keyCode);
            super.keyTyped(typedChar, keyCode);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        addField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void removeSelectedButton() {
        try {
            lifeKnightStringList.removeElement(selectedItem.getButtonText());
            selectedItem.visible = false;
            removeButton.visible = false;
            selectedItem = null;
            listItems();
        } catch (IOException ioException) {
            textFieldSubMessage = RED + ioException.getMessage();
        } catch (NullPointerException ignored) {

        }
    }

    private void listItems() {
        listItemButtons.clear();
        this.buttonList.removeIf(guiButton -> guiButton instanceof ListItemButton);
        scrollBar.yPosition = 0;

        for (String element: lifeKnightStringList.getValue()) {
            if (searchInput.isEmpty() || element.toLowerCase().contains(searchInput.toLowerCase())) {
                ListItemButton listItemButton = new ListItemButton(listItemButtons.size() + 6, element) {
                    @Override
                    public void work() {
                        if (this.isSelectedButton) {
                            this.isSelectedButton = false;
                            selectedItem = null;
                            removeButton.visible = false;
                        } else {
                            this.isSelectedButton = true;
                            selectedItem = this;
                            removeButton.visible = true;
                        }
                    }
                };
                listItemButtons.add(listItemButton);
            }
        }
        listMessage = listItemButtons.size() == 0 ? GRAY + "No items found" : "";

        clearButton.visible = listItemButtons.size() > 1;

        this.buttonList.addAll(listItemButtons);
    }
}
