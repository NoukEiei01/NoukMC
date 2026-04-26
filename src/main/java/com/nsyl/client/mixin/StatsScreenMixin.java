/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.network.chat.Component;
import com.nsyl.client.NsylClient;

@Mixin(StatsScreen.class)
public class StatsScreenMixin
{
	@WrapOperation(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/gui/layouts/LinearLayout;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
		ordinal = 4), method = "initButtons()V")
	private <T extends LayoutElement> T onCreateDoneButton(LinearLayout layout,
		T doneWidget, Operation<T> original)
	{
		if(!(doneWidget instanceof Button doneButton))
			throw new IllegalStateException(
				"The done button in the statistics screen somehow isn't a button");
		
		if(NsylClient.INSTANCE.getOtfs().disableOtf.shouldHideEnableButton())
			return original.call(layout, doneButton);
		
		doneButton.setWidth(150);
		
		LinearLayout subLayout =
			layout.addChild(LinearLayout.horizontal()).spacing(5);
		subLayout.addChild(Button.builder(getButtonText(), this::toggleNsyl)
			.width(150).build());
		return original.call(subLayout, doneButton);
	}
	
	@Unique
	private void toggleNsyl(Button button)
	{
		NsylClient nsyl = NsylClient.INSTANCE;
		nsyl.setEnabled(!nsyl.isEnabled());
		button.setMessage(getButtonText());
	}
	
	@Unique
	private Component getButtonText()
	{
		NsylClient nsyl = NsylClient.INSTANCE;
		String text = (nsyl.isEnabled() ? "Disable" : "Enable") + " NSYL";
		return Component.literal(text);
	}
}
