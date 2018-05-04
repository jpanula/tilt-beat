package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Anna on 16/04/2018.
 */

public class Button extends Rectangle {
    private Texture texture;
    private float fontX; //where the text is drawn
    private float fontY; //where the text is drawn
    private float fontXtwo;
    private float fontYtwo;
    private String text;
    private String textTwo;
    private BitmapFont font;
    private float plusX; //how far from the buttons corner the text is
    private float plusY; //how far from the buttons corner the text is
    private float plusXtwo;
    private float plusYtwo;
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

    public void drawText(SpriteBatch batch) {
        moveText();
        font.draw(batch, text, fontX, fontY);
    }

    public void drawTextTwo(SpriteBatch batch) {
        moveTextTwo();
        font.draw(batch, textTwo, fontXtwo, fontYtwo);
    }

    public void setText(float x, float y, String a, BitmapFont font) {
        plusX = x;
        plusY = y;
        text = a;
        this.font = font;
    }

    public void setTextTwo(float x, float y, String a) {
        plusXtwo = x;
        plusYtwo = y;
        textTwo = a;
    }

    public void setText(String a) {
        text = a;
    }

    public void setTextTwo(String a) {
        textTwo = a;
    }

    public void moveText() {
        fontX = calculateXPos() + plusX;
        fontY = calculateYPos() + plusY;
    }

    public void moveTextTwo() {
        fontXtwo = calculateXPos() + plusXtwo;
        fontYtwo = calculateYPos() + plusYtwo;
    }

    public void repositionText(float x, float y) {
        plusX = x;
        plusY = y;
    }

    public void setTexture(Texture t) {
        texture = t;
    }

    public Texture getTexture() {
        return texture;
    }

    public float calculateXPos() {
        float a = (1280*x) / 16;
        return a;
    }

    public float calculateYPos() {
        float a = (800*y) / 10;
        return a;
    }
}
