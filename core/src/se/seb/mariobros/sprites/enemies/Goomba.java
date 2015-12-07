package se.seb.mariobros.sprites.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import se.seb.mariobros.MarioBros;
import se.seb.mariobros.screens.PlayScreen;

/**
 * Created by Sebastian Börebäck on 2015-12-07.
 */
public class Goomba extends Enemy {

    private float statetime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed = false;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for (int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"),i*16,0,16,16));

        }

        walkAnimation = new Animation(0.4f, frames);
        statetime=0;
        setToDestroy= false;
        destroyed = false;
        setBounds(getX(),getY(),16 / MarioBros.PPM,16 / MarioBros.PPM);

    }

    public void update(float dt) {
        statetime += dt;
        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"),32, 0, 16, 16));
            statetime = 0;
        }
        else if (!destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(statetime,true));
        }
    }

    @Override
    protected void defineEnemy() {

        BodyDef bdef = new BodyDef();
//        bdef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
        bdef.position.set(getX(),getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);

        //what the mario is
        fdef.filter.categoryBits = MarioBros.ENEMY_BIT;
        //what he can collide with
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.MARIO_BIT  |
                MarioBros.OBJECT_BIT;

        fdef.shape = shape;

        b2body.createFixture(fdef).setUserData(this);

        //create the head
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 8).scl(1/ MarioBros.PPM);
        vertice[1] = new Vector2(5, 8).scl(1/ MarioBros.PPM);
        vertice[2] = new Vector2(-3,3).scl(1/ MarioBros.PPM);
        vertice[3] = new Vector2(3, 3).scl(1/ MarioBros.PPM);
        head.set(vertice);
        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);

    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed || statetime < 1) {
            super.draw(batch);
        }
    }

    @Override
    public void hitOnHead() {
        setToDestroy = true;
        MarioBros.manager.get("audio/sound/stomp.wav", Sound.class).play();
    }
}
