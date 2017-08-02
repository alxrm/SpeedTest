package rm.com.speedtest.net.channel;

import android.support.annotation.NonNull;

/**
 * Created by alex
 */

public interface LoadingProgressListener {
  String EMPTY_CALL = "EMPTY_CALL";

  void onProgress(@NonNull String callId, long bytesPassed, long contentLength, boolean done);
}
