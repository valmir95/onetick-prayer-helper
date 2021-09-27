package com.vame.zulrah;

public enum ZulrahProjectile {
    RANGED_PROJECTILE(1044),
    MAGE_PROJECTILE(1046);

    private final int projectileId;

    ZulrahProjectile(int projectileId)
    {
        this.projectileId = projectileId;
    }

    public int getProjectileId(){
        return this.projectileId;
    }
}
