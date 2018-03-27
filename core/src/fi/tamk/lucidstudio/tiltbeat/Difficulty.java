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
    private BitmapFont heading;
    private BitmapFont basic;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private Texture background;
    private Texture buttonTexture;
    private Rectangle buttonEasy;
    private Rectangle buttonNormal;
    private Rectangle buttonHard;
    private Rectangle buttonBackBreaker;
    private Rectangle backButton;
    private Rectangle playButton;
    private Texture backButtonTexture;
    private String difficulty;

    public Difficulty(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        heading = GameMain.getSmallerHeadingFont();
        basic = GameMain.getBasicFont();
        background = GameMain.getBackgroundTexture();
        buttonTexture = GameMain.getButtonTexture();
        backButtonTexture = GameMain.getBackButtonTexture();

        difficulty = "normal";

        buttonEasy = new Rectangle(1.5f, 4.5f, 2.5f, 2.5f);
        buttonNormal = new Rectangle(5f, 4.5f, 2.5f, 2.5f);
        buttonHard = new Rectangle(8.5f, 4.5f, 2.5f, 2.5f);
        buttonBackBreaker = new Rectangle(12f, 4.5f, 2.5f, 2.5f);
        backButton = new Rectangle(0.2f, 8.8f, 1f, 1f);
        playButton = new Rectangle(14.5f, 0.5f, 1f, 1f);
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
        batch.draw(backButtonTexture, playButton.x, playButton.y, playButton.width, playButton.height);
        batch.draw(buttonTexture, buttonEasy.x, buttonEasy.y, buttonEasy.width, buttonEasy.height);
        batch.draw(buttonTexture, buttonNormal.x, buttonNormal.y, buttonNormal.width, buttonNormal.height);
        batch.draw(buttonTexture, buttonHard.x, buttonHard.y, buttonHard.width, buttonHard.height);
        batch.draw(buttonTexture, buttonBackBreaker.x, buttonBackBreaker.y, buttonBackBreaker.width, buttonBackBreaker.height);

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        heading.draw(batch, "Difficulty" , 250, 680);
        basic.draw(batch, "easy" , 150, 500);
        basic.draw(batch, "normal" , 400, 500);
        basic.draw(batch, "hard" , 700, 500);
        basic.draw(batch, "backbreaker" , 900, 500);
        heading.draw(batch, difficulty + "" , 300, 200);

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
