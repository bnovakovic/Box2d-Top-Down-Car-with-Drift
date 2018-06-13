package com.topdowncar.game.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.topdowncar.game.BodyHolder;
import com.topdowncar.game.screens.PlayScreen;
import com.topdowncar.game.tools.MapLoader;

import static com.topdowncar.game.Constants.PPM;


public class Car extends BodyHolder {
    public static final int DRIVE_2WD = 0;
    public static final int DRIVE_4WD = 1;

    public static final int DRIVE_DIRECTION_NONE = 0;
    public static final int DRIVE_DIRECTION_FORWARD = 1;
    public static final int DRIVE_DIRECTION_BACKWARD = 2;

    public static final int TURN_DIRECTION_NONE = 0;
    public static final int TURN_DIRECTION_LEFT = 1;
    public static final int TURN_DIRECTION_RIGHT = 2;

    private static final Vector2 WHEEL_SIZE = new Vector2(16, 32);
    private static final float LINEAR_DAMPING = 0.5f;
    private static final float RESTITUTION = 0.2f;

    private static final float MAX_WHEEL_ANGLE = 20.0f;
    private static final float WHEEL_TURN_INCREMENT = 1.0f;

    private static final float WHEEL_OFFSET_X = 64;
    private static final float WHEEL_OFFSET_Y = 80;
    private static final int WHEEL_NUMBER = 4;

    private static final float BREAK_POWER = 1.3f;
    private static final float REVERSE_POWER = 0.5f;

    private int mDriveDirection = DRIVE_DIRECTION_NONE;
    private int mTurnDirection = TURN_DIRECTION_NONE;

    private float mCurrentWheelAngle = 0;
    private final Array<Wheel> mAllWheels = new Array<Wheel>();
    private final Array<Wheel> mRevolvingWheels = new Array<Wheel>();
    private float mDrift;
    private float mCurrentMaxSpeed;
    private final float mRegularMaxSpeed;
    private float mAcceleration;

    /**
     * Base constructor for Car object
     * @param maxSpeed Maximum car speed
     * @param drift car drift value (0 - no drift, 1 absolute drift)
     * @param acceleration car acceleration amount
     * @param mapLoader {@link MapLoader} used to load get car position from the map
     * @param wheelDrive does this car have 4 wheel drive or 2 wheel drive
     * @param world {@link com.topdowncar.game.screens.PlayScreen#mWorld} used to control and add physics objects
     */
    public Car(final float maxSpeed, final float drift, final float acceleration, final MapLoader mapLoader, final int wheelDrive, final World world) {
        super(mapLoader.getPlayer());
        this.mRegularMaxSpeed = maxSpeed;
        this.mDrift = drift;
        this.mAcceleration = acceleration;
        getBody().setLinearDamping(LINEAR_DAMPING);
        getBody().getFixtureList().get(0).setRestitution(RESTITUTION);
        createWheels(world, wheelDrive);
    }

    /**
     * Method used to create wheel
     * @param world {@link com.topdowncar.game.screens.PlayScreen#mWorld} used to control and add physics objects
     * @param wheelDrive does this car have 4 wheel drive or 2 wheel drive
     */
    private void createWheels(final World world, final int wheelDrive) {
        for (int i = 0; i < WHEEL_NUMBER; i++) {
            float xOffset = 0;
            float yOffset = 0;

            switch (i) {
                case Wheel.UPPER_LEFT:
                    xOffset = -WHEEL_OFFSET_X;
                    yOffset = WHEEL_OFFSET_Y;
                    break;
                case Wheel.UPPER_RIGHT:
                    xOffset = WHEEL_OFFSET_X;
                    yOffset = WHEEL_OFFSET_Y;
                    break;
                case Wheel.DOWN_LEFT:
                    xOffset = -WHEEL_OFFSET_X;
                    yOffset = -WHEEL_OFFSET_Y;
                    break;
                case Wheel.DOWN_RIGHT:
                    xOffset = WHEEL_OFFSET_X;
                    yOffset = -WHEEL_OFFSET_Y;
                    break;
                default:
                    throw new IllegalArgumentException("Wheel number not supported. Create logic for positioning wheel with number " + i);
            }
            final boolean powered = wheelDrive == DRIVE_4WD || (wheelDrive == DRIVE_2WD && i < 2);

            final Wheel wheel = new Wheel(
                    new Vector2(getBody().getPosition().x * PPM + xOffset, getBody().getPosition().y * PPM + yOffset),
                    WHEEL_SIZE,
                    world,
                    i,
                    this,
                    powered);

            if (i < 2) {
                final RevoluteJointDef jointDef = new RevoluteJointDef();
                jointDef.initialize(getBody(), wheel.getBody(), wheel.getBody().getWorldCenter());
                jointDef.enableMotor = false;
                world.createJoint(jointDef);
            } else {
                final PrismaticJointDef jointDef = new PrismaticJointDef();
                jointDef.initialize(getBody(), wheel.getBody(), wheel.getBody().getWorldCenter(), new Vector2(1, 0));
                jointDef.enableLimit = true;
                jointDef.lowerTranslation = jointDef.upperTranslation = 0;
                world.createJoint(jointDef);
            }

            mAllWheels.add(wheel);
            if (i < 2) {
                mRevolvingWheels.add(wheel);
            }
            wheel.setDrift(mDrift);
        }

    }

