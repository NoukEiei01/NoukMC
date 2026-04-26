/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import com.nsyl.client.Category;
import com.nsyl.client.SearchTags;
import com.nsyl.client.events.HandleInputListener;
import com.nsyl.client.events.PreMotionListener;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.mixinterface.IKeyBinding;
import com.nsyl.client.settings.AttackSpeedSliderSetting;
import com.nsyl.client.settings.CheckboxSetting;
import com.nsyl.client.settings.SliderSetting;
import com.nsyl.client.settings.SliderSetting.ValueDisplay;
import com.nsyl.client.settings.SwingHandSetting;
import com.nsyl.client.settings.SwingHandSetting.SwingHand;
import com.nsyl.client.settings.filterlists.EntityFilterList;
import com.nsyl.client.util.EntityUtils;

@SearchTags({"trigger bot", "AutoAttack", "auto attack", "AutoClicker",
	"auto clicker"})
public final class TriggerBotHack extends Hack
	implements PreMotionListener, HandleInputListener
{
	private final SliderSetting range =
		new SliderSetting("Range", 4.25, 1, 6, 0.05, ValueDisplay.DECIMAL);
	
	private final AttackSpeedSliderSetting speed =
		new AttackSpeedSliderSetting();
	
	private final SliderSetting speedRandMS =
		new SliderSetting("Speed randomization",
			"Helps you bypass anti-cheat plugins by varying the delay between"
				+ " attacks.\n\n" + "\u00b1100ms is recommended for Vulcan.\n\n"
				+ "0 (off) is fine for NoCheat+, AAC, Grim, Verus, Spartan, and"
				+ " vanilla servers.",
			100, 0, 1000, 50, ValueDisplay.INTEGER.withPrefix("\u00b1")
				.withSuffix("ms").withLabel(0, "off"));
	
	private final SwingHandSetting swingHand =
		new SwingHandSetting(this, SwingHand.CLIENT);
	
	private final CheckboxSetting attackWhileBlocking =
		new CheckboxSetting("Attack while blocking",
			"Attacks even while you're blocking with a shield or using"
				+ " items.\n\n"
				+ "This would not be possible in vanilla and won't work if"
				+ " \"Simulate mouse click\" is enabled.",
			false);
	
	private final CheckboxSetting simulateMouseClick = new CheckboxSetting(
		"Simulate mouse click",
		"Simulates an actual mouse click (or key press) when attacking. Can be"
			+ " used to trick CPS measuring tools into thinking that you're"
			+ " attacking manually.\n\n"
			+ "\u00a7c\u00a7lWARNING:\u00a7r Simulating mouse clicks can lead"
			+ " to unexpected behavior, like in-game menus clicking themselves."
			+ " Also, the \"Swing hand\" and \"Attack while blocking\" settings"
			+ " will not work while this option is enabled.",
		false);
	
	private final EntityFilterList entityFilters =
		EntityFilterList.genericCombat();
	
	private boolean simulatingMouseClick;
	
	public TriggerBotHack()
	{
		super("TriggerBot");
		setCategory(Category.COMBAT);
		
		addSetting(range);
		addSetting(speed);
		addSetting(speedRandMS);
		addSetting(swingHand);
		addSetting(attackWhileBlocking);
		addSetting(simulateMouseClick);
		
		entityFilters.forEach(this::addSetting);
	}
	
	@Override
	protected void onEnable()
	{
		// disable other killauras
		CLIENT.getHax().clickAuraHack.setEnabled(false);
		CLIENT.getHax().crystalAuraHack.setEnabled(false);
		CLIENT.getHax().fightBotHack.setEnabled(false);
		CLIENT.getHax().killauraLegitHack.setEnabled(false);
		CLIENT.getHax().killauraHack.setEnabled(false);
		CLIENT.getHax().multiAuraHack.setEnabled(false);
		CLIENT.getHax().protectHack.setEnabled(false);
		CLIENT.getHax().tpAuraHack.setEnabled(false);
		
		speed.resetTimer(speedRandMS.getValue());
		EVENTS.add(PreMotionListener.class, this);
		EVENTS.add(HandleInputListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		if(simulatingMouseClick)
		{
			IKeyBinding.get(MC.options.keyAttack).simulatePress(false);
			simulatingMouseClick = false;
		}
		
		EVENTS.remove(PreMotionListener.class, this);
		EVENTS.remove(HandleInputListener.class, this);
	}
	
	@Override
	public void onPreMotion()
	{
		if(!simulatingMouseClick)
			return;
		
		IKeyBinding.get(MC.options.keyAttack).simulatePress(false);
		simulatingMouseClick = false;
	}
	
	@Override
	public void onHandleInput()
	{
		speed.updateTimer();
		if(!speed.isTimeToAttack())
			return;
		
		// don't attack when a container/inventory screen is open
		if(MC.screen instanceof AbstractContainerScreen)
			return;
		
		LocalPlayer player = MC.player;
		if(!attackWhileBlocking.isChecked() && player.isUsingItem())
			return;
		
		if(MC.hitResult == null
			|| !(MC.hitResult instanceof EntityHitResult eResult))
			return;
		
		Entity target = eResult.getEntity();
		if(!isCorrectEntity(target))
			return;
		
		CLIENT.getHax().autoSwordHack.setSlot(target);
		
		if(simulateMouseClick.isChecked())
		{
			IKeyBinding.get(MC.options.keyAttack).simulatePress(true);
			simulatingMouseClick = true;
			
		}else
		{
			MC.gameMode.attack(player, target);
			swingHand.swing(InteractionHand.MAIN_HAND);
		}
		
		speed.resetTimer(speedRandMS.getValue());
	}
	
	private boolean isCorrectEntity(Entity entity)
	{
		if(!EntityUtils.IS_ATTACKABLE.test(entity))
			return false;
		
		if(MC.player.distanceToSqr(entity) > range.getValueSq())
			return false;
		
		return entityFilters.testOne(entity);
	}
}
