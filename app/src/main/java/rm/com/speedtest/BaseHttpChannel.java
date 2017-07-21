package rm.com.speedtest;

import android.support.annotation.NonNull;
import java.util.HashMap;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by alex
 */

abstract public class BaseHttpChannel implements Channel, ChannelProgressListener {
  private static final int DEFAULT_CALL_AMOUNT = 12;

  final HashMap<String, ChannelCall> channelCalls;

  private final OkHttpClient httpClient;
  private Modification modification;

  public BaseHttpChannel(@NonNull OkHttpClient httpClient) {
    this.channelCalls = new HashMap<>(DEFAULT_CALL_AMOUNT);
    this.httpClient = httpClientOf(httpClient);
  }

  @NonNull @Override public ChannelCall open(@NonNull Endpoint src, @NonNull Endpoint dest) {
    final CallId callId = new CallId();
    final Call rawCall = httpClient.newCall(requestOf(callId, src, dest));
    final ChannelCall pendingCall = new ChannelCall(rawCall, callId, src, dest);

    channelCalls.put(callId.string(), pendingCall);

    return pendingCall;
  }

  @Override public void close(@NonNull ChannelCall channelCall) {
    channelCalls.remove(channelCall.id().string());
    channelCall.call().cancel();
  }

  @Override public void closeAll() {
    for (final ChannelCall item : channelCalls.values()) {
      item.call().cancel();
    }

    channelCalls.clear();
  }

  public final void after(@NonNull Modification afterModification) {
    modification = afterModification;
  }

  @Override
  public void update(@NonNull String tag, long bytesPassed, long contentLength, boolean done) {
  }

  @NonNull
  protected abstract Request defaultRequestOf(@NonNull CallId callId, @NonNull Endpoint src,
      @NonNull Endpoint dest);

  @NonNull protected OkHttpClient httpClientOf(@NonNull OkHttpClient httpClient) {
    return httpClient;
  }

  @NonNull
  private Request requestOf(@NonNull CallId callId, @NonNull Endpoint src, @NonNull Endpoint dest) {
    final Request defaultRequest = defaultRequestOf(callId, src, dest);

    return modification == null ? defaultRequest : modification.apply(defaultRequest, src, dest);
  }
}
