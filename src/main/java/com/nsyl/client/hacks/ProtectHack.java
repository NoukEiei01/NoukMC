/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import com.nsyl.client.Category;
import com.nsyl.client.ai.PathFinder;
import com.nsyl.client.ai.PathPos;
import com.nsyl.client.ai.PathProcessor;
import com.nsyl.client.commands.PathCmd;
import com.nsyl.client.events.RenderListener;
import com.nsyl.client.events.UpdateListener;
import com.nsyl.client.hack.DontSaveState;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.settings.AttackSpeedSliderSetting;
import com.nsyl.client.settings.CheckboxSetting;
import com.nsyl.client.settings.PauseAttackOnContainersSetting;
import com.nsyl.client.settings.SwingHandSetting;
import com.nsyl.client.settings.SwingHandSetting.SwingHand;
import com.nsyl.client.settings.filterlists.EntityFilterList;
import com.nsyl.client.settings.filters.*;
import com.nsyl.client.util.EntityUtils;
import com.nsyl.client.util.FakePlayerEntity;

@DontSaveState
public final class ProtectHack extends Hack
	implements UpdateListener, RenderListener
{
	private final AttackSpeedSliderSetting speed =
		new AttackSpeedSliderSetting();
	
	private final SwingHandSetting swingHand = new SwingHandSetting(
		SwingHandSetting.genericCombatDescription(this), SwingHand.CLIENT);
	
	private final CheckboxSetting useAi =
		new CheckboxSetting("Use AI (experimental)", false);
	
	private final PauseAttackOnContainersSetting pauseOnContainers =
		new PauseAttackOnContainersSetting(true);
	
	private final EntityFilterList entityFilters =
		new EntityFilterList(FilterPlayersSetting.genericCombat(false),
			FilterSleepingSetting.genericCombat(false),
			FilterFlyingSetting.genericCombat(0),
			FilterHostileSetting.genericCombat(false),
			FilterNeutralSetting
				.genericCombat(AttackDetectingEntityFilter.Mode.OFF),
			FilterPassiveSetting.genericCombat(false),
			FilterPassiveWaterSetting.genericCombat(false),
			FilterBabiesSetting.genericCombat(false),
			FilterBatsSetting.genericCombat(false),
			FilterSlimesSetting.genericCombat(false),
			FilterPetsSetting.genericCombat(false),
			FilterVillagersSetting.genericCombat(false),
			FilterZombieVillagersSetting.genericCombat(false),
			FilterGolemsSetting.genericCombat(false),
			FilterPiglinsSetting
				.genericCombat(AttackDetectingEntityFilter.Mode.OFF),
			FilterZombiePiglinsSetting
				.genericCombat(FilterZombiePiglinsSetting.Mode.OFF),
			FilterEndermenSetting
				.genericCombat(AttackDetectingEntityFilter.Mode.OFF),
			FilterShulkersSetting.genericCombat(false),
			FilterAllaysSetting.genericCombat(false),
			FilterInvisibleSetting.genericCombat(false),
			FilterNamedSetting.genericCombat(false),
			FilterShulkerBulletSetting.genericCombat(false),
			FilterArmorStandsSetting.genericCombat(false),
			FilterCrystalsSetting.genericCombat(true));
	
	private EntityPathFinder pathFinder;
	private PathProcessor processor;
	private int ticksProcessing;
	
	private Entity friend;
	private Entity enemy;
	
	private double distanceF = 2;
	private double distanceE = 3;
	
	public ProtectHack()
	{
		super("Protect");
		
		setCategory(Category.COMBAT);
		addSetting(speed);
		addSetting(swingHand);
		addSetting(useAi);
		addSetting(pauseOnContainers);
		
		entityFilters.forEach(this::addSetting);
	}
	
	@Override
	public String getRenderName()
	{
		if(friend != null)
			return "Protecting " + friend.getName().getString();
		return "Protect";
	}
	
	@Override
	protected void onEnable()
	{
		CLIENT.getHax().followHack.setEnabled(false);
		CLIENT.getHax().tunnellerHack.setEnabled(false);
		
		// disable other killauras
		CLIENT.getHax().aimAssistHack.setEnabled(false);
		CLIENT.getHax().clickAuraHack.setEnabled(false);
		CLIENT.getHax().crystalAuraHack.setEnabled(false);
		CLIENT.getHax().fightBotHack.setEnabled(false);
		CLIENT.getHax().killauraLegitHack.setEnabled(false);
		CLIENT.getHax().killauraHack.setEnabled(false);
		CLIENT.getHax().multiAuraHack.setEnabled(false);
		CLIENT.getHax().triggerBotHack.setEnabled(false);
		CLIENT.getHax().tpAuraHack.setEnabled(false);
		
		// set friend
		if(friend == null)
		{
			Stream<Entity> stream = StreamSupport
				.stream(MC.level.entitiesForRendering().spliterator(), true)
				.filter(LivingEntity.class::isInstance)
				.filter(
					e -> !e.isRemoved() && ((LivingEntity)e).getHealth() > 0)
				.filter(e -> e != MC.player)
				.filter(e -> !(e instanceof FakePlayerEntity));
			friend = stream
				.min(
					Comparator.comparingDouble(e -> MC.player.distanceToSqr(e)))
				.orElse(null);
		}
		
		pathFinder = new EntityPathFinder(friend, distanceF);
		
		speed.resetTimer();
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(RenderListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(RenderListener.class, this);
		
		pathFinder = null;
		processor = null;
		ticksProcessing = 0;
		PathProcessor.releaseControls();
		
		enemy = null;
		
		if(friend != null)
		{
			MC.options.keyUp.setDown(false);
			friend = null;
		}
	}
	
	@Override
	public void onUpdate()
	{
		speed.updateTimer();
		
		if(pauseOnContainers.shouldPause())
			return;
		
		// check if player died, friend died or disappeared
		if(friend == null || friend.isRemoved()
			|| !(friend instanceof LivingEntity)
			|| ((LivingEntity)friend).getHealth() <= 0
			|| MC.player.getHealth() <= 0)
		{
			friend = null;
			enemy = null;
			setEnabled(false);
			return;
		}
		
		// set enemy
		Stream<Entity> stream = EntityUtils.getAttackableEntities()
			.filter(e -> MC.player.distanceToSqr(e) <= 36)
			.filter(e -> e != friend);
		
		stream = entityFilters.applyTo(stream);
		
		enemy = stream
			.min(Comparator.comparingDouble(e -> MC.player.distanceToSqr(e)))
			.orElse(null);
		
		Entity target =
			enemy == null || MC.player.distanceToSqr(friend) >= 24 * 24 ? friend
				: enemy;
		
		double distance = target == enemy ? distanceE : distanceF;
		
		if(useAi.isChecked())
		{
			// reset pathfinder
			if((processor == null || processor.isDone() || ticksProcessing >= 10
				|| !pathFinder.isPathStillValid(processor.getIndex()))
				&& (pathFinder.isDone() || pathFinder.isFailed()))
			{
				pathFinder = new EntityPathFinder(target, distance);
				processor = null;
				ticksProcessing = 0;
			}
			
			// find path
			if(!pathFinder.isDone() && !pathFinder.isFailed())
			{
				PathProcessor.lockControls();
				CLIENT.getRotationFaker()
					.faceVectorClient(target.getBoundingBox().getCenter());
				pathFinder.think();
				pathFinder.formatPath();
				processor = pathFinder.getProcessor();
			}
			
			// process path
			if(!processor.isDone())
			{
				processor.process();
				ticksProcessing++;
			}
		}else
		{
			// jump if necessary
			if(MC.player.horizontalCollision && MC.player.onGround())
				MC.player.jumpFromGround();
			
			// swim up if necessary
			if(MC.player.isInWater() && MC.player.getY() < target.getY())
				MC.player.push(0, 0.04, 0);
			
			// control height if flying
			if(!MC.player.onGround()
				&& (MC.player.getAbilities().flying
					|| CLIENT.getHax().flightHack.isEnabled())
				&& MC.player.distanceToSqr(target.getX(), MC.player.getY(),
					target.getZ()) <= MC.player.distanceToSqr(MC.player.getX(),
						target.getY(), MC.player.getZ()))
			{
				if(MC.player.getY() > target.getY() + 1D)
					MC.options.keyShift.setDown(true);
				else if(MC.player.getY() < target.getY() - 1D)
					MC.options.keyJump.setDown(true);
			}else
			{
				MC.options.keyShift.setDown(false);
				MC.options.keyJump.setDown(false);
			}
			
			// follow target
			CLIENT.getRotationFaker()
				.faceVectorClient(target.getBoundingBox().getCenter());
			MC.options.keyUp.setDown(MC.player.distanceTo(
				target) > (target == friend ? distanceF : distanceE));
		}
		
		if(target == enemy)
		{
			CLIENT.getHax().autoSwordHack.setSlot(enemy);
			
			// check cooldown
			if(!speed.isTimeToAttack())
				return;
			
			// attack enemy
			MC.gameMode.attack(MC.player, enemy);
			swingHand.swing(InteractionHand.MAIN_HAND);
			speed.resetTimer();
		}
	}
	
	@Override
	public void onRender(PoseStack matrixStack, float partialTicks)
	{
		if(!useAi.isChecked())
			return;
		
		PathCmd pathCmd = CLIENT.getCmds().pathCmd;
		pathFinder.renderPath(matrixStack, pathCmd.isDebugMode(),
			pathCmd.isDepthTest());
	}
	
	public void setFriend(Entity friend)
	{
		this.friend = friend;
	}
	
	private class EntityPathFinder extends PathFinder
	{
		private final Entity entity;
		private double distanceSq;
		
		public EntityPathFinder(Entity entity, double distance)
		{
			super(BlockPos.containing(entity.position()));
			this.entity = entity;
			distanceSq = distance * distance;
			setThinkTime(1);
		}
		
		@Override
		protected boolean checkDone()
		{
			return done =
				entity.distanceToSqr(Vec3.atCenterOf(current)) <= distanceSq;
		}
		
		@Override
		public ArrayList<PathPos> formatPath()
		{
			if(!done)
				failed = true;
			
			return super.formatPath();
		}
	}
}
