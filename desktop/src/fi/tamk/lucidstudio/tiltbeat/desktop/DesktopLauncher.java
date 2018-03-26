package fi.tamk.lucidstudio.tiltbeat.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import fi.tamk.lucidstudio.tiltbeat.GameMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Tilt Beat";
		config.width = 1280;
        config.height = 800;
		new LwjglApplication(new GameMain(), config);
	}
}
