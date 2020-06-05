package com.lifeknight.bridginganalysis.mod;

import com.lifeknight.bridginganalysis.gui.LifeKnightButton;
import com.lifeknight.bridginganalysis.gui.LifeKnightTextField;
import com.lifeknight.bridginganalysis.utilities.Misc;
import com.lifeknight.bridginganalysis.utilities.Text;
import com.lifeknight.bridginganalysis.variables.LifeKnightCycle;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

import static com.lifeknight.bridginganalysis.mod.BridgingAnalysis.getAnalyses;
import static com.lifeknight.bridginganalysis.mod.Mod.*;
import static com.lifeknight.bridginganalysis.utilities.Misc.getScaledHeight;
import static com.lifeknight.bridginganalysis.utilities.Misc.getScaledWidth;
import static net.minecraft.util.EnumChatFormatting.*;

public class BridgingAnalysisGui extends GuiScreen {
    private final BridgingAnalysis bridgingAnalysis;
    private final GuiScreen previousGuiScreen;
    private LifeKnightTextField navigateField, dateField;

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

        if (navigateField != null) {
            navigateField.drawTextBoxAndName();
            navigateField.drawStringBelowBox();
        }

        dateField.drawTextBoxAndName();
        dateField.drawStringBelowBox();
        if (bridgingAnalysis.getTime().isEmpty()) {
            drawCenteredString(fontRendererObj, GRAY + "There is no BridgingAnalysis session to display", super.width / 2, super.height / 2, 0xffffffff);
        } else {
            drawCenteredString(fontRendererObj, DARK_GREEN + "[" + bridgingAnalysis.getDate() + " - " + bridgingAnalysis.getTime() + " - " + bridgingAnalysis.getServerIp() + "]", super.width / 2, getScaledHeight(10), 0xffffffff);
            drawCenteredString(fontRendererObj, AQUA + "Total Elapsed: " + Text.formatTimeFromMilliseconds(bridgingAnalysis.getTotalMilliseconds()), super.width / 2, getScaledHeight(40), 0xffffffff);
            drawCenteredString(fontRendererObj, YELLOW + "Blocks Placed: " + bridgingAnalysis.getBlocksPlacedCount(), super.width / 2, getScaledHeight(70), 0xffffffff);
            drawCenteredString(fontRendererObj, BLUE + "Average Movement Speed: " + Text.shortenDouble(bridgingAnalysis.getAverageMovementSpeed(), decimalCount.getValue()) + " m/s", super.width / 2, getScaledHeight(100), 0xffffffff);
            drawCenteredString(fontRendererObj, BLUE + "Average Placement Speed: " + Text.shortenDouble(bridgingAnalysis.getAveragePlacementSpeed(), decimalCount.getValue()) + " b/s", super.width / 2, getScaledHeight(130), 0xffffffff);
            drawCenteredString(fontRendererObj, GOLD + "Average Time Spent Shifting: " + Text.shortenDouble(bridgingAnalysis.getAverageShiftingTime(), decimalCount.getValue()) + " seconds", super.width / 2, getScaledHeight(160), 0xffffffff);
            drawCenteredString(fontRendererObj, GOLD + "Average Time Spent Waiting: " + Text.shortenDouble(bridgingAnalysis.getAverageWaitingTime(), decimalCount.getValue()) + " seconds", super.width / 2, getScaledHeight(190), 0xffffffff);
            drawCenteredString(fontRendererObj, GOLD + "Average Time Spent Elevating: " + Text.shortenDouble(bridgingAnalysis.getAverageElevationTime(), decimalCount.getValue()) + " seconds", super.width / 2, getScaledHeight(220), 0xffffffff);
            drawCenteredString(fontRendererObj, GOLD + "Average Look Coordinates: " + Text.shortenDouble(bridgingAnalysis.getAverageXLook(), decimalCount.getValue()) + " : " + Text.shortenDouble(bridgingAnalysis.getAverageYLook(), decimalCount.getValue()) + " : " + Text.shortenDouble(bridgingAnalysis.getAverageZLook(), decimalCount.getValue()), super.width / 2, getScaledHeight(250), 0xffffffff);
            drawCenteredString(fontRendererObj, DARK_AQUA + "Total Distance: " + Text.shortenDouble(bridgingAnalysis.getDistanceTraveled(), decimalCount.getValue()) + " meters", super.width / 2, getScaledHeight(280), 0xffffffff);
            drawCenteredString(fontRendererObj, GREEN + "Jumps: " + bridgingAnalysis.getJumpCount(), super.width / 2, getScaledHeight(310), 0xffffffff);
            drawCenteredString(fontRendererObj, RED + "Average CPS: " + Text.shortenDouble(bridgingAnalysis.getAverageClicksPerSecond(), decimalCount.getValue()), super.width / 2, getScaledHeight(340), 0xffffffff);
            drawCenteredString(fontRendererObj, DARK_RED + "Unused Clicks: " + bridgingAnalysis.getWastedClicks(), super.width / 2, getScaledHeight(370), 0xffffffff);
            drawCenteredString(fontRendererObj, LIGHT_PURPLE + "Detected Type: " + bridgingAnalysis.detectBridgeType(), super.width / 2, getScaledHeight(400), 0xffffffff);
        }
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

        if (getAnalyses().indexOf(bridgingAnalysis) != 0 && !bridgingAnalysis.getTime().isEmpty()) {
            super.buttonList.add(new LifeKnightButton("<", super.buttonList.size(), getScaledWidth(15), getScaledHeight(475), 20) {
                @Override
                public void work() {
                    openGui(new BridgingAnalysisGui(getAnalyses().get(getAnalyses().indexOf(bridgingAnalysis) - 1)));
                }
            });
        }

