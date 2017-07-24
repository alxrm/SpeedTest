package rm.com.speedtest.net.channel;

import java.util.UUID;

/**
 * Created by alex
 */

public final class CallId {
  private final String id;

  public CallId() {
    this.id = UUID.randomUUID().toString();
  }

  public String string() {
    return id;
  }
}
