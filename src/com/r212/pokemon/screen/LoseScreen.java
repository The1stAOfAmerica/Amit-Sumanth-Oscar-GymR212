package com.r212.pokemon.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.r212.pokemon.PokemonGame;
import com.r212.pokemon.screen.transition.FadeInTransition;
import com.r212.pokemon.screen.transition.FadeOutTransition;
import com.r212.pokemon.util.Action;

public class LoseScreen extends AbstractScreen {

    private boolean lost;

    public LoseScreen(PokemonGame game, boolean l) {
        super(game);
        lost = l;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void render(float delta) {
        BitmapFont font1 = new BitmapFont(Gdx.files.internal("res/font/u02KYIgXq4iAjKR8gUI2yuqK.ttf.fnt")); //width/15-5
//        BitmapFont font1 = new BitmapFont(Gdx.files.internal("res/font/5nfqMh1X9CNnhXyL6dnGuWIY.ttf.fnt"));
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        getApp().batch.begin();
        //getApp().font.draw(getApp().batch, "  Amit, Oscar & Sumanth", Gdx.graphics.getWidth()/5, Gdx.graphics.getHeight() * .8f);
        //getApp().font.draw(getApp().batch, "Present", 50+Gdx.graphics.getWidth()/3-10, Gdx.graphics.getHeight() * .68f);
        if(lost)
            font1.draw(getApp().batch, "GAME OVER", Gdx.graphics.getWidth() /15, Gdx.graphics.getHeight() * .5f);
        else
            font1.draw(getApp().batch, "Winner winner Chicken dinner!", Gdx.graphics.getWidth() /1-10, Gdx.graphics.getHeight() * .5f);
        //getApp().font.draw(getApp().batch, "  Press enter to start", Gdx.graphics.getWidth()/5, Gdx.graphics.getHeight() * .25f);
        getApp().batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void resume() {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {

    }

    @Override
    public void update(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            getApp().startTransition(
                    this,
                    new InfoScreen(getApp()),
                    new FadeOutTransition(0.5f, Color.BLACK, getApp().getTweenManager(), getApp().getAssetManager()),
                    new FadeInTransition(0.5f, Color.BLACK, getApp().getTweenManager(), getApp().getAssetManager()),
                    new Action(){
                        @Override
                        public void action() {
                        }
                    });
        }
    }
}
