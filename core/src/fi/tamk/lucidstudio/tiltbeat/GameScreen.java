package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Jaakko on 11.3.2018.
 */

public class GameScreen implements Screen {
    private GameMain host;
    private AssetManager manager;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private ShapeRenderer shapeRenderer;
    private Player player;
    private ArrayList<Note> song;
    private boolean paused;
    private boolean changeSettings;
    private boolean useShapeRenderer;
    private Texture pointTexture;
    private Texture holdTexture;
    private Texture slideTexture;
    private Vector3 touchPos;
    //private boolean loaded;

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
    private Button calibration;
    private Button secondSetting;
    private Button backToPauseMenu;
    private Button soundButton;
    private Texture soundOnTexture;
    private Texture soundOffTexture;
    private Texture square;
    private Texture tiltedSquare;
    private Texture sixside;
    private Texture eightside;
    private Texture tenside;
    private Texture pointerTexture;
    private Texture tickTexture;

    /**
     * Pelaajan kulmiosysteemin luokka
     */
    class Player {
        Polygon hitbox;
        ArrayList<Polygon> sectors;
        Texture texture;
        float radius;
        float[] vertices;
        Pointer pointer;
        Preferences prefs = Gdx.app.getPreferences("default");

        // Pelaajan osoittimen luokka
        class Pointer {
            Texture texture;
            Circle hitbox;
            float radius;
            float speed;
            float[] xSmoother;
            float[] ySmoother;
            int smoothIndex;
            int smoothingSamples;

            // Erillinen thread accelerometerin syötteen pehmennystä varten
            class inputSmoother implements Runnable {
                @Override
                public void run() {
                    while(true) {
                        xSmoother[smoothIndex % smoothingSamples] = Gdx.input.getAccelerometerY() - prefs.getFloat("zeroPointY");
                        if (isUseAccelerometerX()) {
                            ySmoother[smoothIndex % smoothingSamples] = -Gdx.input.getAccelerometerX() - prefs.getFloat("zeroPointX");
                        } else {
                            ySmoother[smoothIndex % smoothingSamples] = Gdx.input.getAccelerometerZ() - prefs.getFloat("zeroPointZ");
                        }
                        smoothIndex++;
                        try {
                            Thread.sleep(5);
                        } catch (Exception e) {
                        }
                    }
                }
            }

            public Pointer() {
                radius = 0.4f;
                speed = 8;
                texture = pointerTexture;
                hitbox = new Circle(host.getScreenWidth() / 2, host.getScreenHeight() / 2, radius);
                smoothingSamples = prefs.getInteger("smoothingSamples");
                xSmoother = new float[smoothingSamples];
                ySmoother = new float[smoothingSamples];
                smoothIndex = 0;
                (new Thread(new inputSmoother())).start();
            }

            public boolean isUseAccelerometerX() {
                return prefs.getBoolean("useAccelerometerX");
            }

            // Piirtometodi SpriteBatchille, käyttää kuvia
            public void draw(SpriteBatch batch) {
                batch.draw(texture, hitbox.x - hitbox.radius, hitbox.y - hitbox.radius, hitbox.radius * 2, hitbox.radius * 2);
            }

            // Piirtometodi ShapeRendererille, käyttää pisteitä / muotoja
            public void draw(ShapeRenderer shapeRenderer) {
                shapeRenderer.circle(hitbox.x, hitbox.y, hitbox.radius, 100);
            }

            public void resetSmoothing() {
                for (int i = 0; i < xSmoother.length; i++) {
                    xSmoother[i] = prefs.getFloat("zeroPointY");
                }
                for (int i = 0; i < ySmoother.length; i++) {
                    if (isUseAccelerometerX()) {
                        ySmoother[i] = prefs.getFloat("zeroPointX");
                    } else {
                        ySmoother[i] = prefs.getFloat("zeroPointZ");
                    }
                }
            }

