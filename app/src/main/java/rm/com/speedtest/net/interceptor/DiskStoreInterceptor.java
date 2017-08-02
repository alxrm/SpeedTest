package rm.com.speedtest.net.interceptor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import rm.com.speedtest.net.channel.Channel;
import rm.com.speedtest.net.channel.ChannelCall;

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
    final ChannelCall call = channelCallForRequest(request);

    if (call != null && body != null) {
      saveResponseToDisk(call, body);
    }

    return response;
  }

  private void saveResponseToDisk(@NonNull ChannelCall call, @NonNull ResponseBody body)
      throws IOException {
    final File dest = call.dest().file();

    createDirectoryIfNeeded(dest);

    final BufferedSource source = body.source();
    final BufferedSink sink = Okio.buffer(Okio.sink(dest));

    source.readAll(sink);
    sink.writeAll(source);
    sink.close();
    source.close();
  }

  @Nullable private ChannelCall channelCallForRequest(@NonNull Request request) {
    return channelCalls.get(request.header(Channel.KEY_CHANNEL_CALL));
  }

  private void createDirectoryIfNeeded(@NonNull File dest) {
    final File parentDir = dest.getParentFile();

    if (!parentDir.exists()) {
      //noinspection ResultOfMethodCallIgnored
      parentDir.mkdirs();
    }
  }
}