    /**
     * Used to process input received from GDX handled in {@link PlayScreen#handleInput()}
     */
    private void processInput() {
        final Vector2 baseVector = new Vector2(0, 0);

        if (mTurnDirection == TURN_DIRECTION_LEFT) {
            if (mCurrentWheelAngle < 0) {
                mCurrentWheelAngle = 0;
            }
            mCurrentWheelAngle = Math.min(mCurrentWheelAngle += WHEEL_TURN_INCREMENT, MAX_WHEEL_ANGLE);
        } else if (mTurnDirection == TURN_DIRECTION_RIGHT) {
            if (mCurrentWheelAngle > 0) {
                mCurrentWheelAngle = 0;
            }
            mCurrentWheelAngle = Math.max(mCurrentWheelAngle -= WHEEL_TURN_INCREMENT, -MAX_WHEEL_ANGLE);
        } else {
            mCurrentWheelAngle = 0;
        }

        for (final Wheel wheel : new Array.ArrayIterator<Wheel>(mRevolvingWheels)) {
            wheel.setAngle(mCurrentWheelAngle);
        }

        if (mDriveDirection == DRIVE_DIRECTION_FORWARD) {
            baseVector.set(0, mAcceleration);
        } else if (mDriveDirection == DRIVE_DIRECTION_BACKWARD) {
            if (direction() == DIRECTION_BACKWARD) {
                baseVector.set(0, -mAcceleration * REVERSE_POWER);
            } else if (direction() == DIRECTION_FORWARD) {
                baseVector.set(0, -mAcceleration * BREAK_POWER);
            } else {
                baseVector.set(0, -mAcceleration);
            }
        }
        // we currently set mCurrentMaxSpeed to regular speed, but we can use this to increase max
        // speed if user has turbo, or something like that. So we can apply this logic:
        // if (turboActive) {
        //    mCurrentMaxSpeed = mRegularMaxSpeed * 1.5f;
        //}
        mCurrentMaxSpeed = mRegularMaxSpeed;

        if (getBody().getLinearVelocity().len() < mCurrentMaxSpeed) {
            for (final Wheel wheel : new Array.ArrayIterator<Wheel>(mAllWheels)) {
                if (wheel.isPowered()) {
                    wheel.getBody().applyForceToCenter(wheel.getBody().getWorldVector(baseVector), true);
                }
            }
        }
    }

    /**
     * Setting drive direction either to forward or backward
     * @param driveDirection drive direction to set
     */
    public void setDriveDirection(final int driveDirection) {
        this.mDriveDirection = driveDirection;
    }

    /**
     * Setting turn direction either to left or right
     * @param turnDirection turn direction left or right
     */
    public void setTurnDirection(final int turnDirection) {
        this.mTurnDirection = turnDirection;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        processInput();
        for (final Wheel wheel : new Array.ArrayIterator<Wheel>(mAllWheels)) {
            wheel.update(delta);
        }

    }
}
