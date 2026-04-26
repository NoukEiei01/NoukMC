/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hud;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import com.nsyl.client.NsylClient;
import com.nsyl.client.other_features.NsylLogoOtf;
import com.nsyl.client.util.RenderUtils;

public final class NsylLogo
{
	private static final NsylClient CLIENT = NsylClient.INSTANCE;
	private static final ResourceLocation LOGO_TEXTURE =
		ResourceLocation.fromNamespaceAndPath("nsyl", "nsyl_logo.png");
	
	public void render(GuiGraphics context)
	{
		NsylLogoOtf otf = NsylClient.INSTANCE.getOtfs().nsylLogoOtf;
		if(!otf.isVisible())
			return;
		
		String version = getVersionString();
		Font tr = NsylClient.MC.font;
		
		// background
		int bgColor;
		if(NsylClient.INSTANCE.getHax().rainbowUiHack.isEnabled())
			bgColor = RenderUtils.toIntColor(NsylClient.INSTANCE.getGui().getAcColor(), 0.5F);
		else
			bgColor = otf.getBackgroundColor();
		context.fill(0, 6, tr.width(version) + 76, 17, bgColor);
		
		// version string
		context.drawString(tr, version, 74, 8, otf.getTextColor(), false);
		
		// NSYL logo
		context.blit(RenderType::guiTextured, LOGO_TEXTURE, 0, 3, 0, 0, 72, 18,
			72, 18);
	}
	
	private String getVersionString()
	{
		String version = "NSYL Client v" + NsylClient.VERSION;
		
		
		if(NsylClient.INSTANCE.getUpdater().isOutdated())
			version += " (outdated)";
		
		return version;
	}
}
