package rm.com.speedtest.net.channel;

import android.support.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Request;
import rm.com.speedtest.net.Endpoint;

public final class ChannelCall {
  private final Call call;
  private final CallId callId;
  private final Endpoint src;
  private final Endpoint dest;

  public ChannelCall(@NonNull Call call, @NonNull CallId callId, @NonNull Endpoint src,
      @NonNull Endpoint dest) {
    this.call = call;
    this.callId = callId;
    this.src = src;
    this.dest = dest;
  }

  @NonNull public Call call() {
    return call;
  }

  @NonNull public Request request() {
    return call.request();
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
