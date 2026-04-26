/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.clickgui.components;

import com.nsyl.client.NsylClient;
import java.util.Objects;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import com.nsyl.client.Feature;
import com.nsyl.client.clickgui.ClickGui;
import com.nsyl.client.clickgui.ClickGuiIcons;
import com.nsyl.client.clickgui.Component;
import com.nsyl.client.clickgui.SettingsWindow;
import com.nsyl.client.clickgui.Window;
import com.nsyl.client.hacks.TooManyHaxHack;
import com.nsyl.client.util.ChatUtils;
import com.nsyl.client.util.RenderUtils;

public final class FeatureButton extends Component
{
	private static final ClickGui GUI = NsylClient.INSTANCE.getGui();
	private static final Font TR = MC.font;
	
	private final Feature feature;
	private final boolean hasSettings;
	
	private Window settingsWindow;
	
	public FeatureButton(Feature feature)
	{
		this.feature = Objects.requireNonNull(feature);
		setWidth(getDefaultWidth());
		setHeight(getDefaultHeight());
		hasSettings = !feature.getSettings().isEmpty();
	}
	
	@Override
	public void handleMouseClick(double mouseX, double mouseY, int mouseButton)
	{
		if(mouseButton != 0)
			return;
		
		if(hasSettings && (mouseX > getX() + getWidth() - 12
			|| feature.getPrimaryAction().isEmpty()))
		{
			toggleSettingsWindow();
			return;
		}
		
		TooManyHaxHack tooManyHax = NsylClient.INSTANCE.getHax().tooManyHaxHack;
		if(tooManyHax.isEnabled() && tooManyHax.isBlocked(feature))
		{
			ChatUtils.error(feature.getName() + " is blocked by TooManyHax.");
			return;
		}
		
		feature.doPrimaryAction();
	}
	
	private boolean isSettingsWindowOpen()
	{
		return settingsWindow != null && !settingsWindow.isClosing();
	}
	
	private void toggleSettingsWindow()
	{
		if(!isSettingsWindowOpen())
		{
			settingsWindow = new SettingsWindow(feature, getParent(), getY());
			GUI.addWindow(settingsWindow);
			
		}else
		{
			settingsWindow.close();
			settingsWindow = null;
		}
	}
	
	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY,
		float partialTicks)
	{
		int x1 = getX();
		int x2 = x1 + getWidth();
		int x3 = hasSettings ? x2 - 11 : x2;
		int y1 = getY();
		int y2 = y1 + getHeight();
		
		boolean hovering = isHovering(mouseX, mouseY);
		boolean hFeature = hovering && mouseX < x3;
		boolean hSettings = hovering && mouseX >= x3;
		
		if(hFeature)
			GUI.setTooltip(feature.getWrappedDescription(200));
		
		// buttons
		context.fill(x1, y1, x3, y2,
			getButtonColor(feature.isEnabled(), hFeature));
		if(hasSettings)
			context.fill(x3, y1, x2, y2, getButtonColor(false, hSettings));
		
		// outlines
		int outlineColor = RenderUtils.toIntColor(GUI.getAcColor(), 0.5F);
		RenderUtils.drawBorder2D(context, x1, y1, x2, y2, outlineColor);
		if(hasSettings)
			RenderUtils.drawLine2D(context, x3, y1, x3, y2, outlineColor);
		
		// arrow
		if(hasSettings)
			ClickGuiIcons.drawMinimizeArrow(context, x3, y1 + 0.5F, x2,
				y2 - 0.5F, hSettings, !isSettingsWindowOpen());
		
		// text
		String name = feature.getName();
		int tx = x1 + (x3 - x1 - TR.width(name)) / 2;
		int ty = y1 + 2;
		context.drawString(TR, name, tx, ty, GUI.getTxtColor(), false);
	}
	
	private int getButtonColor(boolean enabled, boolean hovering)
	{
		float[] rgb = enabled ? new float[]{0, 1, 0} : GUI.getBgColor();
		float opacity = GUI.getOpacity() * (hovering ? 1.5F : 1);
		return RenderUtils.toIntColor(rgb, opacity);
	}
	
	@Override
	public int getDefaultWidth()
	{
		int width = TR.width(feature.getName());
		width += hasSettings ? 15 : 4;
		return width;
	}
	
	@Override
	public int getDefaultHeight()
	{
		return 11;
	}
}
