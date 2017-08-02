package rm.com.speedtest.net;

import android.support.annotation.NonNull;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import rm.com.speedtest.net.channel.LoadingProgressListener;

/**
 * Created by alex
 */
public final class ProgressResponseBody extends ResponseBody {
  private final ResponseBody responseBody;
  private final String tag;
  private final LoadingProgressListener progressListener;

  private BufferedSource bufferedSource;

  public ProgressResponseBody(@NonNull ResponseBody responseBody, @NonNull String tag,
      @NonNull LoadingProgressListener progressListener) {
    this.responseBody = responseBody;
    this.tag = tag;
    this.progressListener = progressListener;
  }

  @Override public MediaType contentType() {
    return responseBody.contentType();
  }

  @Override public long contentLength() {
    return responseBody.contentLength();
  }

  @Override public BufferedSource source() {
    if (bufferedSource == null) {
      bufferedSource = Okio.buffer(progressSource(responseBody.source()));
    }
    return bufferedSource;
  }

  @NonNull private Source progressSource(@NonNull Source source) {
    return new ForwardingSource(source) {
      long totalBytesRead = 0L;

      @Override public long read(Buffer sink, long byteCount) throws IOException {
        long bytesRead = super.read(sink, byteCount);
        // read() returns the number of bytes read, or -1 if this progressSource is exhausted.
        totalBytesRead += bytesRead != -1 ? bytesRead : 0;
        progressListener.onProgress(tag, totalBytesRead, responseBody.contentLength(),
            bytesRead == -1);
        return bytesRead;
      }
    };
  }
}