/**************************************************************************
 *  P A C K A G E                                                         *
 **************************************************************************/

package quasarusers.util.swing;

/**************************************************************************/

/**************************************************************************
 *  I M P O R T S                                                         *
 **************************************************************************/

import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Attribute;
import org.jdom.JDOMException;
import org.jdom.DataConversionException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.AbstractButton;
import javax.swing.JToggleButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JDialog;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.BoxLayout;
import javax.swing.JTabbedPane;
import javax.swing.JPopupMenu;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.JSplitPane;
import javax.swing.text.JTextComponent;
import javax.swing.border.TitledBorder;
import java.io.InputStream;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.StringTokenizer;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Component;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.net.URL;
import java.lang.reflect.Method;

import info.clearthought.layout.TableLayout;

/**************************************************************************/

/**************************************************************************
 *  C L A S S E S                                                         *
 **************************************************************************/

/**************************************************************************/

/**
 * SwingXMLBuilder generates a visual representation from a simple XML
 * description.<br>
 * It also can create non-visble components, and connect the actions
 * of the visuable components to any methods of the non-visual objects.<br>
 * It is somehow like the legendary "NIB-Files" from the NeXT/Apple
 * InterfaceBuilder.app, unfortunately without the interactive UI Builder :-(<br>
 *
 * Both visual widgets as well as non-visual objects can be referred by name,
 * see SwingMapping for a detailed description of how this works.<br>
 * The "id" Attribute of the various elements are mapped to the name of a
 * widget, see JComponent.setName() for further information.<br>
 *
 * This class is somehow an alternative to SwingMapping+SwingDecorator,
 * because the XML description contains all the information a SwingDecorator
 * would normally decorate, and a SwingXMLBuilder has the same
 * key/value methods like SwingMapping.<br/>
 *
 * The non-visual objects only have a flat (non-hierarchical) namespace.
 * It would be very easy to fix that, but because there is no generic
 * "add to parent" method, it generates little value.<br/>
 * If the non-visual object has a "void setSwingXMLBuilder(SwingXMLBuilder pBuilder)"
 * method, that method is called with the builder that created the object.<br/>
 *
 * FIXME: Add support for JSpinner (extends JComponent) and
 *        JFormattedTextfield (extends JTextField)<br/>
 * FIXME: Add support for SpringLayout<br/>
 * FIXME: Add real border support (currently only TitleBorders are created)<br/>
 * FIXME: Add depending filling to the popup menu support, and allow more than
 *        one popup per widget<br/>
 *
 * @author    Jürgen Zeller, jzeller@sdm.de
 */
public class SwingXMLBuilder
{
    /** our DTD SYSTEM ID */
    public static final String SYSTEM_ID             = "http://www.sdm.com/dtd/xml2swing-1.0.dtd";
    // file where expect the DTD
    private static final String SYSTEM_ID_FILE       = "xml2swing-1.0.dtd";
    // flag if we validate XML input (false: no validation, faster!)
    private static       boolean XML_VALIDATE        = true;

    /** separator of component names */
    public static final String SEPARATOR             = "/";

    // non-visual elements
    public static final String OBJECT_ELEMENT        = "object";
    public static final String ACTION_ELEMENT        = "action";

    // action specific elements
    public static final String IS_TOGGLE_ELEMENT     = "isToggle";

    // widgets
    public static final String FRAME_ELEMENT         = "frame";
    public static final String DIALOG_ELEMENT        = "dialog";
    public static final String PANEL_ELEMENT         = "panel";
    public static final String ANY_ELEMENT           = "any";
    public static final String BUTTON_ELEMENT        = "button";
    public static final String LABEL_ELEMENT         = "label";
    public static final String RADIOBUTTON_ELEMENT   = "radiobutton";
    public static final String TOGGLEBUTTON_ELEMENT  = "togglebutton";
    public static final String CHECKBOX_ELEMENT      = "checkbox";
    public static final String TEXTFIELD_ELEMENT     = "textfield";
    public static final String PASSWORDFIELD_ELEMENT = "passwordfield";
    public static final String TEXTAREA_ELEMENT      = "textarea";
    public static final String COMBOBOX_ELEMENT      = "combobox";
    public static final String SLIDER_ELEMENT        = "slider";
    public static final String SPLITPANE_ELEMENT     = "splitpane";
    public static final String SCROLLPANE_ELEMENT    = "scrollpane";
    public static final String TABBEDPANE_ELEMENT    = "tabbedpane";
    public static final String LIST_ELEMENT          = "list";
    public static final String TABLE_ELEMENT         = "table";
    public static final String TREE_ELEMENT          = "tree";
    public static final String MENUBAR_ELEMENT       = "menubar";
    public static final String MENU_ELEMENT          = "menu";
    public static final String MENU_ITEM_ELEMENT     = "menuitem";
    public static final String TOOLBAR_ELEMENT       = "toolbar";
    public static final String TOOLBAR_ITEM_ELEMENT  = "toolbaritem";
    public static final String POPUP_ELEMENT         = "popup";
    public static final String POPUPMENU_ELEMENT     = "popupmenu";
    public static final String POPUP_ITEM_ELEMENT    = "popupitem";
    public static final String SEPARATOR_ELEMENT     = "separator";

    // decorations
    public static final String TEXT_ELEMENT          = "text";
    public static final String TITLE_ELEMENT         = "title";
    public static final String MNEMONIC_ELEMENT      = "mnemonic";
    public static final String ACCELERATOR_ELEMENT   = "accelerator";
    public static final String TOOLTIP_ELEMENT       = "tooltip";
    public static final String ICON_ELEMENT          = "icon";
    public static final String COMBOITEM_ELEMENT     = "comboitem";
    public static final String TABMAPPING_ELEMENT    = "tabmapping";
    public static final String TABITEM_ELEMENT       = "tabitem";
    public static final String POPUPREF_ELEMENT      = "popupref";
    public static final String LAYOUTMANAGER_ELEMENT = "layoutmanager";
    public static final String CONSTRAINT_ELEMENT    = "constraint";

    // connections
    public static final String BUTTONACTION_ELEMENT         = "buttonAction";
    public static final String TEXTFIELDACTION_ELEMENT      = "textfieldAction";
    public static final String MENUACTION_ELEMENT           = "menuAction";
    public static final String CARETACTION_ELEMENT          = "caretAction";
    public static final String CHANGEACTION_ELEMENT         = "chageAction";
    public static final String FOCUSACTION_ELEMENT          = "focusAction";
    public static final String LISTSELECTIONACTION_ELEMENT  = "listSelectionAction";
    public static final String PROPERTYCHANGEACTION_ELEMENT = "propertyChangeAction";
    public static final String TREESELECTIONACTION_ELEMENT  = "treeSelectionAction";
    public static final String ACTIONACTION_ELEMENT         = "actionAction";

    // attributes
    public static final String ID_ATTRIBUTE          = "id";
    public static final String ACTIONREF_ATTRIBUTE   = "actionref";
    public static final String CLOSEACTION_ATTRIBUTE = "closeAction";
    public static final String ESCAPEACTION_ATTRIBUTE= "escapeAction";
    public static final String CLASS_ATTRIBUTE       = "class";
    public static final String DEFAULT_ATTRIBUTE     = "default";
    public static final String LANG_ATTRIBUTE        = "lang";
    public static final String TYPE_ATTRIBUTE        = "type";
    public static final String NAME_ATTRIBUTE        = "name";
    public static final String ORIENTATION_ATTRIBUTE = "orientation";
    public static final String ONETOUCHEXPANDABLE_ATTRIBUTE= "oneTouchExpandable";
    public static final String TABPLACEMENT_ATTRIBUTE= "tabPlacement";
    public static final String SOURCE_ATTRIBUTE      = "source";
    public static final String TARGET_ATTRIBUTE      = "target";
    public static final String METHOD_ATTRIBUTE      = "method";
    public static final String REF_ATTRIBUTE         = "ref";

    // to make the creation of radio buttons easy, we store the ButtonGroup in their parents properties
    public static final String BUTTONGROUP_PROPERTY  = "buttongroup";

    // to make the creation of radio buttons easy, we store temporarly
    // a hashmap for them tab-mapping in the JTabbedPane properties
    public static final String TABMAPPING_PROPERTY   = "tabmapping";

    // we store the "ESCAPE"-Action in the JFrame/JDialog under that name
    public static final String CANCEL_ACTION_KEY     = "CANCEL_ACTION_KEY";

    // marker for radio buttons to preselect the pressed one
    public static final String BUTTONGROUP_TRUE      = "true";

    // values for the layoutmanager and constraint type attribute
    public static final String GRIDBAG_LAYOUT        = "gridbag";
    public static final String NULL_LAYOUT           = "null";
    public static final String BORDER_LAYOUT         = "border";
    public static final String BOX_LAYOUT            = "box";
    public static final String CARD_LAYOUT           = "card";
    public static final String FLOW_LAYOUT           = "flow";
    public static final String GRID_LAYOUT           = "grid";
    public static final String TABLE_LAYOUT          = "table";
    public static final String HGAP_ELEMENT          = "hgap";
    public static final String VGAP_ELEMENT          = "vgap";
    public static final String AXIS_ELEMENT          = "axis";
    public static final String ALIGN_ELEMENT         = "align";
    public static final String COLUMNS_ELEMENT       = "columns";
    public static final String ROWS_ELEMENT          = "rows";

