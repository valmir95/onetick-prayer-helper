package com.vame.zulrah.phase;

import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.Varbits;

import java.util.ArrayList;

public enum ZulrahPrayerLookup {
    MYSTIC_LORE(ZulrahType.RANGE, Prayer.MYSTIC_LORE, 27, false, null),
    HAWK_EYE(ZulrahType.MAGIC, Prayer.HAWK_EYE,26,false, null),
    PROTECT_FROM_MISSILES(ZulrahType.RANGE, Prayer.PROTECT_FROM_MISSILES,40, true, null),
    EAGLE_EYE(ZulrahType.MAGIC, Prayer.EAGLE_EYE, 44,false,  null),
    MYSTIC_MIGHT(ZulrahType.RANGE, Prayer.MYSTIC_MIGHT,45, false, null),
    RIGOUR(ZulrahType.MAGIC, Prayer.RIGOUR, 74, false, Varbits.RIGOUR_UNLOCKED),
    AUGURY(ZulrahType.RANGE, Prayer.AUGURY,77, false, Varbits.AUGURY_UNLOCKED);
    private final ZulrahType zulrahType;
    private final Prayer prayer;
    private final int requiredLevel;
    private final boolean protectionPray;
    private final Varbits varbitUnlocked;

    ZulrahPrayerLookup(ZulrahType zulrahType, Prayer prayer, int requiredLevel, boolean protectionPray, Varbits varbitUnlocked){
        this.zulrahType = zulrahType;
        this.prayer = prayer;
        this.requiredLevel = requiredLevel;
        this.protectionPray = protectionPray;
        this.varbitUnlocked = varbitUnlocked;
    }

    public ZulrahType getZulrahType(){
        return this.zulrahType;
    }

    public Prayer getPrayer(){
        return this.prayer;
    }

    public int getRequiredLevel() {
        return this.requiredLevel;
    }

    public boolean isProtectionPray() {
        return this.protectionPray;
    }

    public Varbits getVarbitUnlocked() {
        return this.varbitUnlocked;
    }

    public static ArrayList<Prayer> getPrayersFromZulrahType(ZulrahType zulrahType, int prayerLevel, Client client){
        ArrayList<Prayer> prayers = new ArrayList<>();
        ZulrahPrayerLookup complementPray = null;
        for (ZulrahPrayerLookup zulrahPrayerLookup : ZulrahPrayerLookup.values()){
            if(!zulrahPrayerLookup.protectionPray && zulrahPrayerLookup.getZulrahType() == zulrahType){
                if(complementPray == null){
                    complementPray = zulrahPrayerLookup;
                }
                else if(zulrahPrayerLookup.getRequiredLevel() > complementPray.getRequiredLevel() && zulrahPrayerLookup.getRequiredLevel() <= prayerLevel){
                    if(zulrahPrayerLookup.getVarbitUnlocked() != null){
                        int unlockedVar = client.getVar(zulrahPrayerLookup.getVarbitUnlocked());
                        if(unlockedVar == 1){
                            complementPray = zulrahPrayerLookup;
                        }
                    }
                    else{
                        complementPray = zulrahPrayerLookup;
                    }
                }
            }
        }
        prayers.add(ZulrahPrayerLookup.getProtectionPrayFromType(zulrahType));
        prayers.add(complementPray.getPrayer());

        return prayers;
    }

    public static Prayer getProtectionPrayFromType(ZulrahType zulrahType){
        if(zulrahType == ZulrahType.MAGIC) return Prayer.PROTECT_FROM_MAGIC;
        if(zulrahType == ZulrahType.RANGE) return Prayer.PROTECT_FROM_MISSILES;
        return null;
    }
}
