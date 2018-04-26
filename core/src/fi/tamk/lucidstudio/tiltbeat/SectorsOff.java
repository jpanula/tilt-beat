package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Anna on 26/04/2018.
 */

public class SectorsOff implements Screen {
    GameMain host;
    SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera fontCamera;
    private Texture background;
    BitmapFont heading;
    BitmapFont basic;
    private Button goToMods;
    private Vector3 touchPos;

    public SectorsOff(GameMain host) {
        this.host = host;
        batch = host.getBatch();
        camera = host.getCamera();
        background = host.getBackgroundTexture();
        fontCamera = host.getFontCamera();
        heading = host.getBigFont();
        basic = host.getBasicFont();
        goToMods = new Button(6f, 1f, 4f, 1.5f, host.getButtonTexture());

        goToMods.setText(30, 80, "go to mods", basic);
        touchPos = new Vector3();
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        batch.setProjectionMatrix(camera.combined);
        //piirrellään tausta ja napit
        batch.draw(background, 0, 0, 16, 10);
        goToMods.draw(batch);

        batch.setProjectionMatrix(fontCamera.combined);
        //piirrellään fontit
        goToMods.drawText(batch);
        heading.draw(batch, "You can't play if", 270, 580);
        heading.draw(batch, "all the sectors are off!!", 150, 430);

        batch.end();

        //nappien toiminnallisuus
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }
        if (goToMods.contains(touchPos.x, touchPos.y) && !Gdx.input.isTouched()) {
            host.setScreen(new Mods(host));
        }

        //ottaa napin painalluksen vain kerran
        if (!Gdx.input.isTouched()) {touchPos.set(0, 0, 0);}
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
