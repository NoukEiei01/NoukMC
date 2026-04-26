/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.nochatreports;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import com.nsyl.client.NsylClient;
import com.nsyl.client.other_feature.OtfList;
import com.nsyl.client.util.ChatUtils;
import com.nsyl.client.util.LastServerRememberer;

public final class NcrModRequiredScreen extends Screen
{
	private static final List<String> DISCONNECT_REASONS = Arrays.asList(
		// Older versions of NCR have a bug that sends the raw translation key.
		"disconnect.nochatreports.server",
		"You do not have No Chat Reports, and this server is configured to require it on client!");
	
	private final Screen prevScreen;
	private final Component reason;
	private MultiLineLabel reasonFormatted = MultiLineLabel.EMPTY;
	private int reasonHeight;
	
	private Button signatureButton;
	private final Supplier<String> sigButtonMsg;
	
	private Button vsButton;
	private final Supplier<String> vsButtonMsg;
	
	public NcrModRequiredScreen(Screen prevScreen)
	{
		super(Component.literal(ChatUtils.NSYL_PREFIX + NsylClient.INSTANCE
			.translate("gui.nsyl.nochatreports.ncr_mod_server.title")));
		this.prevScreen = prevScreen;
		
		reason = Component.literal(NsylClient.INSTANCE
			.translate("gui.nsyl.nochatreports.ncr_mod_server.message"));
		
		OtfList otfs = NsylClient.INSTANCE.getOtfs();
		
		sigButtonMsg = () -> NsylClient.INSTANCE
			.translate("button.nsyl.nochatreports.signatures_status")
			+ blockedOrAllowed(otfs.noChatReportsOtf.isEnabled());
		
		vsButtonMsg =
			() -> "VanillaSpoof: " + onOrOff(otfs.vanillaSpoofOtf.isEnabled());
	}
	
	private String onOrOff(boolean on)
	{
		return NsylClient.INSTANCE.translate("options." + (on ? "on" : "off"))
			.toUpperCase();
	}
	
	private String blockedOrAllowed(boolean blocked)
	{
		return NsylClient.INSTANCE.translate(
			"gui.nsyl.generic.allcaps_" + (blocked ? "blocked" : "allowed"));
	}
	
	@Override
	protected void init()
	{
		reasonFormatted = MultiLineLabel.create(font, reason, width - 50);
		reasonHeight = reasonFormatted.getLineCount() * font.lineHeight;
		
		int buttonX = width / 2 - 100;
		int belowReasonY =
			(height - 78) / 2 + reasonHeight / 2 + font.lineHeight * 2;
		int signaturesY = Math.min(belowReasonY, height - 68);
		int reconnectY = signaturesY + 24;
		int backButtonY = reconnectY + 24;
		
		addRenderableWidget(signatureButton = Button
			.builder(Component.literal(sigButtonMsg.get()),
				b -> toggleSignatures())
			.bounds(buttonX - 48, signaturesY, 148, 20).build());
		
		addRenderableWidget(vsButton = Button
			.builder(Component.literal(vsButtonMsg.get()),
				b -> toggleVanillaSpoof())
			.bounds(buttonX + 102, signaturesY, 148, 20).build());
		
		addRenderableWidget(Button
			.builder(Component.literal("Reconnect"),
				b -> LastServerRememberer.reconnect(prevScreen))
			.bounds(buttonX, reconnectY, 200, 20).build());
		
		addRenderableWidget(Button
			.builder(Component.translatable("gui.toMenu"),
				b -> minecraft.setScreen(prevScreen))
			.bounds(buttonX, backButtonY, 200, 20).build());
	}
	
	private void toggleSignatures()
	{
		NsylClient.INSTANCE.getOtfs().noChatReportsOtf.doPrimaryAction();
		signatureButton.setMessage(Component.literal(sigButtonMsg.get()));
	}
	
	private void toggleVanillaSpoof()
	{
		NsylClient.INSTANCE.getOtfs().vanillaSpoofOtf.doPrimaryAction();
		vsButton.setMessage(Component.literal(vsButtonMsg.get()));
	}
	
	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY,
		float partialTicks)
	{
		renderBackground(context, mouseX, mouseY, partialTicks);
		
		int centerX = width / 2;
		int reasonY = (height - 68) / 2 - reasonHeight / 2;
		int titleY = reasonY - font.lineHeight * 2;
		
		context.drawCenteredString(font, title, centerX, titleY, 0xAAAAAA);
		reasonFormatted.renderCentered(context, centerX, reasonY);
		
		for(Renderable drawable : renderables)
			drawable.render(context, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public boolean shouldCloseOnEsc()
	{
		return false;
	}
	
	public static boolean isCausedByLackOfNCR(Component disconnectReason)
	{
		OtfList otfs = NsylClient.INSTANCE.getOtfs();
		if(otfs.noChatReportsOtf.isActive()
			&& !otfs.vanillaSpoofOtf.isEnabled())
			return false;
		
		String text = disconnectReason.getString();
		if(text == null)
			return false;
		
		text = StringUtil.stripColor(text);
		return DISCONNECT_REASONS.contains(text);
	}
}
