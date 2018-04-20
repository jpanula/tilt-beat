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
    private Texture soundButtonTexture;
    private Texture soundOnTexture;
    private Texture soundOffTexture;
    BitmapFont heading;
    BitmapFont basic;
    private Button calibration;
    private Button sound;
    private Button restore;
    private Button backButton;
    private Button soundButton;
    private boolean changeSound = false;
    private Vector3 touchPos;

    public Settings(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        background = GameMain.getBackgroundTexture();
        fontCamera = host.getFontCamera();
        heading = host.getSmallerHeadingFont();
        basic = host.getBasicFont();
        soundOnTexture = new Texture("soundOn.png");
        soundOffTexture = new Texture("soundOff.png");
        soundButtonTexture = soundOnTexture;
        sound = new Button(1.3f, 1.8f, 3.7f, 2f, GameMain.getButtonTexture());
        restore = new Button(11f, 1.8f, 3.7f, 2f, GameMain.getButtonTexture());
        calibration = new Button(6.2f, 1.8f, 3.7f, 2f, GameMain.getButtonTexture());
        backButton = new Button(0.2f, 8.3f, 1.5f, 1.5f, GameMain.getBackButtonTexture());

        calibration.setText(24, 106, "calibration", basic);
        restore.setText(50, 136, "default\nsettings", basic);
        destroySoundButton();
        touchPos = new Vector3();
    }
    @Override
    public void show() {

    }

    public void createSoundButton() {
        soundButton = new Button(2f, 4.5f, 2f, 2f, soundOnTexture);
    }

    public void destroySoundButton() {
        soundButton = new Button(11f, 18f, .1f, .1f, soundOnTexture);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        batch.setProjectionMatrix(camera.combined);
        //piirrellään tausta ja napit
        batch.draw(background, 0, 0, 16, 10);
        calibration.draw(batch);
        sound.draw(batch);
        restore.draw(batch);
        backButton.draw(batch);

        if(changeSound) {
            soundButton.draw(batch);
        } else { destroySoundButton(); }

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        calibration.drawText(batch);
        restore.drawText(batch);

        heading.draw(batch, "Settings", 250, 700);
        basic.draw(batch, "sound", 180, 250);

        batch.end();

        //nappien toiminnallisuus
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }
        if (backButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setScreen(new MainMenu(host));
        }
        if (restore.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            restoreSettings();
        }
        if (sound.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            createSoundButton();
            changeSound ^= true;
            touchPos.setZero();
        }
        if (soundButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setSoundOn(true);
            if (soundButton.getTexture()==soundOnTexture) {
                soundButton.setTexture(soundOffTexture);
            } else { soundButton.setTexture(soundOnTexture); }
            touchPos.setZero();
        }
        // Nollapisteen kalibrointi
        if (calibration.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.calibrateZeroPoint();
            touchPos.setZero();
        }

        //ottaa napin painalluksen vain kerran
        if (!Gdx.input.isTouched()) {touchPos.set(0, 0, 0);}
    }

    public void restoreSettings() {
        //oletusasetukset tänne
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
