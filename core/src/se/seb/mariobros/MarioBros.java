package se.seb.mariobros;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import se.seb.mariobros.screens.PlayScreen;

//public class MarioBros extends ApplicationAdapter {
public class MarioBros extends Game {

    public SpriteBatch batch;
    public static final int  V_WIDTH = 400;
    public static final int  V_HEIGHT = 208;
    public static final  float PPM = 100;


    public static final short GROUND_BIT = 1;
    public static final short MARIO_BIT = 2;
    public static final short BRICK_BIT= 4;
    public static final short COIN_BIT= 8;
    public static final short DESTROYED_BIT= 16;
    public static final short OBJECT_BIT = 32;
    public static final short ENEMY_BIT = 64;
    public static final short ENEMY_HEAD_BIT = 128;
    public static final short ITEM_BIT = 256;
    public static final short MARIO_HEAD_BIT = 512;


    public static AssetManager manager;

	@Override
	public void create () {
		batch = new SpriteBatch();
        manager = new AssetManager();
        manager.load("audio/music/mario_music.ogg", Music.class);
        manager.load("audio/sound/coin.wav", Sound.class);
        manager.load("audio/sound/bump.wav", Sound.class);
        manager.load("audio/sound/breakblock.wav", Sound.class);
        manager.load("audio/sound/powerup_spawn.wav", Sound.class);
        manager.load("audio/sound/powerup.wav", Sound.class);
        manager.load("audio/sound/powerdown.wav", Sound.class);
        manager.load("audio/sound/stomp.wav", Sound.class);
        manager.load("audio/sound/mariodie.wav", Sound.class);

        manager.finishLoading();

        setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
        super.render();
	}

    @Override
    public void dispose() {
        super.dispose();
        manager.dispose();
        batch.dispose();
    }
}
