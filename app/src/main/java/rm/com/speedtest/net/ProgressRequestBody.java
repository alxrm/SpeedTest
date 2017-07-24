package rm.com.speedtest.net;

import android.support.annotation.NonNull;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import rm.com.speedtest.net.channel.ChannelProgressListener;

/**
 * Created by alex
 */

public final class ProgressRequestBody extends RequestBody {
  private final RequestBody requestBody;
  private final String tag;
  private final ChannelProgressListener progressListener;

  public ProgressRequestBody(@NonNull RequestBody requestBody, @NonNull String tag,
      @NonNull ChannelProgressListener progressListener) {
    this.requestBody = requestBody;
    this.tag = tag;
    this.progressListener = progressListener;
  }

  @Override public MediaType contentType() {
    return requestBody.contentType();
  }

  @Override public long contentLength() {
    try {
      return requestBody.contentLength();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return -1;
  }

  @Override public void writeTo(BufferedSink sink) throws IOException {
    final BufferedSink bufferedSink = Okio.buffer(progressSink(sink));
    requestBody.writeTo(bufferedSink);
    bufferedSink.flush();
  }

  @NonNull private Sink progressSink(@NonNull Sink sink) {
    return new ForwardingSink(sink) {
      long totalBytesWritten = 0L;

      @Override public void write(Buffer source, long byteCount) throws IOException {
        super.write(source, byteCount);
        totalBytesWritten += byteCount;

        if (totalBytesWritten > contentLength()) {
          return;
        }

        progressListener.update(tag, totalBytesWritten, contentLength(),
            totalBytesWritten == contentLength());
      }
    };
  }
}