    // XML root element
    public static final String ROOT_ELEMENT          = "xml2swing";
    // XML non-visual element, first under the ROOT_ELEMENT, optional
    public static final String NONVISUAL_ELEMENT     = "nonvisual";
    // XML visual element, second under the ROOT_ELEMENT, optional
    public static final String VISUAL_ELEMENT        = "visual";
    // XML connect element, third under the ROOT_ELEMENT, optional
    public static final String CONNECT_ELEMENT       = "connect";

    // internal name for the viewport
    public static final String VIEWPORT_NAME         = "Viewport";

    /*
     * icon stuff taken from SwingDecorater, should always be in sync
     */
    /// Suffix for the the icon files
    public static final String ICON_PREFIX           = "icons/";
    /// Prefix for the the icon files
    public static final String ICON_SUFFIX           = ".gif";
    /// Suffix for the normal icon
    public static final String ICON_MODIFIER_NORMAL  = "";
    /// Suffix for the disabled icon
    public static final String ICON_MODIFIER_DISABLED= "Disabled";
    /// Suffix for the pressed icon
    public static final String ICON_MODIFIER_PRESSED = "Pressed";
    /// Suffix for the rollover icon
    public static final String ICON_MODIFIER_ROLLOVER= "Rollover";
    /// Suffix for the small icon
    public static final String ICON_MODIFIER_SMALL   = "Small";

    /*
     * start of non-static members
     */

    /// map, key= (hierarchical) name, value= JComponent/JFrame/JDialog
    private Map mNameToVisual;

    /// map, key= JComponent/JFrame/JDialog, value= (hierarchical) name
    private Map mVisualToName;

    /// map, key= name, value= non-visual object
    private Map mNameToNonVisual;

    // cache for our icons
    private  Map mIconCache;


    /**
     * SwingXMLBuilder has a private constructor, because instances are created
     * with the create() methods.
     */
    private SwingXMLBuilder()
    {
        mNameToVisual   = new HashMap();
        mVisualToName   = new HashMap();
        mNameToNonVisual= new HashMap();
        mIconCache      = new HashMap();
    }


    /**
     * Create visual, non-visual objects and a connection between them.
     *
     * @param pElement start point ("root") of the build process
     * @throws IllegalArgumentException if pElement is null, or the XML file contains errors
     * @return a SwingXMLBuilder
     */
    public static SwingXMLBuilder create(Element pElement) throws IllegalArgumentException
    {
        return create(pElement, null);
    }

    /**
     * Create visual, non-visual objects and a connection between them.
     *
     * @param pElement start point ("root") of the build process
     * @param pNonVisual null or non-visual key/object mapping
     * @throws IllegalArgumentException if pElement is null, or the XML file contains errors
     * @return a SwingXMLBuilder
     */
    public static SwingXMLBuilder create(Element pElement, Map pNonVisual) throws IllegalArgumentException
    {
        if (pElement==null)
            throw new IllegalArgumentException("null element");
        if (!ROOT_ELEMENT.equals(pElement.getName()))
            throw new IllegalArgumentException("XML root element ist not "+ROOT_ELEMENT);

        SwingXMLBuilder builder= new SwingXMLBuilder();

        // create non-visual stuff, merge stuff handed in
        if (pNonVisual==null)
        {
            pNonVisual= Collections.EMPTY_MAP;
        }
        builder.buildNonVisualStuff(pElement, pNonVisual);

        // create visual stuff second so we are sure that all actions are parsed
        builder.buildVisualStuff(pElement);

        // handle connnections
        builder.buildConnection(pElement);

        return builder;
    }

    /**
     * Create visual, non-visual objects and a connection between them.
     *
     * @param pInputStream the XML description
     * @throws IllegalArgumentException if pElement is null, or the XML file contains errors
     * @return a SwingXMLBuilder
     */
    public static SwingXMLBuilder create(InputStream pInputStream) throws IllegalArgumentException
    {
        return create(pInputStream, null);
    }

    /**
     * Create visual, non-visual objects and a connection between them.
     *
     * @param pInputStream the XML description
     * @param pNonVisual null or non-visual key/object mapping
     * @throws IllegalArgumentException if pElement is null, or the XML file contains errors
     * @return a SwingXMLBuilder
     */
    public static SwingXMLBuilder create(InputStream pInputStream, Map pNonVisual) throws IllegalArgumentException
    {
        if (pInputStream==null)
            throw new IllegalArgumentException("null inputstream");

        SAXBuilder builder= new SAXBuilder();
        builder.setEntityResolver(new EntityHelper());
        if (XML_VALIDATE)
        {
            builder.setValidation(true);
        }
        try
        {
            Document doc = builder.build(pInputStream);
            Element  root= doc.getRootElement();
            return create(root, pNonVisual);
        }
        catch (JDOMException e)
        {
            throw new IllegalArgumentException(e.toString());
        }
    }

    /**
     * Returns a widget for the given name.
     *
     * @param pName (hierarchical) name of the widget
     * @return null if the object is unknown
     */
    public Container getContainerByName(String pName)
    {
        Object value= getVisualByName(pName);
        if (value instanceof Container)
            return (Container)value;
        else
            return null;
    }

    /**
     * Returns (hierarchical) name for the given widget.
     *
     * @param pContainer a widget
     * @return null if the widget is unknown, (hierarchical) name otherwise
     */
    public String getNameByContainer(Container pContainer)
    {
        return (String)mVisualToName.get(pContainer);
    }

    /**
     * Returns a widget for the given name.
     *
     * @param pName (hierarchical) name of the widget
     * @return null if the object is unknown, or the widget is not a JComponent
     * @throws IllegalArgumentException the name is not resolvable due too many ".."'s
     */
    public JComponent getComponentByName(String pName)
    {
        Object value= getVisualByName(pName);
        if (value instanceof JComponent)
            return (JComponent)value;
        else
            return null;
    }

    /**
     * common part of getContainerByName() and getComponentByName(), does also
     * the ".." resolving
     * @param pName component name (may be null), inside the
     *              name all ".."'s are resolved first, so it is o.k. to
     *              use "somePanel/someButton/../someTextField" to get
     *              from any component to any other.
     * @return null or the object from mNameToVisual that matches the (resolved) name
     */
    private Object getVisualByName(String pName)
    {
        // null maps always to null
        if (pName==null)
        {
            return null;
        }

        String name= null;
        // check if we need the ".." resolving at all
        if (pName.indexOf("..")==-1)
        {
            name= pName;
        }
        else
        {
            // the following code does the ".." resolving
            StringTokenizer tokenizer = new StringTokenizer(pName, SEPARATOR);
            List            list      = new ArrayList(tokenizer.countTokens());
            int             stackCount= 0;
            while (tokenizer.hasMoreTokens())
            {
                String token= tokenizer.nextToken();
                if (token.equals(".."))
                {
                    // pop
                    stackCount--;
                    if (stackCount<0)
                    {
                        throw new IllegalArgumentException(
                                pName + ": contains more ..'s than elements");
                    }
                    list.remove(stackCount);
                }
                else
                {
                    // push
                    list.add(token);
                    stackCount++;
                }
            }
            StringBuffer nameBuffer= new StringBuffer();
            for (int i= 0, n= list.size(); i < n; i++)
            {
                nameBuffer.append((String)list.get(i));
                if (i < n-1)
                {
                    nameBuffer.append(SEPARATOR);
                }
            }
            name= nameBuffer.toString();
        }

        // name now contains the resolved name, the Map will either return
        // null or the matching object
        return mNameToVisual.get(name);
    }

    /**
     * Returns (hierarchical) name for the given widget.
     *
     * @param pComponent a widget
     * @return null if the widget is unknown, (hierarchical) name otherwise
     */
    public String getNameByComponent(JComponent pComponent)
    {
        return getNameByContainer(pComponent);
    }

    /**
     * Returns a non-visual object that was in the description
     *
     * @param pName (hierarchical) name of the non-visual object
     * @return null if the object is unknown
     */
    public Object getNonVisualObject(String pName)
    {
        return mNameToNonVisual.get(pName);
    }


    /**
     * Small helper method to get the trigger of a popup action.
     * We assume that the parent of the trigger widget is always a JPopupMenu, and
     * that all involved popup menus are chained by getInvoker().
     * @param e triggered action event (may be null)
     * @return null or the swing component that triggered the popup
     */
    static public JComponent getPopupTrigger(ActionEvent e)
    {
        JPopupMenu popup= getRootPopup(e);
        if (popup!=null)
        {
            Component trigger= popup.getInvoker();
            if (trigger instanceof JComponent)
            {
                return (JComponent)trigger;
            }
            else
            {
                return null;
            }
        }
        return null;
    }

    /**
     * Small helper method to get the popup menu of a popup action.
     * We assume that the parent of the trigger widget is always a JPopupMenu, and
     * that all involved popup menus are chained by getInvoker().
     * Warning: when the ActionEvent was fired by a menu/toolbar entry,
     *          it returns _NOT_ null!
     * @param e triggered action event (may be null)
     * @return null or the JPopupMenu that triggered the popup
     */
    static public JPopupMenu getRootPopup(ActionEvent e)
    {
        if (e==null)
        {
            return null;
        }

        Object     source= e.getSource();
        JPopupMenu mp    = null;
        if (source instanceof DispatcherAction)
        {
            source= ((DispatcherAction)source).getRealSource();
        }
        if (source instanceof Component)
        {
            Component parent= ((Component)source).getParent();
            if (parent instanceof JPopupMenu)
            {
                mp = (JPopupMenu)parent;
                while((mp!=null)                                         &&
                      (mp.getInvoker() != null)                          &&
                      (mp.getInvoker() instanceof JMenu)                 &&
                      (mp.getInvoker().getParent() != null)              &&
                      (mp.getInvoker().getParent() instanceof JPopupMenu)  )
                {
                    mp = (JPopupMenu) mp.getInvoker().getParent();
                }
            }
        }

        return mp;
    }


