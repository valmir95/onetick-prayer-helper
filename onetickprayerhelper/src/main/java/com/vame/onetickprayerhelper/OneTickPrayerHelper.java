package com.vame.onetickprayerhelper;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.api.Prayer;
import org.pf4j.Extension;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Extension
@PluginDescriptor(
	name = "One Tick Prayer",
	description = "Helps with one tick prayer."
)
@Slf4j
public class OneTickPrayerHelper extends Plugin implements KeyListener
{
	// Injects our config
	@Inject
	private OneTickPrayerHelperConfig config;

	@Inject
	private Client client;

	// Provides our config
	@Provides
	OneTickPrayerHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OneTickPrayerHelperConfig.class);
	}

	@Getter
	private boolean isClicking;
	@Getter
	private boolean lastClick;

	@Inject
	private KeyManager keyManager;

	private ScheduledExecutorService executor;

	@Override
	protected void startUp() throws Exception
	{
		keyManager.registerKeyListener(this);
		executor = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Subscribe
	public void onGameTick(GameTick event){
		if(this.isClicking){
			int firstClickDelay = this.randInt(6, 41);
			int secondClickDelay = this.randInt(72, 153);

			Prayer activePrayer = this.getActivePrayer();

			if(activePrayer == null){
				int preClickDelay = this.randInt(6, 23);
				this.executor.schedule(this::click, preClickDelay, TimeUnit.MILLISECONDS);
				return;
			}

			this.executor.schedule(this::click, firstClickDelay, TimeUnit.MILLISECONDS);
			this.executor.schedule(this::click, secondClickDelay, TimeUnit.MILLISECONDS);
			this.lastClick = true;
		}
		else if(this.lastClick){
			this.schedule(50);
			this.lastClick = false;
		}
	}

	public void schedule(int delay){
		this.executor.schedule(this::click, delay, TimeUnit.MILLISECONDS);
	}

	private void click(){
		try{
			Robot r = new Robot();
			r.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
			r.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == this.config.hotKey().getKeyCode()){
			this.isClicking = !this.isClicking;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	public Prayer getActivePrayer(){
		for (Prayer prayer : Prayer.values())
		{
			if (client.isPrayerActive(prayer))
			{
				return prayer;
			}
		}
		return null;
	}

	public int randInt(int min, int max) {

		// NOTE: This will (intentionally) not run as written so that folks
		// copy-pasting have to think about how to initialize their
		// Random instance.  Initialization of the Random instance is outside
		// the main scope of the question, but some decent options are to have
		// a field that is initialized once and then re-used as needed or to
		// use ThreadLocalRandom (if using at least Java 1.7).
		//
		// In particular, do NOT do 'Random rand = new Random()' here or you
		// will get not very good / not very random results.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		return rand.nextInt((max - min) + 1) + min;
	}
}