package com.vame.drakehelper;

public enum DrakeProjectile {
    RANGED_NORMAL_PROJECTILE(1636),
    RANGED_DRAGONFIRE_PROJECTILE(1637);

    private final int projectileId;

    DrakeProjectile(int projectileId)
    {
        this.projectileId = projectileId;
    }

    public int getProjectileId(){
        return this.projectileId;
    }
}


