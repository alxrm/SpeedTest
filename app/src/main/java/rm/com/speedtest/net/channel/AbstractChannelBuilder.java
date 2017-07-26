package rm.com.speedtest.net.channel;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.HashMap;
import okhttp3.OkHttpClient;

/**
 * Created by alex
 */

@SuppressWarnings("unchecked") //
abstract class AbstractChannelBuilder<B extends AbstractChannelBuilder, C extends BaseChannel> {
  private static final int DEFAULT_CALL_AMOUNT = 20;
  private static final int DEFAULT_LISTENERS_AMOUNT = 20;

  HashMap<String, ChannelCall> channelCalls;
  ArrayList<ChannelProgressListener> progressSubscribers;
  OkHttpClient httpClient;
  Modification modification;

  AbstractChannelBuilder() {
    channelCalls = new HashMap<>(DEFAULT_CALL_AMOUNT);
    progressSubscribers = new ArrayList<>(DEFAULT_LISTENERS_AMOUNT);
    httpClient = new OkHttpClient();
    modification = null;
  }

  AbstractChannelBuilder(C channel) {
    this.channelCalls = new HashMap<>(channel.channelCalls);
    this.progressSubscribers = new ArrayList<>(channel.progressSubscribers);
    this.httpClient = channel.httpClient;
    this.modification = channel.modification;
  }

  public final B requestModification(@NonNull Modification modification) {
    this.modification = modification;
    return (B) this;
  }

  public final B progressSubscribers(
      @NonNull ArrayList<ChannelProgressListener> progressSubscribers) {
    this.progressSubscribers = progressSubscribers;
    return (B) this;
  }

  public final B channelCalls(@NonNull HashMap<String, ChannelCall> channelCalls) {
    this.channelCalls = channelCalls;
    return (B) this;
  }

  public final B httpClient(@NonNull OkHttpClient httpClient) {
    this.httpClient = httpClient;
    return (B) this;
  }

  @NonNull public abstract C build();
}
