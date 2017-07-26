package rm.com.speedtest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rm.com.speedtest.net.Endpoint;
import rm.com.speedtest.net.channel.ChannelProgressListener;
import rm.com.speedtest.net.channel.DownloadChannel;
import rm.com.speedtest.net.channel.Modification;
import rm.com.speedtest.speed.SimpleSpeedMeasure;
import rm.com.speedtest.speed.SpeedMeasure;

public final class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public static void main(String[] args) {
    final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    final DownloadChannel channel = new DownloadChannel.Builder() //
        .requestModification(new Modification() {
          @NonNull @Override
          public Request apply(@NonNull Request to, @NonNull Endpoint src, @NonNull Endpoint dest) {
            return to;
          }
        }) //
        .httpClient(httpClient) //
        .build();

    final Endpoint src =
        new Endpoint("https://pp.userapi.com/c626719/v626719205/367a6/_1jcUOmuC7Y.jpg");
    final Endpoint dest =
        new Endpoint("/Users/alex/AndroidStudioProjects/SpeedTest/app/ebinso.jpg");

    final SpeedMeasure speedMeasure = new SimpleSpeedMeasure();

    channel.subscribe(new ChannelProgressListener() {
      @Override
      public void update(@NonNull String tag, long bytesPassed, long contentLength, boolean done) {
        speedMeasure.updateUnits(bytesPassed);
      }
    });

    channel.open(src, dest).call().enqueue(new Callback() {
      @Override public void onFailure(Call call, IOException e) {
        System.out.println("FAIL");
      }

      @Override public void onResponse(Call call, Response response) throws IOException {
        System.out.println("SUCCESS");
        final double bytesPerSec = speedMeasure.currentSpeed().unitsPerTime(TimeUnit.SECONDS);

        System.out.println("Speed: " + bytesPerSec / Math.pow(2, 20) + " mbps");
      }
    });
  }
}
