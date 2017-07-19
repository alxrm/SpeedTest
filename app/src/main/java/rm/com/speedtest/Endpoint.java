package rm.com.speedtest;

import android.support.annotation.NonNull;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public final class Endpoint {
  final String uri;

  public Endpoint(String uri) {
    this.uri = uri;
  }

  @NonNull public URI asURI() {
    try {
      return new URI(uri);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  @NonNull public File asFile() {
    return new File(uri);
  }
}
