package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * This class is the credits screen.
 */

public class Credits implements Screen {
    GameMain host;
    SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private Texture background;
    BitmapFont heading;
    BitmapFont basic;
    BitmapFont small;
    private Button backButton;
    private Vector3 touchPos;

    /**
     * Constructor for the screen
     * @param host the GameMain that is using this screen.
     */
    public Credits(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        background = host.getBackgroundTexture();
        fontCamera = host.getFontCamera();
        heading = host.getSmallerHeadingFont();
        basic = host.getBasicFont();
        small = host.getSmallFont();
        backButton = new Button(0.2f, 8.3f, 1.5f, 1.5f, host.getBackButtonTexture());
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
        backButton.draw(batch);

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit

        heading.draw(batch, "Credits", 400, 700);
        basic.draw(batch, "Music:", 50, 500);
        small.draw(batch, "Jaunty Gumption, Ouroboros and Snare Bounce Polka\n" +
                              "by Kevin MacLeod (incompetech.com)\n" +
                              "Licensed under Creative Commons: By Attribution 3.0 License\n" +
                              "http://creativecommons.org/licenses/by/3.0/",
                           50, 400);

        batch.end();

        //nappien toiminnallisuus
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }
        if ((backButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) || Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            this.dispose();
            host.setScreen(new MainMenu(host));
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

    }
}

