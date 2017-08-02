package rm.com.speedtest.net.channel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by alex
 */

@SuppressWarnings("WeakerAccess") //
public abstract class BaseChannel<C extends BaseChannel, B extends AbstractChannelBuilder<C, B>>
    implements Channel, ObservableChannel, LoadingProgressListener, Callback {

  final HashMap<String, ChannelCall> channelCalls;
  final ArrayList<ChannelListener> subscribers;
  final OkHttpClient httpClient;
  final Modification modification;

  public BaseChannel(@NonNull B builder) {
    this.channelCalls = builder.channelCalls;
    this.subscribers = builder.progressSubscribers;
    this.httpClient = httpClientOf(builder.httpClient);
    this.modification = builder.modification;
  }

  @NonNull @Override public ChannelCall open(@NonNull Endpoint src, @NonNull Endpoint dest) {
    final CallId callId = new CallId();
    final Call rawCall = httpClient.newCall(requestOf(callId, src, dest));
    final ChannelCall pendingCall = new ChannelCall(callId, rawCall, src, dest);

    channelCalls.put(callId.string(), pendingCall);
    rawCall.enqueue(this);

    return pendingCall;
  }

  @Override public void close(@NonNull ChannelCall channelCall) {
    final String callId = channelCall.id().string();
    final ChannelCall call = channelCalls.get(callId);

    if (call == null) {
      return;
    }

    channelCalls.remove(callId);
    call.call().cancel();
  }

  @Override public void closeAll() {
    for (final ChannelCall item : channelCalls.values()) {
      item.call().cancel();
    }

    channelCalls.clear();
  }

  @Override public final void subscribe(@NonNull ChannelListener listener) {
    if (!subscribers.contains(listener)) {
      subscribers.add(listener);
    }
  }

  @Override public final void unsubscribe(@NonNull ChannelListener listener) {
    subscribers.remove(listener);
  }

  @Override public void onProgress(@NonNull String callId, long bytesPassed, long contentLength,
      boolean done) {
    for (final ChannelListener subscriber : subscribers) {
      subscriber.onProgress(callId, bytesPassed, contentLength, done);
    }
  }

  @Override public void onFailure(Call call, IOException error) {
    final String callId = removeSafely(call);

    if (callId == null) {
      return;
    }

    for (final ChannelListener subscriber : subscribers) {
      subscriber.onFailure(callId, call, error);
    }
  }

  @Override public void onResponse(Call call, Response response) throws IOException {
    final String callId = removeSafely(call);

    if (callId == null) {
      return;
    }

    for (final ChannelListener subscriber : subscribers) {
      subscriber.onSuccess(callId, call, response);
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
    final ArrayList<Interceptor> uniqueInterceptors = new ArrayList<>(size);

    for (final Interceptor interceptor : originalInterceptorsMutable) {
      if (!toAdd.getClass().isInstance(interceptor)) {
        uniqueInterceptors.add(interceptor);
      }
    }

    originalInterceptorsMutable.clear();
    originalInterceptorsMutable.addAll(uniqueInterceptors);
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

  @Nullable private String removeSafely(@NonNull Call call) {
    final String callId = call.request().header(KEY_CHANNEL_CALL);
    final ChannelCall savedCall = channelCalls.get(callId);

    if (TextUtils.isEmpty(callId) || savedCall == null) {
      return null;
    }

    channelCalls.remove(callId);

    return callId;
  }
}