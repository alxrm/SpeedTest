package rm.com.speedtest.net.interceptor;

import android.support.annotation.NonNull;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import rm.com.speedtest.net.ProgressResponseBody;
import rm.com.speedtest.net.channel.Channel;
import rm.com.speedtest.net.channel.LoadingProgressListener;

/**
 * Created by alex
 */

public final class DownloadProgressInterceptor implements Interceptor {
  private final LoadingProgressListener progressListener;

  public DownloadProgressInterceptor(@NonNull LoadingProgressListener progressListener) {
    this.progressListener = progressListener;
  }

  @Override public Response intercept(@NonNull Chain chain) throws IOException {
    final Request request = chain.request();
    final Response originalResponse = chain.proceed(request);
    final String callId = request.header(Channel.KEY_CHANNEL_CALL);

    if (callId == null) {
      return originalResponse;
    }

    return asProgressAwareResponse(callId, originalResponse);
  }

  @NonNull
  private Response asProgressAwareResponse(@NonNull String callId, @NonNull Response response) {
    //noinspection ConstantConditions
    return response.newBuilder()
        .body(new ProgressResponseBody(response.body(), callId, progressListener))
        .build();
  }
}
