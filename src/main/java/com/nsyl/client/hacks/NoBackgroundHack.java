/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import com.nsyl.client.Category;
import com.nsyl.client.SearchTags;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.settings.CheckboxSetting;

@SearchTags({"no background", "NoGuiBackground", "no gui background",
	"NoGradient", "no gradient"})
public final class NoBackgroundHack extends Hack
{
	public final CheckboxSetting allGuis = new CheckboxSetting("All GUIs",
		"Removes the background for all GUIs, not just inventories.", false);
	
	public NoBackgroundHack()
	{
		super("NoBackground");
		setCategory(Category.RENDER);
		addSetting(allGuis);
	}
	
	public boolean shouldCancelBackground(Screen screen)
	{
		if(!isEnabled())
			return false;
		
		if(MC.level == null)
			return false;
		
		if(!allGuis.isChecked() && !(screen instanceof AbstractContainerScreen))
			return false;
		
		return true;
	}
	
	// See ScreenMixin.onRenderBackground()
}
