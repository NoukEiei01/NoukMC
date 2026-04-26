/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.altmanager.screens;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import com.nsyl.client.altmanager.LoginException;
import com.nsyl.client.altmanager.LoginManager;
import com.nsyl.client.altmanager.MicrosoftLoginManager;

public final class DirectLoginScreen extends AltEditorScreen
{
	public DirectLoginScreen(Screen prevScreen)
	{
		super(prevScreen, Component.literal("Direct Login"));
	}
	
	@Override
	protected String getDoneButtonText()
	{
		return getPassword().isEmpty() ? "Change Cracked Name"
			: "Login with Password";
	}
	
	@Override
	protected void pressDoneButton()
	{
		String nameOrEmail = getNameOrEmail();
		String password = getPassword();
		
		if(password.isEmpty())
			LoginManager.changeCrackedName(nameOrEmail);
		else
			try
			{
				MicrosoftLoginManager.login(nameOrEmail, password);
				
			}catch(LoginException e)
			{
				message = "\u00a7c\u00a7lMicrosoft:\u00a7c " + e.getMessage();
				doErrorEffect();
				return;
			}
		
		message = "";
		minecraft.setScreen(new TitleScreen());
	}
}
