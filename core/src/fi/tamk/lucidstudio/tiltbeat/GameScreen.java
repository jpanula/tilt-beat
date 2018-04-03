package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.Iterator;

/**
 * Created by Jaakko on 11.3.2018.
 */

public class
GameScreen implements Screen {
    private GameMain host;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private ShapeRenderer shapeRenderer;
    private Player player;
    private Song song;
    private boolean useShapeRenderer;

    private int playerSides;
    private float playerDiameter;
    private float noteSpeed;

    private int points;
    private BitmapFont basic;
    private BitmapFont heading;

    private Texture background;
    private Texture pauseButtonTexture;
    private Rectangle pauseButton;

    /**
     * Peliruutu, asetukset haetaan GameMainistä, joka toimii "hostina"
     * @param host pelin Main-metodi
     */
    public GameScreen(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        shapeRenderer = host.getShapeRenderer();

        playerSides = GameMain.getPlayerSides();
        playerDiameter = GameMain.getPlayerDiameter();
        player = new Player(playerSides, playerDiameter);

        noteSpeed = GameMain.getNoteSpeed();
        song = new Song();
        for (int i = 0; i < 100 ; i++) {
            int random = MathUtils.random(0, (playerSides-1));
            random = moveNotes(random);
            //jos ei järjestelmällinen siirtäminen toimi (koska uusi sektori myös passiivinen) niin arvotaan uusi paikka
            while (!isSectorActive(random)) {
                random = MathUtils.random(0, (playerSides-1));
            }

            song.addNote(new Hold(random, 3.5f * i * noteSpeed / 6 + 5, 0.75f));
        }

        points = 0;
        basic = GameMain.getBasicFont();
        heading = GameMain.getHeadingFont();
        background = new Texture(Gdx.files.internal("Galaxy dark purple.png"));

        pauseButtonTexture = GameMain.getPauseButtonTexture();
        pauseButton = new Rectangle(0.2f, 8.8f, 1f, 1f);

        useShapeRenderer = false;
    }

    public boolean isSectorActive(int a) {
        //tarkistaa onko sektori aktiivinen
        if (Player.activeSectors[a] == true) {
            return true;
        } else { return false;}
    }

    public int moveNotes(int a) {
        int b = a; int goBack=0; int continuousSectors = 0; int r;

        if (Player.activeSectors[b] == false) {
            //kelataan peräkkäisten aktiviisten sektoreiden ensimmäiseen
            while (Player.activeSectors[b] == false) {
                goBack=b; b--;
                if (b==-1) { b = (playerSides-1); } //että pysytään järkevissä luvuissa
            }
            //lasketaan montako sektoria putkeen
            while (Player.activeSectors[goBack] == false) {
                continuousSectors++; goBack++;
                if (goBack==playerSides) { goBack = 0; } //epjl
            }
        }
        //siirretään nuottia passiivisen alueen verran
        r = a + continuousSectors;
        if (r>(playerSides-1)) { r -= playerSides; } //epjl
        return r;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // Perus clearataan ruutu mustalla ja laitetaan renderereille kameran koordinaatit käyttöön
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Normaali render
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.draw(background, 0, 0 , 16, 10);
        batch.draw(pauseButtonTexture, pauseButton.x, pauseButton.y, pauseButton.width, pauseButton.height);

        player.draw(batch);
        for (Note note : song.notes) {
            note.draw(batch, playerSides);
        }

        batch.setProjectionMatrix(fontCamera.combined);
        basic.draw(batch, "points: " + points, 50, 100);
        // Piirrellään Accelerometerin arvoja ruudulle
        basic.draw(batch, "X: " + Gdx.input.getAccelerometerX(), Gdx.graphics.getWidth() * 2/3, Gdx.graphics.getHeight() / 3);
        basic.draw(batch, " Y: " + Gdx.input.getAccelerometerY(), Gdx.graphics.getWidth() * 2/3, Gdx.graphics.getHeight() / 3 - 50);
        basic.draw(batch, " Z: " + Gdx.input.getAccelerometerZ(), Gdx.graphics.getWidth() * 2/3, Gdx.graphics.getHeight() / 3 - 100);

        batch.end();

        // ShapeRenderer render, piirtää annetuilla pisteillä muotoja
        if (useShapeRenderer) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 0, 1, 1);
            player.draw(shapeRenderer);
            shapeRenderer.end();
        }

        // Printtaa konsoliin accelerometerin arvoja
        //System.out.println("X: " + Gdx.input.getAccelerometerX() + " Y: " + Gdx.input.getAccelerometerY() + " Z: " + Gdx.input.getAccelerometerZ());

        // Pelaajan liike tarvitsee kameraa kosketus-/klikkausohjauksen unprojectia varten
        // (Ruutukoordinaateista maailmakoordinaateiksi)
        player.move(camera);

        // ArrayList pitää käydä läpi Iteratorilla, koska ArrayLististä ei voi poistaa elementtejä,
        // kun sen läpi iteroidaan. Iteraattorissa tämän voi tehdä.
        Iterator<Note> iter = song.notes.iterator();
        while (iter.hasNext()) {
            Note note = iter.next();
            note.move(noteSpeed);

            // Jos nuotin etäisyys keskikulmiosta on 0 tai vähemmän
            if (note.getDistance() <= 0) {
                // Jos pelaajan osoitin on samalla sektorilla, poista nuotti
                if (note.getSector() == player.getPointerSector()) {
                    iter.remove();
                    points++;
                // Muuten FAIL
                } else {
                    System.out.println("FAIL!");
                    iter.remove();
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            host.setScreen(new MainMenu(host));
        }

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (pauseButton.contains(touchPos.x, touchPos.y)) {
                pause();
            }
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        batch.begin();
        heading.draw(batch, "PAUSE", 420, 450);
        batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            host.setScreen(new MainMenu(host));
        }
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
