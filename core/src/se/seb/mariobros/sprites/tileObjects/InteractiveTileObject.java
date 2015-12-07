package se.seb.mariobros.sprites.tileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import se.seb.mariobros.MarioBros;
import se.seb.mariobros.screens.PlayScreen;
import se.seb.mariobros.sprites.Mario;

/**
 * Created by Sebastian Börebäck on 2015-12-05.
 */
public abstract class InteractiveTileObject {



    protected final Fixture fixture;
    protected final MapObject object;
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Body body;
    protected PlayScreen screen;
    protected Rectangle bounds;



    public InteractiveTileObject(PlayScreen screen, MapObject object) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds=((RectangleMapObject) object).getRectangle();
        this.object = object;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / MarioBros.PPM, (bounds.getY() + bounds.getHeight() / 2) / MarioBros.PPM);

        body = world.createBody(bdef);

        shape.setAsBox(bounds.getWidth() / 2 / MarioBros.PPM, bounds.getHeight() / 2 / MarioBros.PPM);
        fdef.shape = shape;
        fixture = body.createFixture(fdef);


    }

    public abstract void onHeadHit(Mario mario);

    public void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }



    public TiledMapTileLayer.Cell getCell() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell((int)(body.getPosition().x*MarioBros.PPM /16),
                             (int)(body.getPosition().y*MarioBros.PPM /16));
    }

    public Filter getFilter() {
        return fixture.getFilterData();
    }
}
