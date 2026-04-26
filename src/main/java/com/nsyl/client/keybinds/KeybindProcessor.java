/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.keybinds;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.gui.screens.Screen;
import com.nsyl.client.NsylClient;
import com.nsyl.client.clickgui.screens.ClickGuiScreen;
import com.nsyl.client.command.CmdProcessor;
import com.nsyl.client.events.KeyPressListener;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.hack.HackList;
import com.nsyl.client.util.ChatUtils;

public final class KeybindProcessor implements KeyPressListener
{
	private final HackList hax;
	private final KeybindList keybinds;
	private final CmdProcessor cmdProcessor;
	
	public KeybindProcessor(HackList hax, KeybindList keybinds,
		CmdProcessor cmdProcessor)
	{
		this.hax = hax;
		this.keybinds = keybinds;
		this.cmdProcessor = cmdProcessor;
	}
	
	@Override
	public void onKeyPress(KeyPressEvent event)
	{
		if(event.getAction() != GLFW.GLFW_PRESS)
			return;
		
		if(InputConstants.isKeyDown(NsylClient.MC.getWindow().getWindow(),
			GLFW.GLFW_KEY_F3))
			return;
		
		Screen screen = NsylClient.MC.screen;
		if(screen != null && !(screen instanceof ClickGuiScreen))
			return;
		
		String keyName = getKeyName(event);
		
		String cmds = keybinds.getCommands(keyName);
		if(cmds == null)
			return;
		
		processCmds(cmds);
	}
	
	private String getKeyName(KeyPressEvent event)
	{
		int keyCode = event.getKeyCode();
		int scanCode = event.getScanCode();
		return InputConstants.getKey(keyCode, scanCode).getName();
	}
	
	private void processCmds(String cmds)
	{
		cmds = cmds.replace(";", "\u00a7").replace("\u00a7\u00a7", ";");
		
		for(String cmd : cmds.split("\u00a7"))
			processCmd(cmd.trim());
	}
	
	private void processCmd(String cmd)
	{
		if(cmd.startsWith("."))
			cmdProcessor.process(cmd.substring(1));
		else if(cmd.contains(" "))
			cmdProcessor.process(cmd);
		else
		{
			Hack hack = hax.getHackByName(cmd);
			
			if(hack == null)
			{
				cmdProcessor.process(cmd);
				return;
			}
			
			if(!hack.isEnabled() && hax.tooManyHaxHack.isEnabled()
				&& hax.tooManyHaxHack.isBlocked(hack))
			{
				ChatUtils.error(hack.getName() + " is blocked by TooManyHax.");
				return;
			}
			
			hack.setEnabled(!hack.isEnabled());
		}
	}
}
