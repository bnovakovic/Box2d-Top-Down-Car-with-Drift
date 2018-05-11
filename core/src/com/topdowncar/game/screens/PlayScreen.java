package com.topdowncar.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.topdowncar.game.entities.Car;
import com.topdowncar.game.tools.MapLoader;

import static com.topdowncar.game.entities.Car.DRIVE_DIRECTION_BACKWARD;
import static com.topdowncar.game.entities.Car.DRIVE_DIRECTION_FORWARD;
import static com.topdowncar.game.entities.Car.DRIVE_DIRECTION_NONE;
import static com.topdowncar.game.entities.Car.TURN_DIRECTION_LEFT;
import static com.topdowncar.game.entities.Car.TURN_DIRECTION_NONE;
import static com.topdowncar.game.entities.Car.TURN_DIRECTION_RIGHT;
import static com.topdowncar.game.Constants.DEFAULT_ZOOM;
import static com.topdowncar.game.Constants.GRAVITY;
import static com.topdowncar.game.Constants.PPM;

public class PlayScreen implements Screen {

    private final SpriteBatch mBatch;
    private final World mWorld;
    private final Box2DDebugRenderer mB2dr;
    private final OrthographicCamera mCamera;
    private final Viewport mViewport;
    private final Car mPlayer;
    private final MapLoader mMapLoader;

    public PlayScreen() {
        mBatch = new SpriteBatch();
        mWorld = new World(GRAVITY, true);
        mB2dr = new Box2DDebugRenderer();
        mCamera = new OrthographicCamera();
        mCamera.zoom = DEFAULT_ZOOM;
        mViewport = new FitViewport(640 / PPM, 480 / PPM, mCamera);
        mMapLoader = new MapLoader(mWorld);
        mPlayer = new Car(35.0f, 0.7f, 50, mMapLoader, Car.DRIVE_2WD, mWorld);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        update(delta);
        draw();

    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            mPlayer.setDriveDirection(DRIVE_DIRECTION_FORWARD);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            mPlayer.setDriveDirection(DRIVE_DIRECTION_BACKWARD);
        } else {
            mPlayer.setDriveDirection(DRIVE_DIRECTION_NONE);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mPlayer.setTurnDirection(TURN_DIRECTION_LEFT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mPlayer.setTurnDirection(TURN_DIRECTION_RIGHT);
        } else {
            mPlayer.setTurnDirection(TURN_DIRECTION_NONE);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            mCamera.zoom -= 0.4f;
        } else if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            mCamera.zoom += 0.4f;
        }
    }

    private void draw() {
        mBatch.setProjectionMatrix(mCamera.combined);
        mB2dr.render(mWorld, mCamera.combined);
    }

    private void update(final float delta) {
        mPlayer.update(delta);
        mCamera.position.set(mPlayer.getBody().getPosition(), 0);
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
