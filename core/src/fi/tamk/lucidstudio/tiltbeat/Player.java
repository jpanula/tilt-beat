package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

import static com.badlogic.gdx.Gdx.input;

/**
 * Created by Jaakko on 11.3.2018.
 */

public class Player {
    Polygon hitbox;
    ArrayList<Polygon> sectors;
    Texture texture;
    float radius;
    float[] vertices;
    Pointer pointer;

    // Osoittimen luokka
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

        public void draw(SpriteBatch batch) {
            batch.draw(pointerTexture, hitbox.x - hitbox.radius, hitbox.y - hitbox.radius, hitbox.radius * 2, hitbox.radius * 2);
        }

        public void draw(ShapeRenderer shapeRenderer) {
            shapeRenderer.circle(hitbox.x, hitbox.y, hitbox.radius, 100);
        }

        public void move(OrthographicCamera camera) {
            if (Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT) && hitbox.x < GameMain.getScreenWidth() / 2 + GameMain.getPlayerInradius() - radius * 2.5f) {
                hitbox.x += speed * Gdx.graphics.getDeltaTime();
            } else if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) && hitbox.x > GameMain.getScreenWidth() / 2 - GameMain.getPlayerInradius() + radius * 2.5f) {
                hitbox.x -= speed * Gdx.graphics.getDeltaTime();
            } else if (Gdx.input.isKeyPressed(Input.Keys.DPAD_UP) && hitbox.y < GameMain.getScreenHeight() / 2 + GameMain.getPlayerInradius() - radius * 2.5f) {
                hitbox.y += speed * Gdx.graphics.getDeltaTime();
            } else if (Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN) && hitbox.y > GameMain.getScreenHeight() / 2 - GameMain.getPlayerInradius() + radius * 2.5f) {
                hitbox.y -= speed * Gdx.graphics.getDeltaTime();
            }
            if (Gdx.input.isTouched()) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                if (Player.this.hitbox.contains(touchPos.x, touchPos.y)) {
                    hitbox.x = touchPos.x;
                    hitbox.y = touchPos.y;
                }
            }
        }
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

        if (playerSides == 10) {
            texture = new Texture("tenside.png");
            vertices = new float[] {
                    0.5f,    1.0f,
                    0.795f,  0.905f,
                    0.98f,   0.65f,
                    0.98f,   0.35f,
                    0.795f,  0.095f,
                    0.5f,    0f,
                    0.205f,  0.095f,
                    0.0203f, 0.35f,
                    0.0203f, 0.65f,
                    0.205f,  0.905f
            };
            for (int i = 0; i < playerSides; i++) {
                float[] triangleVerts = {
                        vertices[i * 2],               vertices[i * 2 + 1],
                        vertices[(i * 2 + 2) % 20],           vertices[(i * 2 + 3) % 20],
                        0.5f, 0.5f
                };
                sectors.add(new Polygon(triangleVerts));
                sectors.get(i).setScale(playerDiameter, playerDiameter);
                sectors.get(i).setPosition(GameMain.getScreenWidth() / 2 - radius, GameMain.getScreenHeight() / 2 - radius);
            }
        } else {
            throw new IllegalArgumentException("Invalid number of playerSides");
        }
        hitbox = new Polygon(vertices);
        hitbox.setScale(playerDiameter, playerDiameter);
        hitbox.setPosition(GameMain.getScreenWidth() / 2 - radius, GameMain.getScreenHeight() / 2 - radius);
    }

    public float[] getVertices() {
        return hitbox.getTransformedVertices();
    }

    public float[] getSectorVertices(int sector) {
        return sectors.get(sector).getTransformedVertices();
    }

    public float getX() {
        return hitbox.getX();
    }

    public float getY() {
        return hitbox.getY();
    }

    public float getScaleX() {
        return hitbox.getScaleX();
    }

    public float getScaleY() {
        return hitbox.getScaleY();
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, hitbox.getX(), hitbox.getY(), hitbox.getScaleX(), hitbox.getScaleY());
        pointer.draw(batch);
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.polygon(getVertices());
        if (getPointerSector() > -1) {
            shapeRenderer.polygon(getSectorVertices(getPointerSector()));
        }
        pointer.draw(shapeRenderer);
    }

    public void move(OrthographicCamera camera) {
        pointer.move(camera);
    }

    public int getPointerSector() {
        return pointer.getSector();
    }
}
