package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Anna on 27/03/2018.
 */

public class Mods implements Screen {
    private GameMain host;
    private SpriteBatch batch;
    private BitmapFont heading;
    private BitmapFont basic;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private Texture background;
    private Texture buttonTexture;
    private Rectangle button6;
    private Rectangle button8;
    private Rectangle button10;
    private Rectangle backButton;
    private Texture backButtonTexture;
    private int sides;

    public  Mods(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        heading = GameMain.getSmallerHeadingFont();
        basic = GameMain.getBasicFont();
        background = GameMain.getBackgroundTexture();
        buttonTexture = GameMain.getButtonTexture();
        backButtonTexture = GameMain.getBackButtonTexture();

        sides = 10;

        button6 = new Rectangle(1.3f, 4.5f, 2.5f, 2.5f);
        button8 = new Rectangle(6.15f, 4.5f, 2.5f, 2.5f);
        button10 = new Rectangle(11f, 4.5f, 2.5f, 2.5f);
        backButton = new Rectangle(0.2f, 8.8f, 1f, 1f);
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
        batch.draw(backButtonTexture, backButton.x, backButton.y, backButton.width, backButton.height);
        batch.draw(buttonTexture, button6.x, button6.y, button6.width, button6.height);
        batch.draw(buttonTexture, button8.x, button8.y, button8.width, button8.height);
        batch.draw(buttonTexture, button10.x, button10.y, button10.width, button10.height);

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        heading.draw(batch, "Modifications" , 250, 680);

        basic.draw(batch, "6" , 180, 500);
        basic.draw(batch, "8" , 550, 500);
        basic.draw(batch, "10" , 950, 500);
        heading.draw(batch, sides + " (sivua kulmiossa)" , 200, 200);

        batch.end();

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (backButton.contains(touchPos.x, touchPos.y)) {
                host.setScreen(new MainMenu(host));
            }
            if (button6.contains(touchPos.x, touchPos.y)) {
                GameMain.setPlayerSides(6);
                sides = 6;
            }
            if (button8.contains(touchPos.x, touchPos.y)) {
                GameMain.setPlayerSides(8);
                sides = 8;
            }
            if (button10.contains(touchPos.x, touchPos.y)) {
                GameMain.setPlayerSides(10);
                sides = 10;
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

    }
}
