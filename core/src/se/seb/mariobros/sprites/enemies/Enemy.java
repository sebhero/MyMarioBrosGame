package se.seb.mariobros.sprites.enemies;


import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import se.seb.mariobros.screens.PlayScreen;
import se.seb.mariobros.sprites.Mario;

/**
* Created by Sebastian Börebäck on 2015-12-07.
*/
public abstract class Enemy extends Sprite {

    protected final PlayScreen screen;
    protected final World world;
    public Body b2body;
    public Vector2 velocity;

    public Enemy(PlayScreen screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(-1, -2);
        b2body.setActive(false);

    }

    protected abstract void defineEnemy();

    public abstract void hitOnHead(Mario mario);

    public void reverseVelocity(boolean x, boolean y) {
        if (x) {
            velocity.x = -velocity.x;
        }
        if (y) {
            velocity.y = -velocity.y;
        }
    }

    public abstract void update(float dt);

    public abstract void onEnemyHit(Enemy enemy);
}
