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

/**
 * Created by Anna on 06/05/2018.
 */

public class Tutorial implements Screen {
    GameMain host;
    SpriteBatch batch;
    private Preferences prefs;
    private AssetManager manager;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private Texture background;
    BitmapFont heading;
    BitmapFont basic;
    private Button fullscreen;
    private Button goForward;
    private Button goBackward;
    private Vector3 touchPos;
    private Texture t1;
    private Texture t2;
    private float timer;
    private float backX;
    private float backY;
    private float frontX;
    private float frontY;

    public Tutorial(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        manager = host.getManager();
        prefs = host.getPrefs();
        camera = host.getCamera();
        background = host.getBackgroundTexture();
        fontCamera = host.getFontCamera();
        heading = host.getBigFont();
        basic = host.getBasicFont();
        timer = 0;

        manager.load("Tutorial Eng1.png", Texture.class);
        manager.load("Tutorial Eng2.png", Texture.class);
        manager.load("Tutorial Fin1.png", Texture.class);
        manager.load("Tutorial Fin2.png", Texture.class);
        manager.finishLoading();

        if (prefs.getString("language").equals("en")) {
            t1 = manager.get("Tutorial Eng1.png");
            t2 = manager.get("Tutorial Eng2.png");
            backX = .3f; backY = .3f;
            frontX = 1.8f; frontY = .3f;
        } else {
            t1 = manager.get("Tutorial Fin1.png");
            t2 = manager.get("Tutorial Fin2.png");
            backX = 2.3f; backY = .3f;
            frontX = 3.8f; frontY = .3f;
        }

        fullscreen = new Button(0f, 0f, 16f, 10f, t1);
        goForward = new Button(14.5f, 8.5f, 1.2f, 1.2f, host.getPlayButtonTexture());
        goBackward = new Button(18f, 18f, 1.2f, 1.2f, host.getBackButtonTexture());

        //fullscreen.setText(30, 80, "go to mods", basic);
        touchPos = new Vector3();
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
        batch.draw(background, 0, 0, 16, 10);
        fullscreen.draw(batch);
        goForward.draw(batch);
        goBackward.draw(batch);

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        //fullscreen.drawText(batch);
        //heading.draw(batch, "text here", 270, 580);

        batch.end();

        timer += Gdx.graphics.getDeltaTime();

        //nappien toiminnallisuus
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }
        if (goForward.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            if (fullscreen.getTexture().equals(t1)) {
                fullscreen.setTexture(t2);
                goBackward.setPosition(backX, backY);
                goForward.setPosition(frontX, frontY);
            } else {
                this.dispose();
                host.setScreen(new MainMenu(host));
            }
        }
        if (goBackward.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            fullscreen.setTexture(t1);
            goBackward.setPosition(18f, 18f);
            goForward.setPosition(14.5f, 8.5f);
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
        manager.unload("Tutorial Eng1.png");
        manager.unload("Tutorial Eng2.png");
        manager.unload("Tutorial Fin1.png");
        manager.unload("Tutorial Fin2.png");
    }
}