    /**
     * Builds the visual tree and adds named widgets to our hashmap.
     *
     * @param pRoot the root (xml2swing) node
     */
    private void buildVisualStuff(Element pRoot) throws IllegalArgumentException
    {
        List topLevelChildren= pRoot.getChild(VISUAL_ELEMENT).getChildren();
        for (int i = 0; i < topLevelChildren.size(); i++)
        {
            Element topLevel = (Element) topLevelChildren.get(i);
            if (isWidgetElement(topLevel))
            {
                addChildren(null, topLevel);
            }
            else
            {
                // silently ignore unknown stuff
            }
        }
    }

    /**
     * Builds the non-visual tree and adds named objects to our hashmap.
     *
     * @param pRoot the root (xml2swing) node
     * @param pNonVisual empty map or pre-defined (external) non-visual elements
     */
    private void buildNonVisualStuff(Element pRoot, Map pNonVisual) throws IllegalArgumentException
    {
        Element nonVisualElement = pRoot.getChild(NONVISUAL_ELEMENT);
        if (nonVisualElement==null)
        {
            return;
        }
        List objects= nonVisualElement.getChildren();
        for (int i = 0; i < objects.size(); i++)
        {
            Element   element   = (Element) objects.get(i);
            String    objectType= element.getName();

            // object or action we create
            Object object= null;

            // get id
            Attribute idAttribute   = element.getAttribute(ID_ATTRIBUTE);
            if (idAttribute == null)
                throw new IllegalArgumentException("a non visual element needs a "+ID_ATTRIBUTE+" attribute");

            // check if we need to create that object at all
            String id= idAttribute.getValue();
            if (pNonVisual.containsKey(id))
            {
                mNameToNonVisual.put(id, pNonVisual.get(id));
                continue;
            }

            if (objectType.equals(OBJECT_ELEMENT))
            {
                object = createObject(element);

            }
            else if (objectType.equals(ACTION_ELEMENT))
            {
                object= createAction(element);
            }
            else
            {
                // ignore unknonwn elements
            }

            // put it in our map
            mNameToNonVisual.put(idAttribute.getValue(), object);
        }
    }

    /**
     * Create the connection between the visual and non visual objects.
     * @param pRoot the root (xml2swing) node
     */
    private void buildConnection(Element pRoot) throws IllegalArgumentException
    {
        Element connectElement = pRoot.getChild(CONNECT_ELEMENT);
        if (connectElement==null)
        {
            return;
        }
        List connections= connectElement.getChildren();
        for (int i = 0; i < connections.size(); i++)
        {
            Element   connection     = (Element) connections.get(i);
            String    connectionType = connection.getName();
            Attribute sourceAttribute= connection.getAttribute(SOURCE_ATTRIBUTE);
            Attribute targetAttribute= connection.getAttribute(TARGET_ATTRIBUTE);
            Attribute methodAttribute= connection.getAttribute(METHOD_ATTRIBUTE);
            if (sourceAttribute == null)
                throw new IllegalArgumentException("a connection element needs a "+SOURCE_ATTRIBUTE+" attribute");
            if (targetAttribute == null)
                throw new IllegalArgumentException("a connection element needs a "+TARGET_ATTRIBUTE+" attribute");
            if (methodAttribute == null)
                throw new IllegalArgumentException("a connection element needs a "+METHOD_ATTRIBUTE+" attribute");

            String  targetName= targetAttribute.getValue();
            Object  target    = getNonVisualObject(targetName);
            String  method    = methodAttribute.getValue();

            if (target==null)
                throw new IllegalArgumentException("no target found for target name "+targetAttribute.getValue());
            if (method==null || method.equals(""))
                throw new IllegalArgumentException("no valid method for target name "+targetAttribute.getValue());

            if (connectionType.equals(BUTTONACTION_ELEMENT))
            {
                AbstractButton source= (AbstractButton)getComponentByName(sourceAttribute.getValue());
                SwingConnectionManager.createActionConnection(source, target, method);
            }
            else if (connectionType.equals(TEXTFIELDACTION_ELEMENT))
            {
                JTextField source= (JTextField)getComponentByName(sourceAttribute.getValue());
                SwingConnectionManager.createActionConnection(source, target, method);
            }
            else if (connectionType.equals(MENUACTION_ELEMENT))
            {
                JMenuItem source= (JMenuItem)getComponentByName(sourceAttribute.getValue());
                SwingConnectionManager.createActionConnection(source, target, method);
            }
            else if (connectionType.equals(CARETACTION_ELEMENT))
            {
                JTextComponent source= (JTextComponent)getComponentByName(sourceAttribute.getValue());
                SwingConnectionManager.createCaretConnection(source, target, method);
            }
            else if (connectionType.equals(CHANGEACTION_ELEMENT))
            {
                JComponent component= getComponentByName(sourceAttribute.getValue());
                if (component instanceof AbstractButton)
                {
                    AbstractButton source= (AbstractButton) component;
                    SwingConnectionManager.createChangeConnection(source, target, method);
                }
                else if (component instanceof JSlider)
                {
                    JSlider source= (JSlider) component;
                    SwingConnectionManager.createChangeConnection(source, target, method);
                }
            }
            else if (connectionType.equals(FOCUSACTION_ELEMENT))
            {
                JComponent source= getComponentByName(sourceAttribute.getValue());
                SwingConnectionManager.createFocusConnection(source, target, method);
            }
            else if (connectionType.equals(LISTSELECTIONACTION_ELEMENT))
            {
                JComponent component= getComponentByName(sourceAttribute.getValue());
                if (component instanceof JList)
                {
                    JList source= (JList) component;
                    SwingConnectionManager.createListSelectionConnection(source, target, method);
                }
                else if (component instanceof JTable)
                {
                    JTable source= (JTable) component;
                    SwingConnectionManager.createListSelectionConnection(source, target, method);
                }
            }
            else if (connectionType.equals(PROPERTYCHANGEACTION_ELEMENT))
            {
                JComponent source= getComponentByName(sourceAttribute.getValue());
                SwingConnectionManager.createPropertyChangeConnection(source, target, method);
            }
            else if (connectionType.equals(TREESELECTIONACTION_ELEMENT))
            {
                JTree source= (JTree)getComponentByName(sourceAttribute.getValue());
                SwingConnectionManager.createTreeSelectionConnection(source, target, method);
            }
            else if (connectionType.equals(ACTIONACTION_ELEMENT))
            {
                DispatcherAction source= (DispatcherAction)getNonVisualObject(sourceAttribute.getValue());
                SwingConnectionManager.createActionConnection(source, target, method);
            }
            else
            {
                throw new IllegalArgumentException("unknown connectionn type "+connectionType);
            }
        }
    }


    /**
     * This method does recursivly create the widgets.
     *
     * @param pSwingParent null or the parent widget
     * @param pXMLCurrent the child widget as a XML element
     * @throws IllegalArgumentException if problems are found
     */
    private Container addChildren(Container pSwingParent, Element pXMLCurrent) throws IllegalArgumentException
    {
        List         childElements      = pXMLCurrent.getChildren();
        List         childWidgetElements= new ArrayList();
        SwingElement swingCurrent       = null;

        // create ourself
        swingCurrent= createWidget(pSwingParent, pXMLCurrent);

        // handle our child widgets and our attributes
        for (Iterator i= childElements.iterator(); i.hasNext(); )
        {
            Element childElement= (Element)i.next();

            if (isWidgetElement(childElement))
            {
                // create a child (deferred, to decorate the current element first)
                childWidgetElements.add(childElement);
            }
            else if (isWidgetDecoration(childElement))
            {
                // decorate ourself
                decorateWidget(swingCurrent.getComponent(), childElement);
            }
            else
            {
                // silently ignore unknown stuff
            }
        }

        // create our childs
        for (Iterator i= childWidgetElements.iterator(); i.hasNext(); )
        {
            Element childElement= (Element)i.next();
            addChildren(swingCurrent.getComponent(), childElement);
        }

        // add ourself to our parent
        if (pSwingParent!=null)
        {
            Component currentComponent= swingCurrent.getComponent();

            // special case for toolbar item build from an action or from a separator
            if (currentComponent == null)
            {
                // nothing to do, the item was added in the create method
            }
            // special case for JSplitPane
            else if (pSwingParent instanceof JSplitPane)
            {
                // if there is no left component, we are the "left" one, otherwise
                // we are the right one
                JSplitPane splitPane= (JSplitPane)pSwingParent;
                if (splitPane.getLeftComponent()==null)
                {
                    splitPane.setLeftComponent(currentComponent);
                }
                else
                {
                    splitPane.setRightComponent(currentComponent);
                }
            }
            // special case for JScrollPane
            else if (pSwingParent instanceof JScrollPane)
            {
                ((JScrollPane)pSwingParent).setViewportView(currentComponent);
            }
            // special case for JTabbedPane
            else if (pSwingParent instanceof JTabbedPane)
            {
                JTabbedPane tabbedPane= (JTabbedPane) pSwingParent;
                Map         tabMapping= (Map)tabbedPane.getClientProperty(TABMAPPING_PROPERTY);
                String      widgetName= currentComponent.getName();
                if ((tabMapping!=null) && tabMapping.containsKey(widgetName))
                {
                    Object[] mapValue= (Object[])tabMapping.get(widgetName);
                    tabbedPane.addTab((String)mapValue[0], (Icon)mapValue[1],
                                      currentComponent, (String)mapValue[2]);

                    // remove this mapping, and remove map if this was the
                    // last entry
                    tabMapping.remove(widgetName);
                    if (tabMapping.size()==0)
                    {
                        tabbedPane.putClientProperty(TABMAPPING_PROPERTY, null);
                    }
                }
                else
                {
                    // silently ignore non-mapped childs
                }
            }
            // special case for JFrame
            else if (pSwingParent instanceof JFrame)
            {
                JFrame frame= (JFrame)pSwingParent;

                if (currentComponent instanceof JMenuBar)
                {
                    frame.setJMenuBar((JMenuBar)currentComponent);
                }
                else if (currentComponent instanceof JToolBar)
                {
                    frame.getContentPane().add(currentComponent, BorderLayout.NORTH);
                }
                else if (currentComponent instanceof JDialog)
                {
                    // nothing to do, already done in the createDialog method
                }
                else
                {
                    // currently we support only one panel per JFrame
                    frame.getContentPane().add(currentComponent);
                }
            }
            // special case for JDialog
            else if (pSwingParent instanceof JDialog)
            {
                ((JDialog)pSwingParent).getContentPane().add(currentComponent);
            }
            else
            {
                Object constraint= swingCurrent.getConstraint();
                if (constraint!=null)
                    pSwingParent.add(currentComponent, constraint);
                else
                    pSwingParent.add(currentComponent);
            }
        }

        return swingCurrent.getComponent();
    }

