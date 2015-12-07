package se.seb.mariobros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import se.seb.mariobros.MarioBros;
import se.seb.mariobros.scenes.Hud;
import se.seb.mariobros.sprites.enemies.Enemy;
import se.seb.mariobros.sprites.Mario;
import se.seb.mariobros.sprites.items.Item;
import se.seb.mariobros.sprites.items.ItemDef;
import se.seb.mariobros.sprites.items.Mushroom;
import se.seb.mariobros.tools.B2WorldCreator;
import se.seb.mariobros.tools.WorldContactListner;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by Sebastian Börebäck on 2015-12-05.
 */
public class PlayScreen implements Screen {

    private final MarioBros game;
    private final Music music;
    private TextureAtlas atlas;
    private final Hud hud;

    private OrthographicCamera gameCam;

    private Viewport gamePort;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    //sprites
    private Mario player;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;


    public PlayScreen(MarioBros game) {

        atlas = new TextureAtlas("Mario_and_enemies.pack");
        this.game = game;
        gameCam = new OrthographicCamera();
//        gamePort = new StretchViewport(800, 400, gameCam);
//        gamePort = new ScreenViewport(gameCam);
        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM,MarioBros.V_HEIGHT / MarioBros.PPM,gameCam);
        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map,1 / MarioBros.PPM);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2,0);



        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);


        player = new Mario(this);


        world.setContactListener(new WorldContactListner());

        music = MarioBros.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        //TODO uncheck for music
//        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();

    }


    public void update(float dt) {
        //check inputs
        handleInput(dt);
        handleSpawningItems();

        world.step(1/60f, 6, 2);

        player.update(dt);
        for (Enemy enemy : creator.getEnemies()) {
            enemy.update(dt);
            if (enemy.getX() < player.getX() + 224 / MarioBros.PPM) {
                enemy.b2body.setActive(true);
            }
        }

        for (Item item : items) {
            item.update(dt);

        }

        hud.update(dt);

        //attach our gamecam to our players.x coords
        if(player.currentState != Mario.State.DEAD)
            gameCam.position.x = player.b2body.getPosition().x;

        gameCam.update();
        renderer.setView(gameCam);
    }

    private void handleInput(float dt) {
        if (player.currentState == Mario.State.DEAD) {
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && (player.b2body.getLinearVelocity().x <= 2)) {
            player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && (player.b2body.getLinearVelocity().x >= -2)) {
            player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
        if (Gdx.input.isTouched()) {
            player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
        }
    }

    public TextureAtlas getAtlas() {
        return this.atlas;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        //render our box2DDebugLines
        b2dr.render(world,gameCam.combined);

        //draw mario
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
            for (Enemy enemy : creator.getEnemies()) {
                enemy.draw(game.batch);
                //14*16 / ppm when ur 14 bricks away activate goomba
                if (enemy.getX() < player.getX() + 224 / MarioBros.PPM) {

                    enemy.b2body.setActive(true);
                }
            }

            for (Item item : items) {
                item.draw(game.batch);
            }
            player.draw(game.batch);
        game.batch.end();

        //draw hud
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();

    }

    public World getWorld() {
        return world;
    }

    public TiledMap getMap() {
        return map;
    }

    public void spawnItem(ItemDef itemDef) {
        itemsToSpawn.add(itemDef);
    }

    public void handleSpawningItems() {
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class){
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    public boolean gameOver(){
        if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }
}
