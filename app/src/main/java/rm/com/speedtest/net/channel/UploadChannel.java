package rm.com.speedtest.net.channel;

import android.support.annotation.NonNull;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import rm.com.speedtest.net.Endpoint;
import rm.com.speedtest.net.ProgressRequestBody;

/**
 * Created by alex
 */

@SuppressWarnings("WeakerAccess") //
public final class UploadChannel extends BaseChannel<UploadChannel, UploadChannel.Builder> {

  public UploadChannel(@NonNull Builder builder) {
    super(builder);
  }

  @NonNull @Override
  protected Request defaultRequestOf(@NonNull CallId callId, @NonNull Endpoint src,
      @NonNull Endpoint dest) {
    final HttpUrl httpUrl = HttpUrl.get(dest.uri());
    final RequestBody body = RequestBody.create(null, src.file());

    if (httpUrl == null) {
      throw new IllegalStateException("Couldn't parse destination URL: " + dest.path());
    }

    return new Request.Builder().url(httpUrl)
        .post(new ProgressRequestBody(body, callId.string(), this))
        .build();
  }

  @NonNull @Override public Builder newBuilder() {
    return new Builder(this);
  }

  public static final class Builder extends AbstractChannelBuilder<UploadChannel, Builder> {
    public Builder() {
      super();
    }

    Builder(UploadChannel channel) {
      super(channel);
    }

    @NonNull @Override public UploadChannel build() {
      return new UploadChannel(this);
    }
  }
}
