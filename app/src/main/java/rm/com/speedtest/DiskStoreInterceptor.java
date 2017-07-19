package rm.com.speedtest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by alex
 */

public final class DiskStoreInterceptor implements Interceptor {
  private final HashMap<String, ChannelCall> channelCalls;

  public DiskStoreInterceptor(@NonNull HashMap<String, ChannelCall> channelCalls) {
    this.channelCalls = channelCalls;
  }

  @Override public Response intercept(@NonNull Chain chain) throws IOException {
    final Request request = chain.request();
    final Response response = chain.proceed(request);
    final ResponseBody body = response.body();
    final ChannelCall call = findActiveChannelCall(request);

    if (call != null && body != null) {
      saveResponseToDisk(call, body);
    }

    return response;
  }

  private void saveResponseToDisk(@NonNull ChannelCall call, @NonNull ResponseBody body)
      throws IOException {
    final BufferedSource source = body.source();
    final BufferedSink sink = Okio.buffer(Okio.sink(call.dest().asFile()));

    source.readAll(sink);
    sink.writeAll(source);
    sink.close();
    source.close();
  }

  @Nullable private ChannelCall findActiveChannelCall(@NonNull Request request) {
    return channelCalls.get(request.header(Channel.KEY_DOWNLOAD_CALL));
  }
}
