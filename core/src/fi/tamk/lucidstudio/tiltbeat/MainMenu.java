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
        button = new Texture(Gdx.files.internal("nappi1.png"));
        background = new Texture(Gdx.files.internal("Galaxy blue.png"));

        buttonPlay = new Rectangle(2f, 1.8f, 3.7f, 2f);
        buttonSettings = new Rectangle(6.5f, 1.8f, 3.7f, 2f);
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
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        //piirrellään tausta ja napit
        batch.draw(background, 0, 0 , 16, 10);
        batch.draw(button, buttonPlay.x, buttonPlay.y, buttonPlay.width, buttonPlay.height);
        batch.draw(button, buttonSettings.x, buttonSettings.y, buttonSettings.width, buttonSettings.height);
        batch.draw(button, buttonHighscore.x, buttonHighscore.y, buttonHighscore.width, buttonHighscore.height);
        //piirrellään fontit
        heading.draw(batch, "TilT" , 4, 8);
        heading.draw(batch, "BeaT" , 4.2f, 7);
        basic.draw(batch, "play" , buttonPlay.x + 0.3f, buttonPlay.y + 1f);
        basic.draw(batch, "settings" , buttonSettings.x + 50, buttonSettings.y + 100);
        basic.draw(batch, "highscore" , buttonHighscore.x + 0.1f, buttonHighscore.y + 0.1f);

        batch.end();

        batch.setProjectionMatrix(camera.combined);
        //nappien kosketuksesta screeni vaihtuu
        if (Gdx.input.isTouched()) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                System.out.println(touchPos.x + " " + touchPos.y);
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
