package com.lifeknight.bridginganalysis.mod;

import com.lifeknight.bridginganalysis.gui.LifeKnightButton;
import com.lifeknight.bridginganalysis.gui.LifeKnightTextField;
import com.lifeknight.bridginganalysis.utilities.Misc;
import com.lifeknight.bridginganalysis.utilities.Text;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

import static com.lifeknight.bridginganalysis.mod.BridgingAnalysis.getAnalyses;
import static com.lifeknight.bridginganalysis.mod.Mod.decimalCount;
import static com.lifeknight.bridginganalysis.mod.Mod.openGui;
import static com.lifeknight.bridginganalysis.utilities.Misc.getScaledHeight;
import static com.lifeknight.bridginganalysis.utilities.Misc.getScaledWidth;
import static net.minecraft.util.EnumChatFormatting.*;

public class BridgingAnalysisGui extends GuiScreen {
    private final BridgingAnalysis bridgingAnalysis;
    private final GuiScreen previousGuiScreen;
    private LifeKnightTextField navigateField;

    public BridgingAnalysisGui(BridgingAnalysis bridgingAnalysis) {
        this(bridgingAnalysis, null);
    }

    public BridgingAnalysisGui(BridgingAnalysis bridgingAnalysis, GuiScreen previousGuiScreen) {
        this.bridgingAnalysis = bridgingAnalysis;
        this.previousGuiScreen = previousGuiScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        navigateField.drawTextBoxAndName();
        navigateField.drawStringBelowBox();
        drawCenteredString(fontRendererObj, DARK_GREEN + "[" + bridgingAnalysis.getDate() + " - " + bridgingAnalysis.getTime() + " - " + bridgingAnalysis.getServerIp() + "]", super.width / 2, getScaledHeight(10), 0xffffffff);
        drawCenteredString(fontRendererObj, AQUA + "Total Elapsed: " + Text.formatTimeFromMilliseconds(bridgingAnalysis.getTotalMilliseconds()), super.width / 2, getScaledHeight(40), 0xffffffff);
        drawCenteredString(fontRendererObj, YELLOW + "Blocks Placed: " + bridgingAnalysis.getBlocksPlacedCount(), super.width / 2, getScaledHeight(70), 0xffffffff);
        drawCenteredString(fontRendererObj, BLUE + "Average Movement Speed: " + Text.shortenDouble(bridgingAnalysis.getAverageMovementSpeed(), decimalCount.getValue()) + " m/s", super.width / 2, getScaledHeight(100), 0xffffffff);
        drawCenteredString(fontRendererObj, BLUE + "Average Placement Speed: " + Text.shortenDouble(bridgingAnalysis.getAveragePlacementSpeed(),decimalCount.getValue()) + " b/s", super.width / 2, getScaledHeight(130), 0xffffffff);
        drawCenteredString(fontRendererObj, GOLD + "Average Time Spent Shifting: " + Text.shortenDouble(bridgingAnalysis.getAverageShiftingTime(), decimalCount.getValue()) + " seconds", super.width / 2, getScaledHeight(160), 0xffffffff);
        drawCenteredString(fontRendererObj, GOLD + "Average Time Spent Waiting: " + Text.shortenDouble(bridgingAnalysis.getAverageWaitingTime(), decimalCount.getValue()) + " seconds", super.width / 2, getScaledHeight(190), 0xffffffff);
        drawCenteredString(fontRendererObj, GOLD + "Average Time Spent Elevating: " + Text.shortenDouble(bridgingAnalysis.getAverageElevationTime(), decimalCount.getValue()) + " seconds", super.width / 2, getScaledHeight(220), 0xffffffff);
        drawCenteredString(fontRendererObj, GOLD + "Average Look Coordinates: " + Text.shortenDouble(bridgingAnalysis.getAverageXLook(), decimalCount.getValue()) + " : " + Text.shortenDouble(bridgingAnalysis.getAverageYLook(), decimalCount.getValue()), super.width / 2, getScaledHeight(250), 0xffffffff);
        drawCenteredString(fontRendererObj, DARK_AQUA + "Total Distance: " + Text.shortenDouble(bridgingAnalysis.getDistanceTraveled(), decimalCount.getValue()) + " blocks", super.width / 2, getScaledHeight(280), 0xffffffff);
        drawCenteredString(fontRendererObj, GREEN + "Jumps: " + bridgingAnalysis.getJumpCount(), super.width / 2, getScaledHeight(310), 0xffffffff);
        drawCenteredString(fontRendererObj, RED + "Average CPS: " + Text.shortenDouble(bridgingAnalysis.getAverageClicksPerSecond(), decimalCount.getValue()), super.width / 2, getScaledHeight(340), 0xffffffff);
        drawCenteredString(fontRendererObj, DARK_RED + "Unused Clicks: " + bridgingAnalysis.getWastedClicks(), super.width / 2, getScaledHeight(370), 0xffffffff);
        drawCenteredString(fontRendererObj, LIGHT_PURPLE + "Detected Type: " + bridgingAnalysis.detectBridgeType(), super.width / 2, getScaledHeight(400), 0xffffffff);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        ((LifeKnightButton) button).work();
    }

    @Override
    public void initGui() {
        Misc.height = super.height;
        Misc.width = super.width;
        if (previousGuiScreen != null) {
            super.buttonList.add(new LifeKnightButton("Back", super.buttonList.size(), getScaledHeight(10), getScaledHeight(10), 50) {
                @Override
                public void work() {
                    openGui(previousGuiScreen);
                }
            });
        }

        if (getAnalyses().indexOf(bridgingAnalysis) != 0) {
            super.buttonList.add(new LifeKnightButton("<", super.buttonList.size(), 10, Misc.getGameHeight() - 30, 20) {
                @Override
                public void work() {
                    openGui(new BridgingAnalysisGui(getAnalyses().get(getAnalyses().indexOf(bridgingAnalysis) - 1)));
                }
            });
        }

        if (getAnalyses().indexOf(bridgingAnalysis) != getAnalyses().size() - 1) {
            super.buttonList.add(new LifeKnightButton(">", super.buttonList.size(), Misc.getGameWidth() - 30, Misc.getGameHeight() - 30, 20) {
                @Override
                public void work() {
                    openGui(new BridgingAnalysisGui(getAnalyses().get(getAnalyses().indexOf(bridgingAnalysis) + 1)));
                }
            });
        }

        navigateField = new LifeKnightTextField(super.buttonList.size(), super.width / 2 - getScaledWidth(75), super.height - getScaledHeight(20) - 30, getScaledWidth(150), 15, getAnalyses().indexOf(bridgingAnalysis) + 1 + " of " + getAnalyses().size()) {
            private String subDisplayMessage = "";

            @Override
            public void handleInput() {
                this.lastInput = this.getText();
                this.setText("");
                if (!lastInput.isEmpty()) {
                    try {
                        int requestedPage = Integer.parseInt(this.lastInput);

                        if (requestedPage > 0 && requestedPage <= getAnalyses().size()) {
                            openGui(new BridgingAnalysisGui(getAnalyses().get(requestedPage - 1)));
                        } else {
                            subDisplayMessage = RED + "The requested page is out of bounds.";
                        }
                    } catch (Exception e) {
                        subDisplayMessage = RED + "Invalid input!";
                    }
                }
            }

            @Override
            public String getSubDisplayString() {
                return subDisplayMessage;
            }
        };
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode != 1) {
            navigateField.textboxKeyTyped(typedChar, keyCode);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        navigateField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
