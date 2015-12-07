package se.seb.mariobros.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import se.seb.mariobros.MarioBros;
import se.seb.mariobros.screens.PlayScreen;
import se.seb.mariobros.sprites.tileObjects.Brick;
import se.seb.mariobros.sprites.tileObjects.Coin;
import se.seb.mariobros.sprites.enemies.Goomba;

/**
 * Created by Sebastian Börebäck on 2015-12-05.
 */
public class B2WorldCreator {

    private final TiledMap map;
    private final World world;

    private Array<Goomba> goombas;

    public B2WorldCreator(PlayScreen screen) {
        this.world = screen.getWorld();
        this.map = screen.getMap();


        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //ground
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY()+rect.getHeight()/2) / MarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth()/2) / MarioBros.PPM,(rect.getHeight()/2) / MarioBros.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        //pipes
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY()+rect.getHeight()/2) / MarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth()/2) / MarioBros.PPM,(rect.getHeight()/2) / MarioBros.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioBros.OBJECT_BIT;
            body.createFixture(fdef);
        }

        //coins
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            new Coin(screen,object);
        }

        //bricks
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            new Brick(screen,object);
        }

        //create all goombas
        goombas = new Array<Goomba>();

        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(screen,rect.getX() / MarioBros.PPM, rect.getY() / MarioBros.PPM));
        }

    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }
}
