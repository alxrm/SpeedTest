package rm.com.speedtest;

import android.support.annotation.NonNull;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public final class Endpoint {
  private final String path;

  public Endpoint(String path) {
    this.path = path;
  }

  @NonNull public URI asURI() {
    try {
      return new URI(path);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  @NonNull public File asFile() {
    return new File(path);
  }
}
