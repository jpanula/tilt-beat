package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;

/**
 * Created by Jaakko on 11.3.2018.
 */

public class Player {
    Polygon hitbox;
    Texture texture;
    float radius;
    float[] vertices;
    Pointer pointer;

    class Pointer {
        Texture pointerTexture;
        float originX;
        float originY;
        float posX;
        float posY;
        float radius;

        public Pointer() {
            pointerTexture = new Texture("pointer.png");
            originX = GameMain.getScreenWidth() / 2;
            originY = GameMain.getScreenHeight() / 2;
            radius = 1;
        }

        public void draw(SpriteBatch batch) {
            batch.draw(pointerTexture, originX + posX - radius / 2, originY + posY - radius / 2, radius, radius);
        }
    }

    public Player(int sides) throws IllegalArgumentException {
        radius = 5;

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
        return hitbox.getVertices();
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
}
