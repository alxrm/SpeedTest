package rm.com.speedtest.net.channel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by alex
 */

public interface ChannelListener extends LoadingProgressListener {
  void onSuccess(@NonNull String callId, @NonNull Call call, @NonNull Response response);

  void onFailure(@NonNull String callId, @NonNull Call call, @Nullable IOException error);
}
