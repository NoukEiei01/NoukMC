/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import net.minecraft.client.particle.ParticleEngine;
import com.nsyl.client.Category;
import com.nsyl.client.SearchTags;
import com.nsyl.client.events.UpdateListener;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.settings.CheckboxSetting;
import com.nsyl.client.settings.SliderSetting;
import com.nsyl.client.settings.SliderSetting.ValueDisplay;

/**
 * NoLag - ลดแลค โดยจำกัด particle, ซ่อน weather, และลด entity render distance
 */
@SearchTags({"no lag", "anti lag", "lag reducer", "fps boost", "performance"})
public final class NoLagHack extends Hack implements UpdateListener
{
	private final CheckboxSetting reduceParticles = new CheckboxSetting(
		"Reduce Particles",
		"ลดจำนวน particle ที่แสดงผล (explosion, splash, etc.) ช่วย FPS มาก",
		true);
	
	private final SliderSetting particleMultiplier = new SliderSetting(
		"Particle %", "เหลือกี่ % ของ particle ปกติ (ยิ่งน้อยยิ่งลื่น)",
		10, 0, 100, 5, ValueDisplay.PERCENTAGE);
	
	private final CheckboxSetting noWeather = new CheckboxSetting(
		"No Weather Particles",
		"ซ่อน rain/snow particle (ไม่ซ่อน weather effect ใน sky)",
		true);
	
	private final CheckboxSetting limitEntities = new CheckboxSetting(
		"Limit Entity Updates",
		"จำกัดจำนวน entity ที่ถูก update ต่อ tick เพื่อลด CPU load",
		false);
	
	private final SliderSetting entityUpdateLimit = new SliderSetting(
		"Entity Limit", "จำนวน entity สูงสุดที่ update ต่อ tick",
		64, 8, 512, 8, ValueDisplay.INTEGER);
	
	// Counter for particle throttling
	private int particleCounter = 0;
	
	public NoLagHack()
	{
		super("NoLag");
		setCategory(Category.RENDER);
		addSetting(reduceParticles);
		addSetting(particleMultiplier);
		addSetting(noWeather);
		addSetting(limitEntities);
		addSetting(entityUpdateLimit);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		particleCounter = 0;
	}
	
	@Override
	public void onUpdate()
	{
		particleCounter++;
		if(particleCounter > 1000)
			particleCounter = 0;
	}
	
	/**
	 * ถูกเรียกจาก ParticleManagerMixin ก่อน spawn particle
	 * คืนค่า true = ให้ spawn, false = บล็อก particle
	 */
	public boolean shouldSpawnParticle()
	{
		if(!isEnabled() || !reduceParticles.isChecked())
			return true;
		
		int threshold = particleMultiplier.getValueI();
		if(threshold >= 100)
			return true;
		if(threshold <= 0)
			return false;
		
		// ใช้ counter แทน Math.random() เพื่อ deterministic และเร็วกว่า
		return (particleCounter % 100) < threshold;
	}
	
	public boolean shouldHideWeatherParticles()
	{
		return isEnabled() && noWeather.isChecked();
	}
	
	public boolean isEntityLimitEnabled()
	{
		return isEnabled() && limitEntities.isChecked();
	}
	
	public int getEntityUpdateLimit()
	{
		return entityUpdateLimit.getValueI();
	}
	
	@Override
	public String getRenderName()
	{
		if(!isEnabled())
			return getName();
		
		StringBuilder sb = new StringBuilder(getName()).append(" [");
		boolean any = false;
		if(reduceParticles.isChecked())
		{
			sb.append("P:").append(particleMultiplier.getValueI()).append('%');
			any = true;
		}
		if(noWeather.isChecked())
		{
			if(any) sb.append(' ');
			sb.append("W");
			any = true;
		}
		if(limitEntities.isChecked())
		{
			if(any) sb.append(' ');
			sb.append("E:").append(entityUpdateLimit.getValueI());
		}
		sb.append(']');
		return sb.toString();
	}
}
