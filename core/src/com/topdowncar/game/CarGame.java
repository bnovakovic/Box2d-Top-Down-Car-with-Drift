package com.topdowncar.game;

import com.badlogic.gdx.Game;
import com.topdowncar.game.screens.PlayScreen;

public class CarGame extends Game {
    @Override
    public void create() {
        setScreen(new PlayScreen());
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
