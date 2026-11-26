package smarthome.structural;

public class OldFan {
    private boolean spinning;
    private int speedLevel;

    public void startSpinning() { spinning = true; }
    public void stopSpinning() { spinning = false; }
    public void setSpeedLevel(int level) {
        if (level < 0) level = 0;
        if (level > 3) level = 3;
        speedLevel = level;
    }

    public boolean isSpinning() { return spinning; }
    public int getSpeedLevel() { return speedLevel; }
}