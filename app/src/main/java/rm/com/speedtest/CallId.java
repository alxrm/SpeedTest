package rm.com.speedtest;

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
