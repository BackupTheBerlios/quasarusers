/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 17.07.2002
 * Time: 11:52:44
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.wings;

import org.wings.DynamicResource;
import org.wings.SAnchor;
import org.wings.SimpleURL;

/**
 * Dies ist ein spezieller Anchor für Wings, der dafür sorgt,
 * dass die dynamische Resource neu erzeugt wird, wenn sie
 * sich geändert hat. Normalerweise (d.h., wenn SAnchor eingesetzt wird)
 * würde eine einmal auf den Client übertragene Resource nicht nochmal übertragen,
 * auch wenn sich ihr Inhalt geändert hat.
 *
 * @author Marco Schmickler
 * @author Matthias Rademacher
 *
 */
public class RemisAnchor extends SAnchor {
  private final DynamicResource resource;

  public RemisAnchor(DynamicResource resource) {
    this.resource = resource;
  }

  public RemisAnchor(String url, DynamicExportResource resource) {
    super(url, null);
    this.resource = resource;
  }

  public RemisAnchor(String url, String target, DynamicExportResource resource) {
    super(url, target);
    this.resource = resource;
  }

  public RemisAnchor(SimpleURL url, String target, DynamicExportResource resource) {
    super(url, target);
    this.resource = resource;
  }

  public SimpleURL getURL() {
    return resource.getURL();
  }
}
