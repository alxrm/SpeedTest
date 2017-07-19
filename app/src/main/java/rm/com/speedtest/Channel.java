package rm.com.speedtest;

import android.support.annotation.NonNull;

public interface Channel {
  String KEY_DOWNLOAD_CALL = "key-download-call";

  @NonNull ChannelCall open(@NonNull Endpoint src, @NonNull Endpoint dest);

  void close(@NonNull ChannelCall channelCall);
}
