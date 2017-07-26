package rm.com.speedtest.speed;

import android.support.annotation.NonNull;

/**
 * Created by alex
 */

public interface SpeedMeasure {
  void updateUnits(long nextUnits);
  void reset();
  @NonNull Speed currentSpeed();
}
