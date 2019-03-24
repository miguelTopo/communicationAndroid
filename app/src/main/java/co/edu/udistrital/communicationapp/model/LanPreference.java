package co.edu.udistrital.communicationapp.model;

public class LanPreference {

    private boolean enable;

    private int priority;

    public LanPreference(boolean enable) {
        this.enable = enable;
    }

    public LanPreference(boolean enable, int priority) {
        this.enable = enable;
        this.priority = priority;
    }

    public boolean isEnable() {
        return enable;
    }

    public int getPriority() {
        return priority;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
