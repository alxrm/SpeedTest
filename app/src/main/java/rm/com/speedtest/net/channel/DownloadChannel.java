package rm.com.speedtest.net.channel;

import android.support.annotation.NonNull;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import rm.com.speedtest.net.Endpoint;
import rm.com.speedtest.net.interceptor.DiskStoreInterceptor;
import rm.com.speedtest.net.interceptor.DownloadProgressInterceptor;

/**
 * Created by alex
 */

@SuppressWarnings("WeakerAccess") //
public class DownloadChannel extends BaseChannel<DownloadChannel, DownloadChannel.Builder> {

  DownloadChannel(@NonNull Builder builder) {
    super(builder);
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
      throw new IllegalStateException("Couldn't parse source URL: " + src.path());
    }

    return new Request.Builder().url(httpUrl).build();
  }

  @NonNull @Override public Builder newBuilder() {
    return new Builder(this);
  }

  public static final class Builder extends AbstractChannelBuilder<Builder, DownloadChannel> {
    public Builder() {
      super();
    }

    Builder(DownloadChannel channel) {
      super(channel);
    }

    @NonNull @Override public DownloadChannel build() {
      return new DownloadChannel(this);
    }
  }
}
