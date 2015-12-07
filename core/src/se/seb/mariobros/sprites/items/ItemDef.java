package se.seb.mariobros.sprites.items;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Sebastian Börebäck on 2015-12-07.
 */
public class ItemDef {
    public Vector2 position;
    public Class<?> type;

    public ItemDef(Vector2 position, Class<?> type) {
        this.position = position;
        this.type = type;
    }
}
