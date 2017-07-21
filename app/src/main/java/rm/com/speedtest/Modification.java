package rm.com.speedtest;

import android.support.annotation.NonNull;
import okhttp3.Request;

/**
 * Created by alex
 */

public interface Modification {
  @NonNull Request apply(@NonNull Request to, @NonNull Endpoint src, @NonNull Endpoint dest);
}
