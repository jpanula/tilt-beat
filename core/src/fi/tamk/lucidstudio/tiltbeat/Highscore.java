package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * This class is the high score screen
 */

public class Highscore implements Screen {
    private GameMain host;
    private SpriteBatch batch;
    private Preferences prefs;
    private BitmapFont heading;
    private BitmapFont basic;
    private BitmapFont small;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private Texture background;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button backButton;
    private Texture backButtonTexture;
    private Texture textBoxTexture;
    private Vector3 touchPos;
    private int[] scores;
    private String difficulty;

    public  Highscore(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        prefs = host.getPrefs();
        heading = host.getHeadingFont();
        basic = host.getBasicFont();
        small = host.getSmallFont();
        background = host.getBackgroundTexture();
        backButtonTexture = host.getBackButtonTexture();
        textBoxTexture = host.getTextBoxTexture();
        touchPos = new Vector3();
        difficulty = host.getDifficulty();

        backButton = new Button(0.2f, 8.3f, 1.5f, 1.5f, host.getBackButtonTexture());
        button1 = new Button(.5f, 5.6f, 4.5f, 1.2f, host.getButtonTexture());
        button2 = new Button(.5f, 3.9f, 4.5f, 1.2f, host.getButtonTexture());
        button3 = new Button(.5f, 2.2f, 4.5f, 1.2f, host.getButtonTexture());
        button4 = new Button(.5f, .5f, 4.5f, 1.2f, host.getButtonTexture());

        if (difficulty.equals("easy")) {
            button1.setTexture(host.getButtonPressedTexture());
            scores = host.getEasyScores();
        } else if (difficulty.equals("normal")) {
            button2.setTexture(host.getButtonPressedTexture());
            scores = host.getNormalScores();
        } else if (difficulty.equals("hard")) {
            button3.setTexture(host.getButtonPressedTexture());
            scores = host.getHardScores();
        } else {
            button4.setTexture(host.getButtonPressedTexture());
            scores = host.getBbScores();
        }

        button1.setText(40, 70, "" + prefs.getString("easy"), basic);
        button2.setText(40, 70, "" + prefs.getString("normal"), basic);
        button3.setText(40, 70, "" + prefs.getString("hard"), basic);
        button4.setText(40, 70, "" + prefs.getString("backbreaker"), basic);

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
        //batch.draw(textBoxTexture, 5.2f, 1, 10, 5);
        batch.draw(textBoxTexture, 7f, 0.8f, 6, 5);
        backButton.draw(batch);
        button1.draw(batch);
        button2.draw(batch);
        button3.draw(batch);
        button4.draw(batch);

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        heading.draw(batch, "Highscore" , 450, 680);
        button1.drawText(batch);
        button2.drawText(batch);
        button3.drawText(batch);
        button4.drawText(batch);

        basic.draw(batch, "1:", 650, 405);
        basic.draw(batch, "2:", 650, 345);
        basic.draw(batch, "3:", 650, 285);
        basic.draw(batch, "4:", 650, 225);
        basic.draw(batch, "5:", 650, 165);
        basic.draw(batch, "" + scores[0] , 850, 405);
        basic.draw(batch, "" + scores[1] , 850, 345);
        basic.draw(batch, "" + scores[2] , 850, 285);
        basic.draw(batch, "" + scores[3] , 850, 225);
        basic.draw(batch, "" + scores[4] , 850, 165);

        //piirtää nuolen pistemäärän kohdalle jonka pelaaja juuri sai
        if (host.getPlacement()!=0) {
            if (host.getPlacement() == 1) {
                basic.draw(batch, "->", 780, 405);
            } else if (host.getPlacement() == 2) {
                basic.draw(batch, "->", 780, 345);
            } else if (host.getPlacement() == 3) {
                basic.draw(batch, "->", 780, 285);
            } else if (host.getPlacement() == 4) {
                basic.draw(batch, "->", 780, 225);
            } else if (host.getPlacement() == 5) {
                basic.draw(batch, "->", 780, 165);
            }
        }

        batch.end();

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }
        if ((backButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) || Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            this.dispose();
            host.setPlacement(0);
            host.setScreen(new MainMenu(host));
        }
        if (button1.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            button1.setTexture(host.getButtonPressedTexture());
            button2.setTexture(host.getButtonTexture());
            button3.setTexture(host.getButtonTexture());
            button4.setTexture(host.getButtonTexture());
            host.setPlacement(0);
            scores = host.getEasyScores();
        }
        if (button2.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            button1.setTexture(host.getButtonTexture());
            button2.setTexture(host.getButtonPressedTexture());
            button3.setTexture(host.getButtonTexture());
            button4.setTexture(host.getButtonTexture());
            host.setPlacement(0);
            scores = host.getNormalScores();
        }
        if (button3.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            button1.setTexture(host.getButtonTexture());
            button2.setTexture(host.getButtonTexture());
            button3.setTexture(host.getButtonPressedTexture());
            button4.setTexture(host.getButtonTexture());
            host.setPlacement(0);
            scores = host.getHardScores();
        }
        if (button4.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            button1.setTexture(host.getButtonTexture());
            button2.setTexture(host.getButtonTexture());
            button3.setTexture(host.getButtonTexture());
            button4.setTexture(host.getButtonPressedTexture());
            host.setPlacement(0);
            scores = host.getBbScores();
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
