package rm.com.speedtest.net.channel;

import android.support.annotation.NonNull;
import rm.com.speedtest.net.Endpoint;

public interface Channel {
  String KEY_CHANNEL_CALL = "Key-channel-call";

  @NonNull ChannelCall open(@NonNull Endpoint src, @NonNull Endpoint dest);

  void close(@NonNull ChannelCall channelCall);

  void closeAll();
}
