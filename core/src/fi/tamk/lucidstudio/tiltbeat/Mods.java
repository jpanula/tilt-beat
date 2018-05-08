package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * This class is the mods screen.
 */

public class Mods implements Screen {
    private GameMain host;
    private SpriteBatch batch;
    private Preferences prefs;
    private BitmapFont heading;
    private BitmapFont basic;
    private BitmapFont small;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private Texture background;
    private Texture buttonTexture;
    private Texture buttonPressedTexture;
    private Button button4;
    private Button button4twist;
    private Button button6;
    private Button button8;
    private Button button10;
    private Button backButton;
    private Button textBox;
    private Vector3 touchPos;
    private boolean tiltedSquare;
    private boolean highscoreOn;
    private AssetManager manager;

    ArrayList<Polygon> sectors;
    float[] vertices;
    Polygon hitbox;
    Texture texture;
    float radius;
    int playerSides;
    float playerDiameter;

    public Mods(GameMain host) {
        this.host = host;
        manager = host.getManager();
        batch = host.getBatch();
        prefs = host.getPrefs();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        heading = host.getSmallerHeadingFont();
        basic = host.getBasicFont();
        small = host.getSmallFont();
        background = host.getBackgroundTexture();
        buttonTexture = host.getButtonTexture();
        buttonPressedTexture = host.getButtonPressedTexture();
        tiltedSquare = host.isTiltedSquare();
        highscoreOn = host.isHighscoreOn();

        manager.load("squaresectors.png", Texture.class);
        manager.load("diamondsectors.png", Texture.class);
        manager.load("sixsidesectors.png", Texture.class);
        manager.load("eightsidesectors.png", Texture.class);
        manager.load("tensidesectors.png", Texture.class);

        manager.finishLoading();

        button4 = new Button(1f, 5.3f, 3f, 1.2f, buttonTexture);
        button4twist = new Button(10.6f, 0.7f, 2f, 1.2f, buttonTexture);
        button6 = new Button(1f, 3.7f, 3f, 1.2f, buttonTexture);
        button8 = new Button(1f, 2.1f, 3f, 1.2f, buttonTexture);
        button10 = new Button(1f, 0.5f, 3f, 1.2f, buttonTexture);
        backButton = new Button(0.2f, 8.3f, 1.5f, 1.5f, host.getBackButtonTexture());
        textBox = new Button(10.5f, 2.5f, 5.4f, 3f, host.getTextBoxTexture());

        button4.setText(40, 70, "" + prefs.getString("4-side"), basic);
        button6.setText(40, 70, "" + prefs.getString("6-side"), basic);
        button8.setText(40, 70, "" + prefs.getString("8-side"), basic);
        button10.setText(40, 70, "" + prefs.getString("10-side"), basic);
        button4twist.setText(30, 70, "" + prefs.getString("flip"), basic);

        if (prefs.getString("language").equals("fi")) {
            button4.repositionText(20f, 70f);
            button6.repositionText(20f, 70f);
            button8.repositionText(20f, 70f);
            button10.repositionText(13f, 70f);
            button4twist.setSize(2.7f, 1.2f);
        }

        playerSides = host.getPlayerSides();
        playerDiameter = host.getPlayerDiameter();
        radius = playerDiameter / 2;
        sectors = new ArrayList<Polygon>();
        touchPos = new Vector3();

        if (!(playerSides == 4)) {
            button4twist.setPosition(18f, 11f);
        }
        makeButtonsAndPolygon();

    }
    /**
     * creates the wanted polygon and sets the right buttons texture as pressed
     */
    public void makeButtonsAndPolygon() {
        if(playerSides==4) {
            createFourside();
            button4.setTexture(buttonPressedTexture);
        } else if(playerSides==6) {
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
    /**
     * selects the right method for creating the wanted polygon
     */
    public void createFourside() {
        if (tiltedSquare) {
            createDiamond();
        } else {createSquare(); }
    }
    /**
     * sets the texture to a square,
     * sets the points for creating the square,
     * resets the active sectors
     */
    public void createSquare() {
        texture = manager.get("squaresectors.png");
        vertices = new float[]{
                1f, 1f,
                1f, 0f,
                0f, 0f,
                0f, 1f
        };
        //jos kulmio&aktiiviset sektorit ovat jo olemassa, ei nollata niitä
        if(prefs.getString("activeSectors").length()!=4) {
            host.resetActiveSectors(prefs.getInteger("playerSides"));
        }
        createSectors(vertices);
    }
    /**
     * sets the texture to a diamond,
     * sets the points for creating the diamond,
     * resets the active sectors
     */
    public void createDiamond() {
        texture = manager.get("diamondsectors.png");
        vertices = new float[]{
                0.5f, 1f,
                1f, 0.5f,
                0.5f, 0f,
                0f, 0.5f
        };
        //jos kulmio&aktiiviset sektorit ovat jo olemassa, ei nollata niitä
        if(prefs.getString("activeSectors").length()!=4) {
            host.resetActiveSectors(prefs.getInteger("playerSides"));
        }
        createSectors(vertices);
    }
    /**
     * sets the texture to a sixside,
     * sets the points for creating the sixside,
     * resets the active sectors
     */
    public void createSixside() {
        texture = manager.get("sixsidesectors.png");
        vertices = new float[]{
                0.5f, 1f,
                0.933f, 0.75f,
                0.933f, 0.25f,
                0.5f, 0f,
                0.067f, 0.25f,
                0.067f, 0.75f
        };
        //jos kulmio&aktiiviset sektorit ovat jo olemassa, ei nollata niitä
        if(prefs.getString("activeSectors").length()!=6) {
            host.resetActiveSectors(prefs.getInteger("playerSides"));
        }
        createSectors(vertices);
    }
    /**
     * sets the texture to a eightside,
     * sets the points for creating the eightside,
     * resets the active sectors
     */
    public void createEightside() {
        texture = manager.get("eightsidesectors.png");
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
        if(prefs.getString("activeSectors").length()!=8) {
            host.resetActiveSectors(prefs.getInteger("playerSides"));
        }
        createSectors(vertices);
    }
    /**
     * sets the texture to a tenside,
     * sets the points for creating the tenside,
     * resets the active sectors
     */
    public void createTenside() {
        texture = manager.get("tensidesectors.png");
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
        if(prefs.getString("activeSectors").length()!=10) {
            host.resetActiveSectors(prefs.getInteger("playerSides"));
        }
        createSectors(vertices);
    }
    /**
     * constructor for the sector selection polygon
     * @param vertices the points for creating the polygon
     */
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
        button4.draw(batch);
        button6.draw(batch);
        button8.draw(batch);
        button10.draw(batch);
        button4twist.draw(batch);
        textBox.draw(batch);
        batch.draw(texture, hitbox.getX(), hitbox.getY(), hitbox.getScaleX(), hitbox.getScaleY());

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        heading.draw(batch, "" + prefs.getString("modifications") , 250, 680);
        button4.drawText(batch);
        button6.drawText(batch);
        button8.drawText(batch);
        button10.drawText(batch);
        button4twist.drawText(batch);
        small.draw(batch, "" + prefs.getString("modsText1") , 885, 400);
        small.draw(batch, "" + prefs.getString("modsText2") , 885, 350);
        small.draw(batch, "" + prefs.getString("modsText3") , 885, 300);
        if (!highscoreOn) {
            if (prefs.getString("language").equals("en")) {
                basic.draw(batch, "" + prefs.getString("hsOffline"), 870, 530);
            } else if (prefs.getString("language").equals("fi")) {
                basic.draw(batch, "" + prefs.getString("hsOffline"), 800, 530);
            }
        }

        //väliaikainen millä näkee onko sektorit päällä vai pois
        //piirtää sektoreihin "on/off"
        if(playerSides==10) {
            draw10sectors();
        } else if(playerSides==8) {
            draw8sectors();
        } else if(playerSides==6) {
            draw6sectors();
        } else if(playerSides==4 && !tiltedSquare) {
            drawSquareSectors();
        } else { drawDiamondSectors(); }

        batch.end();

        if (host.howManySectorsActive() < (playerSides/2)) {
            highscoreOn = false;
        } else { highscoreOn = true; }

        //nappien toiminnallisuus
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }
        if (backButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setHighscoreOn(highscoreOn);
            this.dispose();
            host.setScreen(new MainMenu(host));
        }
        if (button4.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setPlayerSides(4);
            createFourside();
            button4.setTexture(buttonPressedTexture);
            button6.setTexture(buttonTexture);
            button8.setTexture(buttonTexture);
            button10.setTexture(buttonTexture);
            button4twist.setPosition(10.6f, 0.7f);
        }
        if (button4twist.contains(touchPos.x, touchPos.y)) {
            button4twist.setTexture(buttonPressedTexture);
            if (!Gdx.input.isTouched()) {
                tiltedSquare ^= true;
                host.setTiltedSquare(tiltedSquare);
                createFourside();
            }
        }
        if (!button4twist.contains(touchPos.x, touchPos.y)) {
            button4twist.setTexture(buttonTexture);
        }
        if (button6.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setPlayerSides(6);
            createSixside();
            button4.setTexture(buttonTexture);
            button6.setTexture(buttonPressedTexture);
            button8.setTexture(buttonTexture);
            button10.setTexture(buttonTexture);
            button4twist.setPosition(18f, 11f);
        }
        if (button8.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setPlayerSides(8);
            createEightside();
            button4.setTexture(buttonTexture);
            button6.setTexture(buttonTexture);
            button8.setTexture(buttonPressedTexture);
            button10.setTexture(buttonTexture);
            button4twist.setPosition(18f, 11f);
        }
        if (button10.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setPlayerSides(10);
            createTenside();
            button4.setTexture(buttonTexture);
            button6.setTexture(buttonTexture);
            button8.setTexture(buttonTexture);
            button10.setTexture(buttonPressedTexture);
            button4twist.setPosition(18f, 11f);
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
    /**
     * draws on/off to the squares sectors
     */
    public void drawSquareSectors() {
        if(host.getActiveSectors()[0])  //
            small.draw(batch, "on", 700, 300);
        else { small.draw(batch, "off", 700, 300); }
        if(host.getActiveSectors()[1]) {
            small.draw(batch, "on", 570, 180);
        } else { small.draw(batch, "off", 570, 180); }
        if(host.getActiveSectors()[2]) {
            small.draw(batch, "on", 440, 300);
        } else { small.draw(batch, "off", 440, 300); }
        if(host.getActiveSectors()[3]) {
            small.draw(batch, "on", 570, 420);
        } else { small.draw(batch, "off", 570, 420); }
    }
    /**
     * draws on/off to the diamonds sectors
     */
    public void drawDiamondSectors() {
        if(host.getActiveSectors()[0]) {
            small.draw(batch, "on", 630, 350);
        } else { small.draw(batch, "off", 630, 350); }
        if(host.getActiveSectors()[1]) {
            small.draw(batch, "on", 630, 240);
        } else { small.draw(batch, "off", 630, 240); }
        if(host.getActiveSectors()[2]) {
            small.draw(batch, "on", 500, 240);
        } else { small.draw(batch, "off", 500, 240); }
        if(host.getActiveSectors()[3]) {
            small.draw(batch, "on", 500, 350);
        } else { small.draw(batch, "off", 500, 350); }

    }
    /**
     * draws on/off to the sixsides sectors
     */
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
    /**
     * draws on/off to the eightsides sectors
     */
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
    /**
     * draws on/off to the tensides sectors
     */
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
        manager.unload("squaresectors.png");
        manager.unload("diamondsectors.png");
        manager.unload("sixsidesectors.png");
        manager.unload("eightsidesectors.png");
        manager.unload("tensidesectors.png");

    }
}
