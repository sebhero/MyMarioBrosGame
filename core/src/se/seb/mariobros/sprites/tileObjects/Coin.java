package se.seb.mariobros.sprites.tileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import se.seb.mariobros.MarioBros;
import se.seb.mariobros.scenes.Hud;
import se.seb.mariobros.screens.PlayScreen;
import se.seb.mariobros.sprites.Mario;
import se.seb.mariobros.sprites.items.ItemDef;
import se.seb.mariobros.sprites.items.Mushroom;

/**
 * Created by Sebastian Börebäck on 2015-12-05.
 */
public class Coin extends InteractiveTileObject {

    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;//27+1

    public Coin(PlayScreen screen, MapObject bounds) {
        super(screen, bounds);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);


    }

    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log("Coin","Coin hit head");
        if (getCell().getTile().getId() == BLANK_COIN) {
            MarioBros.manager.get("audio/sound/bump.wav",Sound.class).play();
        }
        else {
            if (object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM),
                        Mushroom.class));
                MarioBros.manager.get("audio/sound/powerup_spawn.wav", Sound.class).play();
            }
            else {
                MarioBros.manager.get("audio/sound/coin.wav", Sound.class).play();
            }


        }
        getCell().setTile(tileSet.getTile(BLANK_COIN));
        Hud.addScore(100);
    }
}
