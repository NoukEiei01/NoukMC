/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.altmanager.screens;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.nsyl.client.altmanager.AltManager;
import com.nsyl.client.altmanager.CrackedAlt;
import com.nsyl.client.altmanager.MojangAlt;

public final class AddAltScreen extends AltEditorScreen
{
	private final AltManager altManager;
	
	public AddAltScreen(Screen prevScreen, AltManager altManager)
	{
		super(prevScreen, Component.literal("New Alt"));
		this.altManager = altManager;
	}
	
	@Override
	protected String getDoneButtonText()
	{
		return getPassword().isEmpty() ? "Add Cracked Alt" : "Add Premium Alt";
	}
	
	@Override
	protected void pressDoneButton()
	{
		String nameOrEmail = getNameOrEmail();
		String password = getPassword();
		
		if(password.isEmpty())
			altManager.add(new CrackedAlt(nameOrEmail));
		else
			altManager.add(new MojangAlt(nameOrEmail, password));
		
		minecraft.setScreen(prevScreen);
	}
}
