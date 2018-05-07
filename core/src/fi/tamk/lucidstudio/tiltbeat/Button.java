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
    /**
     * draws the set text on the button
     */
    public void drawText(SpriteBatch batch) {
        moveText();
        font.draw(batch, text, fontX, fontY);
    }
    /**
     * draws the second set text on the button
     */
    public void drawTextTwo(SpriteBatch batch) {
        moveTextTwo();
        font.draw(batch, textTwo, fontXtwo, fontYtwo);
    }
    /**
     * sets the text on the button
     * @param x how far right from the buttons corner the text is
     * @param y how far up from the buttons corner the text is
     * @param a the wanted text
     * @param font the wanted font
     */
    public void setText(float x, float y, String a, BitmapFont font) {
        plusX = x;
        plusY = y;
        text = a;
        this.font = font;
    }
    /**
     * sets the second text on the button
     * @param x how far right from the buttons corner the text is
     * @param y how far up from the buttons corner the text is
     * @param a the wanted text
     */
    public void setTextTwo(float x, float y, String a) {
        plusXtwo = x;
        plusYtwo = y;
        textTwo = a;
    }
    /**
     * changes the text on the button
     * @param a the wanted text
     */
    public void setText(String a) {
        text = a;
    }
    /**
     * changes the second text on the button
     * @param a the wanted text
     */
    public void setTextTwo(String a) {
        textTwo = a;
    }
    /**
     * moves the text to the buttons current location
     */
    public void moveText() {
        fontX = calculateXPos() + plusX;
        fontY = calculateYPos() + plusY;
    }
    /**
     * moves the second text to the buttons current location
     */
    public void moveTextTwo() {
        fontXtwo = calculateXPos() + plusXtwo;
        fontYtwo = calculateYPos() + plusYtwo;
    }
    /**
     * sets the placement of the text on the button
     */
    public void repositionText(float x, float y) {
        plusX = x;
        plusY = y;
    }
    /**
     * sets the placement of the second text on the button
     */
    public void repositionTextTwo(float x, float y) {
        plusXtwo = x;
        plusYtwo = y;
    }
    /**
     * sets the texture of the button
     * @param t the wanted texture
     */
    public void setTexture(Texture t) {
        texture = t;
    }
    /**
     * returns the texture of the button
     * @return the texture
     */
    public Texture getTexture() {
        return texture;
    }
    /**
     * calculates the buttons x-placement in pixels from world coordinates
     * 1280 is screen width in pixels, 16 is screen width in world coordinates
     * @return the x-position in pixels
     */
    public float calculateXPos() {
        float a = (1280*x) / 16;
        return a;
    }
    /**
     * calculates the buttons y-placement in pixels from world coordinates
     * 800 is screen height in pixels, 10 is screen height in world coordinates
     * @return the y-position in pixels
     */
    public float calculateYPos() {
        float a = (800*y) / 10;
        return a;
    }
}
