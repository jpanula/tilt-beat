package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * This class is the splash screen.
 */
public class SplashScreen implements Screen {
    private GameMain host;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Texture exerium;
    private Texture tamk;
    private Texture lucid;
    private float timer;
    private Preferences prefs;
    private boolean finished;

    public SplashScreen(GameMain host) {
        this.host = host;
        prefs = host.getPrefs();
        batch = host.getBatch();
        camera = host.getCamera();
        exerium = new Texture("Splash exerium.png");
        tamk = new Texture("Splash tamk.png");
        lucid = new Texture("Splash lucid.png");
        finished = false;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        if (timer <= 5) {
            if (Gdx.input.justTouched()) {
                timer = 5;
            }
            if (timer < 1) {
                batch.setColor(1, 1, 1, timer);
            } else if (timer > 4) {
                batch.setColor(1, 1, 1, 5 - timer);
            } else {
                batch.setColor(1, 1, 1, 1);
            }
            batch.draw(lucid, 0, 0, 16, 10);
        } else if (timer <= 8) {
            if (Gdx.input.justTouched()) {
                timer = 8;
            }
            if (timer <= 6f) {
                batch.setColor(1, 1, 1, timer - 5);
            } else if (timer >= 7f) {
                batch.setColor(1, 1, 1, 8 - timer);
            } else {
                batch.setColor(1, 1, 1, 1);
            }
            batch.draw(tamk, 0, 0, 16, 10);
        } else if (timer <= 11) {
            if (Gdx.input.justTouched()) {
                timer = 11;
            }
            if (timer <= 9) {
                batch.setColor(1, 1, 1, timer - 8);
            } else if (timer >= 10) {
                batch.setColor(1, 1, 1, 11 - timer);
            } else {
                batch.setColor(1, 1, 1, 1);
            }
            batch.draw(exerium, 0, 0, 16, 10);
        } else {
            finished = true;
            batch.setColor(1, 1, 1, 1);
        }
        batch.end();

        timer += Gdx.graphics.getDeltaTime();
        if (finished) {
            if (!prefs.contains("firstTime")) {
                prefs.putBoolean("firstTime", true);
                this.dispose();
                host.setScreen(new Tutorial(host));
            } else {
                this.dispose();
                host.setScreen(new MainMenu(host));
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
        exerium.dispose();
        tamk.dispose();
        lucid.dispose();

    }
}
