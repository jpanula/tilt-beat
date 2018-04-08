package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Game;
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
 * Created by Anna on 26/03/2018.
 */

public class Settings implements Screen {
    GameMain host;
    SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private Texture background;
    private Texture buttonTexture;
    private Texture backButtonTexture;
    BitmapFont heading;
    BitmapFont basic;
    private Rectangle mode;
    private Rectangle calibration;
    private Rectangle sound;
    private Rectangle backButton;

    public Settings(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        background = GameMain.getBackgroundTexture();
        buttonTexture = GameMain.getButtonTexture();
        backButtonTexture = GameMain.getBackButtonTexture();
        fontCamera = host.getFontCamera();
        heading = GameMain.getSmallerHeadingFont();
        basic = GameMain.getBasicFont();
        mode = new Rectangle(1.3f, 1.8f, 3.7f, 2f);
        calibration = new Rectangle(6.15f, 1.8f, 3.7f, 2f);
        sound = new Rectangle(11f, 1.8f, 3.7f, 2f);
        backButton = new Rectangle(0.2f, 8.3f, 1.5f, 1.5f);
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
        batch.draw(buttonTexture, mode.x, mode.y, mode.width, mode.height);
        batch.draw(buttonTexture, calibration.x, calibration.y, calibration.width, calibration.height);
        batch.draw(buttonTexture, sound.x, sound.y, sound.width, sound.height);
        batch.draw(backButtonTexture, backButton.x, backButton.y, backButton.width, backButton.height);

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        heading.draw(batch, "Settings" , 250, 700);

        basic.draw(batch, "mode" , 195, 250);
        basic.draw(batch, "calibration" , 521, 250);
        basic.draw(batch, "sound" , 960, 250);

        batch.end();

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (backButton.contains(touchPos.x, touchPos.y)) {
                host.setScreen(new MainMenu(host));
            }
            // Nollapisteen kalibrointi
            if (calibration.contains(touchPos.x, touchPos.y)) {
                GameMain.calibrateZeroPoint();
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
