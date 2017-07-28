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
public final class DownloadChannel extends BaseChannel<DownloadChannel, DownloadChannel.Builder> {

  public DownloadChannel(@NonNull Builder builder) {
    super(builder);
  }

  @NonNull @Override protected OkHttpClient httpClientOf(@NonNull OkHttpClient httpClient) {
    final OkHttpClient.Builder builder = httpClient.newBuilder();

    addUniqueInterceptor(builder.interceptors(), new DiskStoreInterceptor(channelCalls));
    addUniqueInterceptor(builder.networkInterceptors(),
        new DownloadProgressInterceptor(channelCalls, this));

    return builder.build();
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

  public static final class Builder extends AbstractChannelBuilder<DownloadChannel, Builder> {
    public Builder() {
      super();
    }

    Builder(@NonNull DownloadChannel channel) {
      super(channel);
    }

    @NonNull @Override public DownloadChannel build() {
      return new DownloadChannel(this);
    }
  }
}