    /**
     * Tests if the given node is a widget decoration and not a ("new") widget.
     *
     * @param pElement node to test
     * @return true if the elment node is a widget description
     */
    private boolean isWidgetDecoration(Element pElement)
    {
        String widgetDecorationName= pElement.getName();
        if (widgetDecorationName.equals(TEXT_ELEMENT))
            return true;
        else if (widgetDecorationName.equals(TITLE_ELEMENT))
            return true;
        else if (widgetDecorationName.equals(MNEMONIC_ELEMENT))
            return true;
        else if (widgetDecorationName.equals(ACCELERATOR_ELEMENT))
            return true;
        else if (widgetDecorationName.equals(TOOLTIP_ELEMENT))
            return true;
        else if (widgetDecorationName.equals(ICON_ELEMENT))
            return true;
        else if (widgetDecorationName.equals(COMBOITEM_ELEMENT))
            return true;
        else if (widgetDecorationName.equals(TABMAPPING_ELEMENT))
            return true;
        else if (widgetDecorationName.equals(TABITEM_ELEMENT))
            return true;
        else if (widgetDecorationName.equals(POPUPREF_ELEMENT))
            return true;
        else if (widgetDecorationName.equals(LAYOUTMANAGER_ELEMENT))
            return true;
        else if (widgetDecorationName.equals(CONSTRAINT_ELEMENT))
            return true;
        else
            return false;
    }

    /**
     * This method decorates the handed component if the handed element does make sense.
     *
     * @param pContainer container that will be decorated
     * @param pElement   element describing the decoration
     */
    private void decorateWidget(Container pContainer, Element pElement)
    {
        String widgetDecorationName= pElement.getName();

        if (widgetDecorationName.equals(TEXT_ELEMENT))
        {
            if (isSameLanguage(pElement))
            {
                if (pContainer instanceof JLabel)
                    ((JLabel)pContainer).setText(pElement.getText());
                else if (pContainer instanceof AbstractButton)
                    ((AbstractButton)pContainer).setText(pElement.getText());
            }
        }
        else if (widgetDecorationName.equals(TITLE_ELEMENT))
        {
            if (isSameLanguage(pElement))
            {
                if (pContainer instanceof JComponent)
                    ((JComponent)pContainer).setBorder(new TitledBorder(pElement.getText()));
                else if (pContainer instanceof JFrame)
                    ((JFrame)pContainer).setTitle(pElement.getText());
                else if (pContainer instanceof JDialog)
                    ((JDialog)pContainer).setTitle(pElement.getText());
            }
        }
        else if (widgetDecorationName.equals(MNEMONIC_ELEMENT))
        {
            if (isSameLanguage(pElement))
            {
                if (pContainer instanceof AbstractButton)
                {
                    ((AbstractButton)pContainer).setMnemonic(pElement.getText().charAt(0));
                }
                else if (pContainer instanceof JLabel)
                {
                    ((JLabel)pContainer).setDisplayedMnemonic(pElement.getText().charAt(0));
                }
            }
        }
        else if (widgetDecorationName.equals(ACCELERATOR_ELEMENT))
        {
            if (isSameLanguage(pElement) && (pContainer instanceof JMenuItem))
            {
                String accelerator= "control " + Character.toUpperCase(pElement.getText().charAt(0));
                ((JMenuItem)pContainer).setAccelerator(KeyStroke.getKeyStroke(accelerator));
            }
        }
        else if (widgetDecorationName.equals(TOOLTIP_ELEMENT))
        {
            if (isSameLanguage(pElement) && (pContainer instanceof JComponent))
            {
                ((JComponent)pContainer).setToolTipText(pElement.getText());
            }
        }
        else if (widgetDecorationName.equals(ICON_ELEMENT))
        {
            Attribute nameAttribute= pElement.getAttribute(NAME_ATTRIBUTE);
            if (isSameLanguage(pElement) && (nameAttribute!=null))
            {
                String iconName= nameAttribute.getValue();
                if (pContainer instanceof AbstractButton)
                {
                    AbstractButton button= (AbstractButton) pContainer;
                    Icon iconNormal  = getIcon(iconName, ICON_MODIFIER_NORMAL);
                    Icon iconPressed = getIcon(iconName, ICON_MODIFIER_PRESSED);
                    Icon iconRollover= getIcon(iconName, ICON_MODIFIER_ROLLOVER);
                    Icon iconDisabled= getIcon(iconName, ICON_MODIFIER_DISABLED);
                    if (iconNormal != null)
                    {
                        button.setIcon        (iconNormal);
                        button.setDisabledIcon(iconDisabled);
                        button.setPressedIcon (iconPressed);
                        if (iconRollover == null)
                        {
                            button.setRolloverEnabled(false);
                        }
                        else
                        {
                            button.setRolloverEnabled(true);
                            button.setRolloverIcon(iconRollover);
                        }
                    }
                }
                else if (pContainer instanceof JFrame)
                {
                    JFrame    frame    = (JFrame)pContainer;
                    ImageIcon imageIcon= getIcon(iconName, ICON_MODIFIER_SMALL);
                    if (imageIcon!=null)
                    {
                        frame.setIconImage(imageIcon.getImage());
                    }
                }
                else if (pContainer instanceof JLabel)
                {
                    JLabel label= (JLabel)pContainer;
                    ImageIcon imageIcon= getIcon(iconName, ICON_MODIFIER_NORMAL);
                    if (imageIcon!=null)
                    {
                        label.setIcon(imageIcon);
                    }
                }
                else if (pContainer instanceof JMenuItem)
                {
                    JMenuItem menuItem = (JMenuItem) pContainer;
                    ImageIcon imageIcon= getIcon(iconName, ICON_MODIFIER_SMALL);
                    if (imageIcon!=null)
                    {
                        menuItem.setIcon(imageIcon);
                    }
                }
                else
                {
                    throw new IllegalArgumentException("no icon support for "+pContainer);
                }
            }
        }
        else if (widgetDecorationName.equals(COMBOITEM_ELEMENT))
        {
            if (isSameLanguage(pElement) && (pContainer instanceof JComboBox))
            {
                JComboBox combobox    = (JComboBox) pContainer;
                String    comboboxItem= pElement.getTextTrim();
                if (!comboboxItem.equals(""))
                {
                    combobox.addItem(comboboxItem);
                }
            }
        }
        else if (widgetDecorationName.equals(TABMAPPING_ELEMENT))
        {
            if (pContainer instanceof JTabbedPane)
            {
                JTabbedPane tabbedPane= (JTabbedPane) pContainer;
                Map         tabMapping= (Map)tabbedPane.getClientProperty(TABMAPPING_PROPERTY);
                if (tabMapping==null)
                {
                    tabMapping= new HashMap();
                    tabbedPane.putClientProperty(TABMAPPING_PROPERTY, tabMapping);
                }
                // extract and store mapping
                storeTabMapInformation(tabMapping, pElement);
            }
        }
        else if (widgetDecorationName.equals(POPUPREF_ELEMENT))
        {
            if (pContainer instanceof JPopupMenu)
            {
                JPopupMenu   popup       = (JPopupMenu) pContainer;
                PopupManager popupManger = (PopupManager) popup.getClientProperty(PopupManager.POPUPMANAGER_PROPERTY);
                Attribute    refAttribute= pElement.getAttribute(REF_ATTRIBUTE);
                JComponent   component   = null;
                if (refAttribute!= null)
                {
                    String widgetName= refAttribute.getValue();
                    component= getComponentByName(widgetName);
                    if (component!=null)
                    {
                        popupManger.addManagedComponent(component);
                    }
                }
            }
        }
        else if (widgetDecorationName.equals(LAYOUTMANAGER_ELEMENT))
        {
            Attribute typeAttribute= pElement.getAttribute(TYPE_ATTRIBUTE);
            if (typeAttribute!= null)
            {
                // create layout manager from type
                String        layoutType   = typeAttribute.getValue();
                LayoutManager layoutManager= null;
                if (layoutType.equals(GRIDBAG_LAYOUT))
                {
                    layoutManager= new GridBagLayout();
                }
                else if (layoutType.equals(NULL_LAYOUT))
                {
                    layoutManager= null;
                }
                else if (layoutType.equals(BORDER_LAYOUT))
                {
                    Element child;
                    int     hgap= 0;
                    int     vgap= 0;
                    if ((child= pElement.getChild(HGAP_ELEMENT))!=null)
                    {
                        hgap= Integer.parseInt(child.getTextTrim());
                    }
                    if ((child= pElement.getChild(VGAP_ELEMENT))!=null)
                    {
                        vgap= Integer.parseInt(child.getTextTrim());
                    }
                    layoutManager= new BorderLayout(hgap, vgap);
                }
                else if (layoutType.equals(BOX_LAYOUT))
                {
                    Element child;
                    int     axis= 0;
                    if ((child= pElement.getChild(AXIS_ELEMENT))!=null)
                    {
                        axis= Integer.parseInt(child.getTextTrim());
                    }
                    layoutManager= new BoxLayout(pContainer, axis);
                }
                else if (layoutType.equals(CARD_LAYOUT))
                {
                    Element child;
                    int     hgap= 0;
                    int     vgap= 0;
                    if ((child= pElement.getChild(HGAP_ELEMENT))!=null)
                    {
                        hgap= Integer.parseInt(child.getTextTrim());
                    }
                    if ((child= pElement.getChild(VGAP_ELEMENT))!=null)
                    {
                        vgap= Integer.parseInt(child.getTextTrim());
                    }
                    layoutManager= new CardLayout(hgap, vgap);
                }
                else if (layoutType.equals(FLOW_LAYOUT))
                {
                    Element child;
                    int     align= 0;
                    int     hgap = 0;
                    int     vgap = 0;
                    if ((child= pElement.getChild(ALIGN_ELEMENT))!=null)
                    {
                        align= Integer.parseInt(child.getTextTrim());
                    }
                    if ((child= pElement.getChild(HGAP_ELEMENT))!=null)
                    {
                        hgap= Integer.parseInt(child.getTextTrim());
                    }
                    if ((child= pElement.getChild(VGAP_ELEMENT))!=null)
                    {
                        vgap= Integer.parseInt(child.getTextTrim());
                    }
                    layoutManager= new FlowLayout(align, hgap, vgap);
                }
                else if (layoutType.equals(GRID_LAYOUT))
                {
                    Element child;
                    int     rows = 0;
                    int     cols = 0;
                    int     hgap = 0;
                    int     vgap = 0;
                    if ((child= pElement.getChild(ROWS_ELEMENT))!=null)
                    {
                        rows= Integer.parseInt(child.getTextTrim());
                    }
                    if ((child= pElement.getChild(COLUMNS_ELEMENT))!=null)
                    {
                        cols= Integer.parseInt(child.getTextTrim());
                    }
                    if ((child= pElement.getChild(HGAP_ELEMENT))!=null)
                    {
                        hgap= Integer.parseInt(child.getTextTrim());
                    }
                    if ((child= pElement.getChild(VGAP_ELEMENT))!=null)
                    {
                        vgap= Integer.parseInt(child.getTextTrim());
                    }
                    layoutManager= new GridLayout(rows, cols, hgap, vgap);
                }
                else if (layoutType.equals(TABLE_LAYOUT))
                {
                    Element  child;
                    double[] columns= {};
                    double[] rows   = {};
                    if ((child= pElement.getChild(COLUMNS_ELEMENT))!=null)
                    {
                        String   childText    = child.getTextTrim();
                        String[] columnsString= childText.split("\\s*,\\s*");
                        columns= new double[columnsString.length];
                        for (int i = 0; i < columnsString.length; i++)
                        {
                            String columnString= columnsString[i];
                            columns[i]= Double.parseDouble(columnString);
                        }

                    }
                    if ((child= pElement.getChild(ROWS_ELEMENT))!=null)
                    {
                        String   childText = child.getTextTrim();
                        String[] rowsString= childText.split("\\s*,\\s*");
                        rows= new double[rowsString.length];
                        for (int i = 0; i < rowsString.length; i++)
                        {
                            String rowString= rowsString[i];
                            rows[i]= Double.parseDouble(rowString);
                        }
                    }
                    double [][] size= {columns, rows};
                    layoutManager= new TableLayout(size);
                }
                else
                {
                    throw new IllegalArgumentException("unknown layout "+layoutType);
                }

                pContainer.setLayout(layoutManager);
            }
        }
        // CONSTRAINT_ELEMENT is already handled in createWidget()
    }

