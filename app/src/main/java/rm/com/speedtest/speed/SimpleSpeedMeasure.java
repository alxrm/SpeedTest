package rm.com.speedtest.speed;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex
 */

public final class SimpleSpeedMeasure implements SpeedMeasure {
  private final List<Speed> measurements = new ArrayList<>(50);

  private long startTimeNano = 0;
  private long lastTimeNano = 0;
  private long unitsCollected = 0;

  @Override public void updateUnits(long nextUnits) {
    if (startTimeNano == 0) {
      throw new IllegalStateException("You must call begin() before updating units");
    }

    lastTimeNano = System.nanoTime();
    unitsCollected = nextUnits;

    measurements.add(currentSpeed());
  }

  @Override public void begin() {
    reset();

    startTimeNano = System.nanoTime();
  }

  @Override public void reset() {
    measurements.clear();
    startTimeNano = 0;
    lastTimeNano = 0;
    unitsCollected = 0;
  }

  @NonNull @Override public Speed currentSpeed() {
    final long timeSpan = lastTimeNano - startTimeNano;

    return new Speed(timeSpan, unitsCollected);
  }
}
