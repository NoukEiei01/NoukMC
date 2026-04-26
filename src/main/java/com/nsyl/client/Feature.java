/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import com.nsyl.client.event.EventManager;
import com.nsyl.client.keybinds.PossibleKeybind;
import com.nsyl.client.mixinterface.IMinecraftClient;
import com.nsyl.client.settings.Setting;
import com.nsyl.client.util.ChatUtils;

public abstract class Feature
{
	protected static final NsylClient CLIENT = NsylClient.INSTANCE;
	protected static final EventManager EVENTS = CLIENT.getEventManager();
	protected static final Minecraft MC = NsylClient.MC;
	protected static final IMinecraftClient IMC = NsylClient.IMC;
	
	private final LinkedHashMap<String, Setting> settings =
		new LinkedHashMap<>();
	private final LinkedHashSet<PossibleKeybind> possibleKeybinds =
		new LinkedHashSet<>();
	
	private final String searchTags =
		getClass().isAnnotationPresent(SearchTags.class) ? String.join("\u00a7",
			getClass().getAnnotation(SearchTags.class).value()) : "";
	
	private final boolean safeToBlock =
		!getClass().isAnnotationPresent(DontBlock.class);
	
	public abstract String getName();
	
	public abstract String getDescription();
	
	public String getWrappedDescription(int width)
	{
		return ChatUtils.wrapText(getDescription(), width);
	}
	
	public Category getCategory()
	{
		return null;
	}
	
	public abstract String getPrimaryAction();
	
	public void doPrimaryAction()
	{
		
	}
	
	public boolean isEnabled()
	{
		return false;
	}
	
	public final Map<String, Setting> getSettings()
	{
		return Collections.unmodifiableMap(settings);
	}
	
	protected final void addSetting(Setting setting)
	{
		String key = setting.getName().toLowerCase();
		
		if(settings.containsKey(key))
			throw new IllegalArgumentException(
				"Duplicate setting: " + getName() + " " + key);
		
		settings.put(key, setting);
		possibleKeybinds.addAll(setting.getPossibleKeybinds(getName()));
	}
	
	protected final void addPossibleKeybind(String command, String description)
	{
		possibleKeybinds.add(new PossibleKeybind(command, description));
	}
	
	public final Set<PossibleKeybind> getPossibleKeybinds()
	{
		return Collections.unmodifiableSet(possibleKeybinds);
	}
	
	public final String getSearchTags()
	{
		return searchTags;
	}
	
	public final boolean isSafeToBlock()
	{
		return safeToBlock;
	}
}
