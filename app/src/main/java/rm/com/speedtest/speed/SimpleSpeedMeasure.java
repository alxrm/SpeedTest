package rm.com.speedtest.speed;

import android.support.annotation.NonNull;

/**
 * Created by alex
 */

public final class SimpleSpeedMeasure implements SpeedMeasure {
  private long startTimeNano = 0;
  private long lastTimeNano = 0;
  private long unitsCollected = 0;

  @Override public void updateUnits(long nextUnits) {
    if (startTimeNano == 0) {
      lastTimeNano = System.nanoTime();
      startTimeNano = System.nanoTime();
      return;
    }

    lastTimeNano = System.nanoTime();
    unitsCollected = nextUnits;
  }

  @Override public void reset() {
    startTimeNano = 0;
    lastTimeNano = 0;
    unitsCollected = 0;
  }

  @NonNull @Override public Speed currentSpeed() {
    final long timeSpan = lastTimeNano - startTimeNano;

    return new Speed(timeSpan, unitsCollected);
  }
}
