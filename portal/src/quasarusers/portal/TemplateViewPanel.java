package quasarusers.portal;

import com.sdm.quasar.lang.Symbol;
import org.wings.SButton;
import org.wings.SCheckBox;
import org.wings.SComboBox;
import org.wings.SIcon;
import org.wings.SPanel;
import org.wings.SPasswordField;
import org.wings.STextArea;
import org.wings.STextField;

/**
 * Dies ist das für REMIS genutzte ViewPanel mit zugrunde liegendem
 * HTML-Template.
 *
 * @author Marco Schmickler
 */
public final class TemplateViewPanel
        extends com.sdm.quasar.view.implementation.wings.TemplateViewPanel {
  /** Erzeugt ein <code>TemplateViewPanel</code>
   *
   * @param  template  der Name des HTML-Templates (mit Datei-Pfad).
   */
  public TemplateViewPanel(String template) {
    super(template);
  }

  /**
   * Fügt dem Panel eine Schaltfläche({@link SButton}) hinzu.
   *
   * @param  component das Panel
   * @param  command   der Name des auf der <code>View</code> auszuführenden
   *                   Kommandos
   */
  public static SButton addCommandButton(SPanel component, String command) {
    SButton sButton = new SButton(command);
    sButton.setName(command + "Command");
    component.add(sButton, command + "Command");

    return sButton;
  }

  /**
   * Fügt dem Panel eine als Icon dargestellte Schaltfläche({@link SButton}) hinzu.
   *
   * @param  component das Panel
   * @param  command   der Name des auf der <code>View</code> auszuführenden
   *                   Kommandos
   * @param  icon      das darzustellende Icon
   */
  public static SButton addCommandButton(SPanel component, String command, SIcon icon) {
    SButton sButton = new SButton(icon);

    sButton.setName(command + "Command");
    component.add(sButton, command + "Command");

    return sButton;
  }

  /**
   * Fügt dem Panel ein Textfeld({@link STextField}) hinzu.
   *
   * @param  component das Panel
   * @param  name      der Name des Textfelds. Der Platzhalter im HTML-Template
   *                   muss den Namen <code>name +"Field"</code> habe.
   */
  public static STextField addTextField(SPanel component, String name) {
    STextField sTextField = new STextField();

    sTextField.setName(name + "Field");
    component.add(sTextField, name + "Field");

    return sTextField;
  }

  /**
   * Fügt dem Panel ein Passwort-Textfeld({@link SPasswordField}) hinzu.
   *
   * @param  component das Panel
   * @param  name      der Name des Textfelds. Der Platzhalter im HTML-Template
   *                   muss den Namen <code>name +"Field"</code> habe.
   */
  public static SPasswordField addPasswordField(SPanel component, String name) {
    SPasswordField passwordField = new SPasswordField();

    passwordField.setName(name + "Field");
    component.add(passwordField, name + "Field");

    return passwordField;
  }

  /**
   * Fügt dem Panel ein Kontrollfeld({@link SCheckBox}) hinzu.
   *
   * @param  component das Panel
   * @param  name      der Name des Kontrollfelds. Der Platzhalter im HTML-Template
   *                   muss den Namen <code>name +"Field"</code> habe.
   */
  public static SCheckBox addCheckBoxField(SPanel component, String name) {
    SCheckBox checkBox = new SCheckBox();

    checkBox.setName(name + "Field");
    component.add(checkBox, name + "Field");

    return checkBox;
  }

  /**
   * Fügt dem Panel ein Auswahlfeld({@link SComboBox}) hinzu.
   *
   * @param  component das Panel
   * @param  name      der Name des Auswahlfeld. Der Platzhalter im HTML-Template
   *                   muss den Namen <code>name +"Field"</code> habe.
   * param   items     die darzustellenden Einträge
   */
  public static SComboBox addComboBoxField(SPanel component, String name, String[] items) {
    SComboBox comboBox = new SComboBox();

    comboBox.setName(name + "Field");
    component.add(comboBox, name + "Field");

    if (items != null) {
      int length = items.length;

      for (int i = 0; i < length; i++)
        comboBox.addItem(items[i]);
    }

    return comboBox;
  }

  /**
   * Fügt dem Panel ein Auswahlfeld ({@link SComboBox}) hinzu.
   *
   * @param  component das Panel
   * @param  name      der Name des Auswahlfeld. Der Platzhalter im HTML-Template
   *                   muss den Namen <code>name +"Field"</code> haben.
   */
  public static SComboBox addComboBoxField(SPanel component, String name) {
    return addComboBoxField(component, name, null);
  }

  /**
   * Fügt dem Panel eine {@link STextArea} hinzu.
   *
   * @param  component das Panel
   * @param  name      der Name des Auswahlfeld. Der Platzhalter im HTML-Template
   *                   muss den Namen <code>name +"Field"</code> haben.
   */
  public static STextArea addTextArea(SPanel component, String name, int c, int r) {
    STextArea sTextArea = new STextArea();

    sTextArea.setColumns(c);
    sTextArea.setRows(r);
    sTextArea.setName(name + "Field");
    component.add(sTextArea, name + "Field");

    return sTextArea;
  }

  /**
   * Fügt dem Panel eine Kommando-Schaltfläche hinzu.
   *
   * @param  command  der Name des Kommandos. Der Platzhalter im HTML-Template
   *                   muss den Namen <code>command +"Command"</code> haben.
   * @return die Schaltfläche
   */
  public SButton addCommandButton(Symbol command) {
    return addCommandButton(this, command.toString());
  }

  /**
   * Fügt dem <code>TemplateViewPanel</code> eine Kommando-Schaltfläche hinzu.
   *
   * @param  command  der Name des Kommandos. Der Platzhalter im HTML-Template
   *                  muss den Namen <code>command +"Command"</code> haben.
   * @return die Schaltfläche
   */
  public SButton addCommandButton(String command) {
    return addCommandButton(this, command);
  }

  /**
   * Fügt dem <code>TemplateViewPanel</code> eine Kommando-Schaltfläche mit Icon hinzu.
   *
   * @param  command  der Name des Kommandos. Der Platzhalter im HTML-Template
   *                  muss den Namen <code>command +"Command"</code> haben.
   * @param  icon     das darzustellende Icon
   * @return die Schaltfläche
   */
  public SButton addCommandButton(String command, SIcon icon) {
    return addCommandButton(this, command, icon);
  }

  /**
   * Fügt dem <code>TemplateViewPanel</code> eine Kommando-Schaltfläche mit Icon hinzu.
   *
   * @param  command  der Name des Kommandos. Der Platzhalter im HTML-Template
   *                  muss den Namen <code>command +"Command"</code> haben.
   * @param  icon     das darzustellende Icon
   * @return die Schaltfläche
   */
  public SButton addCommandButton(Symbol command, SIcon icon) {
    return addCommandButton(this, command.toString(), icon);
  }

  /**
   * Fügt dem <code>TemplateViewPanel</code> eine Textfeld hinzu.
   *
   * @param  name     der Name des Textfelds. Der Platzhalter im HTML-Template
   *                   muss den Namen <code>name +"Field"</code> haben.
   * @return das Textfeld
   */
  public STextField addTextField(String name) {
    return addTextField(this, name);
  }

  /**
   * Fügt dem <code>TemplateViewPanel</code> eine Passwort-Textfeld hinzu.
   *
   * @param  name     der Name des Textefelds. Der Platzhalter im HTML-Template
   *                  muss den Namen <code>name +"Field"</code> haben.
   * @return die Schaltfläche
   */
  public final SPasswordField addPasswordField(String name) {
    return addPasswordField(this, name);
  }

  /**
   * Fügt dem <code>TemplateViewPanel</code> ein Kontrollfeld hinzu.
   *
   * @param  name     der Name des Kontrollfelds. Der Platzhalter im HTML-Template
   *                  muss den Namen <code>name +"Field"</code> haben.
   * @return die Schaltfläche
   */
  public final SCheckBox addCheckBoxField(String name) {
    return addCheckBoxField(this, name);
  }

  /**
   * Fügt dem <code>TemplateViewPanel</code> ein Auswahlfeld hinzu.
   *
   * @param  name     der Name des Auswahlfelds. Der Platzhalter im HTML-Template
   *                  muss den Namen <code>name +"Field"</code> haben.
   * @param  items    die Einträge
   * @return das Auswahlfeld
   */
  public SComboBox addComboBoxField(String name, String[] items) {
    return addComboBoxField(this, name, items);
  }

  /**
   * Fügt dem <code>TemplateViewPanel</code> ein Auswahlfeld hinzu.
   *
   * @param  name     der Name des Auswahlfelds. Der Platzhalter im HTML-Template
   *                  muss den Namen <code>name +"Field"</code> haben.
   * @return das Auswahlfeld
   */
  public SComboBox addComboBoxField(String name) {
    return addComboBoxField(this, name);
  }

  public STextArea addTextArea(String name, int c, int r) {
    return addTextArea(this, name, c, r);
  }

//  public static class TemplateComponentHandler implements SpecialTagHandler {
//    private String name;
//    private String value;
//    private Class componentClass;
//
//    public long getTagStart () {
//        return 0;
//    }
//
//    public long getTagLength () {
//        return 0;
//    }
//
//    public void executeTag(ParseContext context, InputStream input)
//        throws Exception
//    {
//      SComponent component = null;
//
//      if (componentClass == STable.class) {
//        component = new STable(new DefaultTableModel());
//      }
//      else if (componentClass == TemplateViewPanel.class) {
//        component = new TemplateViewPanel(value);
//      }
//      else {
//        try {
//          component = (SComponent)componentClass.newInstance();
//        }
//        catch (InstantiationException e) {
//          e.printStackTrace();
//        }
//        catch (IllegalAccessException e) {
//          e.printStackTrace();
//        }
//      }
//
//      ComponentParseContext parseContext = (ComponentParseContext)context;
//
//      if (component != null) {
//        component.setName(name);
//        parseContext.getPanel().add(component, name);
//      }
//    }
//
//    public SGMLTag parseTag(ParseContext context,
//                            PositionReader input,
//                            long startPosition,
//                            SGMLTag tag)
//        throws IOException
//    {
//      tag.parse(input);
//
//      name = tag.value ("NAME", null);
//      String type = tag.value("TYPE", null);
//      value = tag.value("VALUE", null);
//
//      if (name == null)
//        return null;
//
//      ComponentParseContext parseContext = (ComponentParseContext)context;
//      SComponent component = parseContext.getPanel().getComponent(name);
//
//      if (component != null)
//        return null;
//
//      if (type == null)
//        return null;
//
//      type = type.toUpperCase();
//
//      /*
//      * special handling for radio buttons. They react on the
//      * constraint name "NAME=VALUE"
//      */
//      if ("RADIO".equals(type.toUpperCase())) {
//        if (value != null)
//          name = name + "=" + value;
//      }
//
//      componentClass = (Class)TemplateViewPanel.tagMap.get(type);
//
//      if (componentClass == null) {
//        System.out.println("no component found with name " + name + " for type " + type);
//
//        return null;
//      }
//
//      System.out.println("adding " + componentClass.getName() + " with name " + name + " for type " + type);
//
//      return tag;
//    }
//  }
//
//  private static class ComponentParseContext implements ParseContext {
//    private final OutputStream outStream;
//    private final TemplateViewPanel panel;
//
//    public ComponentParseContext(OutputStream outStream, TemplateViewPanel panel) {
//      this.outStream = outStream;
//      this.panel = panel;
//    }
//
//    public OutputStream getOutputStream() {
//      return outStream;
//    }
//
//    public void startTag(int number) {
//    }
//
//    public void doneTag(int number) {
//    }
//
//    public TemplateViewPanel getPanel() {
//      return panel;
//    }
//  }
}
