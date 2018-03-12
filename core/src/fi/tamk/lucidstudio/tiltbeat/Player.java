package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;

import static com.badlogic.gdx.Gdx.input;

/**
 * Created by Jaakko on 11.3.2018.
 */

public class Player {
    Polygon hitbox;
    Texture texture;
    float radius;
    float inradius;
    float[] vertices;
    Pointer pointer;

    class Pointer {
        Texture pointerTexture;
        Circle hitbox;
        float radius;

        public Pointer() {
            radius = 0.8f;
            pointerTexture = new Texture("pointer.png");
            hitbox = new Circle(GameMain.getScreenWidth() / 2 - radius / 2, GameMain.getScreenHeight() / 2 - radius / 2, radius);
        }

        public void draw(SpriteBatch batch) {
            batch.draw(pointerTexture, hitbox.x, hitbox.y, hitbox.radius, hitbox.radius);
        }

        public void draw(ShapeRenderer shapeRenderer) {
            shapeRenderer.circle(hitbox.x + hitbox.radius / 2, hitbox.y + hitbox.radius / 2, hitbox.radius / 2, 100);
        }

        public void move(float inradius) {
            if (Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT) && hitbox.x < GameMain.getScreenWidth() / 2 + inradius - hitbox.radius) {
                hitbox.x += 2 * Gdx.graphics.getDeltaTime();
            }
        }
    }

    public Player(int sides) throws IllegalArgumentException {
        radius = 5;
        inradius = (float) (radius * Math.cos(Math.PI/sides));
        pointer = new Pointer();

        if (sides == 10) {
            texture = new Texture("tenside.png");
            vertices = new float[] {
                    0.5f, 1.0f,
                    0.795f, 0.905f,
                    0.98f, 0.65f,
                    0.98f, 0.35f,
                    0.795f, 0.095f,
                    0.5f, 0,
                    0.205f, 0.095f,
                    0.0203f, 0.35f,
                    0.0203f, 0.65f,
                    0.205f, 0.905f
            };
        } else {
            throw new IllegalArgumentException("Invalid number of sides");
        }
        hitbox = new Polygon(vertices);
        hitbox.setScale(radius, radius);
        hitbox.setPosition(GameMain.getScreenWidth() / 2 - radius / 2, GameMain.getScreenHeight() / 2 - radius / 2);
    }

    public float[] getVertices() {
        return hitbox.getTransformedVertices();
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

    public float getInradius() {
        return inradius;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, hitbox.getX(), hitbox.getY(), hitbox.getScaleX(), hitbox.getScaleY());
        pointer.draw(batch);
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.polygon(getVertices());
        pointer.draw(shapeRenderer);
    }

    public void move() {
        pointer.move(getInradius() / 2);
    }
}
