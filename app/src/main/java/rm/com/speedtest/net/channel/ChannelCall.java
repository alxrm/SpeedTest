package rm.com.speedtest.net.channel;

import android.support.annotation.NonNull;
import okhttp3.Call;

public final class ChannelCall {
  private final CallId callId;
  private final Call call;
  private final Endpoint src;
  private final Endpoint dest;

  public ChannelCall(@NonNull CallId callId, @NonNull Call call, @NonNull Endpoint src,
      @NonNull Endpoint dest) {
    this.call = call;
    this.callId = callId;
    this.src = src;
    this.dest = dest;
  }

  @NonNull Call call() {
    return call;
  }

  @NonNull public Endpoint src() {
    return src;
  }

  @NonNull public Endpoint dest() {
    return dest;
  }

  @NonNull public CallId id() {
    return callId;
  }
}

