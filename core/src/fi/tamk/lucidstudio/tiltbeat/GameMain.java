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

	public SpriteBatch getBatch() {
	    return batch;
    }

    public OrthographicCamera getCamera() {
	    return camera;
    }

	@Override
	public void create () {
	    batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 16, 9);
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
