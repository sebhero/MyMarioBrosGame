package se.seb.mariobros.sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import se.seb.mariobros.MarioBros;
import se.seb.mariobros.screens.PlayScreen;
import se.seb.mariobros.sprites.enemies.Enemy;
import se.seb.mariobros.sprites.enemies.Turtle;

/**
 * Created by Sebastian Börebäck on 2015-12-05.
 */
public class Mario extends Sprite {


    private boolean timeToDefineBigMario;
    private boolean timeToReDefineBigMario;
    private boolean marioIsDead;


    public enum  State{FALLING,JUMPING,STANDING, RUNNING, DEAD, GROWING};

    public State currentState;
    public State previouseState;

    public World world;
    public Body b2body;
    private TextureRegion marioStand;
    private Animation marioRun;
    private TextureRegion marioJump;
    private TextureRegion bigMarioStand;
    private Animation bigMarioRun;
    private TextureRegion bigMarioJump;
    private TextureRegion marioDead;
    private Animation growMario;

    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;

    public Mario(PlayScreen screen) {
//        super(screen.getAtlas().findRegion("little_mario"));
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previouseState = State.STANDING;
        stateTimer = 0;
        runningRight =true;

        Array<TextureRegion> frames = new Array();
        for (int i = 1; i < 4; i++) {
//            frames.add(new TextureRegion(getTexture(),i*16,10,16,16));
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"),i*16,0,16,16));
        }

        marioRun = new Animation(0.1f, frames);
        frames.clear();

        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),i*16,0,16,32));
//            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),i*16,10,16,32));
        }

        bigMarioRun = new Animation(0.1f, frames);
        frames.clear();

        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32));
        growMario = new Animation(0.2f, frames);
        frames.clear();

//        for (int i = 4; i < 6; i++) {
//            //frames.add(new TextureRegion(getTexture(),i*16,10,16,16));
//            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"),i*16,0,16,16));
//        }

//        = new Animation(0.1f, frames);
        marioJump= new TextureRegion(screen.getAtlas().findRegion("little_mario"),80,0,16,16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"),80,0,16,32);
//        frames.clear();

        definedMario();
        //marioStand = new TextureRegion(getTexture(),0,10,16,16);
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"),0,0,16,16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32);
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);


        setBounds(0,0,16/ MarioBros.PPM, 16/ MarioBros.PPM);
        setRegion(marioStand);
    }


    public void update(float dt) {
        if (marioIsBig) {
            setPosition(b2body.getPosition().x - getWidth() / 2,
                    b2body.getPosition().y - getHeight() / 2 - 6 / MarioBros.PPM);
        }
        else {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }
        setRegion(getFrame(dt));
        if (timeToDefineBigMario) {
            definedBigMario();
        }
        if (timeToReDefineBigMario) {
            redefinedMario();
        }
    }




    private TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region;
        switch (currentState) {
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
//                region = marioJump.getKeyFrame(stateTimer);
                region = marioIsBig? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig? bigMarioRun.getKeyFrame(stateTimer,true) : marioRun.getKeyFrame(stateTimer,true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig? bigMarioStand : marioStand;
                break;
        }
        //which direction mario is running
        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true,false);
            runningRight = false;
        }
        else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true,false);
            runningRight = true;
        }
        //update timer
        stateTimer = currentState == previouseState ? stateTimer +dt: 0;
        //update state
        previouseState = currentState;

        return region;
    }

    public State getState() {
        if (marioIsDead) {
            return State.DEAD;
        }
        else if (runGrowAnimation) {
            return State.GROWING;
        } else if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previouseState == State.JUMPING)) {
            return State.JUMPING;
        } else if (b2body.getLinearVelocity().y < 0) {
            return State.FALLING;
        } else if (b2body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        } else
            return State.STANDING;

    }
    private void definedBigMario() {
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / MarioBros.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    private void definedMario() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);

        //what the mario is
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        //what he can collide with
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT |
                MarioBros.BRICK_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
//        b2body.createFixture(fdef);


        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM,6 / MarioBros.PPM),new Vector2(2 / MarioBros.PPM,6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true; // doesnt collided

        b2body.createFixture(fdef).setUserData(this);
    }

    public void grow() {
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigMario = true;
        setBounds(getX(),getY(),getWidth(),getHeight()*2);
        MarioBros.manager.get("audio/sound/powerup.wav", Sound.class).play();
    }

    public boolean isBig() {
        return marioIsBig;
    }

    public void hit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL) {
            ((Turtle)enemy).kick((this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT: Turtle.KICK_LEFT));

        }else if (marioIsBig) {
            marioIsBig = false;
            timeToReDefineBigMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() / 2);
            MarioBros.manager.get("audio/sound/powerdown.wav", Sound.class).play();
        } else {
            //mario dies
            MarioBros.manager.get("audio/music/mario_music.ogg", Music.class).stop();
            MarioBros.manager.get("audio/sound/mariodie.wav", Sound.class).play();
            marioIsDead = true;
            Filter filter = new Filter();
            filter.maskBits = MarioBros.NOTHING_BIT;
            for (Fixture fixture : b2body.getFixtureList()) {
                fixture.setFilterData(filter);
            }
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);


        }
    }

    private void redefinedMario() {
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);

        //what the mario is
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        //what he can collide with
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT |
                MarioBros.BRICK_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true; // doesnt collided

        b2body.createFixture(fdef).setUserData(this);

        timeToReDefineBigMario =false;

    }
    public boolean isDead() {
        return marioIsDead;
    }

    public float getStateTimer() {
        return stateTimer;
    }
}
