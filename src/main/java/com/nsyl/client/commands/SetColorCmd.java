/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.commands;

import com.nsyl.client.DontBlock;
import com.nsyl.client.Feature;
import com.nsyl.client.command.CmdError;
import com.nsyl.client.command.CmdException;
import com.nsyl.client.command.CmdSyntaxError;
import com.nsyl.client.command.Command;
import com.nsyl.client.settings.ColorSetting;
import com.nsyl.client.settings.Setting;
import com.nsyl.client.util.CmdUtils;
import com.nsyl.client.util.ColorUtils;
import com.nsyl.client.util.json.JsonException;

@DontBlock
public final class SetColorCmd extends Command
{
	public SetColorCmd()
	{
		super("setcolor",
			"Changes a color setting of a feature. Allows you\n"
				+ "to set RGB values through keybinds.",
			".setcolor <feature> <setting> <RGB>",
			"Example: .setcolor ClickGUI AC #FF0000");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 3)
			throw new CmdSyntaxError();
		
		Feature feature = CmdUtils.findFeature(args[0]);
		Setting setting = CmdUtils.findSetting(feature, args[1]);
		ColorSetting colorSetting = getAsColor(feature, setting);
		setColor(colorSetting, args[2]);
	}
	
	private ColorSetting getAsColor(Feature feature, Setting setting)
		throws CmdError
	{
		if(!(setting instanceof ColorSetting))
			throw new CmdError(feature.getName() + " " + setting.getName()
				+ " is not a color setting.");
		
		return (ColorSetting)setting;
	}
	
	private void setColor(ColorSetting setting, String value)
		throws CmdSyntaxError
	{
		try
		{
			setting.setColor(ColorUtils.parseHex(value));
			
		}catch(JsonException e)
		{
			throw new CmdSyntaxError("Invalid color: " + value);
		}
	}
}
