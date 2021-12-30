package ch.skyfy.advancedwild.bigbigtrash;

import javax.xml.crypto.Data;
import java.util.List;

public class PlayerWild {
    public final String uuid;
    public long startTime;
    public PlayerWild(String uuid) {
        this.uuid = uuid;
        startTime = System.currentTimeMillis();
    }
}
