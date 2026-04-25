/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
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
import com.nsyl.client.settings.Setting;
import com.nsyl.client.settings.SliderSetting;
import com.nsyl.client.util.CmdUtils;
import com.nsyl.client.util.MathUtils;

@DontBlock
public final class SetSliderCmd extends Command
{
	public SetSliderCmd()
	{
		super("setslider",
			"Changes a slider setting of a feature. Allows you to\n"
				+ "move sliders through keybinds.",
			".setslider <feature> <setting> <value>",
			".setslider <feature> <setting> (more|less)");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 3)
			throw new CmdSyntaxError();
		
		Feature feature = CmdUtils.findFeature(args[0]);
		Setting setting = CmdUtils.findSetting(feature, args[1]);
		SliderSetting slider = getAsSlider(feature, setting);
		setValue(args[2], slider);
	}
	
	private SliderSetting getAsSlider(Feature feature, Setting setting)
		throws CmdError
	{
		if(!(setting instanceof SliderSetting))
			throw new CmdError(feature.getName() + " " + setting.getName()
				+ " is not a slider setting.");
		
		return (SliderSetting)setting;
	}
	
	private void setValue(String value, SliderSetting slider)
		throws CmdSyntaxError
	{
		switch(value.toLowerCase())
		{
			case "more":
			slider.increaseValue();
			break;
			
			case "less":
			slider.decreaseValue();
			break;
			
			default:
			if(!MathUtils.isDouble(value))
				throw new CmdSyntaxError("Value must be a number.");
			slider.setValue(Double.parseDouble(value));
			break;
		}
	}
}
