/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.commands;

import net.minecraft.core.BlockPos;
import com.nsyl.client.command.CmdException;
import com.nsyl.client.command.CmdSyntaxError;
import com.nsyl.client.command.Command;
import com.nsyl.client.util.ChatUtils;

public final class GetPosCmd extends Command
{
	public GetPosCmd()
	{
		super("getpos", "Shows your current position.", ".getpos",
			"Copy to clipboard: .getpos copy");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		BlockPos pos = BlockPos.containing(MC.player.position());
		String posString = pos.getX() + " " + pos.getY() + " " + pos.getZ();
		
		switch(String.join(" ", args).toLowerCase())
		{
			case "":
			ChatUtils.message("Position: " + posString);
			break;
			
			case "copy":
			MC.keyboardHandler.setClipboard(posString);
			ChatUtils.message("Position copied to clipboard.");
			break;
			
			default:
			throw new CmdSyntaxError();
		}
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Get Position";
	}
	
	@Override
	public void doPrimaryAction()
	{
		CLIENT.getCmdProcessor().process("getpos");
	}
}
