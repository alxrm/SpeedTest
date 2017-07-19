package rm.com.speedtest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by alex
 */

public final class DownloadProgressInterceptor implements Interceptor {
  private final HashMap<String, ChannelCall> channelCalls;
  private final ChannelProgressListener progressListener;

  public DownloadProgressInterceptor( //
      @NonNull HashMap<String, ChannelCall> channelCalls, //
      @NonNull ChannelProgressListener progressListener //
  ) {
    this.channelCalls = channelCalls;
    this.progressListener = progressListener;
  }

  @Override public Response intercept(@NonNull Chain chain) throws IOException {
    final Request request = chain.request();
    final Response originalResponse = chain.proceed(request);
    final ChannelCall call = channelCallForRequest(request);

    if (call == null) {
      return originalResponse;
    }

    return asProgressAwareResponse(call, originalResponse);
  }

  @Nullable private ChannelCall channelCallForRequest(@NonNull Request request) {
    return channelCalls.get(request.header(Channel.KEY_DOWNLOAD_CALL));
  }

  @NonNull
  private Response asProgressAwareResponse(@NonNull ChannelCall call, @NonNull Response response) {
    //noinspection ConstantConditions
    return response.newBuilder()
        .body(new ProgressResponseBody(response.body(), call.id().string(), progressListener))
        .build();
  }
}
