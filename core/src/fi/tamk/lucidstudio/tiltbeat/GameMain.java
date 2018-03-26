package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
// TODO siirrä asetusmuuttujat johonkin asetustiedostoon tms. ja katso mitä muita sinne voisi lisätä
public class GameMain extends Game {
	private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	//private GameScreen gameScreen;
	//private MainMenu menu;
	private static final float SCREEN_WIDTH = 16;
	private static final float SCREEN_HEIGHT = 10;
	private static int playerSides = 10;
	private static float noteSpeed = 4;
	private static float playerDiameter = 3;
	private static float playerInradius = (float) (playerDiameter * Math.cos(180/playerSides));
	private static final float HEADING_SIZE = 0.22f;
    private static final float FONT_SIZE = 0.13f;

	static BitmapFont basicFont;
	static BitmapFont headingFont;
	private FreeTypeFontGenerator generator;

	public SpriteBatch getBatch() {
	    return batch;
    }

    public ShapeRenderer getShapeRenderer() {
	    return shapeRenderer;
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

    public static int getPlayerSides() {
		return playerSides;
	}

	public static float getNoteSpeed() {
		return noteSpeed;
	}

	public static float getPlayerDiameter() {
		return playerDiameter;
	}

	public static float getPlayerInradius() {
		return playerInradius;
	}

    @Override
	public void create () {
	    batch = new SpriteBatch();
	    shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        //menu = new MainMenu(this);
        //gameScreen = new GameScreen(this);
		generator = new FreeTypeFontGenerator(Gdx.files.internal("grove.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		//luodaan perusfontti
		parameter.size = (int)(FONT_SIZE * Gdx.graphics.getHeight());
		parameter.color = Color.RED;
		basicFont = generator.generateFont(parameter);
        basicFont.getData().setScale(SCREEN_HEIGHT / Gdx.graphics.getHeight());
		//luodaan otsikkofontti
		parameter.size = (int)(HEADING_SIZE * Gdx.graphics.getHeight());
		parameter.color = Color.CYAN;
		parameter.borderColor = Color.BLACK;
		parameter.borderWidth = 3;
		headingFont = generator.generateFont(parameter);
		headingFont.getData().setScale(SCREEN_HEIGHT / Gdx.graphics.getHeight());

		setScreen(new MainMenu(this));
	}

	@Override
	public void render () {
	    super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		//gameScreen.dispose();

	}
}
