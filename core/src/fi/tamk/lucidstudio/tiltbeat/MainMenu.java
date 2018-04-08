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

public class MainMenu implements Screen {
    private GameMain host;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private BitmapFont heading;
    private BitmapFont basic;
    private Texture button;
    private Rectangle buttonPlay;
    private Rectangle buttonMods;
    private Rectangle buttonSettings;
    private Rectangle buttonHighscore;
    private Texture background;


    public MainMenu(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        button = GameMain.getButtonTexture();
        background = GameMain.getBackgroundTexture();

        buttonPlay = new Rectangle(2.8f, 4f, 4f, 1.7f);
        buttonMods = new Rectangle(8.5f, 4f, 4f, 1.7f);
        buttonSettings = new Rectangle(2.8f, 1f, 4f, 1.7f);
        buttonHighscore = new Rectangle(8.5f, 1f, 4f, 1.7f);

        heading = GameMain.getHeadingFont();
        basic = GameMain.getBasicFont();

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
        batch.draw(button, buttonPlay.x, buttonPlay.y, buttonPlay.width, buttonPlay.height);
        batch.draw(button, buttonSettings.x, buttonSettings.y, buttonSettings.width, buttonSettings.height);
        batch.draw(button, buttonHighscore.x, buttonHighscore.y, buttonHighscore.width, buttonHighscore.height);
        batch.draw(button, buttonMods.x, buttonMods.y, buttonMods.width, buttonMods.height);

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
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                if (buttonPlay.contains(touchPos.x, touchPos.y)) {
                    host.setScreen(new Difficulty(host));
                }
                if (buttonSettings.contains(touchPos.x, touchPos.y) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    host.setScreen(new Settings(host));
                }
                if (buttonHighscore.contains(touchPos.x, touchPos.y) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    host.setScreen(new Highscore(host));
                }
                if (buttonMods.contains(touchPos.x, touchPos.y) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    host.setScreen(new Mods(host));
                }
        }

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
