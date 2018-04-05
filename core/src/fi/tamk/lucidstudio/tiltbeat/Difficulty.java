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
    private Texture background;
    private Texture buttonTexture;
    private Texture buttonHighTexture;
    private Rectangle buttonEasy;
    private Rectangle buttonNormal;
    private Rectangle buttonHard;
    private Rectangle buttonBackBreaker;
    private Rectangle backButton;
    private Rectangle playButton;
    private Texture backButtonTexture;
    private Texture playButtonTexture;
    private String difficulty;
    private Rectangle textBox;
    private Texture textBoxTexture;
    private Texture buttonPressedTexture;
    private Rectangle buttonSong1;
    private Rectangle buttonSong2;
    private Rectangle buttonSong3;

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
        backButtonTexture = GameMain.getBackButtonTexture();
        playButtonTexture = GameMain.getPlayButtonTexture();
        textBoxTexture = GameMain.getTextBoxTexture();

        difficulty = "normal";

        buttonEasy = new Rectangle(.5f, .5f, 2.6f, 2f);
        buttonNormal = new Rectangle(4f, .5f, 2.6f, 2f);
        buttonHard = new Rectangle(7.5f, .5f, 2.6f, 2f);
        buttonBackBreaker = new Rectangle(11f, .5f, 2.6f, 2f);
        backButton = new Rectangle(0.2f, 8.3f, 1.5f, 1.5f);
        playButton = new Rectangle(14f, 0.5f, 1.5f, 1.5f);
        textBox = new Rectangle(1f, 3f, 7f, 4.6f);
        buttonSong1 = new Rectangle(10f, 7f, 5f, 1.5f);
        buttonSong2 = new Rectangle(10f, 5f, 5f, 1.5f);
        buttonSong3 = new Rectangle(10f, 3f, 5f, 1.5f);
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
        batch.draw(playButtonTexture, playButton.x, playButton.y, playButton.width, playButton.height);
        batch.draw(buttonHighTexture, buttonEasy.x, buttonEasy.y, buttonEasy.width, buttonEasy.height);
        batch.draw(buttonHighTexture, buttonNormal.x, buttonNormal.y, buttonNormal.width, buttonNormal.height);
        batch.draw(buttonHighTexture, buttonHard.x, buttonHard.y, buttonHard.width, buttonHard.height);
        batch.draw(buttonHighTexture, buttonBackBreaker.x, buttonBackBreaker.y, buttonBackBreaker.width, buttonBackBreaker.height);
        batch.draw(textBoxTexture, textBox.x, textBox.y, textBox.width, textBox.height);
        batch.draw(buttonTexture, buttonSong1.x, buttonSong1.y, buttonSong1.width, buttonSong1.height);
        batch.draw(buttonTexture, buttonSong2.x, buttonSong2.y, buttonSong2.width, buttonSong2.height);
        batch.draw(buttonTexture, buttonSong3.x, buttonSong3.y, buttonSong3.width, buttonSong3.height);

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        heading.draw(batch, "Difficulty" , 180, 720);
        basic.draw(batch, "easy" , 80, 160);
        basic.draw(batch, "normal" , 350, 160);
        basic.draw(batch, "hard" , 650, 160);
        basic.draw(batch, "back-" , 925, 190);
        basic.draw(batch, "breaker" , 900, 140);
        heading.draw(batch, difficulty + "" , 150, 500);
        basic.draw(batch, "difficulty description" , 150, 400);
        small.draw(batch, "song 3" , 900, 320);
        small.draw(batch, "song 2" , 900, 490);
        small.draw(batch, "song 1" , 900, 650);

        batch.end();

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (backButton.contains(touchPos.x, touchPos.y)) {
                host.setScreen(new MainMenu(host));
            }
            if (playButton.contains(touchPos.x, touchPos.y)) {
                host.setScreen(new GameScreen(host));
            }
            if (buttonEasy.contains(touchPos.x, touchPos.y)) {
                difficulty = "easy";
            }
            if (buttonNormal.contains(touchPos.x, touchPos.y)) {
                difficulty = "normal";
            }
            if (buttonHard.contains(touchPos.x, touchPos.y)) {
                difficulty = "hard";
            }
            if (buttonBackBreaker.contains(touchPos.x, touchPos.y)) {
                difficulty = "BACKBREAKER";
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
