/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.network.chat.Component;
import com.nsyl.client.NsylClient;

@Mixin(PackSelectionScreen.class)
public class PackScreenMixin extends Screen
{
	private PackScreenMixin(NsylClient nsyl, Component title)
	{
		super(title);
	}
	
	/**
	 * Scans for problematic resource packs (currently just VanillaTweaks
	 * Twinkling Stars) whenever the resource pack screen is closed.
	 */
	@Inject(at = @At("HEAD"), method = "onClose()V")
	public void onClose(CallbackInfo ci)
	{
		NsylClient.INSTANCE.getProblematicPackDetector().start();
	}
}
