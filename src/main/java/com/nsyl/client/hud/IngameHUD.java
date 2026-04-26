/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hud;

import net.minecraft.client.gui.GuiGraphics;
import com.nsyl.client.NsylClient;
import com.nsyl.client.clickgui.ClickGui;
import com.nsyl.client.clickgui.screens.ClickGuiScreen;
import com.nsyl.client.events.GUIRenderListener;

public final class IngameHUD implements GUIRenderListener
{
	private final NsylLogo nsylLogo = new NsylLogo();
	private final HackListHUD hackList = new HackListHUD();
	private TabGui tabGui;
	
	@Override
	public void onRenderGUI(GuiGraphics context, float partialTicks)
	{
		if(!NsylClient.INSTANCE.isEnabled())
			return;
		
		if(tabGui == null)
			tabGui = new TabGui();
		
		ClickGui clickGui = NsylClient.INSTANCE.getGui();
		
		clickGui.updateColors();
		
		nsylLogo.render(context);
		hackList.render(context, partialTicks);
		tabGui.render(context, partialTicks);
		
		// pinned windows
		if(!(NsylClient.MC.screen instanceof ClickGuiScreen))
			clickGui.renderPinnedWindows(context, partialTicks);
	}
	
	public HackListHUD getHackList()
	{
		return hackList;
	}
}
