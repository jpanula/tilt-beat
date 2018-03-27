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

/**
 * Created by Anna on 26/03/2018.
 */

public class Settings implements Screen {
    GameMain host;
    SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private Texture background;
    private Texture button;
    BitmapFont heading;
    BitmapFont basic;
    private Rectangle button1;
    private Rectangle button2;
    private Rectangle button3;

    public Settings(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        background = GameMain.getBackgroundTexture();
        button = GameMain.getButtonTexture();
        fontCamera = host.getFontCamera();
        heading = GameMain.headingFont;
        basic = GameMain.basicFont;
        button1 = new Rectangle(1.3f, 1.8f, 3.7f, 2f);
        button2 = new Rectangle(6.15f, 1.8f, 3.7f, 2f);
        button3 = new Rectangle(11f, 1.8f, 3.7f, 2f);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        batch.setProjectionMatrix(camera.combined);
        //piirrellään tausta ja napit
        batch.draw(background, 0, 0 , 16, 10);
        batch.draw(button, button1.x, button1.y, button1.width, button1.height);
        batch.draw(button, button2.x, button2.y, button2.width, button2.height);
        batch.draw(button, button3.x, button3.y, button3.width, button3.height);

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        heading.draw(batch, "Settings" , 370, 650);
        basic.draw(batch, "click anywhere to exit to main menu" , 280, 400);

        batch.end();

        if (Gdx.input.isTouched()) {
            host.setScreen(new MainMenu(host));
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

    }
}
