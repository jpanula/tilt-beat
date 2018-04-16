package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Anna on 16/04/2018.
 */

public class Button extends Rectangle {
    private Texture texture;

    public Button(float x, float y, float width, float height, Texture texture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
    }

    public void draw(SpriteBatch batch) {
        batch.draw( texture, x, y, width, height);
    }

    public void setTexture(Texture t) {
        texture = t;
    }

    public Texture getTexture() {
        return texture;
    }
}
