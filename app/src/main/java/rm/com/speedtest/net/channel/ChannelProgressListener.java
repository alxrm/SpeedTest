package rm.com.speedtest.net.channel;

import android.support.annotation.NonNull;

/**
 * Created by alex
 */

public interface ChannelProgressListener {
  void update(@NonNull String tag, long bytesPassed, long contentLength, boolean done);
}
