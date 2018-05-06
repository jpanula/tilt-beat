package fi.tamk.lucidstudio.tiltbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameMain extends Game {
    private AssetManager manager;
	private Preferences prefs;
	private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	private OrthographicCamera fontCamera;
	private final float SCREEN_WIDTH = 16;
	private final float SCREEN_HEIGHT = 10;
	private final float SCREEN_WIDTH_PIXELS = 1280;
	private final float SCREEN_HEIGHT_PIXELS = 800;
	private Sound soundEffect;
    private Music song1;
    private Music song2;
    private Music song3;
    private Texture background;
    private Texture button;
    private Texture buttonPressed;
    private Texture buttonGreen;
	private Texture pauseButton;
	private Texture backButton;
    private Texture playButton;
	private Texture playAgainButton;
    private Texture settingsButton;
    private Texture textBox;
    private BitmapFont verySmallFont;
    private BitmapFont smallFont;
    private BitmapFont basicFont;
    private BitmapFont bigFont;
    private BitmapFont headingFont;
    private BitmapFont smallerHeadingFont;

	public void setDefaultPreferences(Preferences prefs) {
        prefs.putInteger("playerSides", 8);
        prefs.putFloat("noteSpeed", 2f);
        prefs.putFloat("playerDiameter", 3f);
        prefs.putFloat("accelerometerDeadzone", 0.25f);
        prefs.putFloat("accelerometerMax", 3f);
        prefs.putString("songChoice", "song 1");
        prefs.putString("difficulty", "normal");
        prefs.putBoolean("soundOn", true);
        prefs.putBoolean("effectsOn", true);
        prefs.putBoolean("highscoreOn", true);
        prefs.putFloat("zeroPointX", 0);
        prefs.putFloat("zeroPointY", 0);
        prefs.putFloat("zeroPointZ", 0);
        prefs.putInteger("smoothingSamples", 23);
        prefs.putString("activeSectors", "1111111111");
        prefs.putBoolean("useAccelerometerX", false);
        prefs.putBoolean("tiltedSquare", false);
        prefs.putString("language", "en");

        prefs.flush();
    }

    public void useFinnish() {
	    prefs.putString("play", "pelaa");
        prefs.putString("mods", "modit");
        prefs.putString("settings", "asetukset");
        prefs.putString("settingsBig", "Asetukset");
        prefs.putString("easy", "helppo");
        prefs.putString("normal", "normaali");
        prefs.putString("hard", "vaikea");
        prefs.putString("backbreaker", "selanrikkoja");
        prefs.putString("easyDescription", "hauskaa! hauskaa!\naloittelijoille");
        prefs.putString("normalDescription", "hauskaa!\nperus mode");
        prefs.putString("hardDescription", "hauskaa?\nhc pelaajille");
        prefs.putString("bbDescription", "ei hauskaa\net selvia hengissa");
        prefs.putString("sound", "aanet");
        prefs.putString("calibration", "kalibrointi");
        prefs.putString("difficultyHead", "Vaikeus");
        prefs.putString("default", "oletus");
        prefs.putString("modifications", "Muokattavuus");
        prefs.putString("hsOffline", "highscore ei tallennu");
        prefs.putString("modsText1", "klikkaamalla saat");
        prefs.putString("modsText2", "sektorit paalle");
        prefs.putString("modsText3", "tai pois paalta");
        prefs.putString("4-side", "4-kulmio");
        prefs.putString("6-side", "6-kulmio");
        prefs.putString("8-side", "8-kulmio");
        prefs.putString("10-side", "10-kulmio");
        prefs.putString("points", "pisteet");
        prefs.putString("youGot", "sait ");
        prefs.putString("youGot2", " pistetta!!");
        prefs.putString("mainMenu", "paavalikko");
        prefs.putString("mainMenu2", "paa-");
        prefs.putString("startAgain", "uudestaan?");
        prefs.putString("youDidIt", "teit sen!!!");
        prefs.putString("continue", "jatka");
        prefs.putString("retry", "yrita");
        prefs.putString("back", "takaisin");
        prefs.putString("seeHighscore", "katso highscoret");
        prefs.putString("effectsAreOn", "efektit paalla");
        prefs.putString("effectsAreOff", "efektit pois");
        prefs.putString("pause", "TAUKO");
        prefs.putString("stayStill", "pysy paikallasi");
        prefs.putString("dontMove", " ala liiku");
        prefs.putString("done", "    valmis!");
        prefs.flush();
    }

    public void useEnglish() {
        prefs.putString("play", "play");
        prefs.putString("mods", "mods");
        prefs.putString("settings", "settings");
        prefs.putString("settingsBig", "Settings");
        prefs.putString("easy", "easy");
        prefs.putString("normal", "normal");
        prefs.putString("hard", "hard");
        prefs.putString("backbreaker", "backbreaker");
        prefs.putString("easyDescription", "fun! fun! fun!\nfor beginners");
        prefs.putString("normalDescription", "fun!\nbasic mode");
        prefs.putString("hardDescription", "fun?\nfor hc players");
        prefs.putString("bbDescription", "not fun\nyou will die");
        prefs.putString("sound", "sound");
        prefs.putString("calibration", "calibration");
        prefs.putString("difficultyHead", "Difficulty");
        prefs.putString("default", "default");
        prefs.putString("modifications", "Modifications");
        prefs.putString("hsOffline", "highscore offline");
        prefs.putString("modsText1", "click sectors to");
        prefs.putString("modsText2", "activate or");
        prefs.putString("modsText3", "de-activate them");
        prefs.putString("4-side", "4-side");
        prefs.putString("6-side", "6-side");
        prefs.putString("8-side", "8-side");
        prefs.putString("10-side", "10-side");
        prefs.putString("points", "points");
        prefs.putString("youGot", "you got ");
        prefs.putString("youGot2", " points!!");
        prefs.putString("mainMenu", "main menu");
        prefs.putString("mainMenu2", "main\nmenu");
        prefs.putString("startAgain", "start again?");
        prefs.putString("youDidIt", "you did it!!!");
        prefs.putString("continue", "continue");
        prefs.putString("retry", "retry");
        prefs.putString("back", "back");
        prefs.putString("seeHighscore", "see highscores");
        prefs.putString("effectsAreOn", "effects: on");
        prefs.putString("effectsAreOff", "effects: off");
        prefs.putString("pause", "PAUSE");
        prefs.putString("stayStill", "  stay still");
        prefs.putString("dontMove", "don't move");
        prefs.putString("done", "     done!");
        prefs.flush();
    }

    public void resetHighscore(Preferences prefs) {
        prefs.putString("1stS", "");
        prefs.putString("2ndS", "");
        prefs.putString("3rdS", "");
        prefs.putString("4thS", "");
        prefs.putString("5thS", "");
        prefs.putInteger("1st", 0);
        prefs.putInteger("2nd", 0);
        prefs.putInteger("3rd", 0);
        prefs.putInteger("4th", 0);
        prefs.putInteger("5th", 0);

        prefs.putString("1stSN", "");
        prefs.putString("2ndSN", "");
        prefs.putString("3rdSN", "");
        prefs.putString("4thSN", "");
        prefs.putString("5thSN", "");
        prefs.putInteger("1stN", 0);
        prefs.putInteger("2ndN", 0);
        prefs.putInteger("3rdN", 0);
        prefs.putInteger("4thN", 0);
        prefs.putInteger("5thN", 0);

        prefs.putString("1stSH", "");
        prefs.putString("2ndSH", "");
        prefs.putString("3rdSH", "");
        prefs.putString("4thSH", "");
        prefs.putString("5thSH", "");
        prefs.putInteger("1stH", 0);
        prefs.putInteger("2ndH", 0);
        prefs.putInteger("3rdH", 0);
        prefs.putInteger("4thH", 0);
        prefs.putInteger("5thH", 0);

        prefs.putString("1stSB", "");
        prefs.putString("2ndSB", "");
        prefs.putString("3rdSB", "");
        prefs.putString("4thSB", "");
        prefs.putString("5thSB", "");
        prefs.putInteger("1stB", 0);
        prefs.putInteger("2ndB", 0);
        prefs.putInteger("3rdB", 0);
        prefs.putInteger("4thB", 0);
        prefs.putInteger("5thB", 0);

        prefs.flush();

    }

    public void setEasyHighscore(int a, int score, int[] highScores) {
	    switch (a) {
            case 1: prefs.putInteger("1st", score);
                    prefs.putInteger("2nd", highScores[0]);
                    prefs.putInteger("3rd", highScores[1]);
                    prefs.putInteger("4th", highScores[2]);
                    prefs.putInteger("5th", highScores[3]); break;
            case 2: prefs.putInteger("2nd", score);
                    prefs.putInteger("3rd", highScores[1]);
                    prefs.putInteger("4th", highScores[2]);
                    prefs.putInteger("5th", highScores[3]); break;
            case 3: prefs.putInteger("3rd", score);
                    prefs.putInteger("4th", highScores[2]);
                    prefs.putInteger("5th", highScores[3]); break;
            case 4: prefs.putInteger("4th", score);
                    prefs.putInteger("5th", highScores[3]); break;
            case 5: prefs.putInteger("5th", score); break;
        }
        prefs.flush();
    }

    public void setNormalHighscore(int a, int score, int[] highScores) {
        switch (a) {
            case 1: prefs.putInteger("1stN", score);
                prefs.putInteger("2ndN", highScores[0]);
                prefs.putInteger("3rdN", highScores[1]);
                prefs.putInteger("4thN", highScores[2]);
                prefs.putInteger("5thN", highScores[3]); break;
            case 2: prefs.putInteger("2ndN", score);
                prefs.putInteger("3rdN", highScores[1]);
                prefs.putInteger("4thN", highScores[2]);
                prefs.putInteger("5thN", highScores[3]); break;
            case 3: prefs.putInteger("3rdN", score);
                prefs.putInteger("4thN", highScores[2]);
                prefs.putInteger("5thN", highScores[3]); break;
            case 4: prefs.putInteger("4thN", score);
                prefs.putInteger("5thN", highScores[3]); break;
            case 5: prefs.putInteger("5thN", score); break;
        }
        prefs.flush();
    }

    public void setHardHighscore(int a, int score, int[] highScores) {
        switch (a) {
            case 1: prefs.putInteger("1stH", score);
                prefs.putInteger("2ndH", highScores[0]);
                prefs.putInteger("3rdH", highScores[1]);
                prefs.putInteger("4thH", highScores[2]);
                prefs.putInteger("5thH", highScores[3]); break;
            case 2: prefs.putInteger("2ndH", score);
                prefs.putInteger("3rdH", highScores[1]);
                prefs.putInteger("4thH", highScores[2]);
                prefs.putInteger("5thH", highScores[3]); break;
            case 3: prefs.putInteger("3rdH", score);
                prefs.putInteger("4thH", highScores[2]);
                prefs.putInteger("5thH", highScores[3]); break;
            case 4: prefs.putInteger("4thH", score);
                prefs.putInteger("5thH", highScores[3]); break;
            case 5: prefs.putInteger("5thH", score); break;
        }
        prefs.flush();
    }

    public void setBbHighscore(int a, int score, int[] highScores) {
        switch (a) {
            case 1: prefs.putInteger("1stB", score);
                prefs.putInteger("2ndB", highScores[0]);
                prefs.putInteger("3rdB", highScores[1]);
                prefs.putInteger("4thB", highScores[2]);
                prefs.putInteger("5thB", highScores[3]); break;
            case 2: prefs.putInteger("2ndB", score);
                prefs.putInteger("3rdB", highScores[1]);
                prefs.putInteger("4thB", highScores[2]);
                prefs.putInteger("5thB", highScores[3]); break;
            case 3: prefs.putInteger("3rdB", score);
                prefs.putInteger("4thB", highScores[2]);
                prefs.putInteger("5thB", highScores[3]); break;
            case 4: prefs.putInteger("4thB", score);
                prefs.putInteger("5thB", highScores[3]); break;
            case 5: prefs.putInteger("5thB", score); break;
        }
        prefs.flush();
    }

    public String getLanguage() {
	    return prefs.getString("language");
    }

    public void setLanguage(String id) {
	    id = id.toLowerCase();
	    if (id.equals("fi") || id.equals("en")) {
	        prefs.putString("language", id);
        } else {
	        throw new IllegalArgumentException("Not a supported language");
        }
    }

    public boolean isUseAccelerometerX() {
	    return prefs.getBoolean("useAccelerometerX");
    }

    public void setUseAccelerometerX(boolean useAccelerometerX) {
	    prefs.putBoolean("useAccelerometerX", useAccelerometerX);
    }

    public boolean isTiltedSquare() {
	    return prefs.getBoolean("tiltedSquare");
    }

    public void setTiltedSquare(boolean tilted) {
	    prefs.putBoolean("tiltedSquare", tilted);
        prefs.flush();
    }

    public AssetManager getManager() {
        return manager;
    }

    public Preferences getPrefs() {
        return prefs;
    }

    public float getSCREEN_WIDTH_PIXELS() {
        return SCREEN_WIDTH_PIXELS;
    }

    public float getSCREEN_HEIGHT_PIXELS() {
        return SCREEN_HEIGHT_PIXELS;
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

    public int howManySectorsActive() {
	    int a = 0;
        String prefString = prefs.getString("activeSectors");
        for (int i=0 ; i<getPlayerSides() ; i++) {
            if (prefString.charAt(i) == '1') {
                a++;
            }
        }
	    return a;
    }

    public boolean isSoundOn() {
        return prefs.getBoolean("soundOn");
    }

    public void setSoundOn(boolean a) {
        prefs.putBoolean("soundOn", a);
        prefs.flush();
    }

    public boolean isEffectOn() {
        return prefs.getBoolean("effectsOn");
    }

    public void setEffectsOn(boolean a) {
        prefs.putBoolean("effectsOn", a);
        prefs.flush();
    }

    public boolean isHighscoreOn() {
        return prefs.getBoolean("highscoreOn");
    }

    public void setHighscoreOn(boolean a) {
        prefs.putBoolean("highscoreOn", a);
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

    public Sound getEffect() {
	    return soundEffect;
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

    public float getScreenWidth() {
        return SCREEN_WIDTH;
    }

    public float getScreenHeight() {
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

	public float getPlayerInradius() {
		float playerInradius = (float) (getPlayerDiameter() / 2 * Math.cos(Math.PI/getPlayerSides()));
		return playerInradius;
	}

	public String getSongChoice() { return prefs.getString("songChoice"); }

    public String getDifficulty() { return prefs.getString("difficulty"); }

    public Texture getBackgroundTexture() { return background; }

    public Texture getButtonTexture() { return button; }

    public Texture getButtonPressedTexture() { return buttonPressed; }

    public Texture getButtonGreenTexture() { return buttonGreen; }

	public Texture getBackButtonTexture() { return backButton; }

	public Texture getPauseButtonTexture() { return pauseButton; }

    public Texture getPlayButtonTexture() { return playButton; }

	public Texture getPlayAgainButtonTexture() { return playAgainButton; }

    public Texture getSettingsButtonTexture() { return settingsButton; }

    public Texture getTextBoxTexture() { return textBox; }

    public BitmapFont getHeadingFont() { return headingFont; }

    public BitmapFont getSmallFont() { return smallFont; }

    public BitmapFont getVerySmallFont() { return verySmallFont; }

    public BitmapFont getBasicFont() { return basicFont; }

    public BitmapFont getBigFont() { return bigFont; }

    public BitmapFont getSmallerHeadingFont() { return smallerHeadingFont; }

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

    public int[] getEasyScores() {
	    int[] a = new int[5];
	    a[0] = prefs.getInteger("1st");
        a[1] = prefs.getInteger("2nd");
        a[2] = prefs.getInteger("3rd");
        a[3] = prefs.getInteger("4th");
        a[4] = prefs.getInteger("5th");
        return a;
    }

    public String[] getEasyNames() {
        String[] a = new String[5];
        a[0] = prefs.getString("1stS");
        a[1] = prefs.getString("2ndS");
        a[2] = prefs.getString("3rdS");
        a[3] = prefs.getString("4thS");
        a[4] = prefs.getString("5thS");
        return a;
    }

    public int[] getNormalScores() {
        int[] a = new int[5];
        a[0] = prefs.getInteger("1stN");
        a[1] = prefs.getInteger("2ndN");
        a[2] = prefs.getInteger("3rdN");
        a[3] = prefs.getInteger("4thN");
        a[4] = prefs.getInteger("5thN");
        return a;
    }

    public String[] getNormalNames() {
        String[] a = new String[5];
        a[0] = prefs.getString("1stSN");
        a[1] = prefs.getString("2ndSN");
        a[2] = prefs.getString("3rdSN");
        a[3] = prefs.getString("4thSN");
        a[4] = prefs.getString("5thSN");
        return a;
    }

    public int[] getHardScores() {
        int[] a = new int[5];
        a[0] = prefs.getInteger("1stH");
        a[1] = prefs.getInteger("2ndH");
        a[2] = prefs.getInteger("3rdH");
        a[3] = prefs.getInteger("4thH");
        a[4] = prefs.getInteger("5thH");
        return a;
    }

    public String[] getHardNames() {
        String[] a = new String[5];
        a[0] = prefs.getString("1stSH");
        a[1] = prefs.getString("2ndSH");
        a[2] = prefs.getString("3rdSH");
        a[3] = prefs.getString("4thSH");
        a[4] = prefs.getString("5thSH");
        return a;
    }

    public int[] getBbScores() {
        int[] a = new int[5];
        a[0] = prefs.getInteger("1stB");
        a[1] = prefs.getInteger("2ndB");
        a[2] = prefs.getInteger("3rdB");
        a[3] = prefs.getInteger("4thB");
        a[4] = prefs.getInteger("5thB");
        return a;
    }

    public String[] getBbNames() {
        String[] a = new String[5];
        a[0] = prefs.getString("1stSB");
        a[1] = prefs.getString("2ndSB");
        a[2] = prefs.getString("3rdSB");
        a[3] = prefs.getString("4thSB");
        a[4] = prefs.getString("5thSB");
        return a;
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
        manager = new AssetManager(new InternalFileHandleResolver());
	    prefs = Gdx.app.getPreferences("default");
	    if (!prefs.contains("firstTime")) {
	        prefs.putBoolean("firstTime", true);
	        setDefaultPreferences(prefs);
	        resetHighscore(prefs);
        }
	    batch = new SpriteBatch();
	    shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, getScreenWidth(), getScreenHeight());
		fontCamera = new OrthographicCamera();
		fontCamera.setToOrtho(false, getSCREEN_WIDTH_PIXELS(), getSCREEN_HEIGHT_PIXELS());

		setActiveSectors(new boolean[prefs.getInteger("playerSides")]);
		for (int i=0 ; i<prefs.getInteger("playerSides") ; i++) {setActiveSector(i, true); }

        manager.load("Galaxy blue.png", Texture.class);
		manager.load("nappi1.png", Texture.class);
		manager.load("nappi2.png", Texture.class);
        manager.load("nappivihree.png", Texture.class);
		manager.load("pausenappi2.png", Texture.class);
		manager.load("backnappi.png", Texture.class);
		manager.load("playnappi.png", Texture.class);
		manager.load("repeat.png", Texture.class);
		manager.load("settingsnappi.png", Texture.class);
        manager.load("folio.png", Texture.class);

		manager.finishLoading();

		background = manager.get("Galaxy blue.png");
        button = manager.get("nappi1.png");
        buttonPressed = manager.get("nappi2.png");
        buttonGreen = manager.get("nappivihree.png");
        pauseButton = manager.get("pausenappi2.png");
		backButton = manager.get("backnappi.png");
		playButton = manager.get("playnappi.png");
		playAgainButton = manager.get("repeat.png");
        settingsButton = manager.get("settingsnappi.png");
		textBox = manager.get("folio.png");

        manager.load("JauntyGumption.ogg", Music.class);
        manager.load("NyanCat.mp3", Music.class);
        manager.load("takeOnMe.mp3", Music.class);
        manager.load("soundeffect2.wav", Sound.class);

        manager.finishLoading();

		song1 = manager.get("JauntyGumption.ogg");
        song2 = manager.get("NyanCat.mp3");
        song3 = manager.get("takeOnMe.mp3");
        soundEffect = manager.get("soundeffect2.wav");

		createFonts();

		setScreen(new MainMenu(this));
	}

	public void createFonts() {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        //luodaan vitun pieni fontti
        FreetypeFontLoader.FreeTypeFontLoaderParameter verySmallFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        verySmallFontParameter.fontFileName = "grove.ttf";
        verySmallFontParameter.fontParameters.size = 25;
        verySmallFontParameter.fontParameters.color = Color.WHITE;
        manager.load("verySmallFont.ttf", BitmapFont.class, verySmallFontParameter);

		//luodaan pieni fontti
        FreetypeFontLoader.FreeTypeFontLoaderParameter smallFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        smallFontParameter.fontFileName = "grove.ttf";
        smallFontParameter.fontParameters.size = 40;
        smallFontParameter.fontParameters.color = Color.WHITE;
        manager.load("smallFont.ttf", BitmapFont.class, smallFontParameter);

		//luodaan perusfontti
        FreetypeFontLoader.FreeTypeFontLoaderParameter basicFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        basicFontParameter.fontFileName = "grove.ttf";
        basicFontParameter.fontParameters.size = 50;
        basicFontParameter.fontParameters.color = Color.WHITE;
        manager.load("basicFont.ttf", BitmapFont.class, basicFontParameter);

        //luodaan iso fontti
        FreetypeFontLoader.FreeTypeFontLoaderParameter bigFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        bigFontParameter.fontFileName = "grove.ttf";
        bigFontParameter.fontParameters.size = 80;
        bigFontParameter.fontParameters.color = Color.WHITE;
        bigFontParameter.fontParameters.borderColor = Color.BLACK;
        bigFontParameter.fontParameters.borderWidth = 3;
        manager.load("bigFont.ttf", BitmapFont.class, bigFontParameter);

		//luodaan otsikkofontti
        FreetypeFontLoader.FreeTypeFontLoaderParameter headingFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        headingFontParameter.fontFileName = "grove.ttf";
        headingFontParameter.fontParameters.size = 150;
        headingFontParameter.fontParameters.color = Color.CYAN;
        headingFontParameter.fontParameters.borderColor = Color.BLACK;
        headingFontParameter.fontParameters.borderWidth = 3;
        manager.load("headingFont.ttf", BitmapFont.class, headingFontParameter);

        FreetypeFontLoader.FreeTypeFontLoaderParameter smallerHeadingFontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        smallerHeadingFontParameter.fontFileName = "grove.ttf";
        smallerHeadingFontParameter.fontParameters.size = 100;
        smallerHeadingFontParameter.fontParameters.color = Color.CYAN;
        smallerHeadingFontParameter.fontParameters.borderColor = Color.BLACK;
        smallerHeadingFontParameter.fontParameters.borderWidth = 3;
        manager.load("smallerHeadingFont.ttf", BitmapFont.class, smallerHeadingFontParameter);

        manager.finishLoading();

        verySmallFont = manager.get("verySmallFont.ttf", BitmapFont.class);
        smallFont = manager.get("smallFont.ttf", BitmapFont.class);
        basicFont = manager.get("basicFont.ttf", BitmapFont.class);
        bigFont = manager.get("bigFont.ttf", BitmapFont.class);
        headingFont = manager.get("headingFont.ttf", BitmapFont.class);
        smallerHeadingFont = manager.get("smallerHeadingFont.ttf", BitmapFont.class);
	}

	@Override
	public void render () {
	    super.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
		manager.dispose();
		//gameScreen.dispose();

	}

}
