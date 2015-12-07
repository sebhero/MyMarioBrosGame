package se.seb.mariobros.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import se.seb.mariobros.screens.PlayScreen;

/**
 * Created by Sebastian Börebäck on 2015-12-07.
 */
public abstract class Enemy extends Sprite {

    protected final PlayScreen screen;
    protected final World world;
    public Body b2body;
    public Vector2 velocity;

    public Enemy(PlayScreen screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x,y);
        defineEnemy();
        velocity = new Vector2(1, 0);
        b2body.setActive(false);

    }

    protected abstract void defineEnemy();

    public abstract void hitOnHead();

    public void reverseVelocity(boolean x, boolean y) {
        if (x) {
            velocity.x = -velocity.x;
        }
        if (y) {
            velocity.y = -velocity.y;
        }
    }

    public abstract void update(float dt);
}
