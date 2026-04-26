/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.commands;

import com.nsyl.client.command.CmdException;
import com.nsyl.client.command.CmdSyntaxError;
import com.nsyl.client.command.Command;

public final class ClearCmd extends Command
{
	public ClearCmd()
	{
		super("clear", "Clears the chat completely.", ".clear");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length > 0)
			throw new CmdSyntaxError();
		
		MC.gui.getChat().clearMessages(true);
	}
}
