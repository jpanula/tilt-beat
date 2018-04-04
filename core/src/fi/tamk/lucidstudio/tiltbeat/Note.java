package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

    public void setSector(int sector) {
        this.sector = sector;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void move(float noteSpeed) {
            distance -= noteSpeed * Gdx.graphics.getDeltaTime();
    }

    abstract void draw(SpriteBatch batch);
}

class Point extends Note {
    private Texture texture;
    private Vector2 vector;
    private float width;
    private float height;
    private boolean flipped;

    public Point(int sector, float distance, Texture texture) {
        super(sector, distance);
        this.texture = texture;
        width = 1;
        height = (float) 0.7 * width;
        vector = new Vector2(distance, 0);
        flipped = false;
    }

    public Vector2 getVector() {
        vector.setLength(getDistance() + GameMain.getPlayerInradius());
        vector.setAngle(90 - (360 / GameMain.getPlayerSides()) * getSector() - (360 / GameMain.getPlayerSides()) / 2);
        return vector;
    }

    public void flip() {
        if (flipped) {
            flipped = false;
        } else {
            flipped = true;
        }
    }

    @Override
    void draw(SpriteBatch batch) {
        // Vektorilla lasketaan pelaajan kulmion kulmien perusteella nuottien liikerata kohti niiden
        // sektoreita
        vector.setLength(getDistance() + GameMain.getPlayerInradius());
        vector.setAngle(90 - (360 / GameMain.getPlayerSides()) * getSector() - (360 / GameMain.getPlayerSides()) / 2);
        batch.draw(texture, GameMain.getScreenWidth() / 2 + vector.x - width / 2, GameMain.getScreenHeight() / 2 + vector.y - height / 2, width / 2, height / 2, width, height, 1, 1, vector.angle() - 90, 0, 0, texture.getWidth(), texture.getHeight(), flipped, false);
    }
}

class Hold extends Note {
    private Texture texture;
    private Texture tickTexture;
    private Vector2 startPoint;
    private Vector2 endPoint;
    private float length;
    private float width;
    private float height;
    private static float tickDiameter;
    private int tickAmount;
    private ArrayList<Tick> ticks;
    private boolean scored;

    class Tick extends Note {
        private Texture texture;
        private Vector2 vector;
        private boolean scored;

        public Tick(int sector, float distance, Texture texture) {
            super(sector, distance);
            this.texture = texture;
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
        void draw(SpriteBatch batch) {
            vector.setLength(getDistance() + GameMain.getPlayerInradius());
            vector.setAngle(90 - (360 / GameMain.getPlayerSides()) * getSector() - (360 / GameMain.getPlayerSides()) / 2);
            if (getDistance() > 0)
            batch.draw(texture, GameMain.getScreenWidth() / 2 + vector.x - tickDiameter / 2, GameMain.getScreenHeight() / 2 + vector.y - tickDiameter / 2, tickDiameter, tickDiameter);
        }
    }

    public Hold(int sector, float distance, Texture texture, float length) {
        super(sector, distance);
        tickTexture = new Texture("Smol Blue Ball.png");
        this.texture = texture;
        this.length = length;
        width = 1;
        tickDiameter = 0.2f;
        height = 0.7f * width;
        tickAmount = (int) (length / (tickDiameter * GameMain.getNoteSpeed()));
        startPoint = new Vector2(distance, 0);
        endPoint = new Vector2(distance + length, 0);
        ticks = new ArrayList<Tick>();
        for (int i = 1; i < tickAmount; i++) {
            ticks.add(new Tick(sector, (distance + (float) i / tickAmount * length), tickTexture));
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
    void draw(SpriteBatch batch) {
        // Vektorilla lasketaan pelaajan kulmion kulmien perusteella nuottien liikerata kohti niiden
        // sektoreita
         for (Tick tick : ticks) {
             tick.draw(batch);
         }
        startPoint.setLength(getDistance() + GameMain.getPlayerInradius());
        startPoint.setAngle(90 - (360 / GameMain.getPlayerSides()) * getSector() - (360 / GameMain.getPlayerSides()) / 2);
        if (getDistance() > 0) {
            batch.draw(texture, GameMain.getScreenWidth() / 2 + startPoint.x - width / 2, GameMain.getScreenHeight() / 2 + startPoint.y - height / 2, width / 2, height / 2, width, height, 1, 1, startPoint.angle() - 90, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
        }
        endPoint.setLength(getDistance() + length + GameMain.getPlayerInradius());
        endPoint.setAngle(90 - (360 / GameMain.getPlayerSides()) * getSector() - (360 / GameMain.getPlayerSides()) / 2);
        batch.draw(texture, GameMain.getScreenWidth() / 2 + endPoint.x - width / 2, GameMain.getScreenHeight() / 2 + endPoint.y - height / 2, width / 2, height / 2, width, height, 1, 1, endPoint.angle() - 90, 0, 0, texture.getWidth(), texture.getHeight(), false, true);
    }

     @Override
     public void move(float noteSpeed) {
         super.move(noteSpeed);
         for (Tick tick : ticks) {
             tick.move(noteSpeed);
         }
     }
 }

class Slide extends Note {
    private ArrayList<Point> notes;

    public Slide(int sector, float distance, ArrayList<Point> notes) {
        super(sector, distance);
        for (int i = 0; i < notes.size(); i++) {
            Point note = notes.get(i);
            note.setSector((note.getSector() + sector) % GameMain.getPlayerSides());
            note.setDistance(note.getDistance() + distance);
        }
        this.notes = notes;
    }

    public ArrayList<Point> getNotes() {
        return notes;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        for (int i = 0; i < notes.size() - 1; i++) {
            Vector2 startPoint = (notes.get(i).getVector().add(new Vector2(GameMain.getScreenWidth() / 2, GameMain.getScreenHeight() / 2)));
            Vector2 endPoint = (notes.get(i + 1).getVector().add(new Vector2(GameMain.getScreenWidth() / 2, GameMain.getScreenHeight() / 2)));
            shapeRenderer.line(startPoint, endPoint);
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