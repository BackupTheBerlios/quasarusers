/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 15.10.2002
 * Time: 12:44:02
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.modelview;

import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.component.NoSuchCommandException;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.ObjectView;
import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.view.UserInterfaceType;
import com.sdm.quasar.view.ViewCommandPerformer;
import com.sdm.quasarx.configuration.Configuration;

/**
 * Dies ist die projektspezifische Klasse für ObjectViews.
 *
 * @author Matthias Rademacher
 */
public abstract class RemisObjectView extends ObjectView {
  public RemisObjectView() throws ComponentException {
  }

  public RemisObjectView(Keywords arguments) throws ComponentException {
    super(arguments);
  }

    public boolean close(Keywords arguments) {
        try {
            setCommandEnabled(REVERT_OBJECT, false);
        }
        catch (NoSuchCommandException e) {
            e.printStackTrace();
        }

        return super.close(arguments);
    }

  public String computeVisualizerClassName(Keywords arguments) {
    UserInterfaceType userInterfaceType = getViewManager().getUserInterfaceType();
    String className = null;

    if (userInterfaceType == UserInterfaceType.WINGS)
      className = getWingsVisualizerClassName();
    else
      Assertion.fail("User interface type " + userInterfaceType + " not supported by " + this);

    return className;
  }

  public void bindObject(Object object) throws ComponentException {
    bindObject(object, new Keywords());
  }

  public void unbindObject() {
    unbindObject(new Keywords());
  }

  protected abstract String getWingsVisualizerClassName();

  public Keywords buildConstructorArguments() {
    return new Keywords("manager", getViewManager(),
                        "commandPerformer", Configuration.getConfiguration().getSingleton(ViewCommandPerformer.class));
  }

  public boolean deleteObject(Keywords arguments) throws ComponentException {
    arguments.addValue("confirm", Boolean.FALSE);

    return super.deleteObject(arguments);
  }
}
