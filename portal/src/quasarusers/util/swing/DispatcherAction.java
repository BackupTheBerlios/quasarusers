package quasarusers.util.swing;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class offers the <code>Action</code> benefits and also acts as a dispatcher.
 * It is most useful in the context of SwingXMLBuilder, and if there are e.g. both toolbar and
 * menu entries for one action.<br/>
 * DispatcherAction also adds support for "selected" buttons (through a shared
 * ButtonModel).
 */
public class DispatcherAction extends AbstractAction
{
    public static final String  TOGGLE_MODEL= "DispatcherAction_TOGGLE_MODEL";

    /** A list of event listeners for this component. */
    protected EventListenerList mListenerList;

    /** null or the "real" source of an ActionEvent, only set during the event delivery */
    protected Object            mRealSource;

    /**
     * Default constuctor, this will create a non menubar/toolbar action, useful for pure dispatching.
     */
    DispatcherAction()
    {
        mListenerList= new EventListenerList();
    }

    /**
     * XXX
     */
    DispatcherAction(String pEntryName, String pEntryAccelerator, String pEntryMnemonic,
                     String pEntryTooltip, Icon pIcon, boolean pIsToggle)
    {
        this();
        if (pEntryName==null)
        {
            throw new IllegalArgumentException("need a entry name");
        }

        Integer mnemonic= null;
        if ((pEntryMnemonic!=null) && (pEntryMnemonic.length()>0))
        {
            mnemonic= new Integer(pEntryMnemonic.charAt(0));
        }
        KeyStroke keyStroke= null;
        if ((pEntryAccelerator!=null) && (pEntryAccelerator.length()>0))
        {
            String accelerator= "control " + Character.toUpperCase(pEntryAccelerator.charAt(0));
            keyStroke= KeyStroke.getKeyStroke(accelerator);
        }
        putValue(NAME,              pEntryName);
        putValue(ACCELERATOR_KEY,   keyStroke);
        putValue(MNEMONIC_KEY,      mnemonic);
        putValue(SHORT_DESCRIPTION, pEntryTooltip);
        putValue(SMALL_ICON,        pIcon);

        if (pIsToggle)
        {
            putValue(TOGGLE_MODEL, new JToggleButton.ToggleButtonModel());
        }
    }

    /**
     * Retunrs the name of the entry for this action.
     * @return null or entry name
     */
    String getEntryName()
    {
        return (String)getValue(NAME);
    }

    /**
     * Returns the entry accelerator.
     * @return null or entry accelerator
     */
    String getEntryAccelerator()
    {
        return (String)getValue(ACCELERATOR_KEY);
    }

    /**
     * Returns the entry mnemonic.
     * @return null or entry mnemonic
     */
    String getEntryMnemonic()
    {
        return (String)getValue(MNEMONIC_KEY);
    }

    /**
     * Returns the tool tip for the entry.
     * @return null or entry tool tip
     */
    String getTooltip()
    {
        return (String)getValue(SHORT_DESCRIPTION);
    }

    /**
     * Returns the (relativ) entry icon name.
     * @return null or entry icon name
     */
    String getEntryIcon()
    {
        return (String)getValue(SMALL_ICON);
    }

    /**
     * Returns whether or not this action acts as a toggle entry.
     * @return true if this action is a toggle action
     */
    boolean isToggle()
    {
        Object toggleModel= getValue(TOGGLE_MODEL);
        if (toggleModel==null)
            return false;
        else
            return true;
    }

    /**
     * Returns null if isToggle() is false, otherwise a ButtonModel for the
     * toggle state.
     *
     * @return null or a toggle model for this action
     */
    public ButtonModel getToggleModel()
    {
        Object toggleModel= getValue(TOGGLE_MODEL);
        return (ButtonModel)toggleModel;
    }

    /**
     * Enables or disables the action.
     *
     * @param newValue  true to enable the action, false to
     *                  disable it
     * @see AbstractAction#setEnabled
     */
    public void setEnabled(boolean newValue)
    {
        super.setEnabled(newValue);

        if (isToggle())
        {
            getToggleModel().setEnabled(newValue);
        }
    }

    /**
     * Returns the toggle state of our toggle model, always false if
     * we are not toggable.
     *
     * @return true if the toggle model is selected, false otherwise
     */
    public boolean isSelected()
    {
        ButtonModel model= getToggleModel();

        if (model!=null)
            return model.isSelected();
        else
            return false;
    }

    /**
     * Sets the toggle state of our button model, does nothing if we
     * are not toggable.
     *
     * @param pSelected new state
     */
    public void setSelected(boolean pSelected)
    {
        ButtonModel model= getToggleModel();

        if (model!=null)
            model.setSelected(pSelected);
    }


    /**
     * Callback method of our visual representations, dispatch the action
     * to our listeners.
     * @param pEvent the event describing the action
     */
    public void actionPerformed(ActionEvent pEvent)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = mListenerList.getListenerList();

        // not nice, but otherwise SwingConnectionManager can't work
        mRealSource= pEvent.getSource();

        // only works with JDK1.4
        pEvent.setSource(this);

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i= listeners.length-2; i>=0; i-=2)
        {
            if (listeners[i]==ActionListener.class)
            {
                ((ActionListener)listeners[i+1]).actionPerformed(pEvent);
            }
        }

        pEvent.setSource(mRealSource);
        mRealSource= null;
    }

    /**
     * Adds an <code>ActionListener</code> to the dispatcher.
     *
     * @param pListener the <code>ActionListener</code> to be added
     */
    public void addActionListener(ActionListener pListener)
    {
        mListenerList.add(ActionListener.class, pListener);
    }

    /**
     * Removes an <code>ActionListener</code> from the button.
     * If the listener is the currently set <code>Action</code>
     * for the button, then the <code>Action</code>
     * is set to <code>null</code>.
     *
     * @param pListener the <code>ActionListener</code> to be removed
     */
    public void removeActionListener(ActionListener pListener)
    {
        mListenerList.remove(ActionListener.class, pListener);
    }

    /**
     * Due to the way SwingConnectionManager works, we must change the source
     * of the action event during callback.
     * This method provides the "real" source of the event.
     * @return null or the real source of the ActionEvent currently delivered
     */
    public Object getRealSource()
    {
        return mRealSource;
    }
}
