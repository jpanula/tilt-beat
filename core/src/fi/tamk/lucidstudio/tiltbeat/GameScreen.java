package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.utils.CharArray;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Jaakko on 11.3.2018.
 */

public class GameScreen implements Screen {
    private GameMain host;
    private AssetManager manager;
    private SpriteBatch batch;
    private Preferences prefs;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private ShapeRenderer shapeRenderer;
    private Player player;
    private ArrayList<Note> song;
    private boolean paused;
    private boolean changeSettings;
    private boolean addHighscore;
    private boolean useShapeRenderer;
    private String[] highscoreNames;
    private int[] highScores;
    private Vector3 touchPos;
    //private boolean loaded;

    // Musiikki ja bpm
    private Sound soundEffect;
    private Music jauntyGumption;
    private float bpm;
    // Pituus minuuteissa
    private float musicLength;
    private int totalBeats;
    private int startOffset;

    private int playerSides;
    private float playerDiameter;
    private float noteSpeed;

    private char[] list;
    private int points;
    private int pointMulti;
    private BitmapFont verySmall;
    private BitmapFont small;
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
    private Button highscoreButton;
    private Texture soundOnTexture;
    private Texture soundOffTexture;
    private Texture square;
    private Texture tiltedSquare;
    private Texture sixside;
    private Texture eightside;
    private Texture tenside;
    private Texture pointerTexture;

    private Texture bluePointTexture;
    private Texture greenPointTexture;
    private Texture yellowPointTexture;
    private Texture pinkPointTexture;
    private Texture redPointTexture;

    private Texture blueHoldTexture;
    private Texture greenHoldTexture;
    private Texture yellowHoldTexture;
    private Texture pinkHoldTexture;
    private Texture redHoldTexture;

    private Texture blueSlideTexture;
    private Texture greenSlideTexture;
    private Texture yellowSlideTexture;
    private Texture pinkSlideTexture;
    private Texture redSlideTexture;

    private Texture blueTickTexture;
    private Texture greenTickTexture;
    private Texture yellowTickTexture;
    private Texture pinkTickTexture;
    private Texture redTickTexture;

    private Animation<TextureRegion> blueHitAnimation;
    private Animation<TextureRegion> greenHitAnimation;
    private Animation<TextureRegion> yellowHitAnimation;
    private Animation<TextureRegion> pinkHitAnimation;
    private Animation<TextureRegion> redHitAnimation;
    private Animation<TextureRegion> blueTickAnimation;
    private Animation<TextureRegion> greenTickAnimation;
    private Animation<TextureRegion> yellowTickAnimation;
    private Animation<TextureRegion> pinkTickAnimation;
    private Animation<TextureRegion> redTickAnimation;

    private ParticleEffect blueParticleEffect;
    private ParticleEffect greenParticleEffect;
    private ParticleEffect yellowParticleEffect;
    private ParticleEffect pinkParticleEffect;
    private ParticleEffect redParticleEffect;

    private TextureAtlas blueHitAnimationAtlas;
    private TextureAtlas greenHitAnimationAtlas;
    private TextureAtlas yellowHitAnimationAtlas;
    private TextureAtlas pinkHitAnimationAtlas;
    private TextureAtlas redHitAnimationAtlas;
    private TextureAtlas blueTickAnimationAtlas;
    private TextureAtlas greenTickAnimationAtlas;
    private TextureAtlas yellowTickAnimationAtlas;
    private TextureAtlas pinkTickAnimationAtlas;
    private TextureAtlas redTickAnimationAtlas;

