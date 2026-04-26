/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.other_features;

import com.nsyl.client.DontBlock;
import com.nsyl.client.SearchTags;
import com.nsyl.client.other_feature.OtherFeature;
import com.nsyl.client.settings.CheckboxSetting;

@SearchTags({"turn off", "hide nsyl logo", "ghost mode", "stealth mode",
	"vanilla Minecraft"})
@DontBlock
public final class DisableOtf extends OtherFeature
{
	private final CheckboxSetting hideEnableButton = new CheckboxSetting(
		"Hide enable button",
		"Removes the \"Enable NSYL\" button as soon as you close the Statistics screen."
			+ " You will have to restart the game to re-enable NSYL.",
		false);
	
	public DisableOtf()
	{
		super("Disable NSYL",
			"To disable NSYL, go to the Statistics screen and press the \"Disable NSYL\" button.\n"
				+ "It will turn into an \"Enable NSYL\" button once pressed.");
		addSetting(hideEnableButton);
	}
	
	public boolean shouldHideEnableButton()
	{
		return !CLIENT.isEnabled() && hideEnableButton.isChecked();
	}
}
