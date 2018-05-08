package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import java.util.Locale;

/**
 * This class is the settings screen.
 */

public class Settings implements Screen {
    GameMain host;
    SpriteBatch batch;
    private Preferences prefs;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private Texture background;
    private Texture soundButtonTexture;
    private Texture soundOnTexture;
    private Texture soundOffTexture;
    BitmapFont heading;
    BitmapFont basic;
    BitmapFont big;
    private Button calibration;
    private Button sound;
    private Button restore;
    private Button backButton;
    private Button soundButton;
    private Button tutorial;
    private boolean changeSound;
    private boolean calibrating;
    private Vector3 touchPos;
    private float timer;
    private String timeInSecs;
    private AssetManager manager;

    public Settings(GameMain host) {
        this.host = host;
        manager = host.getManager();
        batch = host.getBatch();
        prefs = host.getPrefs();
        camera = host.getCamera();
        background = host.getBackgroundTexture();
        fontCamera = host.getFontCamera();
        heading = host.getHeadingFont();
        basic = host.getBasicFont();
        big = host.getBigFont();
        calibrating = false; changeSound = false;
        timer = -6; timeInSecs = "";

        manager.load("soundOn.png", Texture.class);
        manager.load("soundOff.png", Texture.class);

        manager.finishLoading();

        soundOnTexture = manager.get("soundOn.png");
        soundOffTexture = manager.get("soundOff.png");

        soundButtonTexture = soundOnTexture;
        backButton = new Button(0.2f, 8.3f, 1.5f, 1.5f, host.getBackButtonTexture());
        sound = new Button(8.5f, 1f, 4f, 1.7f, host.getButtonTexture());
        restore = new Button(2.8f, 1f, 4, 1.7f, host.getButtonTexture());
        calibration = new Button(2.8f, 4f, 4f, 1.7f, host.getButtonTexture());
        tutorial = new Button(8.5f, 4f, 4f, 1.7f, host.getButtonTexture());

        sound.setText(90, 90, "" + prefs.getString("sound"), basic);
        calibration.setText(30, 90, "" + prefs.getString("calibration"), basic);
        tutorial.setText(55, 90, "" + prefs.getString("tutorial"), basic);
        restore.setText(65, 115, prefs.getString("default") + "", basic);
        restore.setTextTwo(63, 70, "" + prefs.getString("settings"));
        destroySoundButton();
        touchPos = new Vector3();

        if (prefs.getString("language").equals("fi")) {
            restore.repositionTextTwo(40, 70);
            tutorial.repositionText(40, 90);
        }
    }

    @Override
    public void show() {

    }
    /**
     * moves the soundbutton to it's place on the screen
     */
    public void createSoundButton() {
        soundButton = new Button(13.2f, 0.8f, 2f, 2f, soundOnTexture);
    }
    /**
     * moves the soundbutton away from the screen
     */
    public void destroySoundButton() {
        soundButton = new Button(11f, 18f, .1f, .1f, soundOnTexture);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        timeInSecs = String.format(Locale.ENGLISH, "%.1f", timer);
        timer -= Gdx.graphics.getDeltaTime();

        batch.begin();

        batch.setProjectionMatrix(camera.combined);
        //piirrellään tausta ja napit
        batch.draw(background, 0, 0, 16, 10);
        calibration.draw(batch);
        sound.draw(batch);
        restore.draw(batch);
        backButton.draw(batch);
        tutorial.draw(batch);

        if(changeSound) {
            soundButton.draw(batch);
        } else { destroySoundButton(); }

        if(calibrating) {
            calibration.setTexture(host.getButtonGreenTexture());
        } else {
            calibration.setTexture(host.getButtonTexture());
        }

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        calibration.drawText(batch);
        restore.drawText(batch);
        restore.drawTextTwo(batch);
        sound.drawText(batch);
        tutorial.drawText(batch);

        heading.draw(batch, "" + prefs.getString("settingsBig"), 330, 700);

        if(calibrating) {
            big.draw(batch, prefs.getString("stayStill") + "  " + timeInSecs, 30, 315);
        } else if (!calibrating && timer>-4) {
            big.draw(batch, "" + prefs.getString("done"), 90, 315);
        }

        batch.end();

        if (timer<0 && calibrating) {
            host.calibrateZeroPoint();
            calibrating = false;
        }

        //nappien toiminnallisuus
        if(!calibrating) {
            if (Gdx.input.isTouched()) {
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
            }
            if (backButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
                this.dispose();
                host.setScreen(new MainMenu(host));
            }
            if (restore.contains(touchPos.x, touchPos.y)) {
                restore.setTexture(host.getButtonPressedTexture());
                if (!Gdx.input.isTouched()) {
                    host.setDefaultPreferences(host.getPrefs());
                    restore.setTexture(host.getButtonTexture());
                }
            }
            if (!restore.contains(touchPos.x, touchPos.y)) {
                restore.setTexture(host.getButtonTexture());
            }
            if (tutorial.contains(touchPos.x, touchPos.y)) {
                tutorial.setTexture(host.getButtonPressedTexture());
                if (!Gdx.input.isTouched()) {
                    this.dispose();
                    host.setScreen(new Tutorial(host));
                }
            }
            if (!tutorial.contains(touchPos.x, touchPos.y)) {
                tutorial.setTexture(host.getButtonTexture());
            }
            if (sound.contains(touchPos.x, touchPos.y)) {
                sound.setTexture(host.getButtonPressedTexture());
                if (!Gdx.input.isTouched()) {
                    createSoundButton();
                    changeSound ^= true;
                    touchPos.setZero();
                    sound.setTexture(host.getButtonTexture());
                }
            }
            if (!sound.contains(touchPos.x, touchPos.y)) {
                sound.setTexture(host.getButtonTexture());
            }
            if (soundButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
                host.setSoundOn(true);
                if (soundButton.getTexture() == soundOnTexture) {
                    soundButton.setTexture(soundOffTexture);
                } else {
                    soundButton.setTexture(soundOnTexture);
                }
                touchPos.setZero();
            }
            // Nollapisteen kalibrointi
            if (calibration.contains(touchPos.x, touchPos.y)) {
                calibration.setTexture(host.getButtonPressedTexture());
                if (!Gdx.input.isTouched()) {
                    touchPos.setZero();
                    calibrating = true;
                    timer = 3.98f;
                }
            }
            if (!calibration.contains(touchPos.x, touchPos.y)) {
                calibration.setTexture(host.getButtonTexture());
            }

            //ottaa napin painalluksen vain kerran
            if (!Gdx.input.isTouched()) {
                touchPos.set(0, 0, 0);
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
        manager.unload("soundOn.png");
        manager.unload("soundOff.png");
    }
}
