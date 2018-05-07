package com.topdowncar.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.topdowncar.game.tools.MapLoader;

import static com.topdowncar.game.Constants.DEFAULT_ZOOM;
import static com.topdowncar.game.Constants.GRAVITY;
import static com.topdowncar.game.Constants.PPM;

public class PlayScreen implements Screen {

    private static final int DRIVE_DIRECTION_NONE = 0;
    private static final int DRIVE_DIRECTION_FORWARD = 1;
    private static final int DRIVE_DIRECTION_BACKWARD = 2;

    private static final int TURN_DIRECTION_NONE = 0;
    private static final int TURN_DIRECTION_LEFT = 1;
    private static final int TURN_DIRECTION_RIGHT = 2;

    private static final float DRIFT = 0.99f;
    private static final float TURN_SPEED = 2.0f;
    private static final float DRIVE_SPEED = 120.0f;
    private static final float MAX_SPEED = 35.0f;



    private final SpriteBatch mBatch;
    private final World mWorld;
    private final Box2DDebugRenderer mB2dr;
    private final OrthographicCamera mCamera;
    private final Viewport mViewport;
    private final Body mPlayer;
    private final MapLoader mMapLoader;

    private int mDriveDirection = DRIVE_DIRECTION_NONE;
    private int mTurnDirection = TURN_DIRECTION_NONE;

    public PlayScreen() {
        mBatch = new SpriteBatch();
        mWorld = new World(GRAVITY, true);
        mB2dr = new Box2DDebugRenderer();
        mCamera = new OrthographicCamera();
        mCamera.zoom = DEFAULT_ZOOM;
        mViewport = new FitViewport(640 / PPM, 480 / PPM, mCamera);
        mMapLoader = new MapLoader(mWorld);
        mPlayer = mMapLoader.getPlayer();

        mPlayer.setLinearDamping(0.5f);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        processInput();
        update(delta);
        handleDrift();
        draw();

    }

    private void handleDrift() {
        Vector2 forwardSpeed = getForwardVelocity();
        Vector2 lateralSpeed = getLateralVelocity();
        mPlayer.setLinearVelocity(forwardSpeed.x + lateralSpeed.x * DRIFT, forwardSpeed.y + lateralSpeed.y * DRIFT);
    }

    private void processInput() {
        Vector2 baseVector = new Vector2(0,0);

        if (mTurnDirection == TURN_DIRECTION_RIGHT){
            mPlayer.setAngularVelocity(-TURN_SPEED);
        } else if (mTurnDirection == TURN_DIRECTION_LEFT){
            mPlayer.setAngularVelocity(TURN_SPEED);
        } else if (mTurnDirection == TURN_DIRECTION_NONE && mPlayer.getAngularVelocity() != 0){
            mPlayer.setAngularVelocity(0.0f);
        }

        if (mDriveDirection == DRIVE_DIRECTION_FORWARD){
            baseVector.set(0, DRIVE_SPEED);
        } else if (mDriveDirection == DRIVE_DIRECTION_BACKWARD) {
            baseVector.set(0, -DRIVE_SPEED);
        }

        if (!baseVector.isZero() && mPlayer.getLinearVelocity().len() < MAX_SPEED){
            mPlayer.applyForceToCenter(mPlayer.getWorldVector(baseVector), true);
        }
    }

    private Vector2 getForwardVelocity(){
        Vector2 currentNormal = mPlayer.getWorldVector(new Vector2(0, 1));
        float dotProduct = currentNormal.dot(mPlayer.getLinearVelocity());
        return multiply(dotProduct, currentNormal);
    }

    private Vector2 getLateralVelocity(){
        Vector2 currentNormal = mPlayer.getWorldVector(new Vector2(1, 0));
        float dotProduct = currentNormal.dot(mPlayer.getLinearVelocity());
        return multiply(dotProduct, currentNormal);
    }

    private Vector2 multiply(float a, Vector2 v){
        return new Vector2(a * v.x, a * v.y);
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            mDriveDirection = DRIVE_DIRECTION_FORWARD;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            mDriveDirection = DRIVE_DIRECTION_BACKWARD;
        } else {
            mDriveDirection = DRIVE_DIRECTION_NONE;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            mTurnDirection = TURN_DIRECTION_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            mTurnDirection = TURN_DIRECTION_RIGHT;
        } else {
            mTurnDirection = TURN_DIRECTION_NONE;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Q)){
            mCamera.zoom -= 0.4f;
        } else if (Gdx.input.isKeyPressed(Input.Keys.E)){
            mCamera.zoom += 0.4f;
        }
    }

    private void draw() {
        mBatch.setProjectionMatrix(mCamera.combined);
        mB2dr.render(mWorld, mCamera.combined);
    }

    private void update(final float delta) {
        mCamera.position.set(mPlayer.getPosition(), 0);
        mCamera.update();

        mWorld.step(delta, 6, 2);
    }

    @Override
    public void resize(int width, int height) {
        mViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        mBatch.dispose();
        mWorld.dispose();
        mB2dr.dispose();
        mMapLoader.dispose();
    }
}
