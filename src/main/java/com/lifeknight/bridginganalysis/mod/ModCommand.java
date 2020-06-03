package com.lifeknight.bridginganalysis.mod;

import com.lifeknight.bridginganalysis.gui.LifeKnightGui;
import com.lifeknight.bridginganalysis.utilities.Chat;
import com.lifeknight.bridginganalysis.utilities.Text;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static com.lifeknight.bridginganalysis.mod.BridgingAnalysis.getAnalyses;
import static net.minecraft.util.EnumChatFormatting.*;
import static com.lifeknight.bridginganalysis.mod.Mod.*;

public class ModCommand extends CommandBase {
	private final List<String> aliases = Collections.singletonList("ba");
	private final String[] mainCommands = {"open", "latest"};

	public String getCommandName() {
		return modID;
	}

	public String getCommandUsage(ICommandSender arg0) {
		return modID;
	}

	public List<String> addTabCompletionOptions(ICommandSender arg0, String[] arg1, BlockPos arg2) {

		if (arg1.length >= 1) {
			return Text.returnStartingEntries(new ArrayList<>(Arrays.asList(mainCommands)), arg1[0]);
		}

		return new ArrayList<>(Arrays.asList(mainCommands));
	}

	public boolean canCommandSenderUseCommand(ICommandSender arg0) {
		return true;
	}

	public List<String> getCommandAliases() {
		return aliases;
	}

	public boolean isUsernameIndex(String[] arg0, int arg1) {
		return false;
	}

	public int compareTo(ICommand o) {
		return 0;
	}

	public void processCommand(ICommandSender arg0, String[] arg1) throws CommandException {
		if (arg1.length > 0) {
			if (arg1[0].equalsIgnoreCase(mainCommands[0])) {
				if (getAnalyses().size() != 0) {
					openGui(new BridgingAnalysisGui(getAnalyses().get(0)));
				} else {
					Chat.addErrorMessage("There is no BridgingAnalysis session to display.");
				}
			} else if (arg1[0].equalsIgnoreCase(mainCommands[1])) {
				if (getAnalyses().size() != 0) {
					openGui(new BridgingAnalysisGui(getAnalyses().get(getAnalyses().size() - 1)));
				} else {
					Chat.addErrorMessage("There is no BridgingAnalysis session to display.");
				}
			} else {
				addMainCommandMessage();
			}
		} else {
			openGui(new LifeKnightGui("[" + modVersion + "] " + modName, variables));
		}
	}

	public void addMainCommandMessage() {
		StringBuilder result = new StringBuilder(DARK_GREEN + "/" + modID);

		for (String command: mainCommands) {
			result.append(" ").append(command).append(",");
		}

		Chat.addChatMessage(result.substring(0, result.length() - 1));
	}
}
