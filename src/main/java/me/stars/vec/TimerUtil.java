package me.stars.vec;

public class TimerUtil {
    public long lastMS;
    private long time;

    private long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }
    public void setTime(long time) {
        lastMS = time;
    }
    public long time() {
        return System.nanoTime() / 1000000L - time;
    }
    public final long getElapsedTime() {
        return this.getCurrentMS() - this.lastMS;
    }

    public void reset() {
        this.lastMS = this.getCurrentMS();
    }

    public boolean delay(float milliSec) {
        if ((float)(this.getTime() - this.lastMS) >= milliSec) {
            return true;
        }
        return false;
    }

    public boolean hasReached(double milliseconds) {
        if ((double)(this.getCurrentMS() - this.lastMS) >= milliseconds) {
            return true;
        }
        return false;
    }

    public long getTime() {
        return System.nanoTime() / 1000000L;
    }

    public boolean isDelayComplete(long delay) {
        if (System.currentTimeMillis() - this.lastMS > delay)
            return true;
        return false;
    }
}

