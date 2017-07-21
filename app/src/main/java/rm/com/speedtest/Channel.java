package rm.com.speedtest;

import android.support.annotation.NonNull;

public interface Channel {
  String KEY_CHANNEL_CALL = "Key-channel-call";

  @NonNull ChannelCall open(@NonNull Endpoint src, @NonNull Endpoint dest);

  void close(@NonNull ChannelCall channelCall);

  void closeAll();
}
