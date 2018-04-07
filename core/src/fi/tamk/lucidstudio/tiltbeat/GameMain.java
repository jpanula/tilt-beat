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
	private static float noteSpeed = 3f;
	private static float playerDiameter = 3;
    private static float accelerometerDeadzone = 0.25f;
	private static float accelerometerMax = 3f;
    private static String songChoice = "song 1";
    private static String difficulty = "normal";
	public static boolean[] activeSectors;
    private static Texture background;
    private static Texture button;
    private static Texture buttonHigh;
    private static Texture buttonPressed;
	private static Texture pauseButton;
	private static Texture backButton;
    private static Texture playButton;
    private static Texture textBox;
	private static BitmapFont smallFont;
	private static BitmapFont basicFont;
	private static BitmapFont headingFont;
    private static BitmapFont smallerHeadingFont;
	private FreeTypeFontGenerator generator;
	private static float zeroPointX = 0;
	private static float zeroPointY = 0;
	private static float zeroPointZ = 0;
	private static int smoothingSamples = 15;

	public static void setPlayerSides(int a) { playerSides = a; }

	public static void setSongChoice(String a) { songChoice = a; }

    public static void setDifficulty(String a) { difficulty = a; }

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
		float playerInradius = (float) (getPlayerDiameter() / 2 * Math.cos(Math.PI/getPlayerSides()));
		return playerInradius;
	}

	public static String getSongChoice() { return songChoice; }

    public static String getDifficulty() { return difficulty; }

    public static Texture getBackgroundTexture() { return background; }

    public static Texture getButtonTexture() { return button; }

    public static Texture getButtonHighTexture() { return buttonHigh; }

    public static Texture getButtonPressedTexture() { return buttonPressed; }

	public static Texture getBackButtonTexture() { return backButton; }

	public static Texture getPauseButtonTexture() { return pauseButton; }

    public static Texture getPlayButtonTexture() { return playButton; }

    public static Texture getTextBoxTexture() { return textBox; }

	public static BitmapFont getHeadingFont() { return headingFont; }

	public static BitmapFont getSmallFont() { return smallFont; }

	public static BitmapFont getBasicFont() { return basicFont; }

    public static BitmapFont getSmallerHeadingFont() { return smallerHeadingFont; }

    public static float getAccelerometerDeadzone() {
        return accelerometerDeadzone;
    }

    public static float getAccelerometerMax() {
        return accelerometerMax;
    }

    public static float getZeroPointX() {
        return zeroPointX;
    }

    public static float getZeroPointY() {
        return zeroPointY;
    }

    public static float getZeroPointZ() {
        return zeroPointZ;
    }

	public static int getSmoothingSamples() {
		return smoothingSamples;
	}

	// Kalibroi nollapistearvot nykyisiin
    public static void calibrateZeroPoint() {
		zeroPointX = Gdx.input.getAccelerometerX();
		zeroPointY = Gdx.input.getAccelerometerY();
        zeroPointZ = Gdx.input.getAccelerometerZ();
	}

    @Override
	public void create () {
	    batch = new SpriteBatch();
	    shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		fontCamera = new OrthographicCamera();
		fontCamera.setToOrtho(false, SCREEN_WIDTH_PIXELS, SCREEN_HEIGHT_PIXELS);

		activeSectors = new boolean[playerSides];

		background = new Texture(Gdx.files.internal("Galaxy blue.png"));
        button = new Texture(Gdx.files.internal("nappi1korkee.png"));
        buttonHigh = new Texture(Gdx.files.internal("nappi1korkee.png"));
        buttonPressed = new Texture(Gdx.files.internal("nappi2.png"));
        pauseButton = new Texture(Gdx.files.internal("pausenappi.png"));
		backButton = new Texture(Gdx.files.internal("backnappi.png"));
		playButton = new Texture(Gdx.files.internal("playnappi.png"));
		textBox = new Texture(Gdx.files.internal("folio.png"));
        //playButton = new Texture(Gdx.files.internal("playButton.png"));

		createFonts();
        //menu = new MainMenu(this);
        //gameScreen = new GameScreen(this);

		setScreen(new MainMenu(this));
	}

	public void createFonts() {
		generator = new FreeTypeFontGenerator(Gdx.files.internal("grove.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

		//luodaan pieni fontti
		parameter.size = 40;
		parameter.color = Color.WHITE;
		smallFont = generator.generateFont(parameter);

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

        parameter.size = 100;
        parameter.color = Color.CYAN;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 3;
        smallerHeadingFont = generator.generateFont(parameter);
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
