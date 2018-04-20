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

import java.lang.reflect.GenericArrayType;
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
    private Button button6;
    private Button button8;
    private Button button10;
    private Button backButton;
    private Button textBox;
    private Vector3 touchPos;

    ArrayList<Polygon> sectors;
    float[] vertices;
    Polygon hitbox;
    Texture texture;
    float radius;
    int playerSides;
    float playerDiameter;

    public Mods(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        heading = host.getSmallerHeadingFont();
        basic = host.getBasicFont();
        small = host.getSmallFont();
        background = GameMain.getBackgroundTexture();
        buttonTexture = GameMain.getButtonTexture();
        buttonPressedTexture = GameMain.getButtonPressedTexture();

        button6 = new Button(1f, 4.5f, 3f, 1.5f, buttonTexture);
        button8 = new Button(1f, 2.5f, 3f, 1.5f, buttonTexture);
        button10 = new Button(1f, 0.5f, 3f, 1.5f, buttonTexture);
        backButton = new Button(0.2f, 8.3f, 1.5f, 1.5f, GameMain.getBackButtonTexture());
        textBox = new Button(10.5f, 2.5f, 5.4f, 3f, GameMain.getTextBoxTexture());

        button6.setText(40, 80, "6-side", basic);
        button8.setText(40, 80, "8-side", basic);
        button10.setText(40, 80, "10-side", basic);

        playerSides = host.getPlayerSides();
        playerDiameter = host.getPlayerDiameter();
        radius = playerDiameter / 2;
        sectors = new ArrayList<Polygon>();
        touchPos = new Vector3();

        makeButtonsAndPolygon();

    }

    public void makeButtonsAndPolygon() {
        if(playerSides==6) {
            createSixside();
            button6.setTexture(buttonPressedTexture);
        } else if (playerSides==8) {
            createEightside();
            button8.setTexture(buttonPressedTexture);
        } else {
            createTenside();
            button10.setTexture(buttonPressedTexture);
        }

    }

    public void createSixside() {
        texture = new Texture("sixsidesectors.png");
        vertices = new float[]{
                0.5f, 1f,
                0.933f, 0.75f,
                0.933f, 0.25f,
                0.5f, 0f,
                0.067f, 0.25f,
                0.067f, 0.75f
        };
        //jos kulmio&aktiiviset sektorit ovat jo olemassa, ei nollata niitä
        if(host.getActiveSectors().length!=6) {
            host.setActiveSectors(new boolean[6]);
            for (int i=0 ; i<6 ; i++) {
                host.setActiveSector(i, true);
            }
        }
        createSectors(vertices);
    }

    public void createEightside() {
        texture = new Texture("eightsidesectors.png");
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
        //jos kulmio&aktiiviset sektorit ovat jo olemassa, ei nollata niitä
        if(host.getActiveSectors().length!=8) {
            host.setActiveSectors(new boolean[8]);
            for (int i=0 ; i<8 ; i++) {
                host.setActiveSector(i, true);
            }
        }
        createSectors(vertices);
    }

    public void createTenside() {
        texture = new Texture("tensidesectors.png");
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
        //jos kulmio&aktiiviset sektorit ovat jo olemassa, ei nollata niitä
        if(host.getActiveSectors().length!=10) {
            host.setActiveSectors(new boolean[10]);
            for (int i=0 ; i<10 ; i++) {
                host.setActiveSector(i, true);
            }
        }
        createSectors(vertices);
    }

    public void createSectors(float[] vertices) {
        sectors = new ArrayList<Polygon>();
        playerSides = host.getPlayerSides();
        for (int i = 0; i < playerSides; i++) {
            float[] triangleVerts = {
                    vertices[i * 2], vertices[i * 2 + 1],
                    vertices[(i * 2 + 2) % (playerSides * 2)], vertices[(i * 2 + 3) % (playerSides * 2)],
                    0.5f, 0.5f
            };
            sectors.add(new Polygon(triangleVerts));
            // Asetetaan sektorien koko ja sijainti oikeiksi
            sectors.get(i).setScale(playerDiameter+2f, playerDiameter+2f);
            sectors.get(i).setPosition(host.getScreenWidth() / 2 - radius - 1.5f, host.getScreenHeight() / 2 - radius - 2.5f);
            // asetetaan sektori aktiiviseksi
        }
        // Muodostetaan pelaajan kulmio, tehdään siitä oikean kokoinen ja siirretään se keskelle ruutua
        hitbox = new Polygon(vertices);
        hitbox.setScale(playerDiameter+2f, playerDiameter+2f);
        hitbox.setPosition(host.getScreenWidth() / 2 - radius - 1.5f, host.getScreenHeight() / 2 - radius - 2.5f);

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
        button6.draw(batch);
        button8.draw(batch);
        button10.draw(batch);
        textBox.draw(batch);
        batch.draw(texture, hitbox.getX(), hitbox.getY(), hitbox.getScaleX(), hitbox.getScaleY());

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        heading.draw(batch, "Modifications" , 250, 680);
        button6.drawText(batch);
        button8.drawText(batch);
        button10.drawText(batch);
        small.draw(batch, "click sectors to" , 885, 400);
        small.draw(batch, "activate or" , 885, 350);
        small.draw(batch, "de-activate them" , 885, 300);

        //väliaikainen millä näkee onko sektorit päällä vai pois
        //piirtää sektoreihin "on/off"
        if(playerSides==10) {
            draw10sectors();
        } else if(playerSides==8) {
            draw8sectors();
        } else if(playerSides==6) {
            draw6sectors();
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
        if (button6.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setPlayerSides(6);
            createSixside();
            button6.setTexture(buttonPressedTexture);
            button8.setTexture(buttonTexture);
            button10.setTexture(buttonTexture);
        }
        if (button8.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setPlayerSides(8);
            createEightside();
            button6.setTexture(buttonTexture);
            button8.setTexture(buttonPressedTexture);
            button10.setTexture(buttonTexture);
        }
        if (button10.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setPlayerSides(10);
            createTenside();
            button6.setTexture(buttonTexture);
            button8.setTexture(buttonTexture);
            button10.setTexture(buttonPressedTexture);
        }
        for (int i=0 ; i<playerSides ; i++) {
            Polygon polygon = sectors.get(i);
            if(polygon.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
                if (host.getActiveSectors()[i]) {
                    host.setActiveSector(i, false);
                } else {
                    host.setActiveSector(i, true);
                }
                touchPos.setZero();
            }
        }
        //ottaa napin painalluksen vain kerran
        if (!Gdx.input.isTouched()) {touchPos.set(0, 0, 0);}

    }


    public void draw6sectors() {
        if(host.getActiveSectors()[0]) {
            small.draw(batch, "on", 640, 400);
        } else { small.draw(batch, "off", 640, 400); }
        if(host.getActiveSectors()[1]) {
            small.draw(batch, "on", 680, 300);
        } else { small.draw(batch, "off", 680, 300); }
        if(host.getActiveSectors()[2]) {
            small.draw(batch, "on", 640, 200);
        } else { small.draw(batch, "off", 640, 200); }
        if(host.getActiveSectors()[3]) {
            small.draw(batch, "on", 500, 200);
        } else { small.draw(batch, "off", 500, 200); }
        if(host.getActiveSectors()[4]) {
            small.draw(batch, "on", 460, 300);
        } else { small.draw(batch, "off", 460, 300); }
        if(host.getActiveSectors()[5]) {
            small.draw(batch, "on", 500, 400);
        } else { small.draw(batch, "off", 500, 400); }

    }

    public void draw8sectors() {
        if(host.getActiveSectors()[0]) {
            small.draw(batch, "on", 620, 420);
        } else { small.draw(batch, "off", 620, 420); }
        if(host.getActiveSectors()[1]) {
            small.draw(batch, "on", 700, 350);
        } else { small.draw(batch, "off", 700, 350); }
        if(host.getActiveSectors()[2]) {
            small.draw(batch, "on", 700, 240);
        } else { small.draw(batch, "off", 700, 240); }
        if(host.getActiveSectors()[3]) {
            small.draw(batch, "on", 620, 170);
        } else { small.draw(batch, "off", 620, 170); }
        if(host.getActiveSectors()[4]) {
            small.draw(batch, "on", 520, 170);
        } else { small.draw(batch, "off", 520, 170); }
        if(host.getActiveSectors()[5]) {
            small.draw(batch, "on", 440, 240);
        } else { small.draw(batch, "off", 440, 240); }
        if(host.getActiveSectors()[6]) {
            small.draw(batch, "on", 440, 350);
        } else { small.draw(batch, "off", 440, 350); }
        if(host.getActiveSectors()[7]) {
            small.draw(batch, "on", 520, 420);
        } else { small.draw(batch, "off", 520, 420); }

    }

    public void draw10sectors() {
        if(host.getActiveSectors()[0]) {
            small.draw(batch, "on", 620, 440);
        } else { small.draw(batch, "off", 620, 440); }
        if(host.getActiveSectors()[1]) {
            small.draw(batch, "on", 680, 370);
        } else { small.draw(batch, "off", 680, 370); }
        if(host.getActiveSectors()[2]) {
            small.draw(batch, "on", 720, 300);
        } else { small.draw(batch, "off", 720, 300); }
        if(host.getActiveSectors()[3]) {
            small.draw(batch, "on", 680, 220);
        } else { small.draw(batch, "off", 680, 220); }
        if(host.getActiveSectors()[4]) {
            small.draw(batch, "on", 620, 160);
        } else { small.draw(batch, "off", 620, 160); }
        if(host.getActiveSectors()[5]) {
            small.draw(batch, "on", 520, 160);
        } else { small.draw(batch, "off", 520, 160); }
        if(host.getActiveSectors()[6]) {
            small.draw(batch, "on", 460, 220);
        } else { small.draw(batch, "off", 460, 220); }
        if(host.getActiveSectors()[7]) {
            small.draw(batch, "on", 420, 300);
        } else { small.draw(batch, "off", 420, 300); }
        if(host.getActiveSectors()[8]) {
            small.draw(batch, "on", 460, 370);
        } else { small.draw(batch, "off", 460, 370); }
        if(host.getActiveSectors()[9]) {
            small.draw(batch, "on", 520, 440);
        } else { small.draw(batch, "off", 520, 440); }

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
