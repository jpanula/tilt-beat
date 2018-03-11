package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Jaakko on 11.3.2018.
 */

public class GameScreen implements Screen {
    GameMain host;
    SpriteBatch batch;
    OrthographicCamera camera;
    ShapeRenderer shapeRenderer;
    int sides = 10;
    Player player;
    boolean useShapeRenderer;

    public GameScreen(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        shapeRenderer = new ShapeRenderer();
        player = new Player(sides);
        shapeRenderer.translate(player.getX(), player.getY(), 0);
        shapeRenderer.scale(player.getScaleX(), player.getScaleY(), 0);

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
        batch.end();

        if (useShapeRenderer) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 0, 1, 1);
            shapeRenderer.polygon(player.getVertices());
            shapeRenderer.end();
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
