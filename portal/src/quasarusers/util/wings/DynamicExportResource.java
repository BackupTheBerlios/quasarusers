package quasarusers.util.wings;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.view.ViewTransaction;
import org.wings.DynamicResource;
import org.wings.SFrame;
import org.wings.io.Device;

import java.io.IOException;

//todo dpk: 23/01/03 -> MR kommentieren

public class DynamicExportResource extends DynamicResource {
  private View view;
  private Keywords arguments;
  private Symbol command;

  public DynamicExportResource(SFrame frame, String extension, String mimeType, View view, Symbol command) {
    super(frame, extension, mimeType);

    this.view = view;
    this.command = command;
  }

  public DynamicExportResource(SFrame frame, String extension, String mimeType, View view, Symbol command, Keywords arguments) {
    super(frame, extension, mimeType);

    this.view = view;
    this.arguments = arguments;
    this.command = command;
  }

  protected void prepare(Keywords arguments) {

  }

  public void write(Device out) throws IOException {
    final ViewManager viewManager = view.getViewManager();
    final ViewTransaction transaction = viewManager.beginTransaction();

    try {
      arguments.setValue("viewTransaction", transaction);
      prepare(arguments);

      Object result = view.performCommand(command, arguments);

      if (result instanceof byte[])
        out.write((byte[]) result);
      else
        out.print("Export error");

      viewManager.commitTransaction(transaction);
    } catch (Exception e) {
      viewManager.rollbackTransaction(transaction);
      if (e instanceof IOException)
        throw (IOException) e;
      else {
        e.printStackTrace();

        throw new IOException(e.getMessage());
      }
    } finally {
      invalidate(); // sorgt dafür, dass Resource beim nächsten Zugriff neu erzeugt wird
    }
  }
}
