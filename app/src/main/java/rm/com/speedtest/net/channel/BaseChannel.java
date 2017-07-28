package rm.com.speedtest.net.channel;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import rm.com.speedtest.net.Endpoint;

/**
 * Created by alex
 */

@SuppressWarnings("WeakerAccess") //
public abstract class BaseChannel<C extends BaseChannel, B extends AbstractChannelBuilder<C, B>>
    implements Channel, ObservableChannel {

  final HashMap<String, ChannelCall> channelCalls;
  final ArrayList<ChannelProgressListener> progressSubscribers;
  final OkHttpClient httpClient;
  final Modification modification;

  public BaseChannel(@NonNull B builder) {
    this.channelCalls = builder.channelCalls;
    this.progressSubscribers = builder.progressSubscribers;
    this.httpClient = httpClientOf(builder.httpClient);
    this.modification = builder.modification;
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

  @Override public final void subscribe(@NonNull ChannelProgressListener listener) {
    progressSubscribers.add(listener);
  }

  @Override public final void unsubscribe(@NonNull ChannelProgressListener listener) {
    progressSubscribers.remove(listener);
  }

  @Override
  public void update(@NonNull String tag, long bytesPassed, long contentLength, boolean done) {
    for (final ChannelProgressListener subscriber : progressSubscribers) {
      subscriber.update(tag, bytesPassed, contentLength, done);
    }
  }

  @NonNull public abstract B newBuilder();

  @NonNull
  protected abstract Request defaultRequestOf(@NonNull CallId callId, @NonNull Endpoint src,
      @NonNull Endpoint dest);

  @NonNull protected OkHttpClient httpClientOf(@NonNull OkHttpClient httpClient) {
    return httpClient;
  }

  protected <I extends Interceptor> void addUniqueInterceptor(
      @NonNull List<Interceptor> originalInterceptorsMutable, @NonNull I toAdd) {
    final int size = originalInterceptorsMutable.size();
    final ArrayList<Interceptor> interceptors = new ArrayList<>(size);

    for (final Interceptor interceptor : originalInterceptorsMutable) {
      if (!toAdd.getClass().isInstance(interceptor)) {
        interceptors.add(interceptor);
      }
    }

    originalInterceptorsMutable.clear();
    originalInterceptorsMutable.addAll(interceptors);
    originalInterceptorsMutable.add(toAdd);
  }

  @NonNull
  private Request requestOf(@NonNull CallId callId, @NonNull Endpoint src, @NonNull Endpoint dest) {
    final Request defaultRequest = defaultRequestOf(callId, src, dest);

    return afterModification(defaultRequest, src, dest).newBuilder()
        .header(Channel.KEY_CHANNEL_CALL, callId.string())
        .build();
  }

  @NonNull private Request afterModification(@NonNull Request defaultRequest, @NonNull Endpoint src,
      @NonNull Endpoint dest) {
    return modification == null ? defaultRequest : modification.apply(defaultRequest, src, dest);
  }
}
