package rm.com.speedtest;

import android.support.annotation.NonNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by alex
 */

public final class DownloadChannel implements Channel, ChannelProgressListener {
  private static final int DEFAULT_CALL_AMOUNT = 12;

  private final HashMap<String, ChannelCall> channelCalls = new HashMap<>(DEFAULT_CALL_AMOUNT);
  private final OkHttpClient httpClient;

  public DownloadChannel(@NonNull OkHttpClient httpClient) {
    this.httpClient = httpClient.newBuilder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .addNetworkInterceptor(new DownloadProgressInterceptor(channelCalls, this))
        .addInterceptor(new DiskStoreInterceptor(channelCalls))
        .build();
  }

  @NonNull @Override public ChannelCall open(@NonNull Endpoint src, @NonNull Endpoint dest) {
    final CallId callId = new CallId();
    final Call rawCall = httpClient.newCall(buildRequest(src, callId));
    final ChannelCall pendingCall = new ChannelCall(rawCall, callId, src, dest);

    channelCalls.put(callId.string(), pendingCall);

    return pendingCall;
  }

  @Override public void close(@NonNull ChannelCall channelCall) {
    channelCalls.remove(channelCall.request().header(Channel.KEY_DOWNLOAD_CALL));

    if (channelCall.call().isExecuted()) {
      channelCall.call().cancel();
    }
  }

  @Override
  public void update(@NonNull String tag, long bytesPassed, long contentLength, boolean done) {
    if (done) {
      channelCalls.remove(tag);
    }

    System.out.println(tag
        + ": "
        + ((int) ((bytesPassed / ((float) contentLength)) * 100))
        + "% — Finished: "
        + done);
  }

  @NonNull private Request buildRequest(@NonNull Endpoint src, @NonNull CallId callId) {
    final HttpUrl httpUrl = HttpUrl.get(src.asURI());

    if (httpUrl == null) {
      throw new IllegalStateException("Couldn't parse source URL");
    }

    return new Request.Builder().url(httpUrl)
        .addHeader(Channel.KEY_DOWNLOAD_CALL, callId.string())
        .build();
  }

  public static void main(String[] args) throws IOException {
    final OkHttpClient client = new OkHttpClient.Builder().build();
    final DownloadChannel downstream = new DownloadChannel(client);

    final Endpoint src = new Endpoint("https://i.redd.it/d9ejxzs27faz.jpg");
    final Endpoint dest = new Endpoint("/Users/alex/AndroidStudioProjects/SpeedTest/app/tmp1");

    final Endpoint src2 =
        new Endpoint("https://i.redd.it/0cifq3u75jaz.jpg");
    final Endpoint dest2 = new Endpoint("/Users/alex/AndroidStudioProjects/SpeedTest/app/tmp2");

    final ChannelCall call = downstream.open(src, dest);
    final ChannelCall call2 = downstream.open(src2, dest2);

    call.call().enqueue(new Callback() {
      @Override public void onFailure(Call call, IOException e) {
        System.out.println("onFailure 1");
      }

      @Override public void onResponse(Call call, Response response) throws IOException {
        System.out.println("onResponse 1");
      }
    });

    call2.call().enqueue(new Callback() {
      @Override public void onFailure(Call call, IOException e) {
        System.out.println("onFailure 2");
      }

      @Override public void onResponse(Call call, Response response) throws IOException {
        System.out.println("onResponse 2");
      }
    });
  }
}
