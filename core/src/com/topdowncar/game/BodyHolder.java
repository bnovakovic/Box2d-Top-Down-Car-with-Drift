package com.topdowncar.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.topdowncar.game.tools.ShapeFactory;

public abstract class BodyHolder {

    protected static final int DIRECTION_NONE = 0;
    protected static final int DIRECTION_FORWARD = 1;
    protected static final int DIRECTION_BACKWARD = 2;

    private static final float DRIFT_OFFSET = 1.0f;

    private Vector2 mForwardSpeed;
    private Vector2 mLateralSpeed;

    private final Body mBody;
    private float mDrift = 1;
    private final int mId;

    /**
     * Most base constructor used if we already have a body that we need to control by the logic
     * in this class
     * @param mBody body we have already created
     */
    public BodyHolder(final Body mBody) {
        this.mBody = mBody;
        mId = -1;
    }

    /**
     * Advanced constructor where we need to pass in all needed information in order to create body
     * @param position rectangle position
     * @param size rectangle size
     * @param type rectangle type
     * @param world {@link com.topdowncar.game.screens.PlayScreen#mWorld} used to control and add physics objects
     * @param density rectangle density
     * @param sensor is fixture a sensor
     * @param id unique ID
     */
    public BodyHolder(final Vector2 position, final Vector2 size, final BodyDef.BodyType type, final World world, float density, final boolean sensor, final int id) {
        mBody = ShapeFactory.createRectangle(position, size, type, world, density, sensor);
        this.mId = id;
    }

    /**
     * Main logic update method
     * @param delta delta time received from {@link com.topdowncar.game.screens.PlayScreen#render(float)}
     */
    public void update(final float delta) {
        if (mDrift < 1) {
            mForwardSpeed = getForwardVelocity();
            mLateralSpeed = getLateralVelocity();
            if (mLateralSpeed.len() < DRIFT_OFFSET && mId > 1) {
                killDrift();
            } else {
                handleDrift();
            }
        }
    }

    /**
     * Setting body drift
     * @param drift drift value (0 - no drift, 1 - total drift)
     */
    public void setDrift(final float drift) {
        this.mDrift = drift;
    }

    /**
     * Returning body assigned to this body holder
     * @return body object
     */
    public Body getBody() {
        return mBody;
    }

    /**
     * Handling drift
     */
    private void handleDrift() {
        final Vector2 forwardSpeed = getForwardVelocity();
        final Vector2 lateralSpeed = getLateralVelocity();
        mBody.setLinearVelocity(forwardSpeed.x + lateralSpeed.x * mDrift, forwardSpeed.y + lateralSpeed.y * mDrift);
    }

    /**
     * Get extracted forward velocity vector
     * @return extracted forward vector
     */
    private Vector2 getForwardVelocity() {
        final Vector2 currentNormal = mBody.getWorldVector(new Vector2(0, 1));
        final float dotProduct = currentNormal.dot(mBody.getLinearVelocity());
        return multiply(dotProduct, currentNormal);
    }

    /**
     * Kill whole sideways velocity, and only apply forward velocity (no drift for the body)
     */
    public void killDrift() {
        mBody.setLinearVelocity(mForwardSpeed);
    }

    /**
     * Get extracted sideways velocity vector
     * @return extracted sideways velocity vector
     */
    private Vector2 getLateralVelocity() {
        final Vector2 currentNormal = mBody.getWorldVector(new Vector2(1, 0));
        final float dotProduct = currentNormal.dot(mBody.getLinearVelocity());
        return multiply(dotProduct, currentNormal);
    }

    /**
     * Determining if our body is moving forward or backward
     * @return
     */
    public int direction() {
        final float tolerance = 0.2f;
        if (getLocalVelocity().y < -tolerance) {
            return DIRECTION_BACKWARD;
        } else if (getLocalVelocity().y > tolerance) {
            return DIRECTION_FORWARD;
        } else {
            return DIRECTION_NONE;
        }
    }

    /**
     * Getting local velocity of a body
     * @return local velocity vector
     */
    private Vector2 getLocalVelocity() {
        return mBody.getLocalVector(mBody.getLinearVelocityFromLocalPoint(new Vector2(0, 0)));
    }

    /**
     * Multiplying two vectors
     * @param a multiplier
     * @param v vector to multiply
     * @return multiplied vector
     */
    private Vector2 multiply(float a, Vector2 v) {
        return new Vector2(a * v.x, a * v.y);
    }
}
