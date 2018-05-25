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

    protected Vector2 mForwardSpeed;
    protected Vector2 mLateralSpeed;

    private final Body mBody;
    private float mDrift = 1;
    private final int mId;

    public BodyHolder(final Body mBody) {
        this.mBody = mBody;
        mId = -1;
    }

    public BodyHolder(final Vector2 position, final Vector2 size, final BodyDef.BodyType type, final World world, float density, final boolean sensor, final int id) {
        mBody = ShapeFactory.createRectangle(position, size, type, world, density, sensor);
        this.mId = id;
    }

    public void update(final float delta) {
        if (mDrift < 1){
            mForwardSpeed = getForwardVelocity();
            mLateralSpeed = getLateralVelocity();
            if (mLateralSpeed.len() < DRIFT_OFFSET && mId > 1) {
                killDrift();
            } else {
                handleDrift();
            }
        }
    }

    public void setDrift(float drift) {
        this.mDrift = drift;
    }

    public Body getBody() {
        return mBody;
    }

    private void handleDrift() {
        Vector2 forwardSpeed = getForwardVelocity();
        Vector2 lateralSpeed = getLateralVelocity();
        mBody.setLinearVelocity(forwardSpeed.x + lateralSpeed.x * mDrift, forwardSpeed.y + lateralSpeed.y * mDrift);
    }

    private Vector2 getForwardVelocity() {
        Vector2 currentNormal = mBody.getWorldVector(new Vector2(0, 1));
        float dotProduct = currentNormal.dot(mBody.getLinearVelocity());
        return multiply(dotProduct, currentNormal);
    }

    public void killDrift() {
        mBody.setLinearVelocity(mForwardSpeed);
    }

    private Vector2 getLateralVelocity() {
        Vector2 currentNormal = mBody.getWorldVector(new Vector2(1, 0));
        float dotProduct = currentNormal.dot(mBody.getLinearVelocity());
        return multiply(dotProduct, currentNormal);
    }

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

    private Vector2 getLocalVelocity() {
        return mBody.getLocalVector(mBody.getLinearVelocityFromLocalPoint(new Vector2(0, 0)));
    }

    private Vector2 multiply(float a, Vector2 v) {
        return new Vector2(a * v.x, a * v.y);
    }
}
