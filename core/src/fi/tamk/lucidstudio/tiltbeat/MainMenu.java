package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.Scanner;

/**
 * Created by Anna on 26/03/2018.
 */

public class MainMenu implements Screen{
    private GameMain host;
    private SpriteBatch batch;
    private Preferences prefs;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private BitmapFont heading;
    private BitmapFont basic;
    private Button playButton;
    private Button modsButton;
    private Button settingsButton;
    private Button highscoreButton;
    private Button creditsButton;
    private Button enButton;
    private Button finButton;
    private Button circle;
    private Texture background;
    private Vector3 touchPos;
    private String[] words;
    private AssetManager manager;


    public MainMenu(GameMain host) {
        this.host = host;
        manager = host.getManager();
        batch = host.getBatch();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        background = host.getBackgroundTexture();
        prefs = host.getPrefs();

        manager.load("copyright.png", Texture.class);
        manager.load("uk.png", Texture.class);
        manager.load("fin.png", Texture.class);
        manager.load("circle.png", Texture.class);

        manager.finishLoading();

        Texture t = manager.get("copyright.png");
        Texture en = manager.get("uk.png");
        Texture fin = manager.get("fin.png");
        Texture c = manager.get("circle.png");

        playButton = new Button(2.8f, 4f, 4f, 1.7f, host.getButtonTexture());
        modsButton = new Button(8.5f, 4f, 4f, 1.7f, host.getButtonTexture());
        settingsButton = new Button(2.8f, 1f, 4f, 1.7f, host.getButtonTexture());
        highscoreButton = new Button(8.5f, 1f, 4f, 1.7f, host.getButtonTexture());
        creditsButton = new Button(14.5f, .5f, 1f, 1f, t);
        enButton = new Button(14.5f, 8.5f, 1f, 1f, en);
        finButton = new Button(13.2f, 8.5f, 1f, 1f, fin);

        if (host.getLanguage().equals("fi")) {
            circle = new Button(13.1f, 8.4f, 1.2f, 1.2f, c);
        } else {
            circle = new Button(14.4f, 8.4f, 1.2f, 1.2f, c);
        }

        heading = host.getHeadingFont();
        basic = host.getBasicFont();
        touchPos = new Vector3();

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
        playButton.draw(batch);
        settingsButton.draw(batch);
        highscoreButton.draw(batch);
        modsButton.draw(batch);
        creditsButton.draw(batch);
        enButton.draw(batch);
        finButton.draw(batch);
        circle.draw(batch);

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        heading.draw(batch, "TilT" , 370, 750);
        heading.draw(batch, "BeaT" , 550, 670);
        basic.draw(batch, "" + prefs.getString("play") , 320, 410);
        basic.draw(batch, "" + prefs.getString("settings") , 283, 170);
        basic.draw(batch, "" + prefs.getString("mods") , 775, 410);
        basic.draw(batch, "highscore" , 735, 170);

        batch.end();

        //nappien kosketuksesta screeni vaihtuu
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }
        if (playButton.contains(touchPos.x, touchPos.y)  && !Gdx.input.isTouched()) {
            this.dispose();
            host.setScreen(new Difficulty(host));
            if (host.howManySectorsActive()==0) {
                this.dispose();
                host.setScreen(new SectorsOff(host));
            }
        }
        if (settingsButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            this.dispose();
            host.setScreen(new Settings(host));
        }
        if (highscoreButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            this.dispose();
            host.setScreen(new Highscore(host));
        }
        if (modsButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            this.dispose();
            host.setScreen(new Mods(host));
        }
        if (creditsButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            this.dispose();
            host.setScreen(new Credits(host));
        }
        if (enButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setLanguage("en");
            host.useEnglish();
            circle.setPosition(14.4f, 8.4f);
        }
        if (finButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setLanguage("fi");
            host.useFinnish();
            circle.setPosition(13.1f, 8.4f);
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
        manager.unload("copyright.png");
        manager.unload("uk.png");
        manager.unload("fin.png");
        manager.unload("circle.png");
    }
}
