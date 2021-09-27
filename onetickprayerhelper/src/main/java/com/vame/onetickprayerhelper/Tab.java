package com.vame.onetickprayerhelper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.vars.InterfaceTab;

@Getter
@AllArgsConstructor
public enum Tab
{
    COMBAT(4675, InterfaceTab.COMBAT),
    EXP(4676, InterfaceTab.STATS),
    QUESTS(4677, InterfaceTab.QUEST),
    INVENTORY(4678, InterfaceTab.INVENTORY),
    EQUIPMENT(4679, InterfaceTab.EQUIPMENT),
    PRAYER(4680, InterfaceTab.PRAYER),
    SPELLBOOK(4682, InterfaceTab.SPELLBOOK),
    CLAN(4683, InterfaceTab.CLAN),
    FRIENDS(4684, InterfaceTab.FRIENDS),
    SETTINGS(4686, InterfaceTab.OPTIONS),
    EMOTES(4687, InterfaceTab.EMOTES),
    MUSIC(4688, InterfaceTab.MUSIC),
    LOGOUT(4689, InterfaceTab.LOGOUT),
    ACCOUNT(6517, InterfaceTab.ACCOUNT_MANAGMENT);

    private final int varbit;
    private final InterfaceTab interfaceTab;
}