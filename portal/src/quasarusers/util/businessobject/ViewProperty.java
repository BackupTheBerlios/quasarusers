/*
 * Created by IntelliJ IDEA.
 * User: madams
 * Date: Mar 18, 2002
 * Time: 11:07:58 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.businessobject;

import com.sdm.quasar.businessobject.AbstractBusinessObjectProperty;
import com.sdm.quasar.businessobject.View;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.util.LocalizedString;
import com.sdm.quasar.view.ResultProcessor;
import com.sdm.quasar.view.UserInterfaceType;
import quasarusers.util.SimpleContinuation;

//todo dpk: 23/01/03 -> MR kommentieren

public class ViewProperty extends AbstractBusinessObjectProperty {
  private String path;
  private Object object;
  private boolean isWingsSupported = true;
  private boolean isSwingSupported = false;

  public ViewProperty(String name, LocalizedString label, LocalizedString documentation) {
    super(name, label, documentation);
  }

  public ViewProperty(String name, String label, String documentation) {
    super(name, label, documentation);
  }

  public ViewProperty(String name, String label, String documentation, boolean isSwingSupported) {
    super(name, label, documentation);

    this.isSwingSupported = isSwingSupported;
  }

    public ViewProperty(String name, String label, String documentation, Object object, boolean swingSupported) {
        super(name, label, documentation);
        this.object = object;
        isSwingSupported = swingSupported;
    }

  public ViewProperty(String name, String label, String documentation, String path) {
    super(name, label, documentation);

    this.path = path;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

  public boolean isSupported(UserInterfaceType type) {
    if (type == UserInterfaceType.WINGS) {
        return isWingsSupported;
    }

    if (type == UserInterfaceType.SWING) {
        return isSwingSupported;
    }

    return false;
  }

  public void setWingsSupported(boolean wingsSupported) {
    isWingsSupported = wingsSupported;
  }

  public void setSwingSupported(boolean swingSupported) {
    isSwingSupported = swingSupported;
  }

}
