package rm.com.speedtest.net.channel;

import android.support.annotation.NonNull;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import rm.com.speedtest.net.interceptor.DiskStoreInterceptor;
import rm.com.speedtest.net.interceptor.DownloadProgressInterceptor;
import rm.com.speedtest.net.Endpoint;

/**
 * Created by alex
 */

public class DownloadChannel extends BaseChannel {
  public DownloadChannel(@NonNull OkHttpClient httpClient) {
    super(httpClient);
  }

  @NonNull @Override protected OkHttpClient httpClientOf(@NonNull OkHttpClient httpClient) {
    return httpClient.newBuilder()
        .addNetworkInterceptor(new DownloadProgressInterceptor(channelCalls, this))
        .addInterceptor(new DiskStoreInterceptor(channelCalls))
        .build();
  }

  @NonNull @Override
  protected Request defaultRequestOf(@NonNull CallId callId, @NonNull Endpoint src,
      @NonNull Endpoint dest) {
    final HttpUrl httpUrl = HttpUrl.get(src.uri());

    if (httpUrl == null) {
      throw new IllegalStateException("Couldn't parse source URL");
    }

    return new Request.Builder().url(httpUrl).build();
  }
}
