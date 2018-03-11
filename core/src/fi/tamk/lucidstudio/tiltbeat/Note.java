package fi.tamk.lucidstudio.tiltbeat;

/**
 * Created by Jaakko on 11.3.2018.
 */

public class Note {
    private int sector;
    private float distance;

    public int getSector() {
        return sector;
    }

    public float getDistance() {
        return distance;
    }
}

class Point extends Note {

}

class Hold extends Note {

}

class Slide extends Note {

}
