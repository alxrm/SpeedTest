package rm.com.speedtest.speed;

import android.support.annotation.NonNull;
import java.util.concurrent.TimeUnit;

public final class Speed {
  private final long timeSpanMillis;
  private final long units;

  public Speed(long timeSpanMillis, long units) {
    this.timeSpanMillis = timeSpanMillis;
    this.units = units;
  }

  public long timeSpanMillis() {
    return timeSpanMillis;
  }

  public long units() {
    return units;
  }

  public double unitsPerTime(@NonNull TimeUnit scale) {
    // 40 units, 800 millis, 40/800 units/milli, 40 / (800 / second.asMillis) units/second

    if (timeSpanMillis == 0) {
      return 0;
    }

    final double scaledTime = ((double) timeSpanMillis) / scale.toMillis(1);

    return units / scaledTime;
  }
}
