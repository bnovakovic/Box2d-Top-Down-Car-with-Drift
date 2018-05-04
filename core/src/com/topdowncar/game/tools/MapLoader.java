package com.topdowncar.game.tools;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.topdowncar.game.Constants;

import sun.security.provider.SHA;

import static com.topdowncar.game.Constants.PLAYER_DENSITY;
import static com.topdowncar.game.Constants.PLAYER_SIZE;

/**
 * Created by Bojan on 04-May-18.
 * email: bojanche@gmail.com
 * web: www.podmornice.com
 */

public class MapLoader {
    private static final String MAP_WALLS = "wall";
    private static final String MAP_PLAYER = "player";
    private static final String MAP_FILE_NAME = "new_map.tmx";

    private World mWorld;
    private TiledMap mTiledMap;


    public MapLoader(final World world) {
        this.mWorld = world;
        mTiledMap = new TmxMapLoader().load(MAP_FILE_NAME);
        final Array<RectangleMapObject> walls = mTiledMap.getLayers().get(MAP_WALLS).getObjects().getByType(RectangleMapObject.class);

        for (RectangleMapObject object : walls) {
            Rectangle rectangle = object.getRectangle();
            ShapeFactory.createRectangle(
                    new Vector2(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight() / 2), // position
                    new Vector2(rectangle.getWidth() / 2, rectangle.getHeight() / 2), // size
                    BodyDef.BodyType.StaticBody, mWorld, 1); // type, world and density
        }

    }

    public Body getPlayer(){
        final Rectangle player = mTiledMap.getLayers().get(MAP_PLAYER).getObjects().getByType(RectangleMapObject.class).get(0).getRectangle();
        return ShapeFactory.createRectangle(
                new Vector2(player.x + player.getWidth() / 2, player.getY() + player.getHeight() / 2),
                PLAYER_SIZE, BodyDef.BodyType.DynamicBody, mWorld, PLAYER_DENSITY);
    }

}
