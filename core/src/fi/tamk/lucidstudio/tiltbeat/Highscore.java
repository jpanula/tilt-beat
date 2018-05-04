package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Anna on 26/03/2018.
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
    private int[] easyScores;
    private int[] normalScores;
    private int[] hardScores;
    private int[] bbScores;
    private String[] easyNames;
    private String[] normalNames;
    private String[] hardNames;
    private String[] bbNames;
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
        easyScores = host.getEasyScores();
        easyNames = host.getEasyNames();
        normalScores = host.getNormalScores();
        normalNames = host.getNormalNames();
        hardScores = host.getHardScores();
        hardNames = host.getHardNames();
        bbScores = host.getBbScores();
        bbNames = host.getBbNames();

        backButton = new Button(0.2f, 8.3f, 1.5f, 1.5f, host.getBackButtonTexture());
        button1 = new Button(.5f, 5.6f, 4.5f, 1.2f, host.getButtonTexture());
        button2 = new Button(.5f, 3.9f, 4.5f, 1.2f, host.getButtonTexture());
        button3 = new Button(.5f, 2.2f, 4.5f, 1.2f, host.getButtonTexture());
        button4 = new Button(.5f, .5f, 4.5f, 1.2f, host.getButtonTexture());

        if (difficulty.equals("easy")) {
            button1.setTexture(host.getButtonPressedTexture());
        } else if (difficulty.equals("normal")) {
            button2.setTexture(host.getButtonPressedTexture());
        } else if (difficulty.equals("hard")) {
            button3.setTexture(host.getButtonPressedTexture());
        } else {
            button4.setTexture(host.getButtonPressedTexture());
        }

        button1.setText(40, 70, "" + prefs.getString("easy"), basic);
        button2.setText(40, 70, "" + prefs.getString("normal"), basic);
        button3.setText(40, 70, "" + prefs.getString("hard"), basic);
        button4.setText(40, 70, "" + prefs.getString("bb"), basic);

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
        batch.draw(textBoxTexture, 5.2f, 1, 10, 5);
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

        if (difficulty.equals("easy")) {
            drawEasy();
        } else if (difficulty.equals("normal")) {
            drawNormal();
        } else if (difficulty.equals("hard")) {
            drawHard();
        } else {
            drawBb();
        }

        batch.end();

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }
        if (backButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setScreen(new MainMenu(host));
        }
        if (button1.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            button1.setTexture(host.getButtonPressedTexture());
            button2.setTexture(host.getButtonTexture());
            button3.setTexture(host.getButtonTexture());
            button4.setTexture(host.getButtonTexture());
            difficulty = "easy";
        }
        if (button2.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            button1.setTexture(host.getButtonTexture());
            button2.setTexture(host.getButtonPressedTexture());
            button3.setTexture(host.getButtonTexture());
            button4.setTexture(host.getButtonTexture());
            difficulty = "normal";
        }
        if (button3.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            button1.setTexture(host.getButtonTexture());
            button2.setTexture(host.getButtonTexture());
            button3.setTexture(host.getButtonPressedTexture());
            button4.setTexture(host.getButtonTexture());
            difficulty = "hard";
        }
        if (button4.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            button1.setTexture(host.getButtonTexture());
            button2.setTexture(host.getButtonTexture());
            button3.setTexture(host.getButtonTexture());
            button4.setTexture(host.getButtonPressedTexture());
            difficulty = "bb";
        }
        //ottaa napin painalluksen vain kerran
        if (!Gdx.input.isTouched()) {touchPos.set(0, 0, 0);}

    }

    public void drawEasy() {
        basic.draw(batch, "1:  " + easyNames[0] , 500, 420);
        basic.draw(batch, "2:  " + easyNames[1] , 500, 360);
        basic.draw(batch, "3:  " + easyNames[2] , 500, 300);
        basic.draw(batch, "4:  " + easyNames[3] , 500, 240);
        basic.draw(batch, "5:  " + easyNames[4] , 500, 180);
        basic.draw(batch, "" + easyScores[0] , 1050, 420);
        basic.draw(batch, "" + easyScores[1] , 1050, 360);
        basic.draw(batch, "" + easyScores[2] , 1050, 300);
        basic.draw(batch, "" + easyScores[3] , 1050, 240);
        basic.draw(batch, "" + easyScores[4] , 1050, 180);
    }

    public void drawNormal() {
        basic.draw(batch, "1:  " + normalNames[0] , 500, 420);
        basic.draw(batch, "2:  " + normalNames[1] , 500, 360);
        basic.draw(batch, "3:  " + normalNames[2] , 500, 300);
        basic.draw(batch, "4:  " + normalNames[3] , 500, 240);
        basic.draw(batch, "5:  " + normalNames[4] , 500, 180);
        basic.draw(batch, "" + normalScores[0] , 1050, 420);
        basic.draw(batch, "" + normalScores[1] , 1050, 360);
        basic.draw(batch, "" + normalScores[2] , 1050, 300);
        basic.draw(batch, "" + normalScores[3] , 1050, 240);
        basic.draw(batch, "" + normalScores[4] , 1050, 180);
    }

    public void drawHard() {
        basic.draw(batch, "1:  " + hardNames[0] , 500, 420);
        basic.draw(batch, "2:  " + hardNames[1] , 500, 360);
        basic.draw(batch, "3:  " + hardNames[2] , 500, 300);
        basic.draw(batch, "4:  " + hardNames[3] , 500, 240);
        basic.draw(batch, "5:  " + hardNames[4] , 500, 180);
        basic.draw(batch, "" + hardScores[0] , 1050, 420);
        basic.draw(batch, "" + hardScores[1] , 1050, 360);
        basic.draw(batch, "" + hardScores[2] , 1050, 300);
        basic.draw(batch, "" + hardScores[3] , 1050, 240);
        basic.draw(batch, "" + hardScores[4] , 1050, 180);
    }

    public void drawBb() {
        basic.draw(batch, "1:  " + bbNames[0] , 500, 420);
        basic.draw(batch, "2:  " + bbNames[1] , 500, 360);
        basic.draw(batch, "3:  " + bbNames[2] , 500, 300);
        basic.draw(batch, "4:  " + bbNames[3] , 500, 240);
        basic.draw(batch, "5:  " + bbNames[4] , 500, 180);
        basic.draw(batch, "" + bbScores[0] , 1050, 420);
        basic.draw(batch, "" + bbScores[1] , 1050, 360);
        basic.draw(batch, "" + bbScores[2] , 1050, 300);
        basic.draw(batch, "" + bbScores[3] , 1050, 240);
        basic.draw(batch, "" + bbScores[4] , 1050, 180);
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
