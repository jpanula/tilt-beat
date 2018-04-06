package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * Created by Anna on 27/03/2018.
 */

public class Mods implements Screen {
    private GameMain host;
    private SpriteBatch batch;
    private BitmapFont heading;
    private BitmapFont basic;
    private BitmapFont small;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private Texture background;
    private Texture buttonTexture;
    private Texture buttonPressedTexture;
    private Rectangle button6;
    private Rectangle button8;
    private Rectangle button10;
    private Texture button6Texture;
    private Texture button8Texture;
    private Texture button10Texture;
    private Rectangle backButton;
    private Texture backButtonTexture;
    private Rectangle textBox;
    private Texture textBoxTexture;

    ArrayList<Polygon> sectors;
    float[] vertices;
    Polygon hitbox;
    Texture texture;
    float radius;
    static boolean[] activeSectors;
    int playerSides;
    float playerDiameter;

    public Mods(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        heading = GameMain.getSmallerHeadingFont();
        basic = GameMain.getBasicFont();
        small = GameMain.getSmallFont();
        background = GameMain.getBackgroundTexture();
        buttonTexture = GameMain.getButtonTexture();
        buttonPressedTexture = GameMain.getButtonPressedTexture();
        backButtonTexture = GameMain.getBackButtonTexture();
        textBoxTexture = GameMain.getTextBoxTexture();

        button6Texture = buttonTexture;
        button8Texture = buttonTexture;
        button10Texture = buttonPressedTexture;

        button6 = new Rectangle(1f, 4.5f, 3f, 1.5f);
        button8 = new Rectangle(1f, 2.5f, 3f, 1.5f);
        button10 = new Rectangle(1f, 0.5f, 3f, 1.5f);
        backButton = new Rectangle(0.2f, 8.3f, 1.5f, 1.5f);
        textBox = new Rectangle(10.5f, 2.5f, 5.4f, 3f);

        playerSides = GameMain.getPlayerSides();
        playerDiameter = GameMain.getPlayerDiameter();
        radius = playerDiameter / 2;
        sectors = new ArrayList<Polygon>();
        activeSectors = new boolean[playerSides];

        createTenside();

    }

    public void createSixside() {
        texture = new Texture("sixside.png");
        vertices = new float[]{
                0.5f, 1f,
                0.933f, 0.75f,
                0.933f, 0.25f,
                0.5f, 0f,
                0.067f, 0.25f,
                0.067f, 0.75f
        };
        activeSectors = new boolean[6];

        createSectors(vertices);
    }

    public void createEightside() {

        texture = new Texture("eightside.png");
        vertices = new float[]{
                0.5f, 1.0f,
                0.8555f, 0.8555f,
                1f, 0.5f,
                0.8555f, 0.1445f,
                0.5f, 0f,
                0.1445f, 0.1445f,
                0f, 0.5f,
                0.1445f, 0.8555f
        };
        activeSectors = new boolean[8];
        createSectors(vertices);
    }

    public void createTenside() {
        texture = new Texture("tenside.png");
        // Tässä on 10-kulmion pisteet, joista hitbox Polygon muodostetaan
        vertices = new float[]{
                0.5f, 1.0f,
                0.795f, 0.905f,
                0.98f, 0.65f,
                0.98f, 0.35f,
                0.795f, 0.095f,
                0.5f, 0f,
                0.205f, 0.095f,
                0.0203f, 0.35f,
                0.0203f, 0.65f,
                0.205f, 0.905f
        };
        activeSectors = new boolean[10];
        createSectors(vertices);
    }

    public void createSectors(float[] vertices) {
        playerSides = GameMain.getPlayerSides();
        for (int i = 0; i < playerSides; i++) {
            float[] triangleVerts = {
                    vertices[i * 2], vertices[i * 2 + 1],
                    vertices[(i * 2 + 2) % (playerSides * 2)], vertices[(i * 2 + 3) % (playerSides * 2)],
                    0.5f, 0.5f
            };
            sectors.add(new Polygon(triangleVerts));
            // Asetetaan sektorien koko ja sijainti oikeiksi
            sectors.get(i).setScale(playerDiameter, playerDiameter);
            sectors.get(i).setPosition(GameMain.getScreenWidth() / 2 - radius, GameMain.getScreenHeight() / 2 - radius);
            // asetetaan sektori aktiiviseksi
            activeSectors[i] = true;
        }
        // Muodostetaan pelaajan kulmio, tehdään siitä oikean kokoinen ja siirretään se keskelle ruutua
        hitbox = new Polygon(vertices);
        hitbox.setScale(playerDiameter+2f, playerDiameter+2f);
        hitbox.setPosition(GameMain.getScreenWidth() / 2 - radius - 1.5f, GameMain.getScreenHeight() / 2 - radius - 2.5f);

        //testi - ei-aktiiviset sektorit
        //activeSectors[2] = false; activeSectors[3] = false; activeSectors[4] = false;
        //activeSectors[6] = false; activeSectors[7] = false;
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
        batch.draw(button6Texture, button6.x, button6.y, button6.width, button6.height);
        batch.draw(button8Texture, button8.x, button8.y, button8.width, button8.height);
        batch.draw(button10Texture, button10.x, button10.y, button10.width, button10.height);
        batch.draw(textBoxTexture, textBox.x, textBox.y, textBox.width, textBox.height);
        batch.draw(texture, hitbox.getX(), hitbox.getY(), hitbox.getScaleX(), hitbox.getScaleY());

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        heading.draw(batch, "Modifications" , 250, 680);
        basic.draw(batch, "6-side" , 120, 440);
        basic.draw(batch, "8-side" , 120, 280);
        basic.draw(batch, "10-side" , 120, 120);
        small.draw(batch, "click sectors to" , 885, 400);
        small.draw(batch, "activate or" , 885, 350);
        small.draw(batch, "de-activate them" , 885, 300);

        batch.end();

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (backButton.contains(touchPos.x, touchPos.y)) {
                host.setScreen(new MainMenu(host));
            }
            if (button6.contains(touchPos.x, touchPos.y)) {
                GameMain.setPlayerSides(6);
                createSixside();
                button6Texture = buttonPressedTexture;
                button8Texture = buttonTexture;
                button10Texture = buttonTexture;
            }
            if (button8.contains(touchPos.x, touchPos.y)) {
                GameMain.setPlayerSides(8);
                createEightside();
                button6Texture = buttonTexture;
                button8Texture = buttonPressedTexture;
                button10Texture = buttonTexture;
            }
            if (button10.contains(touchPos.x, touchPos.y)) {
                GameMain.setPlayerSides(10);
                createTenside();
                button6Texture = buttonTexture;
                button8Texture = buttonTexture;
                button10Texture = buttonPressedTexture;
            }
            for (int i=0 ; i<playerSides ; i++) {
                Polygon polygon = sectors.get(i);
                if(polygon.contains(touchPos.x, touchPos.y)) {
                    Player.setActiveSectors(i);
                }
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