        if (getAnalyses().indexOf(bridgingAnalysis) != getAnalyses().size() - 1 && !bridgingAnalysis.getTime().isEmpty()) {
            super.buttonList.add(new LifeKnightButton(">", super.buttonList.size(), getScaledWidth(917), getScaledHeight(475), 20) {
                @Override
                public void work() {
                    openGui(new BridgingAnalysisGui(getAnalyses().get(getAnalyses().indexOf(bridgingAnalysis) + 1)));
                }
            });
        }

        super.buttonList.add(new LifeKnightButton(filterType.getCurrentValueString().equals("Breezily/Godbridge") ? "Bridge Type: " + LIGHT_PURPLE + "Breezily/GB" : "Bridge Type: " + LIGHT_PURPLE + filterType.getCurrentValueString(), super.buttonList.size(), getScaledWidth(5), getScaledHeight(10), getScaledWidth(200)) {
            @Override
            public void work() {
                filterType.next();
                openGui(new BridgingAnalysisGui(getAnalyses().get(0)));
            }
        });

        super.buttonList.add(new LifeKnightButton("Direction: " + GOLD + direction.getCurrentValueString(), super.buttonList.size(), getScaledWidth(5), getScaledHeight(60), getScaledWidth(175)) {
            @Override
            public void work() {
                direction.next();
                openGui(new BridgingAnalysisGui(getAnalyses().get(0)));
            }
        });

        super.buttonList.add(new LifeKnightButton("Enter", super.buttonList.size(), getScaledWidth(5), getScaledHeight(220), getScaledWidth(175)) {
            @Override
            public void work() {
                dateField.handleInput();
            }
        });

        super.buttonList.add(new LifeKnightButton("Reset", super.buttonList.size(), getScaledWidth(5), getScaledHeight(270), getScaledWidth(175)) {
            @Override
            public void work() {
                dateToSearch = "";
                openGui(new BridgingAnalysisGui(getAnalyses().get(0)));
            }
        });

        super.buttonList.add(new LifeKnightButton("Sort By: " + sortBy.getCurrentValueString(), super.buttonList.size(), getScaledWidth(5), getScaledHeight(320), getScaledWidth(175)) {
            @Override
            public void work() {
                sortBy.next();
                openGui(new BridgingAnalysisGui(getAnalyses().get(0)));
            }
        });

        dateField = new LifeKnightTextField(super.buttonList.size(), getScaledWidth(5), getScaledHeight(140), getScaledWidth(175), 15, "Date Search") {

            @Override
            public void handleInput() {
                this.lastInput = this.getText();
                this.setText("");
                if (!lastInput.isEmpty()) {
                    if (lastInput.equalsIgnoreCase("today")) {
                        dateToSearch = Misc.getCurrentDate();
                        openGui(new BridgingAnalysisGui(getAnalyses().get(0)));
                    } else {
                        try {
                            String monthSection = this.lastInput.substring(0, this.lastInput.indexOf("/"));
                            int month = Integer.parseInt(monthSection);
                            String daySection = this.lastInput.substring(this.lastInput.indexOf("/") + 1, this.lastInput.lastIndexOf("/"));
                            int day = Integer.parseInt(daySection);
                            String yearSection = this.lastInput.substring(this.lastInput.lastIndexOf("/") + 1);
                            int year = yearSection.length() == 2 ? Integer.parseInt("20" + yearSection) : Integer.parseInt(yearSection);

                            if (month < 1 || month > 12 || day < 1 || day > 31 || year < 1000 || year > 9999) {
                                subDisplayMessage = RED + "Invalid input!";
                            } else {
                                String formattedDate;

                                if (month < 10) {
                                    formattedDate = "0" + month;
                                } else {
                                    formattedDate = monthSection;
                                }

                                formattedDate += "/";

                                if (day < 10) {
                                    formattedDate += "0" + day;
                                } else {
                                    formattedDate += daySection;
                                }

                                formattedDate += "/";

                                formattedDate += String.valueOf(year);

                                dateToSearch = formattedDate;
                                openGui(new BridgingAnalysisGui(getAnalyses().get(0)));
                            }
                        } catch (Exception e) {
                            subDisplayMessage = RED + "Invalid input!";
                        }
                    }
                }
            }

            @Override
            public String getSubDisplayString() {
                return this.subDisplayMessage;
            }

            public void setSubDisplayMessage(String subDisplayMessage) {
                this.subDisplayMessage = subDisplayMessage;
            }
        };
        dateField.setMaxStringLength(10);
        if (!dateToSearch.isEmpty()) {
            dateField.setSubDisplayMessage(dateToSearch);
        }
        if (!bridgingAnalysis.getTime().isEmpty()) {
            navigateField = new LifeKnightTextField(super.buttonList.size() + 1, super.width / 2 - getScaledWidth(75), super.height - getScaledHeight(20) - 30, getScaledWidth(150), 15, getAnalyses().indexOf(bridgingAnalysis) + 1 + " of " + getAnalyses().size()) {
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
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode != 1) {
            if (navigateField != null) {
                navigateField.textboxKeyTyped(typedChar, keyCode);
            }
            dateField.textboxKeyTyped(typedChar, keyCode);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (navigateField != null) {
            navigateField.mouseClicked(mouseX, mouseY, mouseButton);
        }
        dateField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
