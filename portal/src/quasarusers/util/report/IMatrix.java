/*
 * Created by IntelliJ IDEA.
 * User: steindl
 * Date: Apr 10, 2002
 * Time: 9:32:31 AM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.report;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

//todo dpk 23/01/2003 -> MS kommentieren

public interface IMatrix {
  /**
   * Exportiert eine Matrix in einen {@link OutputStream}.
   *
   * @param  spaltenNamen  die Namen der Datenzeilen
   * @param  zeilenNamen  die Namen der Datenspalten
   * @param  valueIt      Iterator über die Datenelemente. Jedes Datenelement
   *                      ist ein String-Array mit Zeilenname und Spaltenname
   * @param  out          der Stream, in den geschrieben werden soll
   * @throws IOException  falls der Stream nicht gelesen werden kann
   */
  public void exportieren(Collection zeilenNamen,
                          Collection spaltenNamen,
                          Iterator valueIt,
                          OutputStream out) throws IOException;

  /**
   * Exportiert eine Matrix in einen {@link OutputStream}.
   *
   * @param  spaltenNamen  die Namen der Datenzeilen (Ausgabeparameter)
   * @param  zeilenNamen  die Namen der Datenspalten (Ausgabeparameter)
   * @param  in           der Stream, aus dem gelesen werden soll
   * @return Iterator über die Datenelemente. Jedes Datenelement
   *         ist ein String-Array mit Zeilenname (Index 0) und
   *         Spaltenname (Index 1)
   * @throws Exception    falls der Stream nicht gelesen werden kann
   */
  public Iterator importieren(Collection zeilenNamen,
                              Collection spaltenNamen,
                              InputStream in) throws Exception;
}
