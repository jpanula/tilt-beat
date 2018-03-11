package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameMain extends Game {
	SpriteBatch batch;
	OrthographicCamera camera;
	GameScreen gameScreen;
	public static final float SCREEN_WIDTH = 16;
	public static final float SCREEN_HEIGHT = 9;

	public SpriteBatch getBatch() {
	    return batch;
    }

    public OrthographicCamera getCamera() {
	    return camera;
    }

    public static float getScreenWidth() {
        return SCREEN_WIDTH;
    }

    public static float getScreenHeight() {
        return SCREEN_HEIGHT;
    }

    @Override
	public void create () {
	    batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        gameScreen = new GameScreen(this);
	    setScreen(gameScreen);
	}

	@Override
	public void render () {
	    super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		gameScreen.dispose();

	}
}
