package com.topdowncar.game.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import static com.topdowncar.game.Constants.PPM;

public class ShapeFactory {
    /**
     * Main constructor set to private to disable object creation by the user
     */
    private ShapeFactory() {
    }

    /**
     * Create basic physics rectangle
     * @param position recangle position
     * @param size rectangle size
     * @param type body type (static, dynamic or kinematic)
     * @param world {@link com.topdowncar.game.screens.PlayScreen#mWorld} used to control and add physics objects
     * @param density body density
     * @param sensor is body sensor or not
     * @return fully created body with parameters provided
     */
    public static Body createRectangle(final Vector2 position, final Vector2 size, final BodyDef.BodyType type, final World world, float density, final boolean sensor) {

        // define body
        final BodyDef bdef = new BodyDef();
        bdef.position.set(position.x / PPM, position.y / PPM);
        bdef.type = type;
        final Body body = world.createBody(bdef);

        // define fixture
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x / PPM, size.y / PPM);
        final FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = density;
        fdef.isSensor = sensor;
        body.createFixture(fdef);
        shape.dispose();

        return body;
    }

}
