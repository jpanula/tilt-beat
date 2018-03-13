package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.util.Iterator;

/**
 * Created by Jaakko on 11.3.2018.
 */

public class GameScreen implements Screen {
    private GameMain host;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private Player player;
    private Song song;
    private boolean useShapeRenderer;

    private int playerSides;
    private float playerDiameter;
    private float noteSpeed;

    public GameScreen(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        shapeRenderer = host.getShapeRenderer();

        playerSides = GameMain.getPlayerSides();
        playerDiameter = GameMain.getPlayerDiameter();
        player = new Player(playerSides, playerDiameter);

        noteSpeed = GameMain.getNoteSpeed();
        song = new Song();
        for (int i = 0; i < 100 ; i++) {
            int random = MathUtils.random(0, 9);
            song.addNote(new Point(random, 3.5f * i * noteSpeed / 6 + 5));
        }

        useShapeRenderer = true;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        batch.begin();
        player.draw(batch);
        for (Note note : song.notes) {
            note.draw(batch, playerSides);
        }
        batch.end();

        if (useShapeRenderer) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 0, 1, 1);
            player.draw(shapeRenderer);
            shapeRenderer.end();
        }

        // System.out.println("X: " + Gdx.input.getAccelerometerX() + " Y: " + Gdx.input.getAccelerometerY() + " Z: " + Gdx.input.getAccelerometerZ());

        player.move(camera);

        Iterator<Note> iter = song.notes.iterator();
        while (iter.hasNext()) {
            Note note = iter.next();
            note.move(noteSpeed);

            if (note.getDistance() <= 0) {
                if (note.getSector() == player.getPointerSector()) {
                    iter.remove();
                } else {
                    System.out.println("FAIL!");
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
