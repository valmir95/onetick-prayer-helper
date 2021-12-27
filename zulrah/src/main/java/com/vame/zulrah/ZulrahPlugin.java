package com.vame.zulrah;/*
 * Copyright (c) 2017, Aria <aria@ar1as.space>
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * Copyright (c) 2017, Devin French <https://github.com/devinfrench>
 * Copyright (c) 2019, Ganom <https://github.com/ganom>
 * Copyright (c) 2020, Valmir95 <https://github.com/valmir95>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.google.inject.Provides;
import com.vame.zulrah.overlays.ZulrahCurrentPhaseOverlay;
import com.vame.zulrah.overlays.ZulrahNextPhaseOverlay;
import com.vame.zulrah.overlays.ZulrahOverlay;
import com.vame.zulrah.overlays.ZulrahPrayerOverlay;
import com.vame.zulrah.patterns.*;
import com.vame.zulrah.phase.ZulrahPhase;
import com.vame.zulrah.phase.ZulrahPrayerLookup;
import com.vame.zulrah.phase.ZulrahType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Extension
@PluginDescriptor(
	name = "Zulrah Helper",
	description = "Shows tiles on where to stand during the phases and what prayer to use.",
	tags = {"zulrah", "boss", "helper"}
)
@Slf4j
public class ZulrahPlugin extends Plugin
{
	private static final ZulrahPattern[] patterns = new ZulrahPattern[]
		{
			new ZulrahPatternA(),
			new ZulrahPatternB(),
			new ZulrahPatternC(),
			new ZulrahPatternD()
		};

	@Getter(AccessLevel.PACKAGE)
	private NPC zulrah;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ZulrahCurrentPhaseOverlay currentPhaseOverlay;

	@Inject
	private ZulrahNextPhaseOverlay nextPhaseOverlay;

	@Inject
	private ZulrahPrayerOverlay zulrahPrayerOverlay;

	@Inject
	private ZulrahOverlay zulrahOverlay;

	@Getter
	private MenuEntryValues menuEntryValues;

	@Getter
	private Prayer prevPrayer;

	private ZulrahInstance instance;

	private ExecutorService executor;

	private ZulrahPhase prevPhase;

	@Inject
	private ZulrahConfig config;

	@Override
	protected void startUp()
	{
		overlayManager.add(currentPhaseOverlay);
		overlayManager.add(nextPhaseOverlay);
		overlayManager.add(zulrahPrayerOverlay);
		overlayManager.add(zulrahOverlay);
		executor = Executors.newSingleThreadExecutor();
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(currentPhaseOverlay);
		overlayManager.remove(nextPhaseOverlay);
		overlayManager.remove(zulrahPrayerOverlay);
		overlayManager.remove(zulrahOverlay);
		zulrah = null;
		instance = null;
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event) {
		if (this.menuEntryValues != null) {
			event.setMenuEntry(this.client.createMenuEntry(
					this.menuEntryValues.getOption(),
					this.menuEntryValues.getTarget(),
					this.menuEntryValues.getIdentifier(),
					this.menuEntryValues.getOpcode(),
					this.menuEntryValues.getParam1(),
					this.menuEntryValues.getParam2(),
					this.menuEntryValues.isForceLeftClick()
			));
			this.menuEntryValues = null;
		}
	}

	@Provides
	ZulrahConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ZulrahConfig.class);
	}



	@Subscribe
	private void onGameTick(GameTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}



		int var = client.getVar(Varbits.RIGOUR_UNLOCKED);
		int varbitval = client.getVarbitValue(Varbits.RIGOUR_UNLOCKED.getId());

		if (zulrah == null)
		{
			if (instance != null)
			{
				log.debug("Zulrah encounter has ended.");
				instance = null;
			}
			return;
		}

		if (instance == null)
		{
			instance = new ZulrahInstance(zulrah);
			log.debug("Zulrah encounter has started.");
		}

		ZulrahPhase currentPhase = ZulrahPhase.valueOf(zulrah, instance.getStartLocation());

		if (instance.getPhase() == null)
		{
			instance.setPhase(currentPhase);
		}
		else if (!instance.getPhase().equals(currentPhase))
		{
			ZulrahPhase previousPhase = instance.getPhase();
			instance.setPhase(currentPhase);
			instance.nextStage();

			log.debug("Zulrah phase has moved from {} -> {}, stage: {}", previousPhase, currentPhase, instance.getStage());
		}

		if(this.client.getBoostedSkillLevel(Skill.PRAYER) > 0){
			boolean isJad = this.instance.getPattern() != null && this.instance.getPattern().getJadIndex() == this.instance.getStage();
			boolean ignoreProtection = false;
			if(this.instance.getPhase().getPrayer() == null || isJad){
				ignoreProtection = true;
			}
			List<Prayer> prayers = ZulrahPrayerLookup.getPrayersFromZulrahType(instance.getPhase().getType(), this.client, this.config, ignoreProtection);
			this.activatePrayers(prayers);
			if(ignoreProtection){
				if(this.prevPhase == null){
					this.prevPhase = this.instance.getPhase();
				}
				if(!prevPhase.equals(this.instance.getPhase())){
					List<Prayer> noProtectPrayers = this.getActivePrayers().stream().filter(p -> p.name().startsWith("PROTECT_FROM")).collect(Collectors.toList());
					this.executor.submit(() -> {
						this.deactivatePrayers(noProtectPrayers);
					});
					this.prevPhase = this.instance.getPhase();
				}
			}
		}

		ZulrahPattern pattern = instance.getPattern();

		if (pattern == null)
		{
			int potential = 0;
			ZulrahPattern potentialPattern = null;

			for (ZulrahPattern p : patterns)
			{
				if (p.stageMatches(instance.getStage(), instance.getPhase()))
				{
					potential++;
					potentialPattern = p;
				}
			}

			if (potential == 1)
			{
				log.debug("Zulrah pattern identified: {}", potentialPattern);

				instance.setPattern(potentialPattern);
			}
		}
		else if (pattern.canReset(instance.getStage()) && (instance.getPhase() == null || instance.getPhase().equals(pattern.get(0))))
		{
			log.debug("Zulrah pattern has reset.");

			instance.reset();
		}
	}


	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN)
		{
			this.zulrah = null;
		}
	}



	@Subscribe
	public void onProjectileMoved(ProjectileMoved event) {
		if(this.instance == null){
			return;
		}
		boolean isJad = this.instance.getPattern() != null && this.instance.getPattern().getJadIndex() == this.instance.getStage();

		if(isJad){
			int randomDelay = ThreadLocalRandom.current().nextInt(23, 133);
			if(event.getProjectile().getId() == ZulrahProjectile.RANGED_PROJECTILE.getProjectileId()){
				this.activatePrayer(Prayer.PROTECT_FROM_MISSILES, randomDelay);
			}
			else if(event.getProjectile().getId() == ZulrahProjectile.MAGE_PROJECTILE.getProjectileId()){
				this.activatePrayer(Prayer.PROTECT_FROM_MAGIC, randomDelay);
			}
		}
	}

	private void activatePrayers(List<Prayer> prayersToActivate){
		for (Prayer prayer : prayersToActivate){
			int randomDelay = ThreadLocalRandom.current().nextInt(33, 164);
			if(prayer != null && !isPrayerActive(prayer)){
				this.executor.submit(() -> {
					this.activatePrayer(prayer, randomDelay);
				});
			}
		}
	}

	private void activatePrayer(Prayer prayer, int msSleepTime){
		try{
			Widget prayerWidget = this.client.getWidget(prayer.getWidgetInfo());
			this.menuEntryValues = new MenuEntryValues("Activate", prayerWidget.getName(), 1, MenuAction.CC_OP.getId(), prayerWidget.getItemId(), prayerWidget.getId(), false);
			this.shadowClick();
			Thread.sleep(msSleepTime);
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	private void deactivatePrayers(List<Prayer> prayersToDeactivate){
		for (Prayer prayer : prayersToDeactivate){
			int randomDelay = ThreadLocalRandom.current().nextInt(33, 164);
			deactivatePrayer(prayer, randomDelay);
		}
	}

	private void deactivatePrayer(Prayer prayer, int msSleepTime){
		try{
			Widget prayerWidget = this.client.getWidget(prayer.getWidgetInfo());
			this.menuEntryValues = new MenuEntryValues("Deactivate", prayerWidget.getName(), 1, MenuAction.CC_OP.getId(), prayerWidget.getItemId(), prayerWidget.getId(), false);
			this.shadowClick();
			Thread.sleep(msSleepTime);
		}catch (Exception ex){
			ex.printStackTrace();
		}

	}

	public ArrayList<Prayer> getActivePrayers(){
		ArrayList<Prayer> prayers = new ArrayList<>();

		for (Prayer prayer : Prayer.values())
		{
			if (client.isPrayerActive(prayer))
			{
				prayers.add(prayer);
			}
		}
		return prayers;
	}

	public boolean isPrayerActive(Prayer prayer){
		ArrayList<Prayer> activePrayers = this.getActivePrayers();
		System.out.println("Active prayers: " + activePrayers.toString());
		for (Prayer activePrayer : activePrayers){
			if(activePrayer.name().equals(prayer.name())){
				return true;
			}
		}
		return false;
	}


	private MenuEntry getActivatePrayerMenuEntry(Prayer prayer){
		Widget prayerWidget = this.client.getWidget(prayer.getWidgetInfo());
		return this.client.createMenuEntry("Activate", prayerWidget.getName(), 1, MenuAction.CC_OP.getId(), prayerWidget.getItemId(), prayerWidget.getId(), false);
	}

	private MenuEntry getDeactivatePrayerMenuEntry(Prayer prayer){
		Widget prayerWidget = this.client.getWidget(prayer.getWidgetInfo());
		return this.client.createMenuEntry("Deactivate", prayerWidget.getName(), 1, MenuAction.CC_OP.getId(), prayerWidget.getItemId(), prayerWidget.getId(), false);
	}


	public void shadowClick() {
		Point pos = client.getMouseCanvasPosition();
		if (client.isStretchedEnabled()) {
			final Dimension stretched = client.getStretchedDimensions();
			final Dimension real = client.getRealDimensions();
			final double width = (stretched.width / real.getWidth());
			final double height = (stretched.height / real.getHeight());
			final Point point = new Point((int)(pos.getX() * width), (int)(pos.getY() * height));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			return;
		}
		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned event)
	{
		NPC npc = event.getNpc();
		if (npc != null && npc.getName() != null &&
			npc.getName().toLowerCase().contains("zulrah"))
		{
			zulrah = npc;
		}
	}

	@Subscribe
	private void onNpcDespawned(NpcDespawned event)
	{
		NPC npc = event.getNpc();
		if (npc != null && npc.getName() != null &&
			npc.getName().toLowerCase().contains("zulrah"))
		{
			zulrah = null;
		}
	}

	public ZulrahInstance getInstance()
	{
		return instance;
	}
}