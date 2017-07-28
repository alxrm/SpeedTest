package rm.com.speedtest.speed;

import android.support.annotation.NonNull;
import java.util.concurrent.TimeUnit;

public final class Speed {
  private final long timeSpanNanos;
  private final long units;

  public Speed(long timeSpanNanos, long units) {
    this.timeSpanNanos = timeSpanNanos;
    this.units = units;
  }

  public long timeSpanNanos() {
    return timeSpanNanos;
  }

  public long units() {
    return units;
  }

  public double unitsPerTime(@NonNull TimeUnit scale) {
    // 40 units, 800 nano, 40/800 units/nano, 40 / (800 / second.asNanos) units/second

    if (timeSpanNanos == 0) {
      return 0;
    }

    final double scaledTime = ((double) timeSpanNanos) / scale.toNanos(1);

    return units / scaledTime;
  }
}
