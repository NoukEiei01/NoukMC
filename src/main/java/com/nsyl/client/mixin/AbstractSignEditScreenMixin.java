/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.network.chat.Component;
import com.nsyl.client.NsylClient;
import com.nsyl.client.hacks.AutoSignHack;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin extends Screen
{
	@Shadow
	@Final
	private String[] messages;
	
	private AbstractSignEditScreenMixin(NsylClient nsyl, Component title)
	{
		super(title);
	}
	
	@Inject(at = @At("HEAD"), method = "init()V")
	private void onInit(CallbackInfo ci)
	{
		AutoSignHack autoSignHack = NsylClient.INSTANCE.getHax().autoSignHack;
		
		String[] autoSignText = autoSignHack.getSignText();
		if(autoSignText == null)
			return;
		
		for(int i = 0; i < 4; i++)
			messages[i] = autoSignText[i];
		
		onDone();
	}
	
	@Inject(at = @At("HEAD"), method = "onDone()V")
	private void onFinishEditing(CallbackInfo ci)
	{
		NsylClient.INSTANCE.getHax().autoSignHack.setSignText(messages);
	}
	
	@Shadow
	private void onDone()
	{
		
	}
}