            public void move(OrthographicCamera camera) {
                // Osoittimen ohjaus nuolinäppäimillä
                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT) && hitbox.x < host.getScreenWidth() / 2 + host.getPlayerInradius() - radius * 2.5f) {
                    hitbox.x += speed * Gdx.graphics.getDeltaTime();
                } else if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) && hitbox.x > host.getScreenWidth() / 2 - host.getPlayerInradius() + radius * 2.5f) {
                    hitbox.x -= speed * Gdx.graphics.getDeltaTime();
                } else if (Gdx.input.isKeyPressed(Input.Keys.DPAD_UP) && hitbox.y < host.getScreenHeight() / 2 + host.getPlayerInradius() - radius * 2.5f) {
                    hitbox.y += speed * Gdx.graphics.getDeltaTime();
                } else if (Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN) && hitbox.y > host.getScreenHeight() / 2 - host.getPlayerInradius() + radius * 2.5f) {
                    hitbox.y -= speed * Gdx.graphics.getDeltaTime();
                }
                // Osoittimen ohjaus kosketuksella / hiirellä
                if (Gdx.input.isTouched()) {
                    Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                    camera.unproject(touchPos);
                    if (Player.this.hitbox.contains(touchPos.x, touchPos.y)) {
                        hitbox.x = touchPos.x;
                        hitbox.y = touchPos.y;
                    }
                }
                // Osoittimen ohjaus accelerometrillä
                if ((Math.abs(prefs.getFloat("zeroPointZ") - Gdx.input.getAccelerometerZ()) > prefs.getFloat("accelerometerDeadzone") && !prefs.getBoolean("useAccelerometerX")) || (Math.abs(prefs.getFloat("zeroPointX") - Gdx.input.getAccelerometerZ()) > prefs.getFloat("accelerometerDeadzone") && prefs.getBoolean("useAccelerometerX")) || Math.abs(prefs.getFloat("zeroPointY") - Gdx.input.getAccelerometerY()) > prefs.getFloat("accelerometerDeadzone")) {
                    float avgX = 0;
                    float avgY = 0;

                    for (int i = 0; i < smoothingSamples; i++) {
                        avgX += xSmoother[i];
                        avgY += ySmoother[i];
                    }
                    avgX /= smoothingSamples;
                    avgY /= smoothingSamples;

                    hitbox.x = host.getScreenWidth() / 2 + avgX;
                    hitbox.y =  host.getScreenHeight() / 2 + avgY;
                    Vector2 vector = new Vector2(hitbox.x - host.getScreenWidth() / 2, hitbox.y - host.getScreenHeight() / 2);
                    if (vector.len() > host.getPlayerInradius() * 0.8f) {
                        vector.setLength(host.getPlayerInradius() * 0.8f);
                        hitbox.x = host.getScreenWidth() / 2 + vector.x;
                        hitbox.y = host.getScreenHeight() / 2 + vector.y;
                    }
                }
            }

            /**
             * Kertoo millä sektorilla pelaajan osoitin on, ensimmäisen sektorin indeksi on 0 ja numerot
             * kasvavat myötäpäivään. Jos osoitin ei ole millään sektorilla, palautetaan -1
             *
             * @return sektorin indeksi int-numerona
             */
            public int getSector() {
                for (Polygon sector : Player.this.sectors) {
                    if (sector.contains(hitbox.x, hitbox.y)) {
                        return Player.this.sectors.indexOf(sector);
                    }
                }
                return -1;
            }
        }

        public Player(int playerSides, float playerDiameter) throws IllegalArgumentException {
            this.radius = playerDiameter / 2;
            pointer = new Pointer();
            sectors = new ArrayList<Polygon>();

            // Pelaaja on 10-sivuinen
            if (playerSides == 10) {
                texture = tenside;
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
                // Kahdeksankulmion pisteet
            } else if (playerSides == 8) {
                texture = eightside;
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
                // Kuusikulmion pisteet
            } else if (playerSides == 6) {
                texture = sixside;
                vertices = new float[]{
                        0.5f, 1f,
                        0.933f, 0.75f,
                        0.933f, 0.25f,
                        0.5f, 0f,
                        0.067f, 0.25f,
                        0.067f, 0.75f
                };
            } else if (playerSides == 4) {
                if (prefs.getBoolean("tiltedSquare")) {
                    texture = tiltedSquare;
                    vertices = new float[]{
                            0.5f, 1f,
                            1f, 0.5f,
                            0.5f, 0f,
                            0f, 0.5f
                    };
                } else {
                    texture = square;
                    vertices = new float[]{
                            1f, 1f,
                            1f, 0f,
                            0f, 0f,
                            0f, 1f
                    };
                }
            }
            else {
                throw new IllegalArgumentException("Invalid number of playerSides");
            }
            // Sektorien muodostus
            for (int i = 0; i < playerSides; i++) {
                float[] triangleVerts = {
                        vertices[i * 2], vertices[i * 2 + 1],
                        vertices[(i * 2 + 2) % (playerSides * 2)], vertices[(i * 2 + 3) % (playerSides * 2)],
                        0.5f, 0.5f
                };
                sectors.add(new Polygon(triangleVerts));
                // Asetetaan sektorien koko ja sijainti oikeiksi
                sectors.get(i).setScale(playerDiameter, playerDiameter);
                sectors.get(i).setPosition(host.getScreenWidth() / 2 - radius, host.getScreenHeight() / 2 - radius);
                // asetetaan sektori aktiiviseksi
            }
            // Muodostetaan pelaajan kulmio, tehdään siitä oikean kokoinen ja siirretään se keskelle ruutua
            hitbox = new Polygon(vertices);
            hitbox.setScale(playerDiameter, playerDiameter);
            hitbox.setPosition(host.getScreenWidth() / 2 - radius, host.getScreenHeight() / 2 - radius);

        }

        // Palauttaa kulmion pisteet
        public float[] getVertices() {
            return hitbox.getTransformedVertices();
        }

        // Palauttaa annetun sektorin pisteet
        public float[] getSectorVertices(int sector) {
            return sectors.get(sector).getTransformedVertices();
        }

        // Palauttaa pelaajan kulmion x-koordinaatin
        public float getX() {
            return hitbox.getX();
        }

        // Palauttaa pelaajan kulmion y-koordinaatin
        public float getY() {
            return hitbox.getY();
        }

        // Palauttaa pelaajan x-skaalan
        public float getScaleX() {
            return hitbox.getScaleX();
        }

        // Palauttaa pelaajan y-skaalan
        public float getScaleY() {
            return hitbox.getScaleY();
        }

        // Piirtometodi SpriteBatchille, käyttää kuvia
        public void draw(SpriteBatch batch) {
            batch.draw(texture, hitbox.getX(), hitbox.getY(), hitbox.getScaleX(), hitbox.getScaleY());
            //pointer.draw(batch);
        }

        // Piirtometodi ShapeRendererille, käyttää pisteitä / muotoja
        public void draw(ShapeRenderer shapeRenderer) {
            //shapeRenderer.polygon(getVertices());
            if (getPointerSector() > -1) {
                float[] verts = getSectorVertices(getPointerSector());
                shapeRenderer.triangle(verts[0], verts[1], verts[2], verts[3], verts[4], verts[5]);
            }
            //pointer.draw(shapeRenderer);
        }

        public void move(OrthographicCamera camera) {
            pointer.move(camera);
        }

        public int getPointerSector() {
            return pointer.getSector();
        }

        //public static void setActiveSectors(int a) { activeSectors[a] ^= true; }
    }

    abstract class Note {
        private int sector;
        private float distance;
        private boolean hit;
        Preferences prefs = Gdx.app.getPreferences("default");

        public Note(int sector, float distance) {
            this.sector = sector;
            this.distance = distance;
            hit = false;
        }

        public int getSector() {
            return sector;
        }

        public void setSector(int sector) {
            this.sector = sector;
        }

        public float getDistance() {
            return distance;
        }

        public void setDistance(float distance) {
            this.distance = distance;
        }

        public boolean isHit() {
            return hit;
        }

        public void setHit(boolean hit) {
            this.hit = hit;
        }

        public void move(float noteSpeed) {
            if (!isHit()) {
                distance -= noteSpeed * Gdx.graphics.getDeltaTime();
            }
        }

        abstract void draw(SpriteBatch batch);
    }

    class Point extends Note {
        private Texture texture;
        private TextureAtlas atlas;
        private TextureAtlas particleAtlas;
        private ParticleEffect effect;
        private Animation<TextureRegion> scoreAnimation;
        private Vector2 vector;
        private float width;
        private float height;
        private boolean flipped;
        private float stateTime;
        private float animationSize;


        public Point(int sector, float distance, Texture texture) {
            super(sector, distance);
            this.texture = pointTexture;
            atlas = new TextureAtlas("Blue Sprite.atlas");
            scoreAnimation = new Animation<TextureRegion>(0.05f, atlas.getRegions());
            particleAtlas = new TextureAtlas("Nuotteja.atlas");
            effect = new ParticleEffect();
            effect.load(Gdx.files.internal("Testi"), particleAtlas);
            effect.scaleEffect(1/80f);
            width = 1;
            height = (float) 0.7 * width;
            animationSize = 1.1f;
            vector = new Vector2(distance, 0);
            flipped = false;
            stateTime = 0;
        }

        @Override
        public void setHit(boolean hit) {
            super.setHit(hit);
            if (hit) {
                effect.start();
            }
        }

        public boolean isAnimationFinished() {
            if (scoreAnimation.isAnimationFinished(stateTime) && effect.isComplete()) {
                return true;
            } else {
                return false;
            }
        }

        public Vector2 getVector() {
            vector.setLength(getDistance() + host.getPlayerInradius());
            vector.setAngle(90 - (360 / prefs.getInteger("playerSides")) * getSector() - (360 / prefs.getInteger("playerSides")) / 2);
            return vector;
        }

        // Kääntää nuotin kuvan vaakasuunnassa ympäri
        public void flip() {
            if (flipped) {
                flipped = false;
            } else {
                flipped = true;
            }
        }

        @Override
        public void draw(SpriteBatch batch) {
            // Vektorilla lasketaan pelaajan kulmion kulmien perusteella nuottien liikerata kohti niiden
            // sektoreita
            vector.setLength(getDistance() + host.getPlayerInradius());
            vector.setAngle(90 - (360 / prefs.getInteger("playerSides")) * getSector() - (360 / prefs.getInteger("playerSides")) / 2);
            if (!isHit()) {
                batch.draw(texture, host.getScreenWidth() / 2 + vector.x - width / 2, host.getScreenHeight() / 2 + vector.y - height / 2, width / 2, height / 2, width, height, 1, 1, vector.angle() - 90, 0, 0, texture.getWidth(), texture.getHeight(), flipped, false);
            } else {
                if (!scoreAnimation.isAnimationFinished(stateTime)) {
                    TextureRegion keyframe = new TextureRegion(scoreAnimation.getKeyFrame(stateTime));
                    batch.draw(keyframe, host.getScreenWidth() / 2 + vector.x - animationSize / 2, host.getScreenHeight() / 2 + vector.y - animationSize / 2, animationSize / 2, animationSize / 2, animationSize, animationSize, animationSize, animationSize, vector.angle(), false);
                }
                effect.setPosition(host.getScreenWidth() / 2 + vector.x, host.getScreenHeight() / 2 + vector.y);
                effect.draw(batch, Gdx.graphics.getDeltaTime());
                stateTime += Gdx.graphics.getDeltaTime();
            }

        }
        public void drawInBackground(SpriteBatch batch) {
            vector.setLength(getDistance() + host.getPlayerInradius());
            vector.setAngle(90 - (360 / prefs.getInteger("playerSides")) * getSector() - (360 / prefs.getInteger("playerSides")) / 2);
        }
    }

    class Hold extends Note {
        private Texture texture;
        private Vector2 startPoint;
        private Vector2 endPoint;
        private float length;
        private float width;
        private float height;
        private float tickDiameter;
        private int tickAmount;
        private ArrayList<Tick> ticks;
        private boolean scored;

        class Tick extends Note {
            private Texture texture;
            private Vector2 vector;
            private boolean scored;

            // Pikkupallerot Hold-nuottien välissä
            public Tick(int sector, float distance, Texture texture) {
                super(sector, distance);
                this.texture = tickTexture;
                vector = new Vector2(distance, 0);
                scored = false;
            }

            public boolean isScored() {
                return scored;
            }

            public void setScored(boolean scored) {
                this.scored = scored;
            }

            @Override
            public void draw(SpriteBatch batch) {
                vector.setLength(getDistance() + host.getPlayerInradius());
                vector.setAngle(90 - (360 / prefs.getInteger("playerSides")) * getSector() - (360 / prefs.getInteger("playerSides")) / 2);
                if (getDistance() > 0)
                    batch.draw(texture, host.getScreenWidth() / 2 + vector.x - tickDiameter / 2, host.getScreenHeight() / 2 + vector.y - tickDiameter / 2, tickDiameter, tickDiameter);
            }
        }

        public Hold(int sector, float distance, Texture texture, float length) {
            super(sector, distance);
            this.texture = holdTexture;
            this.length = length;
            width = 1;
            tickDiameter = 0.2f;
            height = 0.7f * width;
            tickAmount = (int) (length / (tickDiameter * prefs.getFloat("noteSpeed")));
            if (prefs.getString("difficulty").equals("normal")) {
                tickAmount *= 2;
            } else if (prefs.getString("difficulty").equals("hard")) {
                tickAmount *= 3;
            } else if (prefs.getString("difficulty").equals("backbreaker")) {
                tickAmount *= 5;
            }
            tickAmount -= 2;
            startPoint = new Vector2(distance, 0);
            endPoint = new Vector2(distance + length, 0);
            ticks = new ArrayList<Tick>();
            for (int i = 2; i < tickAmount - 1; i++) {
                ticks.add(new Tick(sector, (distance + (float) i / tickAmount * length), tickTexture));
            }

        }

        public boolean isScored() {
            return scored;
        }

        public void setScored(boolean scored) {
            this.scored = scored;
        }

        public float getLength() {
            return length;
        }

        public ArrayList<Tick> getTicks() {
            return ticks;
        }

        @Override
        public void draw(SpriteBatch batch) {
            // Vektorilla lasketaan pelaajan kulmion kulmien perusteella nuottien liikerata kohti niiden
            // sektoreita
            for (Tick tick : ticks) {
                tick.draw(batch);
            }
            startPoint.setLength(getDistance() + host.getPlayerInradius());
            startPoint.setAngle(90 - (360 / prefs.getInteger("playerSides")) * getSector() - (360 / prefs.getInteger("playerSides")) / 2);
            if (getDistance() > 0) {
                batch.draw(texture, host.getScreenWidth() / 2 + startPoint.x - width / 2, host.getScreenHeight() / 2 + startPoint.y - height / 2, width / 2, height / 2, width, height, 1, 1, startPoint.angle() - 90, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
            }
            endPoint.setLength(getDistance() + length + host.getPlayerInradius());
            endPoint.setAngle(90 - (360 / prefs.getInteger("playerSides")) * getSector() - (360 / prefs.getInteger("playerSides")) / 2);
            batch.draw(texture, host.getScreenWidth() / 2 + endPoint.x - width / 2, host.getScreenHeight() / 2 + endPoint.y - height / 2, width / 2, height / 2, width, height, 1, 1, endPoint.angle() - 90, 0, 0, texture.getWidth(), texture.getHeight(), false, true);
        }

        @Override
        public void move(float noteSpeed) {
            super.move(noteSpeed);
            for (Tick tick : ticks) {
                tick.move(noteSpeed);
            }
        }
    }

    class Slide extends Note {
        private ArrayList<Point> notes;

        public Slide(int sector, float distance, ArrayList<Point> notes) {
            super(sector, distance);
            // Muutetaan annetun nuottikasan sektorit ja etäisyys koko Sliden mukaan
            for (int i = 0; i < notes.size(); i++) {
                Point note = notes.get(i);
                note.setSector((note.getSector() + sector) % prefs.getInteger("playerSides"));
                note.setDistance(note.getDistance() + distance);
            }
            this.notes = notes;
        }

        public ArrayList<Point> getNotes() {
            return notes;
        }

        public void draw(ShapeRenderer shapeRenderer) {
            for (int i = 0; i < notes.size() - 1; i++) {
                // Piirretään ShapeRendererillä viiva Sliden sisällä olevien nuottien väliin
                Vector2 startPoint = (notes.get(i).getVector().add(new Vector2(host.getScreenWidth() / 2, host.getScreenHeight() / 2)));
                Vector2 endPoint = (notes.get(i + 1).getVector().add(new Vector2(host.getScreenWidth() / 2, host.getScreenHeight() / 2)));
                shapeRenderer.line(startPoint, endPoint);
            }
        }

        public void draw(SpriteBatch batch) {
            for (int i = 0; i < notes.size(); i++) {
                notes.get(i).draw(batch);
            }
        }

        @Override
        public void move(float noteSpeed) {
            super.move(noteSpeed);
            for (Point note : notes) {
                note.move(noteSpeed);
            }
        }
    }

    /**
     * Peliruutu, asetukset haetaan GameMainistä, joka toimii "hostina"
     * @param host pelin Main-metodi
     */
    public GameScreen(GameMain host) {
        // Katotaan jos toimii purkkakorjauksena ettei heti alussa skippaa eteenpäi
        paused = true;
        changeSettings = false;
        this.host = host;
        manager = host.getManager();
        batch = host.getBatch();
        camera = host.getCamera();
        fontCamera = host.getFontCamera();
        shapeRenderer = host.getShapeRenderer();
        touchPos = new Vector3();
        //loaded = false;

        manager.load("Galaxy dark purple.png", Texture.class);
        manager.load("soundOn.png", Texture.class);
        manager.load("soundOff.png", Texture.class);
        manager.load("Smol Blue.png", Texture.class);
        manager.load("Smol Blue Hold.png", Texture.class);
        manager.load("Smol Blue Slide.png", Texture.class);
        manager.load("Smol Blue Ball.png", Texture.class);
        manager.load("Smol Green.png", Texture.class);
        manager.load("Smol Green Hold.png", Texture.class);
        manager.load("Smol Green Slide.png", Texture.class);
        manager.load("Smol Green Ball.png", Texture.class);
        manager.load("Smol Pink.png", Texture.class);
        manager.load("Smol Pink Hold.png", Texture.class);
        manager.load("Smol Pink Slide.png", Texture.class);
        manager.load("Smol Pink Ball.png", Texture.class);
        manager.load("Smol Yellow.png", Texture.class);
        manager.load("Smol Yellow Hold.png", Texture.class);
        manager.load("Smol Yellow Slide.png", Texture.class);
        manager.load("Smol Yellow Ball.png", Texture.class);
        manager.load("square.png", Texture.class);
        manager.load("tiltedSquare.png", Texture.class);
        manager.load("sixside.png", Texture.class);
        manager.load("eightside.png", Texture.class);
        manager.load("tenside.png", Texture.class);
        manager.load("pointer.png", Texture.class);

        manager.finishLoading();
        //host.setScreen(new LoadingScreen(host, this));

        background = manager.get("Galaxy dark purple.png");
        soundOnTexture = manager.get("soundOn.png");
        soundOffTexture = manager.get("soundOff.png");
        pointTexture = manager.get("Smol Blue.png");
        holdTexture = manager.get("Smol Blue Hold.png");
        slideTexture = manager.get("Smol Blue Slide.png");
        pointerTexture = manager.get("pointer.png");
        square = manager.get("square.png");
        tiltedSquare = manager.get("tiltedSquare.png");
        sixside = manager.get("sixside.png");
        eightside = manager.get("eightside.png");
        tenside = manager.get("tenside.png");
        tickTexture = manager.get("Smol Blue Ball.png");

        playerSides = host.getPlayerSides();
        playerDiameter = host.getPlayerDiameter();
        player = new Player(playerSides, playerDiameter);

        jauntyGumption = host.getSong();
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


        for (int i = 0; i < totalBeats - startOffset ; i++) {
            int noteColor = MathUtils.random(0, 3);
            createPointTexture(noteColor);
            int randomSector = MathUtils.random(0, (playerSides-1));
            randomSector = moveNotes(randomSector);
            //jos ei järjestelmällinen siirtäminen toimi (koska uusi sektori myös passiivinen) niin arvotaan uusi paikka
            while (!isSectorActive(randomSector)) {
                randomSector = MathUtils.random(0, (playerSides-1));
            }
            int randomNoteType = MathUtils.random(0, 7);
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
        verySmall = host.getVerySmallFont();
        basic = host.getBasicFont();
        heading = host.getHeadingFont();

        pauseButton = new Button(0.2f, 8.3f, 1.5f, 1.5f, host.getPauseButtonTexture());
        playButton = new Button(18f, 18f, 1.5f, 1.5f, host.getPlayButtonTexture());
        playAgainButton = new Button(18f, 18f, 1.5f, 1.5f, host.getPlayAgainButtonTexture());
        backButton = new Button(18f, 18f, 1.5f, 1.5f, host.getBackButtonTexture());
        settingsButton = new Button(18f, 18f, 1.5f, 1.5f, host.getSettingsButtonTexture());
        calibration = new Button(18f, 18f, 4f, 2f, host.getButtonTexture());
        secondSetting = new Button(18f, 18f, 4f, 2f, host.getButtonTexture());
        backToPauseMenu = new Button(18f, 18f, 1.5f, 1.5f, host.getBackButtonTexture());
        soundButton = new Button(18f, 18f, 2f, 2f, soundOnTexture);
        if (!host.isSoundOn()) {
            soundButton.setTexture(soundOffTexture);
        }
        resultBox = new Button(2f, 1f, 12f, 8f, host.getTextBoxTexture());

        backButton.setText(0, 260, "main\nmenu", basic);
        playButton.setText(-30, 190, "continue", basic);
        playAgainButton.setText(0, 190, "retry", basic);
        settingsButton.setText(-30, 190, "settings", basic);
        calibration.setText(30, 110, "calibration", basic);
        secondSetting.setText(20, 110, "do something", basic);
        backToPauseMenu.setText(10, 180, "back", basic);

        paused = false;
        useShapeRenderer = true;
    }

    public void createPointTexture(int a) {
        if (a==0) {
            pointTexture = manager.get("Smol Blue.png");
            holdTexture = manager.get("Smol Blue Hold.png");
            slideTexture = manager.get("Smol Blue Slide.png");
            tickTexture = manager.get("Smol Blue Ball.png");
        }
        if (a==1) {
            pointTexture = manager.get("Smol Green.png");
            holdTexture = manager.get("Smol Green Hold.png");
            slideTexture = manager.get("Smol Green Slide.png");
            tickTexture = manager.get("Smol Green Ball.png");
        }
        if (a==2) {
            pointTexture = manager.get("Smol Pink.png");
            holdTexture = manager.get("Smol Pink Hold.png");
            slideTexture = manager.get("Smol Pink Slide.png");
            tickTexture = manager.get("Smol Pink Ball.png");
        }
        if (a==3) {
            pointTexture = manager.get("Smol Yellow.png");
            holdTexture = manager.get("Smol Yellow Hold.png");
            slideTexture = manager.get("Smol Yellow Slide.png");
            tickTexture = manager.get("Smol Yellow Ball.png");
        }
    }

    public void moveHerePauseMenuButtons() {
        playButton.setPosition(6f, 2f);
        playAgainButton.setPosition(8.5f, 2f);
        backButton.setPosition(3.5f, 2f);
        settingsButton.setPosition(11f, 2f);
    }

    public void moveAwayPauseMenuButtons() {
        playButton.setPosition(11f, 18f);
        playAgainButton.setPosition(11f, 18f);
        backButton.setPosition(11f, 18f);
        settingsButton.setPosition(11f, 18f);
    }

    public void moveHereSettingsButtons() {
        calibration.setPosition(5.7f, 4f);
        secondSetting.setPosition(5.7f, 2f);
        backToPauseMenu.setPosition(3.5f, 2f);
        soundButton.setPosition(10.5f, 3.3f);
    }

    public void moveAwaySettingsButtons() {
        calibration.setPosition(11f, 18f);
        secondSetting.setPosition(11f, 18f);
        backToPauseMenu.setPosition(11f, 18f);
        soundButton.setPosition(11f, 18f);
    }

    public void moveHereResultMenuButtons() {
        playAgainButton.setPosition(8f, 2f);
        backButton.setPosition(3f, 2f);
    }

    public void destroyResultMenuButtons() {
        playButton = new Button(11f, 18f, .1f, .1f, host.getPlayButtonTexture());
        playAgainButton = new Button(11f, 18f, .1f, .1f, host.getPlayAgainButtonTexture());
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
        /*if (!loaded) {
            host.setScreen(new LoadingScreen(host, this));
            loaded = true;
        }*/
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
        if (paused && !song.isEmpty() && !changeSettings) {
            resultBox.draw(batch);
            playButton.draw(batch);
            playAgainButton.draw(batch);
            backButton.draw(batch);
            settingsButton.draw(batch);
        }
        //tulosruutu
        if (song.isEmpty()) {
            moveHereResultMenuButtons();
            resultBox.draw(batch);
            backButton.draw(batch);
            playAgainButton.draw(batch);
        }
        //asetusruutu
        if(changeSettings) {
            resultBox.draw(batch);
            backToPauseMenu.draw(batch);
            calibration.draw(batch);
            secondSetting.draw(batch);
            soundButton.draw(batch);
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
        basic.draw(batch, " Z: " + Gdx.input.getAccelerometerZ(), 50, 200);
        basic.draw(batch, "use accelerometer X: " + host.getPrefs().getBoolean("useAccelerometerX"), 50, 350);*/

        //pauseruudun tekstit
        if (paused && !song.isEmpty() && !changeSettings) {
            heading.draw(batch, "PAUSE", 400, 600);
            backButton.drawText(batch);
            playButton.drawText(batch);
            playAgainButton.drawText(batch);
            settingsButton.drawText(batch);
        }
        //tulosruudun tekstit
        if (song.isEmpty()) {
            heading.draw(batch, "you did it!!!", 230, 620);
            basic.draw(batch, "you got " + points + " points!!", 410, 400);
            backButton.setText(140, 90, "main menu", basic);
            playAgainButton.setText(140, 90, "start again?", basic);
            backButton.drawText(batch);
            playAgainButton.drawText(batch);
        }
        //asetusruudun tekstit
        if (changeSettings) {
            heading.draw(batch, "Settings", 320, 630);
            calibration.drawText(batch);
            secondSetting.drawText(batch);
            backToPauseMenu.drawText(batch);
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
                //shapeRenderer.line(host.getScreenWidth() / 2, host.getScreenHeight() / 2, 0, Gdx.input.getAccelerometerY() + host.getScreenWidth() / 2, -Gdx.input.getAccelerometerX() + host.getScreenHeight() / 2, -Math.abs(Gdx.input.getAccelerometerZ()));
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
                moveHerePauseMenuButtons();
            }
            if (playButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
                paused = false;
                moveAwayPauseMenuButtons();
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
                changeSettings = true;
                moveAwayPauseMenuButtons();
                moveHereSettingsButtons();
            }
            if (calibration.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
                //kalibroi tässä
                host.calibrateZeroPoint();
                //player.pointer.resetSmoothing();
            }
            if (secondSetting.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
                //toinen asetusjuttu
            }
            if (soundButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
                jauntyGumption.stop();
                if (host.isSoundOn()) {
                    host.setSoundOn(false);
                    soundButton.setTexture(soundOffTexture);
                } else {
                    host.setSoundOn(true);
                    soundButton.setTexture(soundOnTexture);
                }
            }
            if (backToPauseMenu.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
                changeSettings = false;
                moveAwaySettingsButtons();
                moveHerePauseMenuButtons();
            }
        //ottaa napin painalluksen vain kerran
        if (!Gdx.input.isTouched()) {touchPos.set(0, 0, 0);}

        if(song.isEmpty()) {
            jauntyGumption.stop();
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
    }

    @Override
    public void dispose() {

    }
}
