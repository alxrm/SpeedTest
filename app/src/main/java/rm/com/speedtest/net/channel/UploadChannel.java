package rm.com.speedtest.net.channel;

import android.support.annotation.NonNull;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rm.com.speedtest.net.Endpoint;
import rm.com.speedtest.net.ProgressRequestBody;

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

  public static void main(String[] args) {
    final OkHttpClient httpClient = new OkHttpClient.Builder().build();

    final UploadChannel channel = new UploadChannel(httpClient);
    final Endpoint src = new Endpoint("/Users/alex/AndroidStudioProjects/SpeedTest/app/tmp1");
    final Endpoint dest = new Endpoint("https://api.imgur.com/3/image");

    channel.after(new Modification() {
      @NonNull @Override
      public Request apply(@NonNull Request to, @NonNull Endpoint src, @NonNull Endpoint dest) {
        return to.newBuilder().header("Authorization", "Client-ID 9199fdef135c122").build();
      }
    });

    channel.subscribe(new ChannelProgressListener() {
      @Override
      public void update(@NonNull String tag, long bytesPassed, long contentLength, boolean done) {
        System.out.println("Uploading: " + ((int) ((100F * bytesPassed) / contentLength)) + "%");
      }
    });

    channel.open(src, dest).call().enqueue(new Callback() {
      @Override public void onFailure(Call call, IOException e) {
        System.out.println("FAIL");
      }

      @Override public void onResponse(Call call, Response response) throws IOException {
        System.out.println("SUCCESS");
      }
    });
  }
}
