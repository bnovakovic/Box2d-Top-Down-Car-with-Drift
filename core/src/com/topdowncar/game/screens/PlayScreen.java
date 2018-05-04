package com.topdowncar.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.topdowncar.game.tools.MapLoader;
import com.topdowncar.game.tools.ShapeFactory;

import static com.topdowncar.game.Constants.NO_GRAVITY;
import static com.topdowncar.game.Constants.PPM;

public class PlayScreen implements Screen {
    private final SpriteBatch mBatch;
    private final World mWorld;
    private final Box2DDebugRenderer mBox2dDebugRenderer;
    private final OrthographicCamera mGameCam;
    private final Viewport mGamePort;
    private Body mPlayer;


    public PlayScreen() {
        mBatch = new SpriteBatch();
        mWorld = new World(NO_GRAVITY, true);
        mBox2dDebugRenderer = new Box2DDebugRenderer();
        mGameCam = new OrthographicCamera();
        mGameCam.zoom = 6f;
        mGamePort = new FitViewport(640 / PPM, 480 / PPM, mGameCam);
        final MapLoader mapLoader = new MapLoader(mWorld);
        mPlayer = mapLoader.getPlayer();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0,0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(delta);
        draw();
    }

    private void draw() {
        mBatch.setProjectionMatrix(mGameCam.combined);
        mBox2dDebugRenderer.render(mWorld, mGameCam.combined);
    }

    private void update(final float delta) {
        mWorld.step(delta, 6, 2);
        mGameCam.position.set(mPlayer.getPosition(), 0);
        mGameCam.update();
    }

    @Override
    public void resize(int width, int height) {
        mGamePort.update(width, height);
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
        mBox2dDebugRenderer.dispose();
        mWorld.dispose();
    }
}
