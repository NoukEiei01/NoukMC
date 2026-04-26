/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.gametest.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import com.nsyl.client.NsylClient;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen
{
	private TitleScreenMixin(NsylClient nsyl, Component title)
	{
		super(title);
	}
	
	/**
	 * Replaces the panorama background with a gray background to make test
	 * screenshots consistent.
	 */
	@Inject(at = @At("HEAD"), method = "renderPanorama", cancellable = true)
	public void renderPanoramaBackground(GuiGraphics context, float deltaTicks,
		CallbackInfo ci)
	{
		context.fill(0, 0, width, height, CommonColors.GRAY);
		ci.cancel();
	}
}
