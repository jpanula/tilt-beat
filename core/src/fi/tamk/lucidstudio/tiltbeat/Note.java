package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by Jaakko on 11.3.2018.
 */

public abstract class Note {
    private int sector;
    private float distance;
    private boolean hit;
    Preferences prefs = Gdx.app.getPreferences("default");

    public Note(int sector, float distance) {
        this.sector = sector;
        this.distance = distance;
        hit = false;
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

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public void move(float noteSpeed) {
        if (!isHit()) {
            distance -= noteSpeed * Gdx.graphics.getDeltaTime();
        }
    }

    abstract void draw(SpriteBatch batch);
}

class Point extends Note {
    private Texture texture;
    private Texture scoreAnimationSheet;
    private TextureRegion[] scoreAnimationFrames;
    private Animation<TextureRegion> scoreAnimation;
    private Vector2 vector;
    private float width;
    private float height;
    private boolean flipped;
    private float stateTime;
    private float animationSize;


    public Point(int sector, float distance, Texture texture) {
        super(sector, distance);
        this.texture = texture;
        scoreAnimationSheet = new Texture("Sprite.png");
        TextureAtlas atlas = new TextureAtlas("Sprite2.atlas");
        scoreAnimation = new Animation<TextureRegion>(0.07f, atlas.getRegions());
        width = 1;
        height = (float) 0.7 * width;
        animationSize = 1.1f;
        vector = new Vector2(distance, 0);
        flipped = false;
        stateTime = 0;
    }

    public boolean isAnimationFinished() {
        return scoreAnimation.isAnimationFinished(stateTime);
    }

    public Vector2 getVector() {
        vector.setLength(getDistance() + GameMain.getPlayerInradius(prefs.getFloat("playerDiameter"), prefs.getInteger("playerSides")));
        vector.setAngle(90 - (360 / prefs.getInteger("playerSides")) * getSector() - (360 / prefs.getInteger("playerSides")) / 2);
        return vector;
    }

    // Kääntää nuotin kuvan vaakasuunnassa ympäri
    public void flip() {
        if (flipped) {
            flipped = false;
        } else {
            flipped = true;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Vektorilla lasketaan pelaajan kulmion kulmien perusteella nuottien liikerata kohti niiden
        // sektoreita
        vector.setLength(getDistance() + GameMain.getPlayerInradius(prefs.getFloat("playerDiameter"), prefs.getInteger("playerSides")));
        vector.setAngle(90 - (360 / prefs.getInteger("playerSides")) * getSector() - (360 / prefs.getInteger("playerSides")) / 2);
        if (!isHit()) {
            batch.draw(texture, GameMain.getScreenWidth() / 2 + vector.x - width / 2, GameMain.getScreenHeight() / 2 + vector.y - height / 2, width / 2, height / 2, width, height, 1, 1, vector.angle() - 90, 0, 0, texture.getWidth(), texture.getHeight(), flipped, false);
        } else {
            TextureRegion keyframe = new TextureRegion(scoreAnimation.getKeyFrame(stateTime));
            batch.draw(keyframe, GameMain.getScreenWidth() / 2 + vector.x - animationSize / 2, GameMain.getScreenHeight() / 2 + vector.y - animationSize / 2, animationSize / 2, animationSize / 2, animationSize, animationSize, animationSize, animationSize, vector.angle(), false);
            stateTime += Gdx.graphics.getDeltaTime();
        }
    }
    public void drawInBackground(SpriteBatch batch) {
        vector.setLength(getDistance() + GameMain.getPlayerInradius(prefs.getFloat("playerDiameter"), prefs.getInteger("playerSides")));
        vector.setAngle(90 - (360 / prefs.getInteger("playerSides")) * getSector() - (360 / prefs.getInteger("playerSides")) / 2);
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

        // Pikkupallerot Hold-nuottien välissä
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
        public void draw(SpriteBatch batch) {
            vector.setLength(getDistance() + GameMain.getPlayerInradius(prefs.getFloat("playerDiameter"), prefs.getInteger("playerSides")));
            vector.setAngle(90 - (360 / prefs.getInteger("playerSides")) * getSector() - (360 / prefs.getInteger("playerSides")) / 2);
            if (getDistance() > 0)
            batch.draw(texture, GameMain.getScreenWidth() / 2 + vector.x - tickDiameter / 2, GameMain.getScreenHeight() / 2 + vector.y - tickDiameter / 2, tickDiameter, tickDiameter);
        }
    }

    public Hold(int sector, float distance, Texture texture, float length) {
        super(sector, distance);
        tickTexture = new Texture("Smol Green Ball.png");
        this.texture = texture;
        this.length = length;
        width = 1;
        tickDiameter = 0.2f;
        height = 0.7f * width;
        tickAmount = (int) (length / (tickDiameter * prefs.getFloat("noteSpeed")));
        if (prefs.getString("difficulty").equals("normal")) {
            tickAmount *= 2;
        } else if (prefs.getString("difficulty").equals("hard")) {
            tickAmount *= 3;
        } else if (prefs.getString("difficulty").equals("BACKBREAKER")) {
            tickAmount *= 5;
        }
        tickAmount -= 2;
        startPoint = new Vector2(distance, 0);
        endPoint = new Vector2(distance + length, 0);
        ticks = new ArrayList<Tick>();
        for (int i = 2; i < tickAmount - 1; i++) {
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
    public void draw(SpriteBatch batch) {
        // Vektorilla lasketaan pelaajan kulmion kulmien perusteella nuottien liikerata kohti niiden
        // sektoreita
         for (Tick tick : ticks) {
             tick.draw(batch);
         }
        startPoint.setLength(getDistance() + GameMain.getPlayerInradius(prefs.getFloat("playerDiameter"), prefs.getInteger("playerSides")));
        startPoint.setAngle(90 - (360 / prefs.getInteger("playerSides")) * getSector() - (360 / prefs.getInteger("playerSides")) / 2);
        if (getDistance() > 0) {
            batch.draw(texture, GameMain.getScreenWidth() / 2 + startPoint.x - width / 2, GameMain.getScreenHeight() / 2 + startPoint.y - height / 2, width / 2, height / 2, width, height, 1, 1, startPoint.angle() - 90, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
        }
        endPoint.setLength(getDistance() + length + GameMain.getPlayerInradius(prefs.getFloat("playerDiameter"), prefs.getInteger("playerSides")));
        endPoint.setAngle(90 - (360 / prefs.getInteger("playerSides")) * getSector() - (360 / prefs.getInteger("playerSides")) / 2);
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
        // Muutetaan annetun nuottikasan sektorit ja etäisyys koko Sliden mukaan
        for (int i = 0; i < notes.size(); i++) {
            Point note = notes.get(i);
            note.setSector((note.getSector() + sector) % prefs.getInteger("playerSides"));
            note.setDistance(note.getDistance() + distance);
        }
        this.notes = notes;
    }

    public ArrayList<Point> getNotes() {
        return notes;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        for (int i = 0; i < notes.size() - 1; i++) {
            // Piirretään ShapeRendererillä viiva Sliden sisällä olevien nuottien väliin
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