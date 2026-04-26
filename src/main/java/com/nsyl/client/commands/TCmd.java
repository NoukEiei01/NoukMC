/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.commands;

import com.nsyl.client.DontBlock;
import com.nsyl.client.command.CmdError;
import com.nsyl.client.command.CmdException;
import com.nsyl.client.command.CmdSyntaxError;
import com.nsyl.client.command.Command;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.hacks.TooManyHaxHack;
import com.nsyl.client.util.ChatUtils;

@DontBlock
public final class TCmd extends Command
{
	public TCmd()
	{
		super("t", "Toggles a hack.", ".t <hack> [on|off]", "Examples:",
			"Toggle Nuker: .t Nuker", "Disable Nuker: .t Nuker off");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length < 1 || args.length > 2)
			throw new CmdSyntaxError();
		
		Hack hack = CLIENT.getHax().getHackByName(args[0]);
		if(hack == null)
			throw new CmdError("Unknown hack: " + args[0]);
		
		if(args.length == 1)
			setEnabled(hack, !hack.isEnabled());
		else
			switch(args[1].toLowerCase())
			{
				case "on":
				setEnabled(hack, true);
				break;
				
				case "off":
				setEnabled(hack, false);
				break;
				
				default:
				throw new CmdSyntaxError();
			}
	}
	
	private void setEnabled(Hack hack, boolean enabled)
	{
		TooManyHaxHack tooManyHax = CLIENT.getHax().tooManyHaxHack;
		if(!hack.isEnabled() && tooManyHax.isEnabled()
			&& tooManyHax.isBlocked(hack))
		{
			ChatUtils.error(hack.getName() + " is blocked by TooManyHax.");
			return;
		}
		
		hack.setEnabled(enabled);
	}
}
