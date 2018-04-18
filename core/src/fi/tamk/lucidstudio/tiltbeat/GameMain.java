package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameMain extends Game {
	private Preferences prefs;
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
	//public static boolean[] activeSectors;
    private Music song1;
    private Music song2;
    private Music song3;
    private static Texture background;
    private static Texture button;
    private static Texture buttonPressed;
	private static Texture pauseButton;
	private static Texture backButton;
    private static Texture playButton;
	private static Texture playAgainButton;
    private static Texture settingsButton;
    private static Texture textBox;
    private static BitmapFont verySmallFont;
	private static BitmapFont smallFont;
	private static BitmapFont basicFont;
	private static BitmapFont headingFont;
    private static BitmapFont smallerHeadingFont;
	private FreeTypeFontGenerator generator;

	public void setDefaultPreferences(Preferences prefs) {
        prefs.putInteger("playerSides", 8);
        prefs.putFloat("noteSpeed", 2f);
        prefs.putFloat("playerDiameter", 3f);
        prefs.putFloat("accelerometerDeadzone", 0.25f);
        prefs.putFloat("accelerometerMax", 3f);
        prefs.putString("songChoice", "song 1");
        prefs.putString("difficulty", "normal");
        prefs.putBoolean("soundOn", true);
        prefs.putFloat("zeroPointX", 0);
        prefs.putFloat("zeroPointY", 0);
        prefs.putFloat("zeroPointZ", 0);
        prefs.putInteger("smoothingSamples", 23);
        prefs.putString("activeSectors", "1111111111");
        prefs.putBoolean("useAccelerometerX", false);
        prefs.putBoolean("tiltedSquare", false);
        prefs.flush();
    }

    public Preferences getPrefs() {
        return prefs;
    }

    public boolean[] getActiveSectors() {
	    boolean[] activeSectors = new boolean[prefs.getInteger("playerSides")];
	    String prefString = prefs.getString("activeSectors");
	    for (int i = 0; i < getPlayerSides(); i++) {
	        if (prefString.charAt(i) == '0') {
	            activeSectors[i] = false;
            } else {
	            activeSectors[i] = true;
            }
        }
        return activeSectors;
    }

    public void setActiveSectors(boolean[] activeSectors) {
	    String currentPrefs = prefs.getString("activeSectors");
	    String prefString = "";
	    for (int i = 0; i < currentPrefs.length(); i++) {
	        if (i < activeSectors.length) {
                if (activeSectors[i]) {
                    prefString += 1;
                } else {
                    prefString += 0;
                }
            } else {
	            prefString += currentPrefs.charAt(i);
            }
        }
        prefs.putString("activeSectors", prefString);
	    prefs.flush();
    }

    public void setActiveSector(int sector, boolean active) {
        String currentSetting = prefs.getString("activeSectors");
        String newSetting = "";
        for (int i = 0; i < currentSetting.length(); i++) {
            if (sector == i) {
                if (active) {
                    newSetting += 1;
                } else {
                    newSetting += 0;
                }
            } else {
                newSetting += currentSetting.charAt(i);
            }
        }
        prefs.putString("activeSectors", newSetting);
        prefs.flush();
    }

    public boolean isSoundOn() {
        return prefs.getBoolean("soundOn");
    }

    public void setSoundOn(boolean soundOn) {
        prefs.putBoolean("soundOn", soundOn);
        prefs.flush();
    }

    public void setPlayerSides(int a) {
	    prefs.putInteger("playerSides", a);
	    prefs.flush();
	}

	public void setSongChoice(String a) {
	    prefs.putString("songChoice", a);
	    prefs.flush();
	}

    public void setDifficulty(String a) {
	    prefs.putString("difficulty", a);
        prefs.flush();
	}

    public void setNoteSpeed(float a) {
	    prefs.putFloat("noteSpeed", a);
	    prefs.flush();
	}

	public Music getSong() {
	    if (getSongChoice().equals("song 1")) {
	        return song1;
	    }
	    else if (getSongChoice().equals("song 2")) {
	        return song2;
	    }
	    else {
	        return song3;
	    }
    }

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

    public int getPlayerSides() {
		return prefs.getInteger("playerSides");
	}

	public float getNoteSpeed() {
		return prefs.getFloat("noteSpeed");
	}

	public float getPlayerDiameter() {
		return prefs.getFloat("playerDiameter");
	}

	public static float getPlayerInradius(float playerDiameter, int playerSides) {
		float playerInradius = (float) (playerDiameter / 2 * Math.cos(Math.PI/playerSides));
		return playerInradius;
	}

	public String getSongChoice() { return prefs.getString("songChoice"); }

    public String getDifficulty() { return prefs.getString("difficulty"); }

    public static Texture getBackgroundTexture() { return background; }

    public static Texture getButtonTexture() { return button; }

    public static Texture getButtonPressedTexture() { return buttonPressed; }

	public static Texture getBackButtonTexture() { return backButton; }

	public static Texture getPauseButtonTexture() { return pauseButton; }

    public static Texture getPlayButtonTexture() { return playButton; }

	public static Texture getPlayAgainButtonTexture() { return playAgainButton; }

    public static Texture getSettingsButtonTexture() { return settingsButton; }

    public static Texture getTextBoxTexture() { return textBox; }

	public static BitmapFont getHeadingFont() { return headingFont; }

    public static BitmapFont getSmallFont() { return smallFont; }

	public static BitmapFont getVerySmallFont() { return verySmallFont; }

	public static BitmapFont getBasicFont() { return basicFont; }

    public static BitmapFont getSmallerHeadingFont() { return smallerHeadingFont; }

    public float getAccelerometerDeadzone() {
        return prefs.getFloat("accelerometerDeadzone");
    }

    public float getAccelerometerMax() {
        return prefs.getFloat("accelerometerMax");
    }

    public float getZeroPointX() {
        return prefs.getFloat("zeroPointX");
    }

    public float getZeroPointY() {
        return prefs.getFloat("zeroPointY");
    }

    public float getZeroPointZ() {
        return prefs.getFloat("zeroPointZ");
    }

	public int getSmoothingSamples() {
		return prefs.getInteger("smoothingSamples");
	}

	// Kalibroi nollapistearvot nykyisiin
    public void calibrateZeroPoint() {
		prefs.putFloat("accelerometerX", Gdx.input.getAccelerometerX());
        prefs.putFloat("accelerometerY", Gdx.input.getAccelerometerY());
        prefs.putFloat("accelerometerZ", Gdx.input.getAccelerometerZ());
        if (prefs.getFloat("accelerometerZ") > 7f) {
            prefs.putBoolean("useAccelerometerX",true);
        } else {
            prefs.putBoolean("useAccelerometerX",false);
        }
        prefs.flush();
	}

    @Override
	public void create () {
	    prefs = Gdx.app.getPreferences("default");
	    if (!prefs.contains("firstTime")) {
	        prefs.putBoolean("firstTime", true);
	        setDefaultPreferences(prefs);
        }
	    batch = new SpriteBatch();
	    shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		fontCamera = new OrthographicCamera();
		fontCamera.setToOrtho(false, SCREEN_WIDTH_PIXELS, SCREEN_HEIGHT_PIXELS);

		setActiveSectors(new boolean[prefs.getInteger("playerSides")]);
		for (int i=0 ; i<prefs.getInteger("playerSides") ; i++) {setActiveSector(i, true); }

		background = new Texture(Gdx.files.internal("Galaxy blue.png"));
        button = new Texture(Gdx.files.internal("nappi1.png"));
        buttonPressed = new Texture(Gdx.files.internal("nappi2.png"));
        pauseButton = new Texture(Gdx.files.internal("pausenappi2.png"));
		backButton = new Texture(Gdx.files.internal("backnappi.png"));
		playButton = new Texture(Gdx.files.internal("playnappi.png"));
		playAgainButton = new Texture(Gdx.files.internal("repeat.png"));
        settingsButton = new Texture(Gdx.files.internal("settingsnappi.png"));
		textBox = new Texture(Gdx.files.internal("folio.png"));

		song1 = Gdx.audio.newMusic(Gdx.files.internal("JauntyGumption.ogg"));
        song2 = Gdx.audio.newMusic(Gdx.files.internal("NyanCat.mp3"));
        song3 = Gdx.audio.newMusic(Gdx.files.internal("WorldisMine.mp3"));

		createFonts();

		setScreen(new MainMenu(this));
	}

	public void createFonts() {
		generator = new FreeTypeFontGenerator(Gdx.files.internal("grove.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        //luodaan vitun pieni fontti
        parameter.size = 25;
        parameter.color = Color.WHITE;
        verySmallFont = generator.generateFont(parameter);

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
