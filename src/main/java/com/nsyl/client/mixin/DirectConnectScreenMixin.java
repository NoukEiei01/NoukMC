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

import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import com.nsyl.client.NsylClient;
import com.nsyl.client.util.LastServerRememberer;

@Mixin(DirectJoinServerScreen.class)
public class DirectConnectScreenMixin extends Screen
{
	@Shadow
	@Final
	private ServerData serverData;
	
	private DirectConnectScreenMixin(NsylClient nsyl, Component title)
	{
		super(title);
	}
	
	@Inject(at = @At("TAIL"), method = "onSelect()V")
	private void onSaveAndClose(CallbackInfo ci)
	{
		LastServerRememberer.setLastServer(serverData);
	}
}
