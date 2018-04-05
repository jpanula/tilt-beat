package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

import static com.badlogic.gdx.Gdx.input;

/**
 * Created by Jaakko on 11.3.2018.
 */

/**
 * Pelaajan kulmiosysteemin luokka
 */
public class Player {
    Polygon hitbox;
    ArrayList<Polygon> sectors;
    Texture texture;
    float radius;
    float[] vertices;
    Pointer pointer;
    static boolean[] activeSectors;

    // Pelaajan osoittimen luokka
    class Pointer {
        Texture pointerTexture;
        Circle hitbox;
        float radius;
        float speed;

        public Pointer() {
            radius = 0.4f;
            speed = 8;
            pointerTexture = new Texture("pointer.png");
            hitbox = new Circle(GameMain.getScreenWidth() / 2, GameMain.getScreenHeight() / 2, radius);
        }

        // Piirtometodi SpriteBatchille, käyttää kuvia
        public void draw(SpriteBatch batch) {
            batch.draw(pointerTexture, hitbox.x - hitbox.radius, hitbox.y - hitbox.radius, hitbox.radius * 2, hitbox.radius * 2);
        }

        // Piirtometodi ShapeRendererille, käyttää pisteitä / muotoja
        public void draw(ShapeRenderer shapeRenderer) {
            shapeRenderer.circle(hitbox.x, hitbox.y, hitbox.radius, 100);
        }

        public void move(OrthographicCamera camera) {
            // Osoittimen ohjaus nuolinäppäimillä
            if (Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT) && hitbox.x < GameMain.getScreenWidth() / 2 + GameMain.getPlayerInradius() - radius * 2.5f) {
                hitbox.x += speed * Gdx.graphics.getDeltaTime();
            } else if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) && hitbox.x > GameMain.getScreenWidth() / 2 - GameMain.getPlayerInradius() + radius * 2.5f) {
                hitbox.x -= speed * Gdx.graphics.getDeltaTime();
            } else if (Gdx.input.isKeyPressed(Input.Keys.DPAD_UP) && hitbox.y < GameMain.getScreenHeight() / 2 + GameMain.getPlayerInradius() - radius * 2.5f) {
                hitbox.y += speed * Gdx.graphics.getDeltaTime();
            } else if (Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN) && hitbox.y > GameMain.getScreenHeight() / 2 - GameMain.getPlayerInradius() + radius * 2.5f) {
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
            /*if (Math.abs(Gdx.input.getAccelerometerY()) > GameMain.getAccelerometerDeadzone() || Math.abs(Gdx.input.getAccelerometerX()) > GameMain.getAccelerometerDeadzone()) {
                hitbox.y = GameMain.getScreenHeight() / 2 - Math.min((Gdx.input.getAccelerometerX() / GameMain.getAccelerometerMax() * (GameMain.getPlayerInradius() - hitbox.radius * 2)), (GameMain.getPlayerInradius() - hitbox.radius * 2));
                hitbox.x = GameMain.getScreenWidth() / 2 + Math.min((Gdx.input.getAccelerometerY() / GameMain.getAccelerometerMax() * (GameMain.getPlayerInradius() - hitbox.radius * 2)), (GameMain.getPlayerInradius() - hitbox.radius * 2));
            }*/
            if (Math.abs(GameMain.getZeroPointZ() - Gdx.input.getAccelerometerZ()) > GameMain.getAccelerometerDeadzone() || Math.abs(GameMain.getZeroPointY() - Gdx.input.getAccelerometerY()) > GameMain.getAccelerometerDeadzone()) {
                hitbox.y = 0.05f + GameMain.getScreenHeight() / 2 + (Gdx.input.getAccelerometerZ() - GameMain.getZeroPointZ()) / 6;
                hitbox.x = GameMain.getScreenWidth() / 2 + (Gdx.input.getAccelerometerY() - GameMain.getZeroPointY()) / 4;
                Vector2 vector = new Vector2(hitbox.x - GameMain.getScreenWidth() / 2, hitbox.y - GameMain.getScreenHeight() / 2);
                if (vector.len() > GameMain.getPlayerInradius()) {
                    vector.setLength(GameMain.getPlayerInradius());
                    hitbox.x = GameMain.getScreenWidth() / 2 + vector.x;
                    hitbox.y = GameMain.getScreenHeight() / 2 + vector.y;
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
        activeSectors = new boolean[playerSides];

        // Pelaaja on 10-sivuinen
        if (playerSides == 10) {
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
        // Kahdeksankulmion pisteet
        } else if (playerSides == 8) {
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
        // Kuusikulmion pisteet
        } else if (playerSides == 6) {
            texture = new Texture("sixside.png");
            vertices = new float[]{
                    0.5f, 1f,
                    0.933f, 0.75f,
                    0.933f, 0.25f,
                    0.5f, 0f,
                    0.067f, 0.25f,
                    0.067f, 0.75f
            };
        } else {
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
            sectors.get(i).setPosition(GameMain.getScreenWidth() / 2 - radius, GameMain.getScreenHeight() / 2 - radius);
            // asetetaan sektori aktiiviseksi
            activeSectors[i] = true;
        }
        // Muodostetaan pelaajan kulmio, tehdään siitä oikean kokoinen ja siirretään se keskelle ruutua
        hitbox = new Polygon(vertices);
        hitbox.setScale(playerDiameter, playerDiameter);
        hitbox.setPosition(GameMain.getScreenWidth() / 2 - radius, GameMain.getScreenHeight() / 2 - radius);

        //testi - ei-aktiiviset sektorit
        //activeSectors[2] = false; activeSectors[3] = false; activeSectors[4] = false;
        //activeSectors[6] = false; activeSectors[7] = false;
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
        shapeRenderer.polygon(getVertices());
        if (getPointerSector() > -1) {
            shapeRenderer.polygon(getSectorVertices(getPointerSector()));
        }
        //pointer.draw(shapeRenderer);
    }

    public void move(OrthographicCamera camera) {
        pointer.move(camera);
    }

    public int getPointerSector() {
        return pointer.getSector();
    }

    public static void setActiveSectors(int a) {
        activeSectors[a] ^= true;
    }
}
