/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.other_features;

import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.GuiMessageTag.Icon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.LocalChatSession;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageChain;
import net.minecraft.network.chat.contents.TranslatableContents;
import com.nsyl.client.Category;
import com.nsyl.client.DontBlock;
import com.nsyl.client.SearchTags;
import com.nsyl.client.events.ChatInputListener;
import com.nsyl.client.events.UpdateListener;
import com.nsyl.client.other_feature.OtherFeature;
import com.nsyl.client.settings.CheckboxSetting;
import com.nsyl.client.util.ChatUtils;

@DontBlock
@SearchTags({"no chat reports", "NoEncryption", "no encryption",
	"NoChatSigning", "no chat signing"})
public final class NoChatReportsOtf extends OtherFeature
	implements UpdateListener, ChatInputListener
{
	private final CheckboxSetting disableSignatures =
		new CheckboxSetting("Disable signatures", true)
		{
			@Override
			public void update()
			{
				EVENTS.add(UpdateListener.class, NoChatReportsOtf.this);
			}
		};
	
	public NoChatReportsOtf()
	{
		super("NoChatReports", "description.nsyl.other_feature.nochatreports");
		addSetting(disableSignatures);
		
		ClientLoginConnectionEvents.INIT.register(this::onLoginStart);
		EVENTS.add(ChatInputListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		ClientPacketListener netHandler = MC.getConnection();
		if(netHandler == null)
			return;
		
		if(isActive())
		{
			netHandler.chatSession = null;
			netHandler.signedMessageEncoder =
				SignedMessageChain.Encoder.UNSIGNED;
			
		}else if(netHandler.chatSession == null)
			MC.getProfileKeyPairManager().prepareKeyPair()
				.thenAcceptAsync(optional -> optional
					.ifPresent(profileKeys -> netHandler.chatSession =
						LocalChatSession.create(profileKeys)),
					MC);
		
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onReceivedMessage(ChatInputEvent event)
	{
		if(!isActive())
			return;
		
		Component originalText = event.getComponent();
		if(!(originalText
			.getContents() instanceof TranslatableContents trContent))
			return;
		
		if(!trContent.getKey().equals("chat.disabled.missingProfileKey"))
			return;
		
		event.cancel();
		
		ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL,
			"https://github.com/");
		HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
			Component.literal("Original message: ").append(originalText));
		
		ChatUtils.component(Component.literal(
			"The server is refusing to let you chat without enabling chat reports. Click \u00a7nhere\u00a7r to learn more.")
			.withStyle(
				s -> s.withClickEvent(clickEvent).withHoverEvent(hoverEvent)));
	}
	
	private void onLoginStart(ClientHandshakePacketListenerImpl handler,
		Minecraft client)
	{
		EVENTS.add(UpdateListener.class, NoChatReportsOtf.this);
	}
	
	public GuiMessageTag modifyIndicator(Component message,
		MessageSignature signature, GuiMessageTag indicator)
	{
		if(!CLIENT.isEnabled() || MC.isLocalServer())
			return indicator;
		
		if(indicator != null || signature == null)
			return indicator;
		
		return new GuiMessageTag(0xE84F58, Icon.CHAT_MODIFIED,
			Component.literal(ChatUtils.NSYL_PREFIX
				+ "\u00a7cReportable\u00a7r - "
				+ CLIENT.translate(
					"description.nsyl.nochatreports.message_is_reportable")),
			"Reportable");
	}
	
	@Override
	public boolean isEnabled()
	{
		return disableSignatures.isChecked();
	}
	
	public boolean isActive()
	{
		return isEnabled() && CLIENT.isEnabled() && !MC.isLocalServer();
	}
	
	@Override
	public String getPrimaryAction()
	{
		return CLIENT.translate("button.nsyl.nochatreports."
			+ (isEnabled() ? "re-enable_signatures" : "disable_signatures"));
	}
	
	@Override
	public void doPrimaryAction()
	{
		disableSignatures.setChecked(!disableSignatures.isChecked());
	}
	
	@Override
	public Category getCategory()
	{
		return Category.CHAT;
	}
	
	// See ChatHudMixin, ClientPlayNetworkHandlerMixin.onOnServerMetadata(),
	// MinecraftClientMixin.onGetProfileKeys()
}