    /**
     * store the mapping information in the tabbed pane as a client property,
     * the ref attribute is used as a key, value is a Object[3] array of
     * title, icon and tooltip.
     * @param pTabMapping where to store the information
     * @param pElement the tabmapping element containing the mapping
     */
    private void storeTabMapInformation(Map pTabMapping, Element pElement)
    {
        List tabItems= pElement.getChildren(TABITEM_ELEMENT);
        for (Iterator i = tabItems.iterator(); i.hasNext();)
        {
            // each tabitem contains the widget name to add as a ref attribute,
            // and other information (title, icon, tooltip) as a child element
            Element tabItem = (Element) i.next();

            Attribute refAttribute= tabItem.getAttribute(REF_ATTRIBUTE);
            if (refAttribute==null)
            {
                throw new IllegalArgumentException("tabitem requires a ref attribute");
            }

            // store mapping
            String   refName = refAttribute.getValue();
            Object[] mapValue= new Object[3];
            if (pTabMapping.containsKey(refName))
            {
                throw new IllegalArgumentException("non uniq refName in tabmapping: "+refName);
            }
            pTabMapping.put(refName, mapValue);

            // initialize title with widget name
            mapValue[0]= refName;

            // extract title
            for (Iterator j= tabItem.getChildren(TITLE_ELEMENT).iterator(); j.hasNext();)
            {
                Element titleElement= (Element) j.next();
                if (isSameLanguage(titleElement))
                {
                    String title= titleElement.getText();
                    mapValue[0]= title;
                }
            }
            // extract icon
            for (Iterator j= tabItem.getChildren(ICON_ELEMENT).iterator(); j.hasNext();)
            {
                Element   iconElement  = (Element) j.next();
                Attribute nameAttribute= iconElement.getAttribute(NAME_ATTRIBUTE);
                if (isSameLanguage(iconElement) && (nameAttribute!=null))
                {
                    String iconRelName= nameAttribute.getValue();
                    Icon   icon       = getIcon(iconRelName, ICON_MODIFIER_NORMAL);
                    mapValue[1]= icon;
                }
            }
            // extract tooltip
            for (Iterator j= tabItem.getChildren(TOOLTIP_ELEMENT).iterator(); j.hasNext();)
            {
                Element tooltipElement= (Element) j.next();
                if (isSameLanguage(tooltipElement))
                {
                    String tooltip= tooltipElement.getText();
                    mapValue[2]= tooltip;
                }
            }
        }
    }

    /**
     * Tests if the element contains information that matches the language of the
     * current locale.
     *
     * @param pElement element to check
     * @return true if the element has no special language or language matches
     */
    private boolean isSameLanguage(Element pElement)
    {
        String iso639Language= Locale.getDefault().getLanguage();
        Attribute elementLanguage= pElement.getAttribute(LANG_ATTRIBUTE);
        return ((elementLanguage==null) || elementLanguage.getValue().equals(iso639Language));
    }

    /**
     * Tests if the given node is a ("new") widget and nothing of a widget description.
     *
     * @param pElement node to test
     * @return true if the elment node is a new widget
     */
    private boolean isWidgetElement(Element pElement)
    {
        String widgetTypeName= pElement.getName();
        if (widgetTypeName.equals(FRAME_ELEMENT))
            return true;
        else if (widgetTypeName.equals(DIALOG_ELEMENT))
            return true;
        else if (widgetTypeName.equals(PANEL_ELEMENT))
            return true;
        else if (widgetTypeName.equals(ANY_ELEMENT))
            return true;
        else if (widgetTypeName.equals(BUTTON_ELEMENT))
            return true;
        else if (widgetTypeName.equals(LABEL_ELEMENT))
            return true;
        else if (widgetTypeName.equals(RADIOBUTTON_ELEMENT))
            return true;
        else if (widgetTypeName.equals(TOGGLEBUTTON_ELEMENT))
            return true;
        else if (widgetTypeName.equals(CHECKBOX_ELEMENT))
            return true;
        else if (widgetTypeName.equals(TEXTFIELD_ELEMENT))
            return true;
        else if (widgetTypeName.equals(PASSWORDFIELD_ELEMENT))
            return true;
        else if (widgetTypeName.equals(TEXTAREA_ELEMENT))
            return true;
        else if (widgetTypeName.equals(COMBOBOX_ELEMENT))
            return true;
        else if (widgetTypeName.equals(SLIDER_ELEMENT))
            return true;
        else if (widgetTypeName.equals(SPLITPANE_ELEMENT))
            return true;
        else if (widgetTypeName.equals(SCROLLPANE_ELEMENT))
            return true;
        else if (widgetTypeName.equals(TABBEDPANE_ELEMENT))
            return true;
        else if (widgetTypeName.equals(LIST_ELEMENT))
            return true;
        else if (widgetTypeName.equals(TABLE_ELEMENT))
            return true;
        else if (widgetTypeName.equals(TREE_ELEMENT))
            return true;
        else if (widgetTypeName.equals(MENUBAR_ELEMENT))
            return true;
        else if (widgetTypeName.equals(MENU_ELEMENT))
            return true;
        else if (widgetTypeName.equals(MENU_ITEM_ELEMENT))
            return true;
        else if (widgetTypeName.equals(TOOLBAR_ELEMENT))
            return true;
        else if (widgetTypeName.equals(TOOLBAR_ITEM_ELEMENT))
            return true;
        else if (widgetTypeName.equals(POPUP_ELEMENT))
            return true;
        else if (widgetTypeName.equals(POPUPMENU_ELEMENT))
            return true;
        else if (widgetTypeName.equals(POPUP_ITEM_ELEMENT))
            return true;
        else if (widgetTypeName.equals(SEPARATOR_ELEMENT))
            return true;
        else
            return false;
    }

