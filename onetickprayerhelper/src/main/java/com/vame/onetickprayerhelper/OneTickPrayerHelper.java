package com.vame.onetickprayerhelper;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.vars.InterfaceTab;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.banktags.tabs.TabInterface;
import net.runelite.client.plugins.prayer.PrayerFlickLocation;
import org.pf4j.Extension;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.runelite.rs.api.RSClient;

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

	@Getter
	private MenuEntry menuEntry;

	@Inject
	private KeyManager keyManager;

	private ScheduledExecutorService executor;

	@Inject
	private ClientThread clientThread;

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
			this.executor.schedule(this::click, 50, TimeUnit.MILLISECONDS);

			this.lastClick = false;
		}
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
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}
}