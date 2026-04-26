/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.altmanager.screens;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.nsyl.client.altmanager.Alt;
import com.nsyl.client.altmanager.AltManager;
import com.nsyl.client.altmanager.MojangAlt;

public final class EditAltScreen extends AltEditorScreen
{
	private final AltManager altManager;
	private Alt editedAlt;
	
	public EditAltScreen(Screen prevScreen, AltManager altManager,
		Alt editedAlt)
	{
		super(prevScreen, Component.literal("Edit Alt"));
		this.altManager = altManager;
		this.editedAlt = editedAlt;
	}
	
	@Override
	protected String getDefaultNameOrEmail()
	{
		return editedAlt instanceof MojangAlt
			? ((MojangAlt)editedAlt).getEmail() : editedAlt.getName();
	}
	
	@Override
	protected String getDefaultPassword()
	{
		return editedAlt instanceof MojangAlt
			? ((MojangAlt)editedAlt).getPassword() : "";
	}
	
	@Override
	protected String getDoneButtonText()
	{
		return "Save";
	}
	
	@Override
	protected void pressDoneButton()
	{
		altManager.edit(editedAlt, getNameOrEmail(), getPassword());
		minecraft.setScreen(prevScreen);
	}
}
