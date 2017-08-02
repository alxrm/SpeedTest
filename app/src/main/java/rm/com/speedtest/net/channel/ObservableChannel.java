package rm.com.speedtest.net.channel;

import android.support.annotation.NonNull;

/**
 * Created by alex
 */

public interface ObservableChannel {
  void subscribe(@NonNull ChannelListener listener);

  void unsubscribe(@NonNull ChannelListener listener);
}
