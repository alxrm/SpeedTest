package rm.com.speedtest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import rm.com.speedtest.net.Endpoint;
import rm.com.speedtest.net.channel.ChannelCall;
import rm.com.speedtest.net.channel.ChannelProgressListener;
import rm.com.speedtest.net.channel.DownloadChannel;
import rm.com.speedtest.speed.SimpleSpeedMeasure;

public final class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public static void main(String[] args) {
    final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    final DownloadChannel channel = new DownloadChannel.Builder() //
        .httpClient(httpClient) //
        .build();

    final SimpleSpeedMeasure speedMeasure = new SimpleSpeedMeasure();

    channel.subscribe(new ChannelProgressListener() {
      @Override
      public void update(@NonNull String tag, long bytesPassed, long contentLength, boolean done) {
        System.out.println(bytesPassed);

        if (!done) {
          speedMeasure.updateUnits(bytesPassed);
        }
      }
    });

    // upload
    //final Endpoint src = new Endpoint("/Users/alex/AndroidStudioProjects/SpeedTest/app/test.iso");
    //final Endpoint dest = new Endpoint("http://2.testdebit.info/");

    // download
    final Endpoint dest = new Endpoint("/Users/alex/AndroidStudioProjects/SpeedTest/app/test.iso");
    final Endpoint src = new Endpoint("http://ipv4.bouygues.testdebit.info/1M.iso");

    // upload http://2.testdebit.info/
    // download http://ipv4.bouygues.testdebit.info/1M.iso

    final ChannelCall channelCall = channel.open(src, dest);

    channelCall.call().enqueue(new Callback() {
      @Override public void onFailure(Call call, IOException e) {
        System.out.println("FAIL");
      }

      @Override public void onResponse(Call call, Response response) throws IOException {
        System.out.println("SUCCESS");
      }
    });
  }
}
