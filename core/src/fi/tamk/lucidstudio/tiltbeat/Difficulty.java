package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
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

public class Difficulty implements Screen {
    private GameMain host;
    private SpriteBatch batch;
    private BitmapFont small;
    private BitmapFont basic;
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
    private Texture background;
    private Texture buttonTexture;
    private Texture buttonHighTexture;
    private Texture buttonPressedTexture;
    private Vector3 touchPos;

    public Difficulty(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        heading = GameMain.getSmallerHeadingFont();
        basic = GameMain.getBasicFont();
        small = GameMain.getSmallFont();
        background = GameMain.getBackgroundTexture();
        buttonTexture = GameMain.getButtonTexture();
        buttonHighTexture = GameMain.getButtonHighTexture();
        buttonPressedTexture = GameMain.getButtonPressedTexture();
        touchPos = new Vector3();


        easyButton = new Button(.5f, .5f, 2.6f, 2f, buttonTexture);
        normalButton = new Button(4f, .5f, 2.6f, 2f, buttonTexture);
        hardButton = new Button(7.5f, .5f, 2.6f, 2f, buttonTexture);
        backBreakerButton = new Button(11f, .5f, 2.6f, 2f, buttonTexture);
        backButton = new Button(0.2f, 8.3f, 1.5f, 1.5f, GameMain.getBackButtonTexture());
        playButton = new Button(14f, 0.5f, 1.5f, 1.5f, GameMain.getPlayButtonTexture());
        textBox = new Button(1f, 3f, 7f, 4.6f, GameMain.getTextBoxTexture());
        song1Button = new Button(10f, 7f, 5f, 1.5f, buttonTexture);
        song2Button = new Button(10f, 5f, 5f, 1.5f, buttonTexture);
        song3Button = new Button(10f, 3f, 5f, 1.5f, buttonTexture);

        changeButtonTextures();
    }

    public void changeButtonTextures() {

        if (host.getDifficulty().equals("easy")) {
            easyButton.setTexture(buttonPressedTexture);
            normalButton.setTexture(buttonTexture);
            hardButton.setTexture(buttonTexture);
            backBreakerButton.setTexture(buttonTexture);
        } else if (host.getDifficulty().equals("normal")) {
            easyButton.setTexture(buttonTexture);
            normalButton.setTexture(buttonPressedTexture);
            hardButton.setTexture(buttonTexture);
            backBreakerButton.setTexture(buttonTexture);
        } else if (host.getDifficulty().equals("hard")) {
            easyButton.setTexture(buttonTexture);
            normalButton.setTexture(buttonTexture);
            hardButton.setTexture(buttonPressedTexture);
            backBreakerButton.setTexture(buttonTexture);
        } else {
            easyButton.setTexture(buttonTexture);
            normalButton.setTexture(buttonTexture);
            hardButton.setTexture(buttonTexture);
            backBreakerButton.setTexture(buttonPressedTexture);
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

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        heading.draw(batch, "Difficulty" , 180, 720);
        basic.draw(batch, "easy" , 80, 160);
        basic.draw(batch, "normal" , 350, 160);
        basic.draw(batch, "hard" , 650, 160);
        basic.draw(batch, "back-" , 925, 190);
        basic.draw(batch, "breaker" , 900, 140);
        small.draw(batch, "song 3" , 900, 320);
        small.draw(batch, "song 2" , 900, 490);
        small.draw(batch, "song 1" , 900, 650);
        heading.draw(batch, host.getDifficulty() + "" , 150, 580);
        if (host.getDifficulty().equals("easy")) {
            basic.draw(batch, "fun! fun! fun!" , 150, 420);
            basic.draw(batch, "for beginners" , 150, 350);
        }
        else if (host.getDifficulty().equals("normal")) {
            basic.draw(batch, "fun!" , 150, 420);
            basic.draw(batch, "basic mode" , 150, 350);
        }
        else if (host.getDifficulty().equals("hard")) {
            basic.draw(batch, "fun?" , 150, 490);
            basic.draw(batch, "for experienced" , 150, 420);
            basic.draw(batch, "players only" , 150, 350);
        }
        else if (host.getDifficulty().equals("BACKBREAKER")) {
            basic.draw(batch, "not fun" , 150, 420);
            basic.draw(batch, "you will die" , 150, 350);
        }

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
            host.setDifficulty("BACKBREAKER");
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
