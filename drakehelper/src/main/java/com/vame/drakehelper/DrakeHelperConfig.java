package com.vame.drakehelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

import java.awt.event.KeyEvent;

@ConfigGroup("DrakeHelper")

public interface DrakeHelperConfig extends Config
{
    @ConfigItem(
            keyName = "hotKey",
            name = "Set hot key",
            description = "Set the key to toggle on or off the flicker",
            position = 0
    )
    default Keybind hotKey()
    {
        return new Keybind(KeyEvent.VK_F2, 0);
    }
}
