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
    private Rectangle buttonSettings;
    private Rectangle buttonHighscore;
    private Texture background;


    public MainMenu(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        button = new Texture(Gdx.files.internal("nappi1.png"));
        background = GameMain.getBackgroundTexture();

        buttonPlay = new Rectangle(1.3f, 1.8f, 3.7f, 2f);
        buttonSettings = new Rectangle(6.15f, 1.8f, 3.7f, 2f);
        buttonHighscore = new Rectangle(11f, 1.8f, 3.7f, 2f);

        heading = GameMain.headingFont;
        basic = GameMain.basicFont;

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

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        heading.draw(batch, "TilT" , 370, 650);
        heading.draw(batch, "BeaT" , 550f, 570);
        basic.draw(batch, "play" , 197, 250);
        basic.draw(batch, "settings" , 547, 250);
        basic.draw(batch, "highscore" , 924, 250);

        batch.end();

        //nappien kosketuksesta screeni vaihtuu
        if (Gdx.input.isTouched()) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                if (buttonPlay.contains(touchPos.x, touchPos.y)) {
                    host.setScreen(new GameScreen(host));
                }
                if (buttonSettings.contains(touchPos.x, touchPos.y) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    host.setScreen(new Settings(host));
                }
                if (buttonHighscore.contains(touchPos.x, touchPos.y) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    host.setScreen(new Highscore(host));
                }
        }

        if ( Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            host.setScreen(new Settings(host));
        }
        if ( Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            host.setScreen(new Highscore(host));
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
