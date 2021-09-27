package com.vame.drakehelper;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.input.KeyListener;
import org.pf4j.Extension;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import net.runelite.rs.api.RSClient;
@Extension
@PluginDescriptor(
        name = "Drake helper",
        description = "Tells when you should move before magic attack"
)

@Slf4j
public class DrakeHelperPlugin extends Plugin implements KeyListener {
    @Inject
    private Client client;
    @Getter
    private LocalPoint projectileEndLoc;


    @Provides
    DrakeHelperConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(DrakeHelperConfig.class);
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked event) {
        if (this.projectileEndLoc != null) {
            walkTile(this.projectileEndLoc);
            this.projectileEndLoc = null;
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


    public void walkTile(LocalPoint localPoint) {
        RSClient rsClient = (RSClient) client;
        rsClient.setSelectedSceneTileX(localPoint.getSceneX()+1);
        rsClient.setSelectedSceneTileY(localPoint.getSceneY());
        rsClient.setViewportWalking(true);
        rsClient.setCheckClick(false);
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == KeyEvent.VK_F11){
            try{
                this.projectileEndLoc = client.getLocalPlayer().getLocalLocation();
                this.shadowClick();

            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved event) {
        //event.getProjectile().
        //if(event.getProjectile().getInteracting() == null){
        //return;
        //}
        LocalPoint playerLoc = client.getLocalPlayer().getLocalLocation();
        LocalPoint projectileEnd = event.getPosition();
        boolean isProjectileForMe = playerLoc.equals(projectileEnd);
        boolean isDragonFireProjectile = event.getProjectile().getId() == DrakeProjectile.RANGED_DRAGONFIRE_PROJECTILE.getProjectileId();
        //boolean isInteractingWithMe = event.getProjectile().getInteracting().getName().equals(client.getLocalPlayer().getName());
        System.out.println(event.getProjectile().getId());
        if(isDragonFireProjectile && isProjectileForMe){
            System.out.println("MOVE OUT THE WAY BIATCHHH!!");
            //this.projectileEndLoc = projectileEnd;
            //this.shadowClick();
            this.walkTile(projectileEnd);
        }
    }
}
