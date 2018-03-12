package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;

/**
 * Created by Jaakko on 11.3.2018.
 */

public abstract class Note {
    private int sector;
    private float distance;

    public int getSector() {
        return sector;
    }

    public float getDistance() {
        return distance;
    }

    public void move(float speed) {
        distance -= speed * Gdx.graphics.getDeltaTime();
    }

    abstract void draw(int playerSides);
}

class Point extends Note {
    @Override
    void draw(int playerSides) {

    }
}

class Hold extends Note {
    @Override
    void draw(int playerSides) {

    }
}

class Slide extends Note {
    @Override
    void draw(int playerSides) {

    }
}