    /**
     * Create a widget for the handed element.
     * @param pParent null or parent of the widget
     * @param pElement widget as XML Element
     * @return widget/constrain pair
     */
    private SwingElement createWidget(Container pParent, Element pElement)
    {
        SwingElement widget        = null;
        String       widgetTypeName= pElement.getName();

        if (widgetTypeName.equals(FRAME_ELEMENT))
            widget= createFrame(pElement);
        else if (widgetTypeName.equals(DIALOG_ELEMENT))
            widget= createDialog(pParent, pElement);
        else if (widgetTypeName.equals(PANEL_ELEMENT))
            widget= createPanel(pElement);
        else if (widgetTypeName.equals(ANY_ELEMENT))
            widget= createAny(pElement);
        else if (widgetTypeName.equals(BUTTON_ELEMENT))
            widget= createButton(pElement);
        else if (widgetTypeName.equals(LABEL_ELEMENT))
            widget= createLabel(pElement);
        else if (widgetTypeName.equals(RADIOBUTTON_ELEMENT))
            widget= createRadioButton(pParent, pElement);
        else if (widgetTypeName.equals(TOGGLEBUTTON_ELEMENT))
            widget= createToggleButton(pElement);
        else if (widgetTypeName.equals(CHECKBOX_ELEMENT))
            widget= createCheckBox(pElement);
        else if (widgetTypeName.equals(TEXTFIELD_ELEMENT))
            widget= createTextField(pElement);
        else if (widgetTypeName.equals(PASSWORDFIELD_ELEMENT))
            widget= createPasswordField(pElement);
        else if (widgetTypeName.equals(TEXTAREA_ELEMENT))
            widget= createTextArea(pElement);
        else if (widgetTypeName.equals(COMBOBOX_ELEMENT))
            widget= createComboBox(pElement);
        else if (widgetTypeName.equals(SLIDER_ELEMENT))
            widget= createSlider(pElement);
        else if (widgetTypeName.equals(SPLITPANE_ELEMENT))
            widget= createSplitPane(pElement);
        else if (widgetTypeName.equals(SCROLLPANE_ELEMENT))
            widget= createScrollPane(pElement);
         else if (widgetTypeName.equals(TABBEDPANE_ELEMENT))
            widget= createTabbedPane(pElement);
        else if (widgetTypeName.equals(LIST_ELEMENT))
            widget= createList(pElement);
        else if (widgetTypeName.equals(TABLE_ELEMENT))
            widget= createTable(pElement);
        else if (widgetTypeName.equals(TREE_ELEMENT))
            widget= createTree(pElement);
        else if (widgetTypeName.equals(MENUBAR_ELEMENT))
            widget= createMenubar(pElement);
        else if (widgetTypeName.equals(MENU_ELEMENT))
            widget= createMenu(pElement);
        else if (widgetTypeName.equals(MENU_ITEM_ELEMENT))
            widget= createMenuItem(pElement);
        else if (widgetTypeName.equals(TOOLBAR_ELEMENT))
            widget= createToolbar(pElement);
        else if (widgetTypeName.equals(TOOLBAR_ITEM_ELEMENT))
            widget= createToolbarItem(pParent, pElement);
        else if (widgetTypeName.equals(POPUP_ELEMENT))
            widget= createPopup(pElement);
        else if (widgetTypeName.equals(POPUPMENU_ELEMENT))
            widget= createPopupMenu(pElement);
        else if (widgetTypeName.equals(POPUP_ITEM_ELEMENT))
            widget= createPopupItem(pElement);
        else if (widgetTypeName.equals(SEPARATOR_ELEMENT))
            widget= createSeparator(pParent);
        else
            throw new IllegalArgumentException("unknown widget "+widgetTypeName);

        // use id as the widget's name
        Attribute id= pElement.getAttribute(ID_ATTRIBUTE);
        if (id!=null)
        {
            String name= id.getValue();
            widget.getComponent().setName(name);

            // add it to our map, also check it contains no /, it's parent has a name and
            // the name is uniq
            if (name.indexOf(SEPARATOR) != -1)
                throw new IllegalArgumentException("name " + name + "invalid (no slash allowed)");

            String fullName= null;
            if (pParent!=null)
            {
                String prefix= getNameByContainer(pParent);
                if (prefix==null)
                    throw new IllegalArgumentException("parent of name " + name + " has no name");
                // special case for ScrollPane
                if (pParent instanceof JScrollPane)
                    prefix+= SEPARATOR+VIEWPORT_NAME;
                if (getContainerByName(prefix+SEPARATOR+name)!=null)
                    throw new IllegalArgumentException("name " + name + " contained twice in parent"+
                                                       pParent.getName());
                fullName= prefix+SEPARATOR+name;
            }
            else
            {
                if (getContainerByName(name)!=null)
                    throw new IllegalArgumentException("top level name " + name + " is not uniq");
                fullName= name;
            }
            mNameToVisual.put(fullName, widget.getComponent());
            mVisualToName.put(widget.getComponent(), fullName);
        }

        return widget;
    }

