package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import javax.xml.soap.Text;

// TODO siirrä asetusmuuttujat johonkin asetustiedostoon tms. ja katso mitä muita sinne voisi lisätä
public class GameMain extends Game {
	private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	private OrthographicCamera fontCamera;
	//private GameScreen gameScreen;
	//private MainMenu menu;
	private static final float SCREEN_WIDTH = 16;
	private static final float SCREEN_HEIGHT = 10;
	private static final float SCREEN_WIDTH_PIXELS = 1280;
	private static final float SCREEN_HEIGHT_PIXELS = 800;
	private static int playerSides = 10;
	private static float noteSpeed = 4;
	private static float playerDiameter = 3;
	private static float playerInradius = (float) (playerDiameter * Math.cos(180/playerSides));
    private static Texture background;
    private static Texture button;

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

	public OrthographicCamera getFontCamera() {
		return fontCamera;
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

    public static Texture getBackgroundTexture() { return background; }

    public static Texture getButtonTexture() { return button; }

    @Override
	public void create () {
	    batch = new SpriteBatch();
	    shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		fontCamera = new OrthographicCamera();
		fontCamera.setToOrtho(false, SCREEN_WIDTH_PIXELS, SCREEN_HEIGHT_PIXELS);

		background = new Texture(Gdx.files.internal("Galaxy blue.png"));
        button = new Texture(Gdx.files.internal("nappi1.png"));

		createFonts();
        //menu = new MainMenu(this);
        //gameScreen = new GameScreen(this);

		setScreen(new MainMenu(this));
	}

	public void createFonts() {
		generator = new FreeTypeFontGenerator(Gdx.files.internal("grove.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

		//luodaan perusfontti
		parameter.size = 50;
		parameter.color = Color.WHITE;
		basicFont = generator.generateFont(parameter);

		//luodaan otsikkofontti
		parameter.size = 150;
		parameter.color = Color.CYAN;
		parameter.borderColor = Color.BLACK;
		parameter.borderWidth = 3;
		headingFont = generator.generateFont(parameter);
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
