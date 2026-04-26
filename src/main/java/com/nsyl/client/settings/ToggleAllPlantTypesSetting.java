/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.settings;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import com.nsyl.client.NsylClient;
import com.nsyl.client.clickgui.Component;
import com.nsyl.client.clickgui.components.ToggleAllPlantTypesComponent;
import com.nsyl.client.keybinds.PossibleKeybind;
import com.nsyl.client.util.text.WText;

public final class ToggleAllPlantTypesSetting extends Setting
{
	private final List<PlantTypeSetting> plantTypes;
	
	public ToggleAllPlantTypesSetting(String name,
		Stream<PlantTypeSetting> plantTypes)
	{
		super(name, WText.empty());
		this.plantTypes = plantTypes.toList();
	}
	
	/**
	 * Returns <code>true</code> if all plant types have harvesting enabled,
	 * <code>false</code> if all plant types have harvesting disabled, or
	 * <code>null</code> otherwise.
	 */
	public Boolean isHarvestingEnabled()
	{
		boolean allEnabled =
			plantTypes.stream().allMatch(PlantTypeSetting::isHarvestingEnabled);
		boolean allDisabled =
			plantTypes.stream().allMatch(type -> !type.isHarvestingEnabled());
		return allEnabled ? Boolean.TRUE : allDisabled ? Boolean.FALSE : null;
	}
	
	/**
	 * Returns <code>true</code> if all plant types have replanting enabled,
	 * <code>false</code> if all plant types have replanting disabled, or
	 * <code>null</code> otherwise.
	 */
	public Boolean isReplantingEnabled()
	{
		boolean allEnabled =
			plantTypes.stream().allMatch(PlantTypeSetting::isReplantingEnabled);
		boolean allDisabled =
			plantTypes.stream().allMatch(type -> !type.isReplantingEnabled());
		return allEnabled ? Boolean.TRUE : allDisabled ? Boolean.FALSE : null;
	}
	
	public void setHarvestingEnabled(boolean harvest)
	{
		plantTypes
			.forEach(type -> type.setHarvestingEnabledWithoutSaving(harvest));
		NsylClient.INSTANCE.saveSettings();
	}
	
	public void setReplantingEnabled(boolean replant)
	{
		plantTypes
			.forEach(type -> type.setReplantingEnabledWithoutSaving(replant));
		NsylClient.INSTANCE.saveSettings();
	}
	
	public void toggleHarvestingEnabled()
	{
		Boolean enabled = isHarvestingEnabled();
		if(enabled == null || enabled == Boolean.FALSE)
			setHarvestingEnabled(true);
		else
			setHarvestingEnabled(false);
	}
	
	public void toggleReplantingEnabled()
	{
		Boolean enabled = isReplantingEnabled();
		if(enabled == null || enabled == Boolean.FALSE)
			setReplantingEnabled(true);
		else
			setReplantingEnabled(false);
	}
	
	public void resetHarvestingEnabled()
	{
		for(PlantTypeSetting type : plantTypes)
			type.setHarvestingEnabledWithoutSaving(
				type.isHarvestingEnabledByDefault());
		NsylClient.INSTANCE.saveSettings();
	}
	
	public void resetReplantingEnabled()
	{
		for(PlantTypeSetting type : plantTypes)
			type.setReplantingEnabledWithoutSaving(
				type.isReplantingEnabledByDefault());
		NsylClient.INSTANCE.saveSettings();
	}
	
	@Override
	public Component getComponent()
	{
		return new ToggleAllPlantTypesComponent(this);
	}
	
	@Override
	public void fromJson(JsonElement json)
	{
		
	}
	
	@Override
	public JsonElement toJson()
	{
		return JsonNull.INSTANCE;
	}
	
	@Override
	public JsonObject exportWikiData()
	{
		return new JsonObject();
	}
	
	@Override
	public Set<PossibleKeybind> getPossibleKeybinds(String featureName)
	{
		return new LinkedHashSet<>();
	}
}
