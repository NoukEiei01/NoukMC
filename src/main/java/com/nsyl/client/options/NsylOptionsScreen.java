/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.options;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.Util;
import net.minecraft.Util.OS;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.nsyl.client.NsylClient;
import com.nsyl.client.analytics.PlausibleAnalytics;
import com.nsyl.client.commands.FriendsCmd;
import com.nsyl.client.hacks.XRayHack;
import com.nsyl.client.other_features.VanillaSpoofOtf;
import com.nsyl.client.settings.CheckboxSetting;
import com.nsyl.client.util.ChatUtils;

public class NsylOptionsScreen extends Screen
{
	private Screen prevScreen;
	
	public NsylOptionsScreen(Screen prevScreen)
	{
		super(Component.literal(""));
		this.prevScreen = prevScreen;
	}
	
	@Override
	public void init()
	{
		addRenderableWidget(Button
			.builder(Component.literal("Back"),
				b -> minecraft.setScreen(prevScreen))
			.bounds(width / 2 - 100, height / 4 + 144 - 16, 200, 20).build());
		
		addSettingButtons();
		addManagerButtons();
		addLinkButtons();
	}
	
	private void addSettingButtons()
	{
		NsylClient nsyl = NsylClient.INSTANCE;
		FriendsCmd friendsCmd = nsyl.getCmds().friendsCmd;
		CheckboxSetting middleClickFriends = friendsCmd.getMiddleClickFriends();
		PlausibleAnalytics plausible = NsylClient.INSTANCE.getPlausible();
		VanillaSpoofOtf vanillaSpoofOtf = NsylClient.INSTANCE.getOtfs().vanillaSpoofOtf;
		CheckboxSetting forceEnglish =
			NsylClient.INSTANCE.getOtfs().translationsOtf.getForceEnglish();
		
		new NsylOptionsButton(-154, 24,
			() -> "Click Friends: "
				+ (middleClickFriends.isChecked() ? "ON" : "OFF"),
			middleClickFriends.getWrappedDescription(200),
			b -> middleClickFriends
				.setChecked(!middleClickFriends.isChecked()));
		
		new NsylOptionsButton(-154, 48,
			() -> "Count Users: "
				+ (plausible != null && plausible.isEnabled() ? "ON" : "OFF"),
			"Counts how many people are using NSYL and which versions are the"
				+ " most popular. This data helps me to decide when I can stop"
				+ " supporting old versions.\n\n"
				+ "These statistics are completely anonymous, never sold, and"
				+ " stay in the EU (I'm self-hosting Plausible in Germany)."
				+ " There are no cookies or persistent identifiers"
				+ " (see plausible.io).",
			b -> {
				if(plausible != null)
					plausible.setEnabled(!plausible.isEnabled());
			});
		
		new NsylOptionsButton(-154, 72,
			() -> "Spoof Vanilla: "
				+ (vanillaSpoofOtf.isEnabled() ? "ON" : "OFF"),
			vanillaSpoofOtf.getDescription(),
			b -> vanillaSpoofOtf.doPrimaryAction());
		
		new NsylOptionsButton(-154, 96,
			() -> "Translations: " + (!forceEnglish.isChecked() ? "ON" : "OFF"),
			"Allows text in NSYL to be displayed in other languages than"
				+ " English. It will use the same language that Minecraft is"
				+ " set to.\n\n" + "This is an experimental feature!",
			b -> forceEnglish.setChecked(!forceEnglish.isChecked()));
	}
	
	private void addManagerButtons()
	{
		XRayHack xRayHack = NsylClient.INSTANCE.getHax().xRayHack;
		
		new NsylOptionsButton(-50, 24, () -> "Keybinds",
			"Keybinds allow you to toggle any hack or command by simply"
				+ " pressing a button.",
			b -> minecraft.setScreen(new KeybindManagerScreen(this)));
		
		new NsylOptionsButton(-50, 48, () -> "X-Ray Blocks",
			"Manager for the blocks that X-Ray will show.",
			b -> xRayHack.openBlockListEditor(this));
		
		new NsylOptionsButton(-50, 72, () -> "Zoom",
			"The Zoom Manager allows you to change the zoom key and how far it"
				+ " will zoom in.",
			b -> minecraft.setScreen(new ZoomManagerScreen(this)));
	}
	
