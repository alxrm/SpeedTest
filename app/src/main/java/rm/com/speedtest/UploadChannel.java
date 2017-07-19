package rm.com.speedtest;

import android.support.annotation.NonNull;

/**
 * Created by alex
 */

public final class UploadChannel implements Channel {

  @NonNull @Override public ChannelCall open(@NonNull Endpoint src, @NonNull Endpoint dest) {
    return null;
  }

  @Override public void close(@NonNull ChannelCall channelCall) {

  }
}
