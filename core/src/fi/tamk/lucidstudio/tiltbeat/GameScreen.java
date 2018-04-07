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

import java.util.ArrayList;
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
    private ArrayList<Note> song;
    private boolean paused;
    private boolean useShapeRenderer;
    private Texture noteTexture;

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
        song = new ArrayList<Note>();
        noteTexture = new Texture("Smol Red.png");

        for (int i = 0; i < 100 ; i++) {
            int random = MathUtils.random(0, (playerSides-1));
            random = moveNotes(random);
            //jos ei järjestelmällinen siirtäminen toimi (koska uusi sektori myös passiivinen) niin arvotaan uusi paikka
            while (!isSectorActive(random)) {
                random = MathUtils.random(0, (playerSides-1));
            }
            //song.add(new Point(((random) % playerSides), 2.5f * i * noteSpeed * 0.8f * noteSpeed, noteTexture));
            int rand = MathUtils.random(0, 5);
            for (int j = 0; j < 3; j++) {
                if (rand == 0) {
                song.add(new Point(((rand + j) % playerSides), 2.5f * i * noteSpeed + j * 0.8f * noteSpeed, noteTexture));
                } else if (rand == 1) {
                    song.add(new Point(((rand - j) % playerSides), 2.5f * i * noteSpeed + j * 0.8f * noteSpeed, noteTexture));
                } else {
                    song.add(new Point(((rand) % playerSides), 2.5f * i * noteSpeed + j * 0.5f *  noteSpeed, noteTexture));
                }
            }
        }

        points = 0;
        basic = GameMain.getBasicFont();
        heading = GameMain.getHeadingFont();
        background = new Texture(Gdx.files.internal("Galaxy dark purple.png"));

        pauseButtonTexture = GameMain.getPauseButtonTexture();
        pauseButton = new Rectangle(0.2f, 8.3f, 1.5f, 1.5f);
        paused = false;

        useShapeRenderer = true;
    }

    public boolean isSectorActive(int a) {
        //tarkistaa onko sektori aktiivinen
        if (GameMain.activeSectors[a]) {
            return true;
        } else { return false;}
    }

    public int moveNotes(int a) {
        int b = a; int goBack=0; int continuousSectors = 0; int r;

        if (!GameMain.activeSectors[a]) {
            //kelataan peräkkäisten aktiviisten sektoreiden ensimmäiseen
            while (!GameMain.activeSectors[b]) {
                goBack=b; b--;
                if (b==-1) { b = (playerSides-1); } //että pysytään järkevissä luvuissa
            }
            //lasketaan montako sektoria putkeen
            while (!GameMain.activeSectors[goBack]) {
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
        for (Note note : song) {
            note.draw(batch);
        }

        batch.setProjectionMatrix(fontCamera.combined);
        basic.draw(batch, "points: " + points, 50, 100);
        // Piirrellään Accelerometerin arvoja ruudulle
        basic.draw(batch, "X: " + Gdx.input.getAccelerometerX(), 50, 300);
        basic.draw(batch, " Y: " + Gdx.input.getAccelerometerY(), 50, 250);
        basic.draw(batch, " Z: " + Gdx.input.getAccelerometerZ(), 50, 200);
        if (paused) {
            heading.draw(batch, "PAUSE", 420, 450);
        }

        batch.end();

        // ShapeRenderer render, piirtää annetuilla pisteillä muotoja
        if (useShapeRenderer) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 0, 1, 1);
            player.draw(shapeRenderer);
            for (Note note : song) {
                if (note instanceof Slide) {
                    ((Slide) note).draw(shapeRenderer);
                }
            }
            shapeRenderer.setColor(1, 0, 0, 0);
            //shapeRenderer.line(GameMain.getScreenWidth() / 2, GameMain.getScreenHeight() / 2, 0, Gdx.input.getAccelerometerY() + GameMain.getScreenWidth() / 2, -Gdx.input.getAccelerometerX() + GameMain.getScreenHeight() / 2, -Math.abs(Gdx.input.getAccelerometerZ()));
            shapeRenderer.end();
        }

        // Printtaa konsoliin accelerometerin arvoja
        //System.out.println("X: " + Gdx.input.getAccelerometerX() + " Y: " + Gdx.input.getAccelerometerY() + " Z: " + Gdx.input.getAccelerometerZ());

        // Pelaajan liike tarvitsee kameraa kosketus-/klikkausohjauksen unprojectia varten
        // (Ruutukoordinaateista maailmakoordinaateiksi)
        if (!paused) {
            player.move(camera);
        }
        // ArrayList pitää käydä läpi Iteratorilla, koska ArrayLististä ei voi poistaa elementtejä,
        // kun sen läpi iteroidaan. Iteraattorissa tämän voi tehdä.
        Iterator<Note> iter = song.iterator();
        while (iter.hasNext()) {
            Note note = iter.next();
            if (!paused) {
                note.move(noteSpeed);
            }
            // Jos nuotin etäisyys keskikulmiosta on 0 tai vähemmän
            if (note instanceof Point && note.getDistance() <= 0) {
                // Jos pelaajan osoitin on samalla sektorilla, poista nuotti
                if (note.getSector() == player.getPointerSector()) {
                    iter.remove();
                    points += 5;
                // Muuten FAIL
                } else {
                    System.out.println("FAIL!");
                    iter.remove();
                }
            // Hold-tyyppisten nuottien tarkistus
            } else if (note instanceof  Hold) {
                if (note.getDistance() + ((Hold) note).getLength() <= 0) {
                    if (note.getSector() == player.getPointerSector()) {
                        iter.remove();
                        points += 10;
                    } else {
                        System.out.println("FAIL!");
                        iter.remove();
                    }
                }
                if (note.getDistance() <= 0 && !((Hold) note).isScored()) {
                    if (note.getSector() == player.getPointerSector()) {
                        points += 10;
                        ((Hold) note).setScored(true);
                    } else {
                        System.out.println("FAIL!");
                        ((Hold) note).setScored(true);
                    }
                }
                // Holdin pikkupalleroiden tarkistus
                for (Hold.Tick tick : ((Hold) note).getTicks()) {
                    if (tick.getDistance() <= 0 && !tick.isScored()) {
                        if (tick.getSector() == player.getPointerSector()) {
                            points++;
                            tick.setScored(true);
                        } else {
                            System.out.println("FAIL!");
                            tick.setScored(true);
                        }
                    }
                }
            // Slide-tyyppisten nuottirykelmien tarkistus
            } else if (note instanceof Slide) {
                Iterator<Point> slideIter = ((Slide) note).getNotes().iterator();
                while (slideIter.hasNext()) {
                    Point point = slideIter.next();
                    if (point.getDistance() <= 0) {
                        if (point.getSector() == player.getPointerSector()) {
                            points += 10;
                            slideIter.remove();
                        } else {
                            System.out.println("FAIL!");
                            slideIter.remove();
                        }
                    }
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
                paused = true;
                // Väliaikainen kalibrointi paussinapissa
                GameMain.calibrateZeroPoint();
                //player.pointer.resetSmoothing();
            } else {
                paused = false;
            }
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = true;
    }

    @Override
    public void hide() {
        paused = true;
    }

    @Override
    public void dispose() {

    }
}
