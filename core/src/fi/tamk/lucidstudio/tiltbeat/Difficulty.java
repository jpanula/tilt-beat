package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
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

public class Difficulty implements Screen {
    private GameMain host;
    private AssetManager manager;
    private SpriteBatch batch;
    private BitmapFont small;
    private BitmapFont basic;
    private BitmapFont big;
    private BitmapFont heading;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private Button textBox;
    private Button backButton;
    private Button playButton;
    private Button easyButton;
    private Button normalButton;
    private Button hardButton;
    private Button backBreakerButton;
    private Button song1Button;
    private Button song2Button;
    private Button song3Button;
    private Button ringButton;
    private Texture background;
    private Texture buttonTexture;
    private Texture buttonPressedTexture;
    private Vector3 touchPos;

    public Difficulty(GameMain host) {
        this.host = host;
        manager = host.getManager();
        batch = host.getBatch();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        heading = host.getSmallerHeadingFont();
        small = host.getSmallFont();
        basic = host.getBasicFont();
        big = host.getBigFont();
        background = host.getBackgroundTexture();
        buttonTexture = host.getButtonTexture();
        buttonPressedTexture = host.getButtonPressedTexture();
        touchPos = new Vector3();
/*
        manager.load("easynappi.png", Texture.class);
        manager.load("normalnappi.png", Texture.class);
        manager.load("hardnappi.png", Texture.class);
        manager.load("bbnappi.png", Texture.class);
        manager.load("ring.png", Texture.class);
        Texture easyT = manager.get("easynappi.png");
        Texture normalT = manager.get("normalnappi.png");
        Texture hardT = manager.get("hardnappi.png");
        Texture bbT = manager.get("bbnappi.png");
        Texture ring = manager.get("ring.png");
*/
        Texture easyT = new Texture("easynappi.png");
        Texture normalT = new Texture("normalnappi.png");
        Texture hardT = new Texture("hardnappi.png");
        Texture bbT = new Texture("bbnappi.png");
        Texture ring = new Texture("planet-ring.png");

        backButton = new Button(0.2f, 8.3f, 1.5f, 1.5f, host.getBackButtonTexture());
        playButton = new Button(14f, 0.5f, 1.5f, 1.5f, host.getPlayButtonTexture());
        textBox = new Button(1f, 3f, 7f, 4.6f, host.getTextBoxTexture());
        easyButton = new Button(1.5f, .5f, 2f, 2f, easyT);
        normalButton = new Button(4.5f, .5f, 2f, 2f, normalT);
        hardButton = new Button(7.2f, .4f, 2.5f, 2.5f, hardT);
        backBreakerButton = new Button(10.5f, .4f, 2.3f, 2.3f, bbT);
        song1Button = new Button(10f, 7f, 5f, 1.5f, buttonTexture);
        song2Button = new Button(10f, 5f, 5f, 1.5f, buttonTexture);
        song3Button = new Button(10f, 3f, 5f, 1.5f, buttonTexture);
        ringButton = new Button(10f, 3.5f, 3f, 1f, ring);

        easyButton.setText(28, 110, "easy", basic);
        normalButton.setText(5, 110, "normal", basic);
        hardButton.setText(55, 120, "hard", basic);
        backBreakerButton.setText(-20, 120, "backbreaker", basic);
        song1Button.setText(40, 80, "jaumpty gumpty", small);
        song2Button.setText(40, 80, "nyan cat", small);
        song3Button.setText(40, 80, "take on me", small);
        textBox.setText(50, 180, "fun! fun! fun!\nfor beginners", basic);

        changeButtonTextures();
    }

    public void changeButtonTextures() {

        if (host.getDifficulty().equals("easy")) {
            textBox.setText("fun! fun! fun!\nfor beginners");
            ringButton.setPosition(1f, 1f);
        } else if (host.getDifficulty().equals("normal")) {
            textBox.setText("fun!\nbasic mode");
            ringButton.setPosition(4f, 1f);
        } else if (host.getDifficulty().equals("hard")) {
            textBox.setText("fun?\nfor hc players");
            ringButton.setPosition(6.95f, 1f);
        } else {
            textBox.setText("not fun\nyou will die");
            ringButton.setPosition(10.1f, 1f);
        }

        if (host.getSongChoice().equals("song 1")) {
            song1Button.setTexture(buttonPressedTexture);
            song2Button.setTexture(buttonTexture);
            song3Button.setTexture(buttonTexture);
        } else if (host.getSongChoice().equals("song 2")) {
            song1Button.setTexture(buttonTexture);
            song2Button.setTexture(buttonPressedTexture);
            song3Button.setTexture(buttonTexture);
        } else {
            song1Button.setTexture(buttonTexture);
            song2Button.setTexture(buttonTexture);
            song3Button.setTexture(buttonPressedTexture);
        }
        //ottaa napin painalluksen vain kerran
        if (!Gdx.input.isTouched()) {touchPos.set(0, 0, 0);}


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
        playButton.draw(batch);
        easyButton.draw(batch);
        normalButton.draw(batch);
        hardButton.draw(batch);
        backBreakerButton.draw(batch);
        textBox.draw(batch);
        song1Button.draw(batch);
        song2Button.draw(batch);
        song3Button.draw(batch);
        ringButton.draw(batch);

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        heading.draw(batch, "Difficulty" , 180, 720);
        easyButton.drawText(batch);
        normalButton.drawText(batch);
        hardButton.drawText(batch);
        backBreakerButton.drawText(batch);
        song1Button.drawText(batch);
        song2Button.drawText(batch);
        song3Button.drawText(batch);
        textBox.drawText(batch);
        big.draw(batch, host.getDifficulty() + "" , 130, 550);
        batch.end();

        //nappien toiminnallisuus
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }
        if (backButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setScreen(new MainMenu(host));
        }
        if (playButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setScreen(new GameScreen(host));
        }
        if (easyButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setDifficulty("easy");
            host.setNoteSpeed(1);
            changeButtonTextures();
        }
        if (normalButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setDifficulty("normal");
            host.setNoteSpeed(2);
            changeButtonTextures();
        }
        if (hardButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setDifficulty("hard");
            host.setNoteSpeed(3);
            changeButtonTextures();
        }
        if (backBreakerButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setDifficulty("backbreaker");
            host.setNoteSpeed(4);
            changeButtonTextures();
        }
        if (song1Button.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setSongChoice("song 1");
            changeButtonTextures();
        }
        if (song2Button.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setSongChoice("song 2");
            changeButtonTextures();
        }
        if (song3Button.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setSongChoice("song 3");
            changeButtonTextures();
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