    private TextureAtlas blueParticleEffectAtlas;
    private TextureAtlas greenParticleEffectAtlas;
    private TextureAtlas yellowParticleEffectAtlas;
    private TextureAtlas pinkParticleEffectAtlas;
    private TextureAtlas redParticleEffectAtlas;

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
                        xSmoother[smoothIndex % smoothingSamples] = Gdx.input.getAccelerometerY() - host.getZeroPointY();
                        if (host.isUseAccelerometerX()) {
                            ySmoother[smoothIndex % smoothingSamples] = -Gdx.input.getAccelerometerX() - host.getZeroPointX();
                        } else {
                            ySmoother[smoothIndex % smoothingSamples] = Gdx.input.getAccelerometerZ() - host.getZeroPointZ();
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
                smoothingSamples = host.getSmoothingSamples();
                xSmoother = new float[smoothingSamples];
                ySmoother = new float[smoothingSamples];
                smoothIndex = 0;
                (new Thread(new inputSmoother())).start();
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
                    xSmoother[i] = host.getZeroPointY();
                }
                for (int i = 0; i < ySmoother.length; i++) {
                    if (host.isUseAccelerometerX()) {
                        ySmoother[i] = host.getZeroPointX();
                    } else {
                        ySmoother[i] = host.getZeroPointZ();
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
                if ((Math.abs(host.getZeroPointZ() - Gdx.input.getAccelerometerZ()) > host.getAccelerometerDeadzone() && !host.isUseAccelerometerX()) || (Math.abs(host.getZeroPointX() - Gdx.input.getAccelerometerZ()) > host.getAccelerometerDeadzone() && host.isUseAccelerometerX()) || Math.abs(host.getZeroPointY() - Gdx.input.getAccelerometerY()) > host.getAccelerometerDeadzone()) {
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
                if (host.isTiltedSquare()) {
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
        private Texture texture;
        private ParticleEffect effect;
        private Animation<TextureRegion> scoreAnimation;
        private float stateTime;
        private float animationSize;

        public Note(int sector, float distance, String color) {
            this.sector = sector;
            this.distance = distance;
            this.effect = new ParticleEffect();
            this.stateTime = 0;
            this.animationSize = 1f;
            setColor(color);
            hit = false;
        }

        public float getStateTime() {
            return stateTime;
        }

        public void setStateTime(float stateTime) {
            this.stateTime = stateTime;
        }

        public float getAnimationSize() {
            return animationSize;
        }

        public void setAnimationSize(float animationSize) {
            this.animationSize = animationSize;
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
            if (hit) {
                getEffect().start();
            }
        }

        public void setTexture(Texture texture) {
            this.texture = texture;
        }

        public Texture getTexture() {
            return texture;
        }

        public ParticleEffect getEffect() {
            return effect;
        }

        public Animation<TextureRegion> getScoreAnimation() {
            return scoreAnimation;
        }

        public void move(float noteSpeed) {
            if (!isHit()) {
                distance -= noteSpeed * Gdx.graphics.getDeltaTime();
            }
        }

        public boolean isAnimationFinished() {
            if (getScoreAnimation().isAnimationFinished(getStateTime()) && getEffect().isComplete()) {
                return true;
            } else {
                return false;
            }
        }

        public void setColor(String color) {
            color = color.toLowerCase();
            if (color.equals("blue")) {
                if (this instanceof  Point) {
                    this.texture = bluePointTexture;
                } else if (this instanceof Hold) {
                    this.texture = blueHoldTexture;
                } else if (this instanceof Hold.Tick) {
                    this.texture = blueTickTexture;
                }
                if (this instanceof Hold.Tick) {
                    this.scoreAnimation = blueTickAnimation;
                } else {
                    this.scoreAnimation = blueHitAnimation;
                }
                this.effect.load(Gdx.files.internal("SininenEfekti"), blueParticleEffectAtlas);
                this.effect.scaleEffect(1/80f);

            } else if (color.equals("green")) {
                if (this instanceof  Point) {
                    this.texture = greenPointTexture;
                } else if (this instanceof Hold) {
                    this.texture = greenHoldTexture;
                } else if (this instanceof Hold.Tick) {
                    this.texture = greenTickTexture;
                }
                if (this instanceof Hold.Tick) {
                    this.scoreAnimation = greenTickAnimation;
                } else {
                    this.scoreAnimation = greenHitAnimation;
                }
                this.effect.load(Gdx.files.internal("VihreäEfekti"), greenParticleEffectAtlas);
                this.effect.scaleEffect(1/80f);

            } else if (color.equals("yellow")) {
                if (this instanceof  Point) {
                    this.texture = yellowPointTexture;
                } else if (this instanceof Hold) {
                    this.texture = yellowHoldTexture;
                } else if (this instanceof Hold.Tick) {
                    this.texture = yellowTickTexture;
                }
                if (this instanceof Hold.Tick) {
                    this.scoreAnimation = yellowTickAnimation;
                } else {
                    this.scoreAnimation = yellowHitAnimation;
                }
                this.effect.load(Gdx.files.internal("KeltainenEfekti"), yellowParticleEffectAtlas);
                this.effect.scaleEffect(1/80f);

            } else if (color.equals("red")){
                if (this instanceof  Point) {
                    this.texture = redPointTexture;
                } else if (this instanceof Hold) {
                    this.texture = redHoldTexture;
                } else if (this instanceof Hold.Tick) {
                    this.texture = redTickTexture;
                }
                if (this instanceof Hold.Tick) {
                    this.scoreAnimation = redTickAnimation;
                } else {
                    this.scoreAnimation = redHitAnimation;
                }
                this.effect.load(Gdx.files.internal("PunainenEfekti"), redParticleEffectAtlas);
                this.effect.scaleEffect(1/80f);

            } else if (color.equals("pink")) {
                if (this instanceof  Point) {
                    this.texture = pinkPointTexture;
                } else if (this instanceof Hold) {
                    this.texture = pinkHoldTexture;
                } else if (this instanceof Hold.Tick) {
                    this.texture = pinkTickTexture;
                }
                if (this instanceof Hold.Tick) {
                    this.scoreAnimation = pinkTickAnimation;
                } else {
                    this.scoreAnimation = pinkHitAnimation;
                }
                this.effect.load(Gdx.files.internal("PinkkiEfekti"), pinkParticleEffectAtlas);
                this.effect.scaleEffect(1/80f);

            } else {
                throw new IllegalArgumentException("Invalid color");
            }
        }

        abstract void draw(SpriteBatch batch);
    }

    class Point extends Note {
        private Vector2 vector;
        private float width;
        private float height;
        private boolean flipped;


        public Point(int sector, float distance, String color) {
            super(sector, distance, color);
            width = 1;
            height = (float) 0.7 * width;
            setAnimationSize(1.1f);
            vector = new Vector2(distance, 0);
            flipped = false;
            setStateTime(0);
        }

        public Vector2 getVector() {
            vector.setLength(getDistance() + host.getPlayerInradius());
            vector.setAngle(90 - (360 / host.getPlayerSides()) * getSector() - (360 / host.getPlayerSides()) / 2);
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
            vector.setAngle(90 - (360 / host.getPlayerSides()) * getSector() - (360 / host.getPlayerSides()) / 2);
            if (!host.isTiltedSquare() && host.getPlayerSides() == 4) {
                vector.setAngle(vector.angle() - 45);
                vector.setLength(getDistance() + host.getPlayerDiameter() / 2);
            }
            if (!isHit()) {
                batch.draw(getTexture(), host.getScreenWidth() / 2 + vector.x - width / 2, host.getScreenHeight() / 2 + vector.y - height / 2, width / 2, height / 2, width, height, 1, 1, vector.angle() - 90, 0, 0, getTexture().getWidth(), getTexture().getHeight(), flipped, false);
            } else {
                if (!getScoreAnimation().isAnimationFinished(getStateTime())) {
                    TextureRegion keyframe = new TextureRegion(getScoreAnimation().getKeyFrame(getStateTime()));
                    batch.draw(keyframe, host.getScreenWidth() / 2 + vector.x - getAnimationSize() / 2, host.getScreenHeight() / 2 + vector.y - getAnimationSize() / 2, getAnimationSize() / 2, getAnimationSize() / 2, getAnimationSize(), getAnimationSize(), getAnimationSize(), getAnimationSize(), vector.angle(), false);
                }
                getEffect().setPosition(host.getScreenWidth() / 2 + vector.x, host.getScreenHeight() / 2 + vector.y);
                getEffect().draw(batch, Gdx.graphics.getDeltaTime());
                setStateTime(getStateTime() + Gdx.graphics.getDeltaTime());
            }

        }
        public void drawInBackground(SpriteBatch batch) {
            vector.setLength(getDistance() + host.getPlayerInradius());
            vector.setAngle(90 - (360 / host.getPlayerSides()) * getSector() - (360 / host.getPlayerSides()) / 2);
        }
    }

    class Hold extends Note {
        private Vector2 startPoint;
        private Vector2 endPoint;
        private float length;
        private float width;
        private float height;
        private float tickDiameter;
        private int tickAmount;
        private ArrayList<Tick> ticks;
        private boolean scored;
        private float endPointStateTime;
        private boolean startPointHit;

        class Tick extends Note {
            private Vector2 vector;
            private boolean scored;

            // Pikkupallerot Hold-nuottien välissä
            public Tick(int sector, float distance, String color) {
                super(sector, distance, color);
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
                vector.setAngle(90 - (360 / host.getPlayerSides()) * getSector() - (360 / host.getPlayerSides()) / 2);
                if (!host.isTiltedSquare() && host.getPlayerSides() == 4) {
                    vector.setAngle(vector.angle() - 45);
                    vector.setLength(getDistance() + host.getPlayerDiameter() / 2);
                }
                if (!isHit()) {
                    batch.draw(getTexture(), host.getScreenWidth() / 2 + vector.x - tickDiameter / 2, host.getScreenHeight() / 2 + vector.y - tickDiameter / 2, tickDiameter, tickDiameter);
                } else if (!getScoreAnimation().isAnimationFinished(getStateTime())) {
                    TextureRegion keyframe = new TextureRegion(getScoreAnimation().getKeyFrame(getStateTime()));
                    batch.draw(keyframe, host.getScreenWidth() / 2 + vector.x - getAnimationSize() / 2, host.getScreenHeight() / 2 + vector.y - getAnimationSize() / 2, getAnimationSize() / 2, getAnimationSize() / 2, getAnimationSize(), getAnimationSize(), getAnimationSize(), getAnimationSize(), vector.angle(), false);
                    setStateTime(getStateTime() + Gdx.graphics.getDeltaTime());
                }

            }
        }

        public Hold(int sector, float distance, float length, String color) {
            super(sector, distance, color);
            this.startPointHit = false;
            this.length = length;
            setAnimationSize(1.1f);
            width = 1;
            tickDiameter = 0.2f;
            height = 0.7f * width;
            tickAmount = (int) (length / (tickDiameter * host.getNoteSpeed()));
            if (host.getDifficulty().equals("normal")) {
                tickAmount *= 2;
            } else if (host.getDifficulty().equals("hard")) {
                tickAmount *= 3;
            } else if (host.getDifficulty().equals("backbreaker")) {
                tickAmount *= 5;
            }
            tickAmount -= 2;
            startPoint = new Vector2(distance, 0);
            endPoint = new Vector2(distance + length, 0);
            ticks = new ArrayList<Tick>();
            for (int i = 2; i < tickAmount - 1; i++) {
                ticks.add(new Tick(sector, (distance + (float) i / tickAmount * length), color));
            }
            endPointStateTime = 0;
        }

        public boolean isStartPointHit() {
            return startPointHit;
        }

        public void setStartPointHit(boolean startPointHit) {
            this.startPointHit = startPointHit;
        }

        @Override
        public boolean isAnimationFinished() {
            if (getScoreAnimation().isAnimationFinished(endPointStateTime) && getEffect().isComplete()) {
                return true;
            } else {
                return false;
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
            if (!isStartPointHit()) {
                startPoint.setLength(getDistance() + host.getPlayerInradius());
            }
            startPoint.setAngle(90 - (360 / host.getPlayerSides()) * getSector() - (360 / host.getPlayerSides()) / 2);
            if (!host.isTiltedSquare() && host.getPlayerSides() == 4) {
                if (!isStartPointHit()) {
                    startPoint.setAngle(startPoint.angle() - 45);
                }
                startPoint.setLength(getDistance() + host.getPlayerDiameter() / 2);
            }
            if (!isStartPointHit()) {
                if (getDistance() > 0){
                    batch.draw(getTexture(), host.getScreenWidth() / 2 + startPoint.x - width / 2, host.getScreenHeight() / 2 + startPoint.y - height / 2, width / 2, height / 2, width, height, 1, 1, startPoint.angle() - 90, 0, 0, getTexture().getWidth(), getTexture().getHeight(), false, false);
                }
            } else {
                if (!getScoreAnimation().isAnimationFinished(getStateTime())) {
                    TextureRegion keyframe = new TextureRegion(getScoreAnimation().getKeyFrame(getStateTime()));
                    batch.draw(keyframe, host.getScreenWidth() / 2 + startPoint.x - getAnimationSize() / 2, host.getScreenHeight() / 2 + startPoint.y - getAnimationSize() / 2, getAnimationSize() / 2, getAnimationSize() / 2, getAnimationSize(), getAnimationSize(), getAnimationSize(), getAnimationSize(), startPoint.angle(), false);
                }
                setStateTime(getStateTime() + Gdx.graphics.getDeltaTime());
            }
            endPoint.setLength(getDistance() + length + host.getPlayerInradius());
            endPoint.setAngle(90 - (360 / host.getPlayerSides()) * getSector() - (360 / host.getPlayerSides()) / 2);
            if (!host.isTiltedSquare() && host.getPlayerSides() == 4) {
                endPoint.setAngle(endPoint.angle() - 45);
                endPoint.setLength(getDistance() + host.getPlayerDiameter() / 2 + length);
            }
            if (!isHit()) {
                batch.draw(getTexture(), host.getScreenWidth() / 2 + endPoint.x - width / 2, host.getScreenHeight() / 2 + endPoint.y - height / 2, width / 2, height / 2, width, height, 1, 1, endPoint.angle() - 90, 0, 0, getTexture().getWidth(), getTexture().getHeight(), false, true);
            } else {
                if (!getScoreAnimation().isAnimationFinished(endPointStateTime)) {
                TextureRegion keyframe = new TextureRegion(getScoreAnimation().getKeyFrame(endPointStateTime));
                batch.draw(keyframe, host.getScreenWidth() / 2 + endPoint.x - getAnimationSize() / 2, host.getScreenHeight() / 2 + endPoint.y - getAnimationSize() / 2, getAnimationSize() / 2, getAnimationSize() / 2, getAnimationSize(), getAnimationSize(), getAnimationSize(), getAnimationSize(), endPoint.angle(), false);
                }
                getEffect().setPosition(host.getScreenWidth() / 2 + endPoint.x, host.getScreenHeight() / 2 + endPoint.y);
                getEffect().draw(batch, Gdx.graphics.getDeltaTime());
                endPointStateTime += Gdx.graphics.getDeltaTime();
            }
        }

        @Override
        public void move(float noteSpeed) {
            super.move(noteSpeed);
            for (Tick tick : ticks) {
                if (!tick.isHit()) {
                    tick.move(noteSpeed);
                }
            }
        }
    }

    class Slide extends Note {
        private ArrayList<Point> notes;

        public Slide(int sector, float distance, ArrayList<Point> notes, String color) {
            super(sector, distance, color);
            // Muutetaan annetun nuottikasan sektorit ja etäisyys koko Sliden mukaan
            for (int i = 0; i < notes.size(); i++) {
                Point note = notes.get(i);
                note.setColor(color);
                setPointColor(note, color);
                note.setSector((note.getSector() + sector) % host.getPlayerSides());
                note.setDistance(note.getDistance() + distance);
            }
            this.notes = notes;
        }
        
        public void setPointColor(Point note, String color) {
            color = color.toLowerCase();
            if (color.equals("blue")) {
                note.setTexture(blueSlideTexture);
            } else if (color.equals("green")) {
                note.setTexture(greenSlideTexture);
            } else if (color.equals("yellow")) {
                note.setTexture(yellowSlideTexture);
            } else if (color.equals("pink")) {
                note.setTexture(pinkSlideTexture);
            } else if (color.equals("red")) {
                note.setTexture(redSlideTexture);
            } else {
                throw new IllegalArgumentException("Invalid color");
            }
        }
        
        public ArrayList<Point> getNotes() {
            return notes;
        }

        public void draw(ShapeRenderer shapeRenderer) {
            if (!host.isTiltedSquare() && host.getPlayerSides() == 4) {
                for (int j = 0; j < notes.size() - 1; j++) {
                    notes.get(j).getVector().setAngle(notes.get(j).getVector().angle() - 45);
                    notes.get(j).getVector().setLength(getDistance() + host.getPlayerDiameter() / 2);
                }
            }
            for (int i = 0; i < notes.size() - 1; i++) {
                // Piirretään ShapeRendererillä viiva Sliden sisällä olevien nuottien väliin
                if (!notes.get(i).isHit()){
                    Vector2 startPoint = (notes.get(i).getVector().add(new Vector2(host.getScreenWidth() / 2, host.getScreenHeight() / 2)));
                    Vector2 endPoint = (notes.get(i + 1).getVector().add(new Vector2(host.getScreenWidth() / 2, host.getScreenHeight() / 2)));

                    shapeRenderer.line(startPoint, endPoint);
                }
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
        addHighscore = true;
        this.host = host;
        manager = host.getManager();
        batch = host.getBatch();
        prefs = host.getPrefs();
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
        manager.load("Blue sprite.atlas", TextureAtlas.class);
        manager.load("Nuotteja.atlas", TextureAtlas.class);
        ParticleEffectLoader.ParticleEffectParameter blueParticleEffectParameter = new ParticleEffectLoader.ParticleEffectParameter();
        blueParticleEffectParameter.atlasFile = "Nuotteja.atlas";
        manager.load("SininenEfekti", ParticleEffect.class, blueParticleEffectParameter);
        manager.load("Blue dot.atlas", TextureAtlas.class);

        manager.load("Smol Green.png", Texture.class);
        manager.load("Smol Green Hold.png", Texture.class);
        manager.load("Smol Green Slide.png", Texture.class);
        manager.load("Smol Green Ball.png", Texture.class);
        manager.load("Green sprite.atlas", TextureAtlas.class);
        manager.load("Vihree.atlas", TextureAtlas.class);
        ParticleEffectLoader.ParticleEffectParameter greenParticleEffectParameter = new ParticleEffectLoader.ParticleEffectParameter();
        greenParticleEffectParameter.atlasFile = "Vihree.atlas";
        manager.load("VihreäEfekti", ParticleEffect.class, greenParticleEffectParameter);
        manager.load("Green dot.atlas", TextureAtlas.class);

        manager.load("Smol Pink.png", Texture.class);
        manager.load("Smol Pink Hold.png", Texture.class);
        manager.load("Smol Pink Slide.png", Texture.class);
        manager.load("Smol Pink Ball.png", Texture.class);
        manager.load("Pink sprite.atlas", TextureAtlas.class);
        manager.load("Pinkki.atlas", TextureAtlas.class);
        ParticleEffectLoader.ParticleEffectParameter pinkParticleEffectParameter = new ParticleEffectLoader.ParticleEffectParameter();
        pinkParticleEffectParameter.atlasFile = "Pinkki.atlas";
        manager.load("PinkkiEfekti", ParticleEffect.class, pinkParticleEffectParameter);
        manager.load("Pink dot.atlas", TextureAtlas.class);

        manager.load("Smol Yellow.png", Texture.class);
        manager.load("Smol Yellow Hold.png", Texture.class);
        manager.load("Smol Yellow Slide.png", Texture.class);
        manager.load("Smol Yellow Ball.png", Texture.class);
        manager.load("Yellow sprite.atlas", TextureAtlas.class);
        manager.load("Keltane.atlas", TextureAtlas.class);
        ParticleEffectLoader.ParticleEffectParameter yellowParticleEffectParameter = new ParticleEffectLoader.ParticleEffectParameter();
        yellowParticleEffectParameter.atlasFile = "Keltane.atlas";
        manager.load("KeltainenEfekti", ParticleEffect.class, yellowParticleEffectParameter);
        manager.load("Yellow dot.atlas", TextureAtlas.class);

        manager.load("Smol Red.png", Texture.class);
        manager.load("Smol Red Hold.png", Texture.class);
        manager.load("Smol Red Slide.png", Texture.class);
        manager.load("Smol Red Ball.png", Texture.class);
        manager.load("Red sprite.atlas", TextureAtlas.class);
        manager.load("Punane.atlas", TextureAtlas.class);
        ParticleEffectLoader.ParticleEffectParameter redParticleEffectParameter = new ParticleEffectLoader.ParticleEffectParameter();
        redParticleEffectParameter.atlasFile = "Punane.atlas";
        manager.load("PunainenEfekti", ParticleEffect.class, redParticleEffectParameter);
        manager.load("Red dot.atlas", TextureAtlas.class);

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
        pointerTexture = manager.get("pointer.png");
        square = manager.get("square.png");
        tiltedSquare = manager.get("tiltedSquare.png");
        sixside = manager.get("sixside.png");
        eightside = manager.get("eightside.png");
        tenside = manager.get("tenside.png");

        bluePointTexture = manager.get("Smol Blue.png");
        blueHoldTexture = manager.get("Smol Blue Hold.png");
        blueSlideTexture = manager.get("Smol Blue Slide.png");
        blueTickTexture = manager.get("Smol Blue Ball.png");
        blueHitAnimationAtlas = manager.get("Blue sprite.atlas");
        blueParticleEffectAtlas = manager.get("Nuotteja.atlas");
        blueHitAnimation = new Animation<TextureRegion>(0.05f, blueHitAnimationAtlas.getRegions());
        blueParticleEffect = manager.get("SininenEfekti");
        blueParticleEffect.scaleEffect(1/80f);
        blueTickAnimationAtlas = manager.get("Blue dot.atlas");
        blueTickAnimation = new Animation<TextureRegion>(0.05f, blueTickAnimationAtlas.getRegions());

        greenPointTexture = manager.get("Smol Green.png");
        greenHoldTexture = manager.get("Smol Green Hold.png");
        greenSlideTexture = manager.get("Smol Green Slide.png");
        greenTickTexture = manager.get("Smol Green Ball.png");
        greenHitAnimationAtlas = manager.get("Green sprite.atlas");
        greenParticleEffectAtlas = manager.get("Vihree.atlas");
        greenHitAnimation = new Animation<TextureRegion>(0.05f, greenHitAnimationAtlas.getRegions());
        greenParticleEffect = manager.get("VihreäEfekti");
        greenParticleEffect.scaleEffect(1/80f);
        greenTickAnimationAtlas = manager.get("Green dot.atlas");
        greenTickAnimation = new Animation<TextureRegion>(0.05f, greenTickAnimationAtlas.getRegions());

        yellowPointTexture = manager.get("Smol Yellow.png");
        yellowHoldTexture = manager.get("Smol Yellow Hold.png");
        yellowSlideTexture = manager.get("Smol Yellow Slide.png");
        yellowTickTexture = manager.get("Smol Yellow Ball.png");
        yellowHitAnimationAtlas = manager.get("Yellow sprite.atlas");
        yellowParticleEffectAtlas = manager.get("Keltane.atlas");
        yellowHitAnimation = new Animation<TextureRegion>(0.05f, yellowHitAnimationAtlas.getRegions());
        yellowParticleEffect = manager.get("KeltainenEfekti");
        yellowParticleEffect.scaleEffect(1/80f);
        yellowTickAnimationAtlas = manager.get("Yellow dot.atlas");
        yellowTickAnimation = new Animation<TextureRegion>(0.05f, yellowTickAnimationAtlas.getRegions());

        pinkPointTexture = manager.get("Smol Pink.png");
        pinkHoldTexture = manager.get("Smol Pink Hold.png");
        pinkSlideTexture = manager.get("Smol Pink Slide.png");
        pinkTickTexture = manager.get("Smol Pink Ball.png");
        pinkHitAnimationAtlas = manager.get("Pink sprite.atlas");
        pinkParticleEffectAtlas = manager.get("Pinkki.atlas");
        pinkHitAnimation = new Animation<TextureRegion>(0.05f, pinkHitAnimationAtlas.getRegions());
        pinkParticleEffect = manager.get("PinkkiEfekti");
        pinkParticleEffect.scaleEffect(1/80f);
        pinkTickAnimationAtlas = manager.get("Pink dot.atlas");
        pinkTickAnimation = new Animation<TextureRegion>(0.05f, pinkTickAnimationAtlas.getRegions());

        redPointTexture = manager.get("Smol Red.png");
        redHoldTexture = manager.get("Smol Red Hold.png");
        redSlideTexture = manager.get("Smol Red Slide.png");
        redTickTexture = manager.get("Smol Red Ball.png");
        redHitAnimationAtlas = manager.get("Red sprite.atlas");
        redParticleEffectAtlas = manager.get("Punane.atlas");
        redHitAnimation = new Animation<TextureRegion>(0.05f, redHitAnimationAtlas.getRegions());
        redParticleEffect = manager.get("PunainenEfekti");
        redParticleEffect.scaleEffect(1/80f);
        redTickAnimationAtlas = manager.get("Red dot.atlas");
        redTickAnimation = new Animation<TextureRegion>(0.05f, redTickAnimationAtlas.getRegions());

        playerSides = host.getPlayerSides();
        playerDiameter = host.getPlayerDiameter();
        player = new Player(playerSides, playerDiameter);

        soundEffect = host.getEffect();
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

        selectNoteType();
        boolean clockwise = true;
        boolean roomForSlide = false;
        for (int i = 0; i < totalBeats - startOffset ; i++) {
            int randomSector = MathUtils.random(0, (playerSides-1));
            randomSector = moveNotes(randomSector);
            //jos ei järjestelmällinen siirtäminen toimi (koska uusi sektori myös passiivinen) niin arvotaan uusi paikka
            while (!isSectorActive(randomSector)) {
                randomSector = MathUtils.random(0, (playerSides-1));
            }
            if ((list[i]=='s') && !(host.getPlayerSides()==4)) {
                //lasketaan valmiiks vierekkäiset sektorit
                int helperPlus = randomSector+1;
                if (helperPlus==playerSides) { helperPlus=0; }
                int helperMinus = randomSector-1;
                if (helperMinus<0) { helperMinus=playerSides-1; }
                //tehdään slide vaan jos on 2 vierekkäistä sektoria auki
                if (isSectorActive(helperPlus)) {
                    ArrayList<Point> slideGen = new ArrayList<Point>();
                    slideGen.add(new Point((randomSector) % playerSides, i * noteSpeed / (bpm / 60) + noteSpeed * startOffset / (bpm / 60) + noteSpeed / (146 / 60), randomColor()));

                    slideGen.add(new Point((helperPlus) % playerSides, (i + 0.5f) * noteSpeed / (bpm / 60) + noteSpeed * startOffset / (bpm / 60) + noteSpeed / (146 / 60), randomColor()));
                    i++;

                    slideGen.add(new Point((randomSector) % playerSides, i * noteSpeed / (bpm / 60) + noteSpeed * startOffset / (bpm / 60) + noteSpeed / (146 / 60), randomColor()));
                    slideGen.get(1).flip();
                    song.add(new Slide(0, 0, slideGen, randomColor()));

                } else if (isSectorActive(helperMinus)) {
                    ArrayList<Point> slideGen = new ArrayList<Point>();
                    slideGen.add(new Point((randomSector) % playerSides, i * noteSpeed / (bpm / 60) + noteSpeed * startOffset / (bpm / 60) + noteSpeed / (146 / 60), randomColor()));

                    slideGen.add(new Point((helperMinus) % playerSides, (i + 0.5f) * noteSpeed / (bpm / 60) + noteSpeed * startOffset / (bpm / 60) + noteSpeed / (146 / 60), randomColor()));
                    i++;

                    slideGen.add(new Point((randomSector) % playerSides, i * noteSpeed / (bpm / 60) + noteSpeed * startOffset / (bpm / 60) + noteSpeed / (146 / 60), randomColor()));
                    slideGen.get(0).flip(); slideGen.get(2).flip();
                    song.add(new Slide(0, 0, slideGen, randomColor()));
                //jos ei oo slidelle tilaa nii norminuotti
                } else {
                    song.add(new Point((randomSector) % playerSides, i * noteSpeed / (bpm / 60) + noteSpeed * startOffset / (bpm / 60) + noteSpeed / (146 / 60), randomColor()));
                }
            } else if (list[i]=='h'){
                song.add(new Hold((randomSector) % playerSides, i * noteSpeed / (bpm / 60) + noteSpeed * startOffset / (bpm / 60) + noteSpeed / (146 / 60), noteSpeed / (bpm / 60), randomColor()));
                i++;
            } else  {
                song.add(new Point((randomSector) % playerSides, i * noteSpeed / (bpm / 60) + noteSpeed * startOffset / (bpm / 60) + noteSpeed / (146 / 60), randomColor()));
            }
        }

        points = 0;
        verySmall = host.getVerySmallFont();
        small = host.getSmallFont();
        basic = host.getBasicFont();
        heading = host.getHeadingFont();

        pauseButton = new Button(0.2f, 8.3f, 1.5f, 1.5f, host.getPauseButtonTexture());
        playButton = new Button(18f, 18f, 1.5f, 1.5f, host.getPlayButtonTexture());
        playAgainButton = new Button(18f, 18f, 1.5f, 1.5f, host.getPlayAgainButtonTexture());
        backButton = new Button(18f, 18f, 1.5f, 1.5f, host.getBackButtonTexture());
        settingsButton = new Button(18f, 18f, 1.5f, 1.5f, host.getSettingsButtonTexture());
        calibration = new Button(18f, 18f, 4f, 1.6f, host.getButtonTexture());
        secondSetting = new Button(18f, 18f, 4f, 1.6f, host.getButtonTexture());
        highscoreButton = new Button(18f, 18f, 5.6f, 1.2f, host.getButtonTexture());
        backToPauseMenu = new Button(18f, 18f, 1.5f, 1.5f, host.getBackButtonTexture());
        soundButton = new Button(18f, 18f, 2f, 2f, soundOnTexture);
        if (!host.isSoundOn()) {
            soundButton.setTexture(soundOffTexture);
        }
        resultBox = new Button(2f, 1f, 12f, 8f, host.getTextBoxTexture());

        backButton.setText(0, 260, "" + prefs.getString("mainMenu2"), basic);
        backButton.setTextTwo(0, 0, " ");
        playButton.setText(-30, 190, "" + prefs.getString("continue"), basic);
        playAgainButton.setText(0, 190, "" + prefs.getString("retry"), basic);
        playAgainButton.setTextTwo(0, 0, " ");
        settingsButton.setText(-30, 190, "" + prefs.getString("settings"), basic);
        calibration.setText(50, 80, "" + prefs.getString("calibration"), small);
        highscoreButton.setText(62, 70, "" + prefs.getString("seeHighscore"), basic);
        backToPauseMenu.setText(10, 180, "" + prefs.getString("back"), basic);
        secondSetting.setText(30, 80, "" + prefs.getString("effectsAreOn"), small);
        if (!host.isEffectOn()) {
            secondSetting.setText("" + prefs.getString("effectsAreOff"));
        }

        if (prefs.getString("language").equals("fi")) {
            backButton.repositionText(10f, 260f);
            backButton.setTextTwo(-20f, 190f, "valikko");
            playButton.repositionText(0f, 225f);
            playAgainButton.repositionText(-10f, 260f);
            playAgainButton.setTextTwo(-55f, 190f, "uudestaan");
            settingsButton.repositionText(-30f, 225f);
            highscoreButton.repositionText(30f, 70f);
            backToPauseMenu.repositionText(-20f, 180f);
            secondSetting.repositionText(30f, 80f);
        }

        if (host.getDifficulty().equals("easy")) {
            highscoreNames = host.getEasyNames();
            highScores = host.getEasyScores();
        } else if (host.getDifficulty().equals("normal")) {
            highscoreNames = host.getNormalNames();
            highScores = host.getNormalScores();
        } else if (host.getDifficulty().equals("hard")) {
            highscoreNames = host.getHardNames();
            highScores = host.getHardScores();
        } else {
            highscoreNames = host.getBbNames();
            highScores = host.getBbScores();
        }

        if (playerSides==4) {
            pointMulti = 3;
        } else if (playerSides==6) {
            pointMulti = 5;
        } else if (playerSides==8) {
            pointMulti = 7;
        } else {
            pointMulti = 10;
        }

        paused = false;
        useShapeRenderer = true;
    }

    public void selectNoteType() {
        int rand = 3;
        int helper = totalBeats - startOffset;
        list = new char[helper+1];

        for (int i = 0; i < totalBeats - startOffset ; i++) {
            list[i] = 'b'; //basic
        }

        for (int i = 0; i < helper/6 ; i++) {
            while (list[rand]!=('b')) {
                rand = MathUtils.random(0, (helper));
            }
            list[rand] = 'h'; //hold
        }
        for (int i = 0; i < helper/6 ; i++) {
            while (list[rand]!=('b')) {
                rand = MathUtils.random(0, (helper));
            }
            if (playerSides!=4) {
                list[rand] = 's'; //slide
            } else { list[rand] = 'h'; } //hold
        }
    }

    public String randomColor() {
        int random = MathUtils.random(0, 4);
        switch (random) {
        case 0:
            return "blue";
        case 1:
            return "green";
        case 2:
            return "yellow";
        case 3:
            return "red";
        case 4:
            return "pink";
        default:
            return "blue";
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
        calibration.setPosition(5.7f, 4.2f);
        secondSetting.setPosition(5.7f, 2.2f);
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
        playAgainButton.setPosition(8f, 1.8f);
        backButton.setPosition(3f, 1.8f);
        highscoreButton.setPosition(5.3f, 3.5f);
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
            highscoreButton.draw(batch);
        }
        //asetusruutu
        if (changeSettings) {
            resultBox.draw(batch);
            backToPauseMenu.draw(batch);
            calibration.draw(batch);
            secondSetting.draw(batch);
            soundButton.draw(batch);
        }

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään tekstit
        basic.draw(batch, prefs.getString("points") + ": " + points, 50, 100);

        //väliaikainen millä näkee onko sektorit päällä vai pois
        //piirtää sektoreihin "on/off"
        if (!song.isEmpty() && !paused) {
            if (playerSides == 10) {
                draw10sectors();
            } else if (playerSides == 8) {
                draw8sectors();
            } else if (playerSides == 6) {
                draw6sectors();
            } else if (playerSides == 4) {
                if (host.isTiltedSquare()) {
                    drawDiamondSectors();
                } else {
                    drawSquareSectors();
                }
            }
        }

        // Piirrellään Accelerometerin arvoja ruudulle
        /*basic.draw(batch, "X: " + Gdx.input.getAccelerometerX(), 50, 300);
        basic.draw(batch, " Y: " + Gdx.input.getAccelerometerY(), 50, 250);
        basic.draw(batch, " Z: " + Gdx.input.getAccelerometerZ(), 50, 200);
        basic.draw(batch, "use accelerometer X: " + host.getPrefs().getBoolean("useAccelerometerX"), 50, 350);*/

        //pauseruudun tekstit
        if (paused && !song.isEmpty() && !changeSettings) {
            heading.draw(batch, "" + prefs.getString("pause"), 400, 600);
            backButton.drawText(batch);
            playButton.drawText(batch);
            playAgainButton.drawText(batch);
            settingsButton.drawText(batch);
            backButton.drawTextTwo(batch);
            playAgainButton.drawTextTwo(batch);
        }
        //tulosruudun tekstit
        if (song.isEmpty()) {
            if (prefs.getString("language").equals("en")) {
                heading.draw(batch, "" + prefs.getString("youDidIt"), 230, 620);
            } else { heading.draw(batch, "" + prefs.getString("youDidIt"), 280, 620); }
            basic.draw(batch, "" + prefs.getString("youGot") + points + prefs.getString("youGot2"), 410, 450);
            backButton.setText(140, 90, "" + prefs.getString("mainMenu"), basic);
            playAgainButton.setText(140, 90, "" + prefs.getString("startAgain"), basic);
            backButton.drawText(batch);
            playAgainButton.drawText(batch);
            highscoreButton.drawText(batch);
        }
        //asetusruudun tekstit
        if (changeSettings) {
            heading.draw(batch, "" + prefs.getString("settingsBig"), 320, 630);
            calibration.drawText(batch);
            secondSetting.drawText(batch);
            backToPauseMenu.drawText(batch);
        }

        batch.end();

        //piirretään sektorit&osoitin vain kun peli päällä
        if (!song.isEmpty() && !paused) {
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
        } else if (song.isEmpty()) {
            jauntyGumption.stop();
        } else if (!paused && !jauntyGumption.isPlaying() && !song.isEmpty()) {
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
                    points += pointMulti;
                    if (host.isSoundOn()) {
                        soundEffect.play();
                    }
                // Muuten FAIL
                } else if (note.getDistance() < -0.35f || !note.isHit()){
                    System.out.println("FAIL!");
                    iter.remove();
                }
                if (note.isHit()) {
                    if (note.isAnimationFinished()) {
                        iter.remove();
                    }
                }
            // Hold-tyyppisten nuottien tarkistus
            } else if (note instanceof  Hold) {
                if (note.getDistance() + ((Hold) note).getLength() <= 0) {
                    if (note.getSector() == player.getPointerSector() && !note.isHit()) {
                        note.setHit(true);
                        points += pointMulti*2;
                        if (host.isSoundOn()) {
                            soundEffect.play();
                        }
                    } else if (note.getDistance() + ((Hold) note).getLength() < -0.35f || !note.isHit()){
                        System.out.println("FAIL!");
                        iter.remove();
                    }
                    if (note.isHit()) {
                        if (note.isAnimationFinished()) {
                            iter.remove();
                        }
                    }
                }
                if (note.getDistance() <= 0 && !((Hold) note).isStartPointHit()) {
                    if (note.getSector() == player.getPointerSector()) {
                        ((Hold) note).setStartPointHit(true);
                        points += pointMulti*2;
                        if (host.isSoundOn()) {
                            soundEffect.play();
                        }
                    } else if (note.getDistance() < -0.35f){
                        System.out.println("FAIL!");
                    }
                }
                // Holdin pikkupalleroiden tarkistus
                Iterator<Hold.Tick> tickIter = ((Hold) note).getTicks().iterator();
                while (tickIter.hasNext()) {
                    Hold.Tick tick = tickIter.next();
                    if (tick.getDistance() <= 0) {
                        if (tick.getSector() == player.getPointerSector() && !tick.isHit()) {
                            tick.setHit(true);
                            points += pointMulti/3;
                            if (host.isSoundOn()) {
                                soundEffect.play();
                            }
                        } else if (tick.getDistance() < -0.35f || !tick.isHit()){
                            System.out.println("FAIL!");
                            tickIter.remove();
                        }
                        if (tick.isHit()) {
                            if (tick.isAnimationFinished()) {
                                tickIter.remove();
                            }
                        }
                    }
                }
            // Slide-tyyppisten nuottirykelmien tarkistus
            } else if (note instanceof Slide) {
                Iterator<Point> slideIter = ((Slide) note).getNotes().iterator();
                while (slideIter.hasNext()) {
                    Point point = slideIter.next();
                    if (point.getDistance() <= 0) {
                        if (point.getSector() == player.getPointerSector() && !point.isHit()) {
                            points += pointMulti*2;
                            point.setHit(true);
                            if (host.isSoundOn()) {
                                soundEffect.play();
                            }
                        } else if (point.getDistance() < -0.35f || !point.isHit()){
                            System.out.println("FAIL!");
                            slideIter.remove();
                        }
                        if (point.isHit()) {
                            if (point.isAnimationFinished()) {
                                slideIter.remove();
                            }
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
                Gdx.input.setOnscreenKeyboardVisible(true);
            }
            if (playButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
                paused = false;
                moveAwayPauseMenuButtons();
            }
            if (highscoreButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
                host.setScreen(new Highscore(host));
            }
            if (playAgainButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
                jauntyGumption.stop();
                this.dispose();
                host.setScreen(new GameScreen(host));
            }
            if (backButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
                jauntyGumption.stop();
                this.dispose();
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
                if (host.isEffectOn()) {
                    host.setEffectsOn(false);
                    secondSetting.setText("" + prefs.getString("effectsAreOff"));
                } else {
                    host.setEffectsOn(true);
                    secondSetting.setText("" + prefs.getString("effectsAreOn"));
                }
            }
            if (soundButton.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
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

        if (song.isEmpty() && addHighscore && host.isHighscoreOn()) {
            checkHighscore();
        }

    }

    public void checkHighscore() {
        if (host.getDifficulty().equals("easy")) {
            if (points > highScores[0]) {
                host.setEasyHighscore(1, points, highScores);
            } else if (points > highScores[1]) {
                host.setEasyHighscore(2, points, highScores);
            } else if (points > highScores[2]) {
                host.setEasyHighscore(3, points, highScores);
            } else if (points > highScores[3]) {
                host.setEasyHighscore(4, points, highScores);
            } else if (points > highScores[4]) {
                host.setEasyHighscore(5, points, highScores);
            }
        } else if (host.getDifficulty().equals("normal")) {
            if (points > highScores[0]) {
                host.setNormalHighscore(1, points, highScores);
            } else if (points > highScores[1]) {
                host.setNormalHighscore(2, points, highScores);
            } else if (points > highScores[2]) {
                host.setNormalHighscore(3, points, highScores);
            } else if (points > highScores[3]) {
                host.setNormalHighscore(4, points, highScores);
            } else if (points > highScores[4]) {
                host.setNormalHighscore(5, points, highScores);
            }
        } else if (host.getDifficulty().equals("hard")) {
            if (points > highScores[0]) {
                host.setHardHighscore(1, points, highScores);
            } else if (points > highScores[1]) {
                host.setHardHighscore(2, points, highScores);
            } else if (points > highScores[2]) {
                host.setHardHighscore(3, points, highScores);
            } else if (points > highScores[3]) {
                host.setHardHighscore(4, points, highScores);
            } else if (points > highScores[4]) {
                host.setHardHighscore(5, points, highScores);
            }
        } else {
            if (points > highScores[0]) {
                host.setBbHighscore(1, points, highScores);
            } else if (points > highScores[1]) {
                host.setBbHighscore(2, points, highScores);
            } else if (points > highScores[2]) {
                host.setBbHighscore(3, points, highScores);
            } else if (points > highScores[3]) {
                host.setBbHighscore(4, points, highScores);
            } else if (points > highScores[4]) {
                host.setBbHighscore(5, points, highScores);
            }
        }
        addHighscore=false;
    }

    public void drawSquareSectors() {
        if(!host.getActiveSectors()[0]) {
            verySmall.draw(batch, "off", 690, 410); }
        if(!host.getActiveSectors()[1]) {
            verySmall.draw(batch, "off", 610, 340); }
        if(!host.getActiveSectors()[2]) {
            verySmall.draw(batch, "off", 550, 410); }
        if(!host.getActiveSectors()[3]) {
            verySmall.draw(batch, "off", 610, 470); }
    }

    public void drawDiamondSectors() {
        if(!host.getActiveSectors()[0]) {
            verySmall.draw(batch, "off", 650, 450); }
        if(!host.getActiveSectors()[1]) {
            verySmall.draw(batch, "off", 650, 370); }
        if(!host.getActiveSectors()[2]) {
            verySmall.draw(batch, "off", 580, 370); }
        if(!host.getActiveSectors()[3]) {
            verySmall.draw(batch, "off", 580, 450); }
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
        //paused = true;
        //moveHerePauseMenuButtons();
    }

    @Override
    public void resume() {
        //paused = true;
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        manager.unload("Galaxy dark purple.png");
        manager.unload("soundOn.png");
        manager.unload("soundOff.png");

        manager.unload("Smol Blue.png");
        manager.unload("Smol Blue Hold.png");
        manager.unload("Smol Blue Slide.png");
        manager.unload("Smol Blue Ball.png");
        manager.unload("Blue sprite.atlas");
        manager.unload("SininenEfekti");

        manager.unload("Smol Green.png");
        manager.unload("Smol Green Hold.png");
        manager.unload("Smol Green Slide.png");
        manager.unload("Smol Green Ball.png");
        manager.unload("Green sprite.atlas");
        manager.unload("VihreäEfekti");

        manager.unload("Smol Pink.png");
        manager.unload("Smol Pink Hold.png");
        manager.unload("Smol Pink Slide.png");
        manager.unload("Smol Pink Ball.png");
        manager.unload("Pink sprite.atlas");
        manager.unload("PinkkiEfekti");

        manager.unload("Smol Yellow.png");
        manager.unload("Smol Yellow Hold.png");
        manager.unload("Smol Yellow Slide.png");
        manager.unload("Smol Yellow Ball.png");
        manager.unload("Yellow sprite.atlas");
        manager.unload("KeltainenEfekti");

        manager.unload("Smol Red.png");
        manager.unload("Smol Red Hold.png");
        manager.unload("Smol Red Slide.png");
        manager.unload("Smol Red Ball.png");
        manager.unload("Red sprite.atlas");
        manager.unload("PunainenEfekti");

        manager.unload("square.png");
        manager.unload("tiltedSquare.png");
        manager.unload("sixside.png");
        manager.unload("eightside.png");
        manager.unload("tenside.png");
        manager.unload("pointer.png");
        
    }
}
