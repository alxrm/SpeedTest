package rm.com.speedtest;

import android.support.annotation.NonNull;

/**
 * Created by alex
 */

public interface ObservableChannel extends ChannelProgressListener {
  void subscribe(@NonNull ChannelProgressListener listener);
  void unsubscribe(@NonNull ChannelProgressListener listener);
}