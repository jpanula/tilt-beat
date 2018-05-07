package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 *
 */
public class LoadingScreen implements Screen {
    private GameMain host;
    private Screen parent;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;

    /**
     *
     * @param host
     * @param parent
     */
    public LoadingScreen(GameMain host, Screen parent) {
        this.host = host;
        this.parent = parent;
        this.batch = host.getBatch();
        this.camera = host.getCamera();
        this.fontCamera = host.getFontCamera();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // Tarkistaa onko assetit ladattu ja siirtyy takaisin Screeniin josta kutsuttiin kun valmis
        if (host.getManager().update()) {
            this.dispose();
            host.setScreen(parent);
        }

        Gdx.gl.glClearColor(0.7f, 0.2f, 0.7f, 0.7f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // Renderaa joku latausruutu
        batch.end();
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
