/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import com.nsyl.client.DontBlock;
import com.nsyl.client.SearchTags;
import com.nsyl.client.hack.DontSaveState;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.navigator.NavigatorMainScreen;

@DontSaveState
@DontBlock
@SearchTags({"ClickGUI", "click gui", "SearchGUI", "search gui", "HackMenu",
	"hack menu"})
public final class NavigatorHack extends Hack
{
	public NavigatorHack()
	{
		super("Navigator");
	}
	
	@Override
	protected void onEnable()
	{
		if(MC.screen instanceof NavigatorMainScreen)
			MC.setScreen(null);
		else
			MC.setScreen(new NavigatorMainScreen());
		
		setEnabled(false);
	}
}
