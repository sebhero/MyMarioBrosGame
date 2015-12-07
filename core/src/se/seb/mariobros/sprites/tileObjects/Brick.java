package se.seb.mariobros.sprites.tileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import se.seb.mariobros.MarioBros;
import se.seb.mariobros.scenes.Hud;
import se.seb.mariobros.screens.PlayScreen;
import se.seb.mariobros.sprites.Mario;

/**
 * Created by Sebastian Börebäck on 2015-12-05.
 */
public class Brick extends InteractiveTileObject {

    public Brick(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);

    }


    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log("Brick","Brick hit head");
        if (mario.isBig()) {
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            MarioBros.manager.get("audio/sound/breakblock.wav",Sound.class).play();
            Hud.addScore(200);
        }
        else
        {
            MarioBros.manager.get("audio/sound/bump.wav",Sound.class).play();
        }
    }
}
