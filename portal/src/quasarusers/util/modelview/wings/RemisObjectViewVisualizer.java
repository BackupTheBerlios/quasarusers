/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 15.10.2002
 * Time: 12:01:26
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.modelview.wings;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.modelview.implementation.wings.WingsObjectViewVisualizer;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.view.implementation.wings.CommandTrigger;
import quasarusers.portal.TemplateViewPanel;
import quasarusers.util.mapping.HtmlFieldMappingFactory;
import quasarusers.util.modelview.MatchView;
import quasarusers.util.modelview.LookupView;
import org.wings.SButton;
import org.wings.SComboBox;
import org.wings.SComponent;

import java.util.List;

/**
 * Dies ist die projektspezifische Visualizer-Klasse für
 * ObjectViews.
 *
 * @author Matthias Rademacher
 */
public class RemisObjectViewVisualizer extends WingsObjectViewVisualizer {
  public final static class FieldDoesNotExistException extends Exception {
    public FieldDoesNotExistException() {
    }

    public FieldDoesNotExistException(String s) {
      super(s);
    }
  }

  /**
   * Erzeugt einen <code>RemisObjectViewVisualizer</code>.
   *
   * @param  arguments siehe {@link WingsObjectViewVisualizer#WingsObjectViewVisualizer}
   */
  public RemisObjectViewVisualizer(Keywords arguments) {
    super(arguments);
  }

  protected void initialize(Keywords arguments) {
    arguments.addValue("fieldMappingFactory", new HtmlFieldMappingFactory());

    super.initialize(arguments);
  }

  protected ViewVisualizer provideChildVisualizer(View child, Keywords arguments) {
    if (child instanceof MatchView) {
      // Baut die Combobox für eine MatchView
      final String name = child.getName();
      final SComponent representation = new SComboBox();

      representation.setName(name);

      ((TemplateViewPanel) getVisualRepresentation()).add(representation, name);

      return super.provideChildVisualizer(child, new Keywords(arguments,
                                                              "visualRepresentation", representation));
    } else if (child instanceof LookupView) {
      final String name = child.getName();
      ViewVisualizer viewVisualizer = super.provideChildVisualizer(child, arguments);
      ((TemplateViewPanel) getVisualRepresentation()).add((SComponent) viewVisualizer.getVisualRepresentation(), name);

      return viewVisualizer;
    } else
      return super.provideChildVisualizer(child, arguments);
  }

  /**
   * Wandelt ein Feld in einen internen Link um.
   *
   * @param  localControls die Widgets des Panels
   * @param  fieldName     der Name des Felds, das als Link dargestellt werden soll
   * @param  commandName   der Name des Kommandos der (clientseitigen) View
   * @throws FieldDoesNotExistException falls das Feld im HTML-Template fehlt
   * existiert.
   */
  protected void addLink(List localControls,
                         String fieldName,
                         Symbol commandName) throws FieldDoesNotExistException {
    TemplateViewPanel templateViewPanel = ((TemplateViewPanel) getVisualRepresentation());

    SButton button = (SButton) templateViewPanel.getComponent(fieldName);

    if (button == null)
      throw new FieldDoesNotExistException("Field " + fieldName + " does not exist in HTML template");

    new CommandTrigger(commandName).associateElement(button);

    localControls.add(button);
  }
}

