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

import net.minecraft.client.KeyboardHandler;
import com.nsyl.client.event.EventManager;
import com.nsyl.client.events.KeyPressListener.KeyPressEvent;

@Mixin(KeyboardHandler.class)
public class KeyboardMixin
{
	@Inject(at = @At("HEAD"), method = "keyPress(JIIII)V")
	private void onOnKey(long windowHandle, int key, int scancode, int action,
		int modifiers, CallbackInfo ci)
	{
		EventManager.fire(new KeyPressEvent(key, scancode, action, modifiers));
	}
}
