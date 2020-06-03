package com.lifeknight.modbase.mod;

import com.lifeknight.modbase.gui.LifeKnightGui;
import com.lifeknight.modbase.utilities.Chat;
import com.lifeknight.modbase.utilities.Text;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static com.lifeknight.modbase.mod.Core.*;
import static net.minecraft.util.EnumChatFormatting.*;

public class ModCommand extends CommandBase {
	private final List<String> aliases = Collections.singletonList("mb");
	private final String[] mainCommands = {};

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
		Core.openGui(new LifeKnightGui("[" + modVersion + "] " + modName, variables));
	}

	public void addMainCommandMessage() {
		StringBuilder result = new StringBuilder(DARK_GREEN + "/" + modID);

		for (String command: mainCommands) {
			result.append(" ").append(command).append(",");
		}

		Chat.addChatMessage(result.substring(0, result.length() - 1));
	}
}
