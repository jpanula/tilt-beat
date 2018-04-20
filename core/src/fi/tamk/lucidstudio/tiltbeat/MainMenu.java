package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.Scanner;

/**
 * Created by Anna on 26/03/2018.
 */

public class MainMenu implements Screen{
    private GameMain host;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private BitmapFont heading;
    private BitmapFont basic;
    private Button playButton;
    private Button modsButton;
    private Button settingsButton;
    private Button highscoreButton;
    private Texture background;
    private Vector3 touchPos;


    public MainMenu(GameMain host) {
        this.host = host;
        host.setScreen(new LoadingScreen(host, this));
        batch = host.getBatch();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        background = host.getBackgroundTexture();

        playButton = new Button(2.8f, 4f, 4f, 1.7f, host.getButtonTexture());
        modsButton = new Button(8.5f, 4f, 4f, 1.7f, host.getButtonTexture());
        settingsButton = new Button(2.8f, 1f, 4f, 1.7f, host.getButtonTexture());
        highscoreButton = new Button(8.5f, 1f, 4f, 1.7f, host.getButtonTexture());

        heading = host.getHeadingFont();
        basic = host.getBasicFont();
        touchPos = new Vector3();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.7f, 0.2f, 0.7f, 0.7f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        batch.setProjectionMatrix(camera.combined);
        //piirrellään tausta ja napit
        batch.draw(background, 0, 0 , 16, 10);
        playButton.draw(batch);
        settingsButton.draw(batch);
        highscoreButton.draw(batch);
        modsButton.draw(batch);

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        heading.draw(batch, "TilT" , 370, 750);
        heading.draw(batch, "BeaT" , 550, 670);
        basic.draw(batch, "play" , 320, 410);
        basic.draw(batch, "settings" , 283, 170);
        basic.draw(batch, "highscore" , 735, 170);
        basic.draw(batch, "mods" , 775, 410);

        batch.end();

        //nappien kosketuksesta screeni vaihtuu
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }
        if (playButton.contains(touchPos.x, touchPos.y)  && !Gdx.input.isTouched()) {
            host.setScreen(new Difficulty(host));
        }
        if (settingsButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setScreen(new Settings(host));
        }
        if (highscoreButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setScreen(new Highscore(host));
        }
        if (modsButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setScreen(new Mods(host));
        }
        //ottaa napin painalluksen vain kerran
        if (!Gdx.input.isTouched()) {touchPos.set(0, 0, 0);}

    }

    @Override
    public void resize(int width, int height) {

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
        heading.dispose();
    }
}
