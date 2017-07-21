package rm.com.speedtest;

import android.support.annotation.NonNull;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by alex
 */

public class UploadChannel extends BaseChannel {
  public UploadChannel(@NonNull OkHttpClient httpClient) {
    super(httpClient);
  }

  @NonNull @Override
  protected Request defaultRequestOf(@NonNull CallId callId, @NonNull Endpoint src,
      @NonNull Endpoint dest) {
    final HttpUrl httpUrl = HttpUrl.get(dest.uri());
    final RequestBody body = RequestBody.create(null, src.file());

    if (httpUrl == null) {
      throw new IllegalStateException("Couldn't parse source URL");
    }

    return new Request.Builder().url(httpUrl)
        .post(new ProgressRequestBody(body, callId.string(), this))
        .build();
  }
}
