package rm.com.speedtest.net.channel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface Channel {
  String KEY_CHANNEL_CALL = "Key-channel-call";

  @NonNull ChannelCall open(@NonNull Endpoint src, @NonNull Endpoint dest);

  void close(@Nullable ChannelCall channelCall);

  void closeAll();
}
