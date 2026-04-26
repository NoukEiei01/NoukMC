/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.mixin;

import java.io.File;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.platform.WindowEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import com.nsyl.client.NsylClient;
import com.nsyl.client.event.EventManager;
import com.nsyl.client.events.HandleBlockBreakingListener.HandleBlockBreakingEvent;
import com.nsyl.client.events.HandleInputListener.HandleInputEvent;
import com.nsyl.client.events.LeftClickListener.LeftClickEvent;
import com.nsyl.client.events.RightClickListener.RightClickEvent;
import com.nsyl.client.mixinterface.IClientPlayerEntity;
import com.nsyl.client.mixinterface.IClientPlayerInteractionManager;
import com.nsyl.client.mixinterface.IMinecraftClient;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin
	extends ReentrantBlockableEventLoop<Runnable>
	implements WindowEventHandler, IMinecraftClient
{
	@Shadow
	@Final
	public File gameDirectory;
	@Shadow
	public MultiPlayerGameMode gameMode;
	@Shadow
	public LocalPlayer player;
	@Shadow
	@Final
	private YggdrasilAuthenticationService authenticationService;
	
	private User nsylSession;
	private ProfileKeyPairManager nsylProfileKeys;
	
	private MinecraftClientMixin(NsylClient nsyl, String name)
	{
		super(name);
	}
	
	/**
	 * Runs just before {@link Minecraft#handleKeybinds()}, bypassing
	 * the <code>overlay == null && currentScreen == null</code> check in
	 * {@link Minecraft#tick()}.
	 */
	@Inject(at = @At(value = "FIELD",
		target = "Lnet/minecraft/client/Minecraft;overlay:Lnet/minecraft/client/gui/screens/Overlay;",
		ordinal = 0), method = "tick()V")
	private void onHandleInputEvents(CallbackInfo ci)
	{
		// Make sure this event is not fired outside of gameplay
		if(player == null)
			return;
		
		EventManager.fire(HandleInputEvent.INSTANCE);
	}
	
	@Inject(at = @At(value = "FIELD",
		target = "Lnet/minecraft/client/Minecraft;hitResult:Lnet/minecraft/world/phys/HitResult;",
		ordinal = 0), method = "startAttack()Z", cancellable = true)
	private void onDoAttack(CallbackInfoReturnable<Boolean> cir)
	{
		LeftClickEvent event = new LeftClickEvent();
		EventManager.fire(event);
		
		if(event.isCancelled())
			cir.setReturnValue(false);
	}
	
	@Inject(
		at = @At(value = "FIELD",
			target = "Lnet/minecraft/client/Minecraft;rightClickDelay:I",
			ordinal = 0),
		method = "startUseItem()V",
		cancellable = true)
	private void onDoItemUse(CallbackInfo ci)
	{
		RightClickEvent event = new RightClickEvent();
		EventManager.fire(event);
		
		if(event.isCancelled())
			ci.cancel();
	}
	
	@Inject(at = @At("HEAD"), method = "pickBlock()V")
	private void onDoItemPick(CallbackInfo ci)
	{
		if(!NsylClient.INSTANCE.isEnabled())
			return;
		
		HitResult hitResult = NsylClient.MC.hitResult;
		if(!(hitResult instanceof EntityHitResult eHitResult))
			return;
		
		NsylClient.INSTANCE.getFriends().middleClick(eHitResult.getEntity());
	}
	
	/**
	 * Allows hacks to cancel vanilla block breaking and replace it with their
	 * own. Useful for Nuker-like hacks.
	 */
	@Inject(at = @At("HEAD"), method = "continueAttack(Z)V", cancellable = true)
	private void onHandleBlockBreaking(boolean breaking, CallbackInfo ci)
	{
		HandleBlockBreakingEvent event = new HandleBlockBreakingEvent();
		EventManager.fire(event);
		
		if(event.isCancelled())
			ci.cancel();
	}
	
	@Inject(at = @At("HEAD"),
		method = "getUser()Lnet/minecraft/client/User;",
		cancellable = true)
	private void onGetSession(CallbackInfoReturnable<User> cir)
	{
		if(nsylSession != null)
			cir.setReturnValue(nsylSession);
	}
	
	@Inject(at = @At("RETURN"),
		method = "getGameProfile()Lcom/mojang/authlib/GameProfile;",
		cancellable = true)
	public void onGetGameProfile(CallbackInfoReturnable<GameProfile> cir)
	{
		if(nsylSession == null)
			return;
		
		GameProfile oldProfile = cir.getReturnValue();
		GameProfile newProfile = new GameProfile(nsylSession.getProfileId(),
			nsylSession.getName());
		newProfile.getProperties().putAll(oldProfile.getProperties());
		cir.setReturnValue(newProfile);
	}
	
	@Inject(at = @At("HEAD"),
		method = "getProfileKeyPairManager()Lnet/minecraft/client/multiplayer/ProfileKeyPairManager;",
		cancellable = true)
	private void onGetProfileKeys(
		CallbackInfoReturnable<ProfileKeyPairManager> cir)
	{
		if(NsylClient.INSTANCE.getOtfs().noChatReportsOtf.isActive())
			cir.setReturnValue(ProfileKeyPairManager.EMPTY_KEY_MANAGER);
		
		if(nsylProfileKeys == null)
			return;
		
		cir.setReturnValue(nsylProfileKeys);
	}
	
	@Inject(at = @At("HEAD"), method = "allowsTelemetry()Z", cancellable = true)
	private void onIsTelemetryEnabledByApi(CallbackInfoReturnable<Boolean> cir)
	{
		cir.setReturnValue(
			!NsylClient.INSTANCE.getOtfs().noTelemetryOtf.isEnabled());
	}
	
	@Inject(at = @At("HEAD"),
		method = "extraTelemetryAvailable()Z",
		cancellable = true)
	private void onIsOptionalTelemetryEnabledByApi(
		CallbackInfoReturnable<Boolean> cir)
	{
		cir.setReturnValue(
			!NsylClient.INSTANCE.getOtfs().noTelemetryOtf.isEnabled());
	}
	
	@Override
	public IClientPlayerEntity getPlayer()
	{
		return (IClientPlayerEntity)player;
	}
	
	@Override
	public IClientPlayerInteractionManager getInteractionManager()
	{
		return (IClientPlayerInteractionManager)gameMode;
	}
	
	@Override
	public User getNsylSession()
	{
		return nsylSession;
	}
	
	@Override
	public void setNsylSession(User session)
	{
		nsylSession = session;
		if(session == null)
		{
			nsylProfileKeys = null;
			return;
		}
		
		UserApiService userApiService =
			session.getType() == User.Type.MSA ? authenticationService
				.createUserApiService(session.getAccessToken())
				: UserApiService.OFFLINE;
		nsylProfileKeys = ProfileKeyPairManager.create(userApiService, session,
			gameDirectory.toPath());
	}
}
