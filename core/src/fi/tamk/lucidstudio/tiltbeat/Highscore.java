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

/**
 * Created by Anna on 26/03/2018.
 */

public class Highscore implements Screen {
    private GameMain host;
    private SpriteBatch batch;
    private BitmapFont heading;
    private BitmapFont basic;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private Texture background;
    private Texture buttonTexture;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button backButton;
    private Texture backButtonTexture;
    private Texture textBoxTexture;
    private Vector3 touchPos;

    public  Highscore(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        heading = GameMain.getSmallerHeadingFont();
        basic = GameMain.getBasicFont();
        background = GameMain.getBackgroundTexture();
        //buttonTexture = GameMain.getButtonTexture();
        backButtonTexture = GameMain.getBackButtonTexture();
        textBoxTexture = GameMain.getTextBoxTexture();
        touchPos = new Vector3();

        /*
        button1 = new Button(1.3f, 1.8f, 3.7f, 2f, GameMain.getButtonTexture());
        button2 = new Button(6.15f, 1.8f, 3.7f, 2f, GameMain.getButtonTexture());
        button3 = new Button(11f, 1.8f, 3.7f, 2f, GameMain.getButtonTexture());
        */
        backButton = new Button(0.2f, 8.3f, 1.5f, 1.5f, GameMain.getBackButtonTexture());

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
        backButton.draw(batch);
        batch.draw(textBoxTexture, 1.5f, 1, 10, 5);

        //button1.draw(batch);
        /*
        batch.draw(buttonTexture, button1.x, button1.y, button1.width, button1.height);
        batch.draw(buttonTexture, button2.x, button2.y, button2.width, button2.height);
        batch.draw(buttonTexture, button3.x, button3.y, button3.width, button3.height);
        */

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        heading.draw(batch, "Highscore" , 300, 650);

        basic.draw(batch, "1:  anna              817p" , 200, 400);
        basic.draw(batch, "2:  miko              647p" , 200, 340);
        basic.draw(batch, "3:  leia               420p" , 200, 280);
        basic.draw(batch, "4:  jaakko             17p" , 200, 220);

        batch.end();

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }
        if (backButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setScreen(new MainMenu(host));
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

    }
}
