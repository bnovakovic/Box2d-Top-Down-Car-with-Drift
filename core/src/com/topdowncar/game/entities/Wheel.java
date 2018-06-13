package com.topdowncar.game.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.topdowncar.game.BodyHolder;

public class Wheel extends BodyHolder {
    public static final int UPPER_LEFT = 0;
    public static final int UPPER_RIGHT = 1;
    public static final int DOWN_LEFT = 2;
    public static final int DOWN_RIGHT = 3;

    private static final float WHEEL_DENSITY = 0.4f;

    private static final float DEGTORAD = 0.0174532925199432957f;

    private final boolean mPowered;
    private final Car mCar;

    /**
     * Base constructor wor Wheel
     * @param position wheel position
     * @param size wheel size
     * @param world {@link com.topdowncar.game.screens.PlayScreen#mWorld} used to control and add physics objects
     * @param id wheel unique IS
     * @param car {@link Car} class used to set correct wheel angle depending of car rotation
     * @param powered is wheel powered or not
     */
    public Wheel(final Vector2 position, final Vector2 size, final World world, final int id, final Car car, final boolean powered) {
        super(position, size, BodyDef.BodyType.DynamicBody, world, WHEEL_DENSITY, true, id);
        this.mCar = car;
        this.mPowered = powered;
    }

    /**
     * Set wheel angle
     * @param angle angle to which to rotate the wheel
     */
    public void setAngle(final float angle) {
        getBody().setTransform(getBody().getPosition(), mCar.getBody().getAngle() + angle * DEGTORAD);
    }

    /**
     * Returning boolean value if wheel is powered or not
     * @return powered wheel or not
     */
    public boolean isPowered() {
        return mPowered;
    }
}
