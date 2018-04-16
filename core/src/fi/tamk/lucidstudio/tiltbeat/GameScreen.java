package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private Texture pointTexture;
    private Texture holdTexture;
    private Texture slideTexture;
    private Vector3 touchPos;

    // Musiikki ja bpm
    private Music jauntyGumption;
    private float bpm;
    // Pituus minuuteissa
    private float musicLength;
    private int totalBeats;
    private int startOffset;

    private int playerSides;
    private float playerDiameter;
    private float noteSpeed;

    private int points;
    private BitmapFont verySmall;
    private BitmapFont basic;
    private BitmapFont heading;

    private Texture background;
    private Button pauseButton;
    private Button playAgainButton;
    private Button playButton;
    private Button backButton;
    private Button settingsButton;
    private Button resultBox;

    /**
     * Peliruutu, asetukset haetaan GameMainistä, joka toimii "hostina"
     * @param host pelin Main-metodi
     */
    public GameScreen(GameMain host) {
        // Katotaan jos toimii purkkakorjauksena ettei heti alussa skippaa eteenpäi
        paused = true;
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        shapeRenderer = host.getShapeRenderer();
        touchPos = new Vector3();

        playerSides = host.getPlayerSides();
        playerDiameter = host.getPlayerDiameter();
        player = new Player(playerSides, playerDiameter);

        jauntyGumption = Gdx.audio.newMusic(Gdx.files.internal("JauntyGumption.ogg"));
        bpm = 146;
        // Muutetaan nuottien tiheyttä vaikeusasteen mukaan
        if (host.getDifficulty().equals("easy")) {
            bpm /= 8;
            startOffset = 1;
        } else if (host.getDifficulty().equals(("normal"))) {
            bpm /= 4;
            startOffset = 2;
        } else if (host.getDifficulty().equals("hard")) {
            bpm /= 2;
            startOffset = 5;
        } else {
            startOffset = 10;
        }
        musicLength = 1 + 58f / 60;
        totalBeats = (int) (musicLength * bpm);

        noteSpeed = host.getNoteSpeed();
        song = new ArrayList<Note>();
        pointTexture = new Texture("Smol Green.png");
        holdTexture = new Texture("Smol Green Hold.png");
        slideTexture = new Texture("Smol Green Slide.png");


        for (int i = 0; i < totalBeats - startOffset ; i++) {
            int randomSector = MathUtils.random(0, (playerSides-1));
            randomSector = moveNotes(randomSector);
            //jos ei järjestelmällinen siirtäminen toimi (koska uusi sektori myös passiivinen) niin arvotaan uusi paikka
            while (!isSectorActive(randomSector)) {
                randomSector = MathUtils.random(0, (playerSides-1));
            }
            int randomNoteType = MathUtils.random(0, 4);
            if (randomNoteType < 5) {
                song.add(new Point((randomSector) % playerSides, i * noteSpeed / (bpm / 60) + noteSpeed * startOffset / (bpm / 60) + noteSpeed / (146 / 60), pointTexture));
            } else if (randomNoteType < 7) {
                song.add(new Hold((randomSector) % playerSides, i * noteSpeed / (bpm / 60) + noteSpeed * startOffset / (bpm / 60) + noteSpeed / (146 / 60), holdTexture, noteSpeed / (bpm / 60)));
                i++;
            } else {
                ArrayList<Point> slideGen = new ArrayList<Point>();
                slideGen.add(new Point((randomSector) % playerSides, i * noteSpeed / (bpm / 60) + noteSpeed * startOffset / (bpm / 60) + noteSpeed / (146 / 60), slideTexture));
                randomSector++;
                randomSector = moveNotes(randomSector % playerSides);
                while (!isSectorActive(randomSector % playerSides)) {
                    randomSector = MathUtils.random(0, (playerSides - 1));
                }
                slideGen.add(new Point((randomSector) % playerSides, (i + 0.5f) * noteSpeed / (bpm / 60) + noteSpeed * startOffset / (bpm / 60) + noteSpeed / (146 / 60), slideTexture));
                i++;
                if (randomSector == 0) {
                    randomSector = host.getPlayerSides() - 1;
                } else {
                    randomSector--;
                }
                randomSector = moveNotes(randomSector % playerSides);
                while (!isSectorActive(randomSector % playerSides)) {
                    randomSector = MathUtils.random(0, (playerSides - 1));
                }
                slideGen.add(new Point((randomSector) % playerSides, i * noteSpeed / (bpm / 60) + noteSpeed * startOffset / (bpm / 60) + noteSpeed / (146 / 60), slideTexture));
                slideGen.get(1).flip();
                song.add(new Slide(0, 0, slideGen));
            }
        }
            /*
            int rand = MathUtils.random(0, 5);
            for (int j = 0; j < 3; j++) {
                if (rand == 0) {
                song.add(new Point(((rand + j) % playerSides), 2.5f * i * noteSpeed + j * 0.8f * noteSpeed, pointTexture));
                } else if (rand == 1) {
                    song.add(new Point(((rand - j) % playerSides), 2.5f * i * noteSpeed + j * 0.8f * noteSpeed, pointTexture));
                } else {
                    song.add(new Point(((rand) % playerSides), 2.5f * i * noteSpeed + j * 0.5f *  noteSpeed, pointTexture));
                }
            }*/

        points = 0;
        verySmall = GameMain.getVerySmallFont();
        basic = GameMain.getBasicFont();
        heading = GameMain.getHeadingFont();
        background = new Texture(Gdx.files.internal("Galaxy dark purple.png"));

        pauseButton = new Button(0.2f, 8.3f, 1.5f, 1.5f, GameMain.getPauseButtonTexture());
        destroyPauseMenuButtons();

        resultBox = new Button(2f, 1f, 12f, 8f, GameMain.getTextBoxTexture());

        paused = false;
        useShapeRenderer = true;
        if (host.isSoundOn()) {
            jauntyGumption.play();
        }
    }

    public void createPauseMenuButtons() {
        playButton = new Button(6f, 2f, 1.5f, 1.5f, GameMain.getPlayButtonTexture());
        playAgainButton = new Button(8.5f, 2f, 1.5f, 1.5f, GameMain.getPlayAgainButtonTexture());
        backButton = new Button(3.5f, 2f, 1.5f, 1.5f, GameMain.getBackButtonTexture());
        settingsButton = new Button(11f, 2f, 1.5f, 1.5f, GameMain.getSettingsButtonTexture());
    }

    public void destroyPauseMenuButtons() {
        playButton = new Button(11f, 18f, .1f, .1f, GameMain.getPlayButtonTexture());
        playAgainButton = new Button(11f, 18f, .1f, .1f, GameMain.getPlayAgainButtonTexture());
        backButton = new Button(11f, 18f, .1f, .1f, GameMain.getBackButtonTexture());
        settingsButton = new Button(11f, 18f, .1f, .1f, GameMain.getSettingsButtonTexture());
    }

    public void createResultMenuButtons() {
        playAgainButton = new Button(8.5f, 2f, 1.5f, 1.5f, GameMain.getPlayAgainButtonTexture());
        backButton = new Button(3.5f, 2f, 1.5f, 1.5f, GameMain.getBackButtonTexture());
    }

    public void destroyResultMenuButtons() {
        playButton = new Button(11f, 18f, .1f, .1f, GameMain.getPlayButtonTexture());
        playAgainButton = new Button(11f, 18f, .1f, .1f, GameMain.getPlayAgainButtonTexture());
    }

    public boolean isSectorActive(int a) {
        //tarkistaa onko sektori aktiivinen
        if (host.getActiveSectors()[a]) {
            return true;
        } else { return false;}
    }

    public int moveNotes(int a) {
        int b = a; int goBack=0; int continuousSectors = 0; int r;

        if (!host.getActiveSectors()[a]) {
            //kelataan peräkkäisten aktiviisten sektoreiden ensimmäiseen
            while (!host.getActiveSectors()[b]) {
                goBack=b; b--;
                if (b==-1) { b = (playerSides-1); } //että pysytään järkevissä luvuissa
            }
            //lasketaan montako sektoria putkeen
            while (!host.getActiveSectors()[goBack]) {
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

        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        batch.draw(background, 0, 0, 16, 10);
        pauseButton.draw(batch);

        player.draw(batch);
        for (Note note : song) {
            note.draw(batch);
        }
        //pauseruutu
        if (paused && !song.isEmpty()) {
            createPauseMenuButtons();
            resultBox.draw(batch);
            playButton.draw(batch);
            playAgainButton.draw(batch);
            backButton.draw(batch);
            settingsButton.draw(batch);
        }
        //tulosruutu
        if (song.isEmpty()) {
            createResultMenuButtons();
            resultBox.draw(batch);
            playAgainButton.draw(batch);
            backButton.draw(batch);
        }

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään tekstit
        basic.draw(batch, "points: " + points, 50, 100);

        //väliaikainen millä näkee onko sektorit päällä vai pois
        //piirtää sektoreihin "on/off"
        if(!song.isEmpty() && !paused) {
            if (playerSides == 10) {
                draw10sectors();
            } else if (playerSides == 8) {
                draw8sectors();
            } else if (playerSides == 6) {
                draw6sectors();
            }
        }

        // Piirrellään Accelerometerin arvoja ruudulle
        /*basic.draw(batch, "X: " + Gdx.input.getAccelerometerX(), 50, 300);
        basic.draw(batch, " Y: " + Gdx.input.getAccelerometerY(), 50, 250);
        basic.draw(batch, " Z: " + Gdx.input.getAccelerometerZ(), 50, 200);*/
        //pauseruudun tekstit
        if (paused && !song.isEmpty()) {
            heading.draw(batch, "PAUSE", 300, 600);
            basic.draw(batch, "main", 280, 400);
            basic.draw(batch, "menu", 280, 350);
            basic.draw(batch, "continue", 440, 380);
            basic.draw(batch, "retry", 680, 380);
            basic.draw(batch, "settings", 830, 380);
        }
        //tulosruudun tekstit
        if (song.isEmpty()) {
            heading.draw(batch, "you did it!!!", 220, 650);
            basic.draw(batch, "you got " + points + " points!!", 300, 400);
            basic.draw(batch, "main menu", 410, 230);
            basic.draw(batch, "start again?", 820, 230);
        }

        batch.end();

        //piirretään sektorit&osoitin vain kun peli päällä
        if(!song.isEmpty() && !paused) {
            // ShapeRenderer render, piirtää annetuilla pisteillä muotoja
            if (useShapeRenderer) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(1, 1, 1, 0);
                player.draw(shapeRenderer);
                shapeRenderer.end();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(0, 1, 0, 0);
                for (Note note : song) {
                    if (note instanceof Slide) {
                        ((Slide) note).draw(shapeRenderer);
                    }
                }
                //shapeRenderer.line(GameMain.getScreenWidth() / 2, GameMain.getScreenHeight() / 2, 0, Gdx.input.getAccelerometerY() + GameMain.getScreenWidth() / 2, -Gdx.input.getAccelerometerX() + GameMain.getScreenHeight() / 2, -Math.abs(Gdx.input.getAccelerometerZ()));
                shapeRenderer.end();
            }
        }

        // Musiikin toiminta pausen kanssa
        if (paused) {
            jauntyGumption.pause();
        } else if (!paused && !jauntyGumption.isPlaying() && !song.isEmpty() && host.isSoundOn()) {
            jauntyGumption.play();
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
                if (note.getSector() == player.getPointerSector() && !note.isHit()) {
                    note.setHit(true);
                    points += 5;
                // Muuten FAIL
                } else if (note.getDistance() < -0.35f || !note.isHit()){
                    System.out.println("FAIL!");
                    iter.remove();
                }
                if (note.isHit()) {
                    if (((Point) note).isAnimationFinished()) {
                        iter.remove();
                    }
                }
            // Hold-tyyppisten nuottien tarkistus
            } else if (note instanceof  Hold) {
                if (note.getDistance() + ((Hold) note).getLength() <= 0) {
                    if (note.getSector() == player.getPointerSector()) {
                        iter.remove();
                        points += 10;
                    } else if (note.getDistance() < -0.35f){
                        System.out.println("FAIL!");
                        iter.remove();
                    }
                }
                if (note.getDistance() <= 0 && !((Hold) note).isScored()) {
                    if (note.getSector() == player.getPointerSector()) {
                        points += 10;
                        ((Hold) note).setScored(true);
                    } else if (note.getDistance() < -0.35f){
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
                        } else if (note.getDistance() < -0.35f){
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
                        } else if (point.getDistance() < -0.35f){
                            System.out.println("FAIL!");
                            slideIter.remove();
                        }
                    }
                }
                if (((Slide) note).getNotes().isEmpty()) {
                    iter.remove();
                }
            }
        }

        //nappien toiminnot
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }
        if (pauseButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            paused = true;
            // Väliaikainen kalibrointi paussinapissa
            host.calibrateZeroPoint();
            //player.pointer.resetSmoothing();
        }
        if (playButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            paused = false;
            destroyPauseMenuButtons();
        }
        if (playAgainButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            jauntyGumption.stop();
            host.setScreen(new GameScreen(host));
        }
        if (backButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            jauntyGumption.stop();
            host.setScreen(new MainMenu(host));
        }
        if (settingsButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            jauntyGumption.stop();
            host.setScreen(new Settings(host));
        }
    }


    public void draw6sectors() {
        if(!host.getActiveSectors()[0]) {
            verySmall.draw(batch, "off", 650, 470); }
        if(!host.getActiveSectors()[1]) {
            verySmall.draw(batch, "off", 680, 410); }
        if(!host.getActiveSectors()[2]) {
            verySmall.draw(batch, "off", 650, 350); }
        if(!host.getActiveSectors()[3]) {
            verySmall.draw(batch, "off", 580, 350); }
        if(!host.getActiveSectors()[4]) {
            verySmall.draw(batch, "off", 550, 410); }
        if(!host.getActiveSectors()[5]) {
            verySmall.draw(batch, "off", 580, 470); }
    }

    public void draw8sectors() {
        if(!host.getActiveSectors()[0]) {
            verySmall.draw(batch, "off", 650, 480); }
        if(!host.getActiveSectors()[1]) {
            verySmall.draw(batch, "off", 690, 440); }
        if(!host.getActiveSectors()[2]) {
            verySmall.draw(batch, "off", 690, 380); }
        if(!host.getActiveSectors()[3]) {
            verySmall.draw(batch, "off", 650, 330); }
        if(!host.getActiveSectors()[4]) {
            verySmall.draw(batch, "off", 590, 330); }
        if(!host.getActiveSectors()[5]) {
            verySmall.draw(batch, "off", 550, 380); }
        if(!host.getActiveSectors()[6]) {
            verySmall.draw(batch, "off", 550, 440); }
        if(!host.getActiveSectors()[7]) {
            verySmall.draw(batch, "off", 590, 480); }
    }

    public void draw10sectors() {
        if(!host.getActiveSectors()[0]) {
            verySmall.draw(batch, "off", 650, 490); }
        if(!host.getActiveSectors()[1]) {
            verySmall.draw(batch, "off", 690, 450); }
        if(!host.getActiveSectors()[2]) {
            verySmall.draw(batch, "off", 710, 410); }
        if(!host.getActiveSectors()[3]) {
            verySmall.draw(batch, "off", 690, 360); }
        if(!host.getActiveSectors()[4]) {
            verySmall.draw(batch, "off", 650, 330); }
        if(!host.getActiveSectors()[5]) {
            verySmall.draw(batch, "off", 590, 330); }
        if(!host.getActiveSectors()[6]) {
            verySmall.draw(batch, "off", 550, 360); }
        if(!host.getActiveSectors()[7]) {
            verySmall.draw(batch, "off", 530, 410); }
        if(!host.getActiveSectors()[8]) {
            verySmall.draw(batch, "off", 550, 450); }
        if(!host.getActiveSectors()[9]) {
            verySmall.draw(batch, "off", 590, 490); }
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
