package com.vame.zulrah;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

import java.awt.event.KeyEvent;

@ConfigGroup("Zulrah")
public interface ZulrahConfig extends Config {
    @ConfigItem(
            keyName = "autoPray",
            name = "Auto pray switch",
            description = "Automatically switches prayers for you.",
            position = 1
    )
    default boolean autoPray()
    {
        return true;
    }


    @ConfigItem(
            keyName = "rangeOnly",
            name = "Range only mode",
            description = "Sets the prayers to accommodate for range-only attacks",
            position = 2
    )
    default boolean rangeOnly()
    {
        return false;
    }
}