	private void addLinkButtons()
	{
		OS os = Util.getPlatform();
		
		new NsylOptionsButton(54, 24, () -> "Official Website",
			"§n§lNsylClient.net",
			b -> os.openUri("https://github.com/"));
		
		new NsylOptionsButton(54, 48, () -> "NSYL Wiki", "",
			b -> os.openUri("https://github.com/"));
		
		new NsylOptionsButton(54, 72, () -> "", "",
			b -> os.openUri("https://github.com/"));
		
		new NsylOptionsButton(54, 96, () -> "Twitter", "@NSYL_Client",
			b -> os.openUri("https://github.com/"));
		
		new NsylOptionsButton(54, 120, () -> "Donate",
			"§n§lNsylClient.net/donate\n"
				+ "Donate now to help me keep the NSYL Client alive and free"
				+ " to use for everyone.\n\n"
				+ "Every bit helps and is much appreciated! You can also get a"
				+ " few cool perks in return.",
			b -> os.openUri("https://github.com/"));
	}
	
	@Override
	public void onClose()
	{
		minecraft.setScreen(prevScreen);
	}
	
	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY,
		float partialTicks)
	{
		renderBackground(context, mouseX, mouseY, partialTicks);
		renderTitles(context);
		
		for(Renderable drawable : renderables)
			drawable.render(context, mouseX, mouseY, partialTicks);
		
		renderButtonTooltip(context, mouseX, mouseY);
	}
	
	private void renderTitles(GuiGraphics context)
	{
		Font tr = minecraft.font;
		int middleX = width / 2;
		int y1 = 40;
		int y2 = height / 4 + 24 - 28;
		
		context.drawCenteredString(tr, "NSYL Options", middleX, y1, 0xffffff);
		
		context.drawCenteredString(tr, "Settings", middleX - 104, y2, 0xcccccc);
		context.drawCenteredString(tr, "Managers", middleX, y2, 0xcccccc);
		context.drawCenteredString(tr, "Links", middleX + 104, y2, 0xcccccc);
	}
	
	private void renderButtonTooltip(GuiGraphics context, int mouseX,
		int mouseY)
	{
		for(AbstractWidget button : Screens.getButtons(this))
		{
			if(!button.isHoveredOrFocused()
				|| !(button instanceof NsylOptionsButton))
				continue;
			
			NsylOptionsButton woButton = (NsylOptionsButton)button;
			
			if(woButton.tooltip.isEmpty())
				continue;
			
			context.renderComponentTooltip(font, woButton.tooltip, mouseX,
				mouseY);
			break;
		}
	}
	
	private final class NsylOptionsButton extends Button
	{
		private final Supplier<String> messageSupplier;
		private final List<Component> tooltip;
		
		public NsylOptionsButton(int xOffset, int yOffset,
			Supplier<String> messageSupplier, String tooltip,
			OnPress pressAction)
		{
			super(NsylOptionsScreen.this.width / 2 + xOffset,
				NsylOptionsScreen.this.height / 4 - 16 + yOffset, 100, 20,
				Component.literal(messageSupplier.get()), pressAction,
				Button.DEFAULT_NARRATION);
			
			this.messageSupplier = messageSupplier;
			
			if(tooltip.isEmpty())
				this.tooltip = Arrays.asList();
			else
			{
				String[] lines = ChatUtils.wrapText(tooltip, 200).split("\n");
				
				Component[] lines2 = new Component[lines.length];
				for(int i = 0; i < lines.length; i++)
					lines2[i] = Component.literal(lines[i]);
				
				this.tooltip = Arrays.asList(lines2);
			}
			
			addRenderableWidget(this);
		}
		
		@Override
		public void onPress()
		{
			super.onPress();
			setMessage(Component.literal(messageSupplier.get()));
		}
	}
}
