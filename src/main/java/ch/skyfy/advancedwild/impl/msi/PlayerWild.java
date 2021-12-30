package ch.skyfy.advancedwild.impl.msi;

public class PlayerWild {
    public final String uuid;
    public long startTime;
    public int count;
    public PlayerWild(String uuid, long startTime, int count) {
        this.uuid = uuid;
        this.startTime = startTime;
        this.count = count;
    }
}
