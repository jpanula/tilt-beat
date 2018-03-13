package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by Jaakko on 11.3.2018.
 */

public abstract class Note {
    private int sector;
    private float distance;

    public Note(int sector, float distance) {
        this.sector = sector;
        this.distance = distance;
    }

    public int getSector() {
        return sector;
    }

    public float getDistance() {
        return distance;
    }

    public void move(float noteSpeed) {
        if (distance > 0) {
            distance -= noteSpeed * Gdx.graphics.getDeltaTime();
        }
    }

    abstract void draw(SpriteBatch batch, int playerSides);
}

class Point extends Note {
    private Texture texture;
    private Vector2 vector;
    private float diameter;
    private float radius;

    public Point(int sector, float distance) {
        super(sector, distance);
        texture = new Texture("pointer.png");
        diameter = 1;
        radius = diameter / 2;
        vector = new Vector2(distance, 0);
    }
    @Override
    void draw(SpriteBatch batch, int playerSides) {
        // Vektorilla lasketaan pelaajan kulmion kulmien perusteella nuottien liikerata kohti niiden
        // sektoreita
        vector.setLength(getDistance() + GameMain.getPlayerInradius() - radius);
        vector.setAngle(90 - (360 / playerSides) * getSector() - (360 / playerSides) / 2);
        batch.draw(texture, GameMain.getScreenWidth() / 2 + vector.x - radius, GameMain.getScreenHeight() / 2 + vector.y - radius, diameter, diameter);
    }
}

// TODO implementoi hold-tyyppinen nuotti
/* class Hold extends Note {
    float length;

    public Hold(int sector, float distance, float length) {
        super(sector, distance);
        this.length = length;
    }
    @Override
    void draw(int playerSides) {

    }
}*/

// TODO implementoi slide-tyyppinen nuotti
/*class Slide extends Note {
    @Override
    void draw(int playerSides) {

    }
}*/

// TODO arvioi onko luokka tarpeellinen vai voiko käyttää suoraan arraylistiä
class Song {
    ArrayList<Note> notes;

    public Song() {
        notes = new ArrayList<Note>();
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public void removeNote(Note note) {
        notes.remove(note);
    }
}