package rm.com.speedtest.speed;

import android.support.annotation.NonNull;

/**
 * Created by alex
 */

public final class SimpleSpeedMeasure implements SpeedMeasure {
  private long startTimeMillis = 0;
  private long lastTimeMillis = 0;
  private long unitsCollected = 0;

  @Override public void updateUnits(long nextUnits) {
    lastTimeMillis = System.currentTimeMillis();

    if (startTimeMillis == 0) {
      startTimeMillis = System.currentTimeMillis();
      return;
    }

    unitsCollected = nextUnits;
  }

  @Override public void reset() {
    startTimeMillis = 0;
    lastTimeMillis = 0;
    unitsCollected = 0;
  }

  @NonNull @Override public Speed currentSpeed() {
    final long timeSpan = lastTimeMillis - startTimeMillis;

    return new Speed(timeSpan, unitsCollected);
  }
}