    private SwingElement createFrame(Element pElement) throws IllegalArgumentException
    {
        JFrame frame= new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Attribute closeActionAttribute= pElement.getAttribute(CLOSEACTION_ATTRIBUTE);
        if (closeActionAttribute!=null)
        {
            String                 closeAction= closeActionAttribute.getValue();
            final DispatcherAction action     = (DispatcherAction)mNameToNonVisual.get(closeAction);
            if (action==null)
            {
                throw new IllegalArgumentException("unknown closeAction attribute"+closeAction);
            }
            frame.addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    ActionEvent actionEvent= new ActionEvent(e.getSource(), e.getID(), "WINDOW_CLOSING");
                    action.actionPerformed(actionEvent);
                }
            });
        }

        Object constraint= createConstraint(pElement);
        return new SwingElement(frame, constraint);
    }

    private SwingElement createDialog(Container pParent, Element pElement)
    {
        // check if we belong to a frame
        JDialog dialog;
        if (pParent instanceof JFrame)
        {
            dialog= new JDialog((JFrame)pParent);
        }
        else
        {
            dialog= new JDialog();
        }

        dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        Attribute closeActionAttribute= pElement.getAttribute(CLOSEACTION_ATTRIBUTE);
        if (closeActionAttribute!=null)
        {
            String                 closeAction= closeActionAttribute.getValue();
            final DispatcherAction action     = (DispatcherAction)mNameToNonVisual.get(closeAction);
            if (action==null)
            {
                throw new IllegalArgumentException("unknown closeAction attribute"+closeAction);
            }
            dialog.addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    ActionEvent actionEvent= new ActionEvent(e.getSource(), e.getID(), "WINDOW_CLOSING");
                    action.actionPerformed(actionEvent);
                }
            });
        }

        Attribute escapeActionAttribute= pElement.getAttribute(ESCAPEACTION_ATTRIBUTE);
        if (escapeActionAttribute!=null)
        {
            String                 escapeAction= escapeActionAttribute.getValue();
            final DispatcherAction action      = (DispatcherAction)mNameToNonVisual.get(escapeAction);
            if (action==null)
            {
                throw new IllegalArgumentException("unknown escapeAction attribute"+escapeAction);
            }
            KeyStroke escStroke= KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
            InputMap  inputMap = dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionMap= dialog.getRootPane().getActionMap();
            inputMap .put(escStroke, CANCEL_ACTION_KEY);
            actionMap.put(CANCEL_ACTION_KEY, action);

            /*
             * for further details on input handling, see
             * http://java.sun.com/products/jfc/tsc/special_report/kestrel/keybindings.html
             */
        }
        Object  constraint= createConstraint(pElement);
        return new SwingElement(dialog, constraint);
    }

    private SwingElement createPanel(Element pElement)
    {
        JPanel panel= new JPanel();
        Object constraint= createConstraint(pElement);
        return new SwingElement(panel, constraint);
    }

    private SwingElement createAny(Element pElement) throws IllegalArgumentException
    {
        JComponent component;
        Attribute classAttribute= pElement.getAttribute(CLASS_ATTRIBUTE);
        if (classAttribute == null)
        {
            throw new IllegalArgumentException("a any element needs a "+CLASS_ATTRIBUTE+" attribute");
        }

        String panelClassName= classAttribute.getValue();
        try
        {
            component= (JComponent)Class.forName(panelClassName).newInstance();
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(e.toString());
        }

        Object constraint= createConstraint(pElement);

        return new SwingElement(component, constraint);
    }

    private SwingElement createButton(Element pElement)
    {
        // check if this button is build from an action
        JButton          button= null;
        DispatcherAction action= getAction(pElement);
        if (action!=null)
        {
            button= new JButton(action);
        }
        else
        {
            button= new JButton();
        }
        Object constraint= createConstraint(pElement);
        return new SwingElement(button, constraint);
    }

    private SwingElement createLabel(Element pElement)
    {
        JLabel label    = new JLabel();
        Object constraint= createConstraint(pElement);
        return new SwingElement(label, constraint);
    }

    private SwingElement createRadioButton(Component pParent, Element pElement) throws IllegalArgumentException
    {
        if (!(pParent instanceof JComponent))
        {
            throw new IllegalArgumentException("parent of a radiobutton must be a JComponent");
        }
        JComponent   parent     = (JComponent)pParent;

        // check if this button is build from an action
        JRadioButton     radioButton= null;
        DispatcherAction action     = getAction(pElement);
        if (action!=null)
        {
            radioButton= new JRadioButton(action);
        }
        else
        {
            radioButton= new JRadioButton();
        }

        Object constraint= createConstraint(pElement);
        ButtonGroup buttonGroup= (ButtonGroup)parent.getClientProperty(BUTTONGROUP_PROPERTY);
        if (buttonGroup==null)
        {
            buttonGroup= new ButtonGroup();
            parent.putClientProperty(BUTTONGROUP_PROPERTY, buttonGroup);
        }
        buttonGroup.add(radioButton);

        Attribute defaultAttribute= pElement.getAttribute(DEFAULT_ATTRIBUTE);
        if ((defaultAttribute != null) && (BUTTONGROUP_TRUE.equals(defaultAttribute.getValue())))
        {
            buttonGroup.setSelected(radioButton.getModel(), true);
        }

        return new SwingElement(radioButton, constraint);
    }

    private SwingElement createToggleButton(Element pElement)
    {
        // check if this button is build from an action
        JToggleButton    button= null;
        DispatcherAction action= getAction(pElement);
        if (action!=null)
        {
            button= new JToggleButton(action);
        }
        else
        {
            button= new JToggleButton();
        }
        Object constraint= createConstraint(pElement);
        return new SwingElement(button, constraint);
    }

    private SwingElement createCheckBox(Element pElement)
    {
        // check if this button is build from an action
        JCheckBox button= null;
        DispatcherAction action= getAction(pElement);
        if (action!=null && action.isToggle())
        {
            button= new JCheckBox(action);
            button.setModel(action.getToggleModel());
        }
        else
        {
            button= new JCheckBox();
        }
        Object constraint= createConstraint(pElement);
        return new SwingElement(button, constraint);
    }

    private SwingElement createTextField(Element pElement)
    {
        JTextField textField= new JTextField();
        Object constraint= createConstraint(pElement);
        return new SwingElement(textField, constraint);
    }

    private SwingElement createPasswordField(Element pElement)
    {
        JPasswordField textField= new JPasswordField();
        Object constraint= createConstraint(pElement);
        return new SwingElement(textField, constraint);
    }

    private SwingElement createTextArea(Element pElement)
    {
        JTextArea textArea= new JTextArea();
        Object constraint= createConstraint(pElement);
        return new SwingElement(textArea, constraint);
    }

    private SwingElement createComboBox(Element pElement)
    {
        JComboBox comboBox= new JComboBox();
        Object constraint= createConstraint(pElement);
        return new SwingElement(comboBox, constraint);
    }

    private SwingElement createSlider(Element pElement)
    {
        JSlider slider= new JSlider();
        Object constraint= createConstraint(pElement);
        return new SwingElement(slider, constraint);
    }

    private SwingElement createSplitPane(Element pElement) throws IllegalArgumentException
    {
        // orientation is 1 (HORIZONTAL_SPLIT) or 0 (VERTICAL_SPLIT)
        int orientation= 1;
        Attribute orientationAttribute= pElement.getAttribute(ORIENTATION_ATTRIBUTE);
        if (orientationAttribute != null)
        {
            try
            {
                orientation= orientationAttribute.getIntValue();
            }
            catch (DataConversionException e)
            {
                throw new IllegalArgumentException("wrong splitpane orientation"+e.toString());
            }
        }
        JSplitPane splitPane = new JSplitPane(orientation);

        Attribute oneTouchExpandableAttribute= pElement.getAttribute(ONETOUCHEXPANDABLE_ATTRIBUTE);
        if (oneTouchExpandableAttribute != null)
        {
            splitPane.setOneTouchExpandable(true);
        }

        Object constraint= createConstraint(pElement);
        return new SwingElement(splitPane, constraint);
    }

    private SwingElement createScrollPane(Element pElement)
    {
        JScrollPane scrollPane= new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setName(VIEWPORT_NAME);
        Object constraint= createConstraint(pElement);
        return new SwingElement(scrollPane, constraint);
    }

    private SwingElement createTabbedPane(Element pElement)
    {
        int       tabPlacement         = JTabbedPane.TOP;
        Attribute tabPlacementAttribute= pElement.getAttribute(TABPLACEMENT_ATTRIBUTE);
        if (tabPlacementAttribute!= null)
        {
            tabPlacement= Integer.parseInt(tabPlacementAttribute.getValue());
        }

        JTabbedPane tabbedPane= new JTabbedPane(tabPlacement);
        Object      constraint= createConstraint(pElement);
        return new SwingElement(tabbedPane, constraint);
    }

    private SwingElement createList(Element pElement)
    {
        JList list= new JList();
        Object constraint= createConstraint(pElement);
        return new SwingElement(list, constraint);
    }

    private SwingElement createTable(Element pElement)
    {
        JTable table= new JTable();
        Object constraint= createConstraint(pElement);
        return new SwingElement(table, constraint);
    }

    private SwingElement createTree(Element pElement)
    {
        JTree tree= new JTree();
        Object constraint= createConstraint(pElement);
        return new SwingElement(tree, constraint);
    }

    private SwingElement createMenubar(Element pElement) throws IllegalArgumentException
    {
        JMenuBar menuBar= new JMenuBar();
        Object constraint= createConstraint(pElement);
        return new SwingElement(menuBar, constraint);
    }

    private SwingElement createMenu(Element pElement) throws IllegalArgumentException
    {
        JMenu menu= new JMenu();
        Object constraint= createConstraint(pElement);
        return new SwingElement(menu, constraint);
    }

    private SwingElement createMenuItem(Element pElement) throws IllegalArgumentException
    {
        JMenuItem menuItem= null;

        // check if this menu item is build from an action
        DispatcherAction action= getAction(pElement);
        if (action!=null)
        {
            if (action.isToggle())
            {
                menuItem= new JCheckBoxMenuItem(action);
                menuItem.setModel(action.getToggleModel());
            }
            else
            {
                menuItem= new JMenuItem(action);
            }
        }
        else
        {
            menuItem= new JMenuItem();
        }

        Object constraint= createConstraint(pElement);
        return new SwingElement(menuItem, constraint);
    }

    private SwingElement createToolbar(Element pElement) throws IllegalArgumentException
    {
        JToolBar toolbar= new JToolBar();
        Object constraint= createConstraint(pElement);
        return new SwingElement(toolbar, constraint);
    }

    private SwingElement createToolbarItem(Container pParent, Element pElement)
    {
        // check if this toolbar item is build from an action
        DispatcherAction action= getAction(pElement);
        if (action!=null)
        {
            ((JToolBar)pParent).add(action);
            return new SwingElement(null, null);
        }
        else
        {
            // no action -> insert normal button
            return createButton(pElement);
        }
    }

    private SwingElement createPopup(Element pElement) throws IllegalArgumentException
    {
        // extract title
        String title= null;
        for (Iterator i= pElement.getChildren(TITLE_ELEMENT).iterator(); i.hasNext();)
        {
            Element titleElement= (Element) i.next();
            if (isSameLanguage(titleElement))
            {
                title= titleElement.getText();
            }
        }
        PopupManager popupManager= new PopupManager(title);
        JPopupMenu   popup       = popupManager.getPopup();

        Object constraint= createConstraint(pElement);
        return new SwingElement(popup, constraint);
    }

    private SwingElement createPopupMenu(Element pElement) throws IllegalArgumentException
    {
        return createMenu(pElement);
    }

    private SwingElement createPopupItem(Element pElement) throws IllegalArgumentException
    {
        return createMenuItem(pElement);
    }

    private SwingElement createSeparator(Container pParent) throws IllegalArgumentException
    {
        if (pParent instanceof JMenu)
        {
            ((JMenu)pParent).addSeparator();
        }
        else if (pParent instanceof JPopupMenu)
        {
            ((JPopupMenu)pParent).addSeparator();
        }
        else if (pParent instanceof JToolBar)
        {
            ((JToolBar)pParent).addSeparator();
        }
        else
        {
            throw new IllegalArgumentException("parent of a Separator must be a JMenu, JPopupMenu or JToolBar");
        }

        return new SwingElement(null, null);
    }

    private DispatcherAction getAction(Element pElement) throws IllegalArgumentException
    {
        Attribute actionrefAttribute= pElement.getAttribute(ACTIONREF_ATTRIBUTE);
        if (actionrefAttribute!=null)
        {
            String           actionref= actionrefAttribute.getValue();
            DispatcherAction action   = (DispatcherAction)mNameToNonVisual.get(actionref);
            if (action==null)
            {
                throw new IllegalArgumentException("unknown actionref attribute"+actionref);
            }
            return action;
        }

        return null;
    }

    private Object createConstraint(Element pElement) throws IllegalArgumentException
    {
        Object  constraintObject = null;
        Element constraintElement= pElement.getChild(CONSTRAINT_ELEMENT);
        if (constraintElement!=null)
        {
            Attribute typeAttribute= constraintElement.getAttribute(TYPE_ATTRIBUTE);
            if (typeAttribute!=null)
            {
                // generate constraint from type
                String constraintType= typeAttribute.getValue();
                if (constraintType.equals(GRIDBAG_LAYOUT))
                {
                    GridBagConstraints constraint= new GridBagConstraints();
                    Element child;
                    if ((child= constraintElement.getChild("gridx"))!=null)
                    {
                        constraint.gridx= Integer.parseInt(child.getTextTrim());
                    }
                    if ((child= constraintElement.getChild("gridy"))!=null)
                    {
                        constraint.gridy= Integer.parseInt(child.getTextTrim());
                    }
                    if ((child= constraintElement.getChild("fill"))!=null)
                    {
                        constraint.fill= Integer.parseInt(child.getTextTrim());
                    }
                    if ((child= constraintElement.getChild("weightx"))!=null)
                    {
                        constraint.weightx= Double.parseDouble(child.getTextTrim());
                    }
                    if ((child= constraintElement.getChild("weighty"))!=null)
                    {
                        constraint.weighty= Double.parseDouble(child.getTextTrim());
                    }
                    if ((child= constraintElement.getChild("gridheight"))!=null)
                    {
                        constraint.gridheight= Integer.parseInt(child.getTextTrim());
                    }
                    if ((child= constraintElement.getChild("gridwidth"))!=null)
                    {
                        constraint.gridwidth= Integer.parseInt(child.getTextTrim());
                    }
                    if ((child= constraintElement.getChild("insets"))!=null)
                    {
                        int     top   = 0;
                        int     left  = 0;
                        int     bottom= 0;
                        int     right = 0;
                        Element inset;
                        if ((inset= child.getChild("top"))!=null)
                            top= Integer.parseInt(inset.getTextTrim());
                        if ((inset= child.getChild("left"))!=null)
                            left= Integer.parseInt(inset.getTextTrim());
                        if ((inset= child.getChild("bottom"))!=null)
                            bottom= Integer.parseInt(inset.getTextTrim());
                        if ((inset= child.getChild("right"))!=null)
                            right= Integer.parseInt(inset.getTextTrim());
                        constraint.insets= new Insets(top, left, bottom, right);
                    }
                    if ((child= constraintElement.getChild("anchor"))!=null)
                    {
                        constraint.anchor= Integer.parseInt(child.getTextTrim());
                    }
                    if ((child= constraintElement.getChild("ipadx"))!=null)
                    {
                        constraint.ipadx= Integer.parseInt(child.getTextTrim());
                    }
                    if ((child= constraintElement.getChild("ipady"))!=null)
                    {
                        constraint.ipady= Integer.parseInt(child.getTextTrim());
                    }

                    constraintObject= constraint;
                }
                else
                {
                    throw new IllegalArgumentException("unknown constraint type"+constraintType);
                }
            }
            else
            {
                // no type => use the plain content, but only if it is not nothing
                String constraint= constraintElement.getTextTrim();
                if (!constraint.equals(""))
                {
                    constraintObject= constraint;
                }
            }
        }

        return constraintObject;
    }

    /**
     * creates a new object
     * @param pElement element describing the object
     * @throws IllegalArgumentException if we have a create problem (class not found, ...)
     */
    private Object createObject(Element pElement) throws IllegalArgumentException
    {
        Object object;
        // get class
        Attribute classAttribute= pElement.getAttribute(CLASS_ATTRIBUTE);
        if (classAttribute == null)
            throw new IllegalArgumentException("a non visual object element needs a "+
                                               CLASS_ATTRIBUTE+" attribute");

        // create object
        String objectClassName= classAttribute.getValue();
        try
        {
            Class objectClass= Class.forName(objectClassName);
            object= objectClass.newInstance();

            // check if there is a "void setSwingXMLBuilder(SwingXMLBuilder pBuilder)" method
            try
            {
                Class[] args  = new Class[] {SwingXMLBuilder.class};
                Method  method= objectClass.getMethod("setSwingXMLBuilder", args);
                method.invoke(object, new Object[]{this});

            }
            catch (NoSuchMethodException ignored)
            {
                // it's not a problem if that method doesn't exist.
            }
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(e.toString());
        }
        return object;
    }

    /**
     * creates a new DispatcherAction
     * @param pElement element describing the action
     */
    private DispatcherAction createAction(Element pElement)
    {
        DispatcherAction action= null;

        // check for menu/toolbar action
        String name       = null;
        String accelerator= null;
        String mnemonic   = null;
        String tooltip    = null;
        String iconRelName= null;

        for (Iterator i = pElement.getChildren(TEXT_ELEMENT).iterator(); i.hasNext();)
        {
            Element textElement = (Element) i.next();
            if (isSameLanguage(textElement))
                name= textElement.getTextTrim();
        }

        for (Iterator i = pElement.getChildren(ACCELERATOR_ELEMENT).iterator(); i.hasNext();)
        {
            Element acceleratorElement= (Element) i.next();
            if (isSameLanguage(acceleratorElement))
                accelerator= acceleratorElement.getTextTrim();
        }

        for (Iterator i = pElement.getChildren(MNEMONIC_ELEMENT).iterator(); i.hasNext();)
        {
            Element mnemonicElement= (Element) i.next();
            if (isSameLanguage(mnemonicElement))
                mnemonic= mnemonicElement.getTextTrim();
        }

        for (Iterator i = pElement.getChildren(TOOLTIP_ELEMENT).iterator(); i.hasNext();)
        {
            Element tooltipElement= (Element) i.next();
            if (isSameLanguage(tooltipElement))
                tooltip= tooltipElement.getTextTrim();
        }

        for (Iterator i = pElement.getChildren(ICON_ELEMENT).iterator(); i.hasNext();)
        {
            Element iconRelNameElement= (Element) i.next();
            if (isSameLanguage(iconRelNameElement))
                iconRelName= iconRelNameElement.getTextTrim();
        }

        boolean isToogle= pElement.getChild(IS_TOGGLE_ELEMENT)!=null;

        if (name!=null)
        {
            Icon icon= getIcon(iconRelName, ICON_MODIFIER_NORMAL);
            action= new DispatcherAction(name, accelerator, mnemonic,
                                         tooltip, icon, isToogle);
        }
        else
        {
            // non-visual action
            action= new DispatcherAction();
        }

        return action;
    }

    /**
     * Loads an Icon for the handed (relative) name and modifier.
     * If no icon is found, null is returned.
     *
     * @param pName relative name
     * @param pIconModifier modifier for the icon name
     * @return an ImageIcon or null
     */
    public ImageIcon getIcon(String pName, String pIconModifier)
    {
        StringBuffer buf = new StringBuffer(ICON_PREFIX);
        buf.append(pName);
        buf.append(pIconModifier);
        buf.append(ICON_SUFFIX);
        String name = buf.toString();

        if (mIconCache.containsKey(name))
        {
            return (ImageIcon) mIconCache.get(name);
        }

        ImageIcon imageIcon= null;
        try
        {
            URL url= getClass().getResource("/" +name);
            imageIcon= new ImageIcon(url);
            imageIcon.getImage();
        }
        catch (Exception ignored)
        {
            // we ignore load problems, and will not try to load the
            // same icon again
        }

        mIconCache.put(name, imageIcon);
        return imageIcon;
    }



    /**
     * SwingElement is a helper class to return both a component as well as its constraints.
     */
    static private class SwingElement
    {
        // Swing object, or a JFrame/JDialog
        private Container mComponent;
        // constraint for it
        private Object    mConstraint;

        public SwingElement(Container pComponent, Object pConstraint)
        {
            mComponent  = pComponent;
            mConstraint = pConstraint;
        }

        public Container getComponent()
        {
            return mComponent;
        }

        public Object getConstraint()
        {
            return mConstraint;
        }
    }

    /**
     * helper class to resolve our dtd via a local file
     */
    static private class EntityHelper  implements EntityResolver
    {
        /**
         * callback method from the XML parser to resolve public/system ID's
         * @param pPublicId not used
         * @param pSystemId checked against our SYSTEM_ID member
         * @return null or a InputSource for pSystemId
         */
        public InputSource resolveEntity (String pPublicId, String pSystemId)
        {
            if (pSystemId.equals(SYSTEM_ID))
            {
                InputStream dtd= getClass().getClassLoader().getSystemResourceAsStream(SYSTEM_ID_FILE);
                if (dtd!=null)
                {
                    return new InputSource(dtd);
                }
            }

            // if not our special SYSTEMID, use the default behaviour
            return null;
        }
    }
}
