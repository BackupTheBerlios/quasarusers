/**************************************************************************
 *  P A C K A G E                                                         *
 **************************************************************************/

package quasarusers.util.swing;

/**************************************************************************/

/**************************************************************************
 *  I M P O R T S                                                         *
 **************************************************************************/

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.AbstractButton;
import javax.swing.ListSelectionModel;
import javax.swing.text.JTextComponent;

/**************************************************************************/

/**************************************************************************
 *  C L A S S E S                                                         *
 **************************************************************************/

/**************************************************************************/
/**
 * The SwingConnectionManager singelton makes the Swing callback handling
 * much more convenient. <br/>
 * Instead of bundeling all callbacks from the
 * various widgets in the actionPerformed()/stateChanged()/valueChanged()/
 * focusGained()/focusLost()/propertyChange() methods, and having a huge
 * if()else()if() cascade inside that method, with SwingConnectionManager
 * any method of a class that matches the
 * <ul>
 * <li>void <I>pMethodname</I>(java.awt.event.ActionEvent) or
 * <li>void <I>pMethodname</I>(javax.swing.event.ChangeEvent) or
 * <li>void <I>pMethodname</I>(javax.swing.event.TreeSelectionEvent) or
 * <li>void <I>pMethodname</I>(javax.swing.event.ListSelectionEvent) or
 * <li>void <I>pMethodname</I>(javax.swing.event.CaretEvent) or
 * <li>void <I>pMethodname</I>(java.awt.event.FocusEvent)
 * <li>void <I>pMethodname</I>(java.beans.PropertyChangeEvent)
 * </ul>
 * signature can be called.
 *
 * Currently SwingConnectionManager supports all Swing widgets that
 * provide an ActionEvent callback: JComboBox, all Buttons (derived from
 * AbstractButton), and all one line text fields (derived from JTextField).
 * It also provides support for ChangeEvent callbacks coming from JSlider's
 * or any Buttons (derived from AbstractButton), TreeSelectionEvent
 * callbacks coming from JTree's, ListSelectionEvent callbacks
 * coming from JTable's or JList's and CaretEvent callbacks coming from
 * one line text fields (derived from JTextField).<br/>
 * As a special case, the SwingConnectionManager maps both the focusGained()
 * as well as the focusLost() callbacks to a single method, because the
 * delivered FocusEvent contains all needed information to distinguish
 * between lost and gained focus.<br/>
 * To support JavaBeans, the SwingConnectionManager also supports
 * propertyChange() callbacks.<br/>
 *
 * SwingConnectionManager can be used completly stand alone, but it
 * provides only "one half" of the dispatch work, that is the call back
 * from a UI element to a business logic object.<br/>
 * For the other half, that one that maps from business logic objects
 * to widgets, have a look at the SwingMapping class.<br/>
 *
 * FIXME: currently only one object can be registered for any given
 *        source, registering a second will silently remove the
 *        first.
 *
 * @author    Jürgen Zeller, jzeller@sdm.de
 */
public class SwingConnectionManager implements
        ActionListener, ChangeListener,
        ListSelectionListener, TreeSelectionListener,
        CaretListener, FocusListener, PropertyChangeListener
{
    /**********************************************************************/
    /**
     * Source is a private class encapsulating one source object and
     * the event class that will be used by that source, so we have a key
     * in a hash map that distinguishes between the various events a souce
     * can deliver.
     */
    static private class Source
    {
        private Object  mSource;
        private Class   mEventClass;

        Source(Object pSource, Class pEventClass)
        {
            mSource    = pSource;
            mEventClass= pEventClass;
        }

        public Object getSource()
        {
            return mSource;
        }

        public Class getEventClass()
        {
            return mEventClass;
        }

        public boolean equals(Object pObject)
        {
            if (pObject==this)
                return true;
            if (!(pObject instanceof Source))
                return false;

            Source source= (Source) pObject;
            return (source.mSource == mSource)            &&
                   (source.mEventClass.equals(mEventClass)  );
        }

        public int hashCode()
        {
            return (mSource.hashCode() | mEventClass.hashCode());
        }

        public String toString()
        {
            return mSource.toString() + mEventClass.toString();
        }
    }


    /**********************************************************************/
    /**
     * Target is a private class encapsulating one target object and
     * its method, so we can store them together in a hash map.
     */
    static private class Target
    {
        private Object mTarget;
        private Method mMethod;

        Target(Object pTarget, Method pMethod)
        {
            mTarget= pTarget;
            mMethod= pMethod;
        }

        public Object getTarget()
        {
            return mTarget;
        }

        public Method getMethod()
        {
            return mMethod;
        }

        public boolean equals(Object pObject)
        {
            if (pObject==this)
                return true;
            if (!(pObject instanceof Target))
                return false;

            Target target= (Target) pObject;
            return (target.mTarget == mTarget)                        &&
                   (target.mMethod.getName().equals(mMethod.getName())   );
        }

        public int hashCode()
        {
            return (mTarget.hashCode() | mMethod.hashCode());
        }

        public String toString()
        {
            return mTarget.toString() + mMethod.toString();
        }
    }


    /**
     * mSourceToTarget provides a mapping from the Action/Change/... Event
     * source object including the Event class to the registered object
     * including the method we should callback.
     * key  : Source object containing Event source and Event class
     * value: Target object containing object and method
     */
    private Map mSourceToTarget;

    /**
     * SwingConnectionManager is modeled as a singelton, and created at class
     * load time to avoid any race conditions.
     */
    private static SwingConnectionManager sSingelton= new SwingConnectionManager();


    /**
     * SwingConnectionManager is modeled as a singelton, so we have need
     * a private constructor.
     */
    private SwingConnectionManager()
    {
        mSourceToTarget= new HashMap();
    }

    /**
     * This methods does the common part of the overloaded createActionConnection()
     * methods, that is error checking and creationg of a Target object containing
     * both the target object and a method object matching pMethodName.
     * It then stores the Target object in our source to target map.
     *
     * @param  pActionSource    the button/combo box/text field/... instance triggering the callback
     * @param  pTarget          the target for the callback
     * @param  pMethodName      the method of pTarget we should call back
     * @throws IllegalArgumentException   if pActionSource or pTarget or pMethodName is null
     *                                    or pMethodName is not a method of pTarget with
     *                                    the right signature
     */
    static private void checkAndStoreActionTarget(Object pActionSource,
                                                  Object pTarget,
                                                  String pMethodName)
    {
        if (pActionSource==null)
            throw new IllegalArgumentException("ActionSource must not be null");
        if (pTarget==null)
            throw new IllegalArgumentException("Target must not be null");
        if (pMethodName==null)
            throw new IllegalArgumentException("MethodName must not be null");
        if (!Modifier.isPublic(pTarget.getClass().getModifiers()))
            throw new IllegalArgumentException("Target must be an object from a public class");

        /*
         * create Method and Target Object, the signature is always
         * void <pMethodname>(java.awt.event.ActionEvent)
         */
        Class[] params= new Class[]{ActionEvent.class};
        Method  method= null;
        try
        {
            method= pTarget.getClass().getMethod(pMethodName, params);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
        Target target= new Target(pTarget, method);

        // store the target Object in the singelton map
        synchronized (sSingelton.mSourceToTarget)
        {
            Source source= new Source(pActionSource, ActionEvent.class);
            sSingelton.mSourceToTarget.put(source, target);
        }
    }


    /**
     * This methods provides callback dispatching for a Swing JComboBox.
     *
     * @param  pActionSource    the combo box instance triggering the callback
     * @param  pTarget          the target for the callback
     * @param  pMethodName      the method of pTarget we should call back
     * @throws IllegalArgumentException   if pActionSource or pTarget or pMethodName is null
     *                                    or pMethodName is not a method of pTarget with
     *                                    the right signature
     */
    static public void createActionConnection(JComboBox pActionSource,
                                              Object pTarget,
                                              String pMethodName)
    {
        // do common stuff
        checkAndStoreActionTarget(pActionSource, pTarget, pMethodName);

        // register the singelton as an action listener
        pActionSource.addActionListener(sSingelton);
    }

    /**
     * This methods provides callback dispatching for all Swing one-line text
     * fields (derived from javax.swing.JTextField).
     *
     * @param  pActionSource    the text field instance triggering the callback
     * @param  pTarget          the target for the callback
     * @param  pMethodName      the method of pTarget we should call back
     * @throws IllegalArgumentException   if pActionSource or pTarget or pMethodName is null
     *                                    or pMethodName is not a method of pTarget with
     *                                    the right signature
     */
    static public void createActionConnection(JTextField pActionSource,
                                              Object     pTarget,
                                              String     pMethodName)
    {
        // do common stuff
        checkAndStoreActionTarget(pActionSource, pTarget, pMethodName);

        // register the singelton as an action listener
        pActionSource.addActionListener(sSingelton);
    }

    /**
     * This methods provides callback dispatching for all Swing buttons (derived
     * from javax.swing.AbstractButton).
     *
     * @param  pActionSource    the button instance triggering the callback
     * @param  pTarget          the target for the callback
     * @param  pMethodName      the method of pTarget we should call back
     * @throws IllegalArgumentException   if pActionSource or pTarget or pMethodName is null
     *                                    or pMethodName is not a method of pTarget with
     *                                    the right signature
     */
    static public void createActionConnection(AbstractButton pActionSource,
                                              Object         pTarget,
                                              String         pMethodName)
    {
        // do common stuff
        checkAndStoreActionTarget(pActionSource, pTarget, pMethodName);

        // register the singelton as an action listener
        pActionSource.addActionListener(sSingelton);
    }


    /**
     * This methods provides callback dispatching for the DispatcherAction.
     * If the DispatcherAction is not needed, this method can savely be deleted.
     *
     * @param  pActionSource    the DispatcherAction instance triggering the callback
     * @param  pTarget          the target for the callback
     * @param  pMethodName      the method of pTarget we should call back
     * @throws IllegalArgumentException   if pActionSource or pTarget or pMethodName is null
     *                                    or pMethodName is not a method of pTarget with
     *                                    the right signature
     */
    static public void createActionConnection(DispatcherAction pActionSource,
                                              Object           pTarget,
                                              String           pMethodName)
    {
        // do common stuff
        checkAndStoreActionTarget(pActionSource, pTarget, pMethodName);

        // register the singelton as an action listener
        pActionSource.addActionListener(sSingelton);
    }


    /**
     * This methods does the common part of the overloaded createChangeConnection()
     * methods, that is error checking and creationg of a Target object containing
     * both the target object and a method object matching pMethodName.
     *
     * @param  pChangeSource    the button/slider instance triggering the callback
     * @param  pTarget          the target for the callback
     * @param  pMethodName      the method of pTarget we should call back
     * @throws IllegalArgumentException   if pChangeSource or pTarget or pMethodName is null
     *                                    or pMethodName is not a method of pTarget with
     *                                    the right signature
     */
    static private void checkAndStoreChangeTarget(Object pChangeSource,
                                                  Object pTarget,
                                                  String pMethodName)
    {
        if (pChangeSource==null)
            throw new IllegalArgumentException("ChangeSource must not be null");
        if (pTarget==null)
            throw new IllegalArgumentException("Target must not be null");
        if (pMethodName==null)
            throw new IllegalArgumentException("MethodName must not be null");

        /*
         * create Method and Target Object, the signature is always
         * void <pMethodname>(javax.swing.event.ChangeEvent)
         */
        Class[] params= new Class[]{ChangeEvent.class};
        Method  method= null;
        try
        {
            method= pTarget.getClass().getMethod(pMethodName, params);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
        Target target= new Target(pTarget, method);

        // store the target Object in the singelton map
        synchronized (sSingelton.mSourceToTarget)
        {
            Source source= new Source(pChangeSource, ChangeEvent.class);
            sSingelton.mSourceToTarget.put(source, target);
        }
    }

    /**
     * This methods provides callback dispatching for a Swing JSlider.
     *
     * @param  pChangeSource    the slider instance triggering the callback
     * @param  pTarget          the target for the callback
     * @param  pMethodName      the method of pTarget we should call back
     * @throws IllegalArgumentException   if pChangeSource or pTarget or pMethodName is null
     *                                    or pMethodName is not a method of pTarget with
     *                                    the right signature
     */
    static public void createChangeConnection(JSlider pChangeSource,
                                              Object  pTarget,
                                              String  pMethodName)
    {
        // do common stuff
        checkAndStoreChangeTarget(pChangeSource, pTarget, pMethodName);

        // register the singelton as an action listener
        pChangeSource.addChangeListener(sSingelton);
    }

    /**
     * This methods provides callback dispatching for all Swing buttons (derived
     * from javax.swing.AbstractButton).
     *
     * @param  pChangeSource    the button instance triggering the callback
     * @param  pTarget          the target for the callback
     * @param  pMethodName      the method of pTarget we should call back
     * @throws IllegalArgumentException   if pChangeSource or pTarget or pMethodName is null
     *                                    or pMethodName is not a method of pTarget with
     *                                    the right signature
     */
    static public void createChangeConnection(AbstractButton pChangeSource,
                                              Object         pTarget,
                                              String         pMethodName)
    {
        // do common stuff
        checkAndStoreChangeTarget(pChangeSource, pTarget, pMethodName);

        // register the singelton as an action listener
        pChangeSource.addChangeListener(sSingelton);
    }

    /**
     * This methods provides callback dispatching for JTree's.
     *
     * @param  pTreeSelectionSource the JTree instance triggering the callback
     * @param  pTarget              the target for the callback
     * @param  pMethodName          the method of pTarget we should call back
     * @throws IllegalArgumentException   if pTreeSelectionSource or pTarget or pMethodName is null
     *                                    or pMethodName is not a method of pTarget with
     *                                    the right signature
     */
    static public void createTreeSelectionConnection(JTree  pTreeSelectionSource,
                                                     Object pTarget,
                                                     String pMethodName)
    {
        if (pTreeSelectionSource==null)
            throw new IllegalArgumentException("TreeSelectionSource must not be null");
        if (pTarget==null)
            throw new IllegalArgumentException("Target must not be null");
        if (pMethodName==null)
            throw new IllegalArgumentException("MethodName must not be null");

        /*
         * create Method and Target Object, the signature is always
         * void <pMethodname>(javax.swing.event.TreeSelectionEvent)
         */
        Class[] params= new Class[]{TreeSelectionEvent.class};
        Method  method= null;
        try
        {
            method= pTarget.getClass().getMethod(pMethodName, params);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
        Target target= new Target(pTarget, method);


        // store the target Object in the singelton map
        synchronized (sSingelton.mSourceToTarget)
        {
            Source source= new Source(pTreeSelectionSource, TreeSelectionEvent.class);
            sSingelton.mSourceToTarget.put(source, target);
        }

        // register the singelton as an action listener
        pTreeSelectionSource.addTreeSelectionListener(sSingelton);
    }


    /**
     * This methods does the common part of the overloaded createChangeConnection()
     * methods, that is error checking and creationg of a Target object containing
     * both the target object and a method object matching pMethodName.
     *
     * @param  pListSelectionSource the JList/JTable instance triggering the callback
     * @param  pTarget              the target for the callback
     * @param  pMethodName          the method of pTarget we should call back
     * @throws IllegalArgumentException   if pListSelectionSource or pTarget or
     *                                    pMethodName is null or pMethodName is not
     *                                    a method of pTarget with the right signature
     */
    static private void checkAndStoreListSelectionTarget(Object pListSelectionSource,
                                                         Object pTarget,
                                                         String pMethodName)
    {
        if (pListSelectionSource==null)
            throw new IllegalArgumentException("ListSelectionSource must not be null");
        if (pTarget==null)
            throw new IllegalArgumentException("Target must not be null");
        if (pMethodName==null)
            throw new IllegalArgumentException("MethodName must not be null");

        /*
         * create Method and Target Object, the signature is always
         * void <pMethodname>(javax.swing.event.ListSelectionEvent)
         */
        Class[] params= new Class[]{ListSelectionEvent.class};
        Method  method= null;
        try
        {
            method= pTarget.getClass().getMethod(pMethodName, params);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
        Target target= new Target(pTarget, method);

        // store the target Object in the singelton map
        synchronized (sSingelton.mSourceToTarget)
        {
            Source source= new Source(pListSelectionSource, ListSelectionEvent.class);
            sSingelton.mSourceToTarget.put(source, target);
        }
    }

    /**
     * This methods provides callback dispatching for JList's.
     *
     * @param  pListSelectionSource the JList instance triggering the callback
     * @param  pTarget              the target for the callback
     * @param  pMethodName          the method of pTarget we should call back
     * @throws IllegalArgumentException   if pListSelectionSource or pTarget or pMethodName is null
     *                                    or pMethodName is not a method of pTarget with
     *                                    the right signature
     */
    static public void createListSelectionConnection(JList  pListSelectionSource,
                                                     Object pTarget,
                                                     String pMethodName)
    {
        // do common stuff
        checkAndStoreListSelectionTarget(pListSelectionSource,
                                         pTarget,
                                         pMethodName);

        // register the singelton as an action listener
        pListSelectionSource.addListSelectionListener(sSingelton);
    }

    /**
     * This methods provides callback dispatching for JTable's.
     *
     * @param  pListSelectionSource the JTable instance triggering the callback
     * @param  pTarget              the target for the callback
     * @param  pMethodName          the method of pTarget we should call back
     * @throws IllegalArgumentException   if pListSelectionSource or pTarget or pMethodName is null
     *                                    or pMethodName is not a method of pTarget with
     *                                    the right signature
     */
    static public void createListSelectionConnection(JTable pListSelectionSource,
                                                     Object pTarget,
                                                     String pMethodName)
    {
        // do common stuff
        checkAndStoreListSelectionTarget(pListSelectionSource.getSelectionModel(),
                                         pTarget,
                                         pMethodName);

        // register the singelton as an action listener
        pListSelectionSource.getSelectionModel().addListSelectionListener(sSingelton);
    }


    /**
     * This methods provides callback dispatching for all Swing one-line text
     * fields (derived from JTextComponent).
     *
     * @param  pCaretSource     the text field instance triggering the callback
     * @param  pTarget          the target for the callback
     * @param  pMethodName      the method of pTarget we should call back
     * @throws IllegalArgumentException   if pCaretSource or pTarget or pMethodName is null
     *                                    or pMethodName is not a method of pTarget with
     *                                    the right signature
     */
    static public void createCaretConnection(JTextComponent pCaretSource,
                                             Object         pTarget,
                                             String         pMethodName)
    {
        if (pCaretSource==null)
            throw new IllegalArgumentException("CaretSource must not be null");
        if (pTarget==null)
            throw new IllegalArgumentException("Target must not be null");
        if (pMethodName==null)
            throw new IllegalArgumentException("MethodName must not be null");

        /*
         * create Method and Target Object, the signature is always
         * void <pMethodname>(javax.swing.event.CaretEvent)
         */
        Class[] params= new Class[]{CaretEvent.class};
        Method  method= null;
        try
        {
            method= pTarget.getClass().getMethod(pMethodName, params);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
        Target target= new Target(pTarget, method);

        // store the target Object in the singelton map
        synchronized (sSingelton.mSourceToTarget)
        {
            Source source= new Source(pCaretSource, CaretEvent.class);
            sSingelton.mSourceToTarget.put(source, target);
        }

        // register the singelton as an caret listener
        pCaretSource.addCaretListener(sSingelton);
    }

    /**
     * This methods provides focus callback dispatching for all JComponent's.
     *
     * @param  pFocusSource     the Swing component triggering the callback
     * @param  pTarget          the target for the callback
     * @param  pMethodName      the method of pTarget we should call back
     * @throws IllegalArgumentException   if pFocusSource or pTarget or pMethodName is null
     *                                    or pMethodName is not a method of pTarget with
     *                                    the right signature
     */
    static public void createFocusConnection(JComponent pFocusSource,
                                             Object     pTarget,
                                             String     pMethodName)
    {
        if (pFocusSource==null)
            throw new IllegalArgumentException("FocusSource must not be null");
        if (pTarget==null)
            throw new IllegalArgumentException("Target must not be null");
        if (pMethodName==null)
            throw new IllegalArgumentException("MethodName must not be null");

        /*
         * create Method and Target Object, the signature is always
         * void <pMethodname>(java.awt.event.FocusEvent)
         */
        Class[] params= new Class[]{FocusEvent.class};
        Method  method= null;
        try
        {
            method= pTarget.getClass().getMethod(pMethodName, params);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
        Target target= new Target(pTarget, method);

        // store the target Object in the singelton map
        synchronized (sSingelton.mSourceToTarget)
        {
            Source source= new Source(pFocusSource, FocusEvent.class);
            sSingelton.mSourceToTarget.put(source, target);
        }

        // register the singelton as an focus listener
        pFocusSource.addFocusListener(sSingelton);
    }


    /**
     * This methods provides property change callback dispatching
     * for all JComponent's.
     *
     * @param  pPropertyChangeSource    the Swing component triggering the callback
     * @param  pTarget                  the target for the callback
     * @param  pMethodName              the method of pTarget we should call back
     * @throws IllegalArgumentException if pPropertyChangeSource or pTarget or pMethodName
     *                                  is null or pMethodName is not a method of pTarget
     *                                  with the right signature
     */
    static public void createPropertyChangeConnection(JComponent pPropertyChangeSource,
                                                      Object     pTarget,
                                                      String     pMethodName)
    {
        if (pPropertyChangeSource==null)
            throw new IllegalArgumentException("PropertyChangeSource must not be null");
        if (pTarget==null)
            throw new IllegalArgumentException("Target must not be null");
        if (pMethodName==null)
            throw new IllegalArgumentException("MethodName must not be null");

        /*
         * create Method and Target Object, the signature is always
         * void <pMethodname>(java.beans.PropertyChangeEvent)
         */
        Class[] params= new Class[]{PropertyChangeEvent.class};
        Method  method= null;
        try
        {
            method= pTarget.getClass().getMethod(pMethodName, params);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
        Target target= new Target(pTarget, method);

        // store the target Object in the singelton map
        synchronized (sSingelton.mSourceToTarget)
        {
            Source source= new Source(pPropertyChangeSource, PropertyChangeEvent.class);
            sSingelton.mSourceToTarget.put(source, target);
        }

        // register the singelton as an property change listener
        pPropertyChangeSource.addPropertyChangeListener(sSingelton);
    }


    /**
     * This methods removes all mappings pointing to the target object.
     *
     * @param pTarget   the target all mappings should be removed from
     */
    static public void removeConnection(Object pTarget)
    {
        synchronized (sSingelton.mSourceToTarget)
        {
            for (Iterator i= sSingelton.mSourceToTarget.entrySet().iterator();
                 i.hasNext();)
            {
                Map.Entry entry = (Map.Entry) i.next();
                Target    target= (Target)entry.getValue();
                if (target.getTarget() == pTarget)
                {
                    Object eventSource= ((Source)entry.getKey()).getSource();
                    Class  eventClass = ((Source)entry.getKey()).getEventClass();

                    if (eventSource instanceof AbstractButton)
                    {
                        ((AbstractButton)eventSource).removeActionListener(sSingelton);
                        ((AbstractButton)eventSource).removeChangeListener(sSingelton);
                        ((AbstractButton)eventSource).removeFocusListener (sSingelton);
                        i.remove();
                    }
                    // if DispatcherAction is not used, the following else if case can savely be removed
                    else if (eventSource instanceof DispatcherAction)
                    {
                        ((DispatcherAction)eventSource).removeActionListener(sSingelton);
                        i.remove();
                    }
                    else if (eventSource instanceof JComboBox)
                    {
                        ((JComboBox)eventSource).removeActionListener(sSingelton);
                        ((JComboBox)eventSource).removeFocusListener (sSingelton);
                        i.remove();
                    }
                    else if (eventSource instanceof JTextField)
                    {
                        ((JTextField)eventSource).removeActionListener(sSingelton);
                        ((JTextField)eventSource).removeCaretListener (sSingelton);
                        ((JTextField)eventSource).removeFocusListener (sSingelton);
                        i.remove();
                    }
                    else if (eventSource instanceof JSlider)
                    {
                        ((JSlider)eventSource).removeChangeListener(sSingelton);
                        ((JSlider)eventSource).removeFocusListener (sSingelton);
                        i.remove();
                    }
                    else if (eventSource instanceof JTree)
                    {
                        ((JTree)eventSource).removeTreeSelectionListener(sSingelton);
                        ((JTree)eventSource).removeFocusListener        (sSingelton);
                        i.remove();
                    }
                    else if (eventSource instanceof JList)
                    {
                        ((JList)eventSource).removeListSelectionListener(sSingelton);
                        ((JList)eventSource).removeFocusListener        (sSingelton);
                        i.remove();
                    }
                    else if (eventSource instanceof ListSelectionModel)
                    {
                        ((ListSelectionModel)eventSource).removeListSelectionListener(sSingelton);
                        i.remove();
                    }
                    else if (eventClass.equals(FocusEvent.class))
                    {
                        ((JComponent)eventSource).removeFocusListener(sSingelton);
                        i.remove();
                    }
                    else if (eventClass.equals(PropertyChangeEvent.class))
                    {
                        ((JComponent)eventSource).removePropertyChangeListener(sSingelton);
                        i.remove();
                    }
                    else
                    {
                        // very strange, should never happen
                        throw new IllegalStateException(
                            "SwingConnectionManager.removeConnection() "+
                            "internal error");
                    }
                }
            }
        }
    }


    /**
     * This method is not part of the public API, it's internally used as
     * the Swing callback method and forwards pAction to the registered
     * objekt.
     *
     * @param    pAction    event describing the action
     */
    public void actionPerformed(ActionEvent pAction)
    {
        // get source of this event and check if we know it
        Object actionSource= pAction.getSource();
        Target target      = null;
        synchronized (mSourceToTarget)
        {
            Source source= new Source(actionSource, ActionEvent.class);
            target= (Target)mSourceToTarget.get(source);
        }
        if (target!=null)
        {
            // we know the target, let's dispatch the call
            try
            {
                Object[] args= new Object[]{pAction};

                // doit: call the method with the incoming ActionEvent
                target.getMethod().invoke(target.getTarget(), args);
            }
            catch (IllegalAccessException e)
            {
                // map e to a IllegalArgumentException
                throw new IllegalArgumentException(e.getMessage());
            }
            catch (InvocationTargetException e)
            {
                // map the cause of e to a IllegalArgumentException, if it is
                // not a RuntimeException or an Error
                Throwable cause= e.getTargetException();

                if (cause instanceof RuntimeException)
                {
                    throw (RuntimeException) cause;
                }
                if (cause instanceof Error)
                {
                    throw (Error) cause;
                }

                // FIXME: starting with JDK 1.4, we should use the
                //        IllegalArgumentException(cause) constructor
                throw new IllegalArgumentException(cause!=null ? cause.getMessage() : e.getMessage());
            }
        }
        else
        {
            // very strange, print at least a warning
            System.err.println(
                "WARNING: SwingConnectionManager.actionPerformed()" +
                " called from unknown source");
        }
    }

    /**
     * This method is not part of the public API, it's internally used as
     * the Swing callback method and forwards pChange to the registered
     * objekt.
     *
     * @param    pChange    event describing the change
     */
    public void stateChanged(ChangeEvent pChange)
    {
        // get source of this event and check if we know it
        Object changeSource= pChange.getSource();
        Target target      = null;
        synchronized (mSourceToTarget)
        {
            Source source= new Source(changeSource, ChangeEvent.class);
            target= (Target)mSourceToTarget.get(source);
        }
        if (target!=null)
        {
            // we know the target, let's dispatch the call
            try
            {
                Object[] args= new Object[]{pChange};

                // doit: call the method with the incoming ActionEvent
                target.getMethod().invoke(target.getTarget(), args);
            }
            catch (IllegalAccessException e)
            {
                // map e to a IllegalArgumentException
                throw new IllegalArgumentException(e.getMessage());
            }
            catch (InvocationTargetException e)
            {
                // map the cause of e to a IllegalArgumentException, if it is
                // not a RuntimeException or an Error
                Throwable cause= e.getTargetException();

                if (cause instanceof RuntimeException)
                {
                    throw (RuntimeException) cause;
                }
                if (cause instanceof Error)
                {
                    throw (Error) cause;
                }

                // FIXME: starting with JDK 1.4, we should use the
                //        IllegalArgumentException(cause) constructor
                throw new IllegalArgumentException(cause!=null ? cause.getMessage() : e.getMessage());
            }
        }
        else
        {
            // very strange, print at least a warning
            System.err.println(
                "WARNING: SwingConnectionManager.stateChanged()" +
                " called from unknown source");
        }
    }

    /**
     * This method is not part of the public API, it's internally used as
     * the Swing callback method and forwards pTreeSelection to the registered
     * objekt.
     *
     * @param    pTreeSelection     event describing the new tree selection
     */
    public void valueChanged(TreeSelectionEvent pTreeSelection)
    {
        // get source of this event and check if we know it
        Object treeSelectionSource= pTreeSelection.getSource();
        Target target             = null;
        synchronized (mSourceToTarget)
        {
            Source source= new Source(treeSelectionSource, TreeSelectionEvent.class);
            target= (Target)mSourceToTarget.get(source);
        }
        if (target!=null)
        {
            // we know the target, let's dispatch the call
            try
            {
                Object[] args= new Object[]{pTreeSelection};

                // doit: call the method with the incoming ActionEvent
                target.getMethod().invoke(target.getTarget(), args);
            }
            catch (IllegalAccessException e)
            {
                // map e to a IllegalArgumentException
                throw new IllegalArgumentException(e.getMessage());
            }
            catch (InvocationTargetException e)
            {
                // map the cause of e to a IllegalArgumentException, if it is
                // not a RuntimeException or an Error
                Throwable cause= e.getTargetException();

                if (cause instanceof RuntimeException)
                {
                    throw (RuntimeException) cause;
                }
                if (cause instanceof Error)
                {
                    throw (Error) cause;
                }

                // FIXME: starting with JDK 1.4, we should use the
                //        IllegalArgumentException(cause) constructor
                throw new IllegalArgumentException(cause!=null ? cause.getMessage() : e.getMessage());
            }
        }
        else
        {
            // very strange, print at least a warning
            System.err.println(
                "WARNING: SwingConnectionManager.valueChanged(TreeSelectionEvent)" +
                " called from unknown source");
        }
    }

    /**
     * This method is not part of the public API, it's internally used as
     * the Swing callback method and forwards pListSelection to the registered
     * objekt.
     *
     * @param    pListSelection     event describing the new list selection
     */
    public void valueChanged(ListSelectionEvent pListSelection)
    {
        // get source of this event and check if we know it
        Object listSelectionSource= pListSelection.getSource();
        Target target             = null;
        synchronized (mSourceToTarget)
        {
            Source source= new Source(listSelectionSource, ListSelectionEvent.class);
            target= (Target)mSourceToTarget.get(source);
        }
        if (target!=null)
        {
            // we know the target, let's dispatch the call
            try
            {
                Object[] args= new Object[]{pListSelection};

                // doit: call the method with the incoming ActionEvent
                target.getMethod().invoke(target.getTarget(), args);
            }
            catch (IllegalAccessException e)
            {
                // map e to a IllegalArgumentException
                throw new IllegalArgumentException(e.getMessage());
            }
            catch (InvocationTargetException e)
            {
                // map the cause of e to a IllegalArgumentException, if it is
                // not a RuntimeException or an Error
                Throwable cause= e.getTargetException();

                if (cause instanceof RuntimeException)
                {
                    throw (RuntimeException) cause;
                }
                if (cause instanceof Error)
                {
                    throw (Error) cause;
                }

                // FIXME: starting with JDK 1.4, we should use the
                //        IllegalArgumentException(cause) constructor
                throw new IllegalArgumentException(cause!=null ? cause.getMessage() : e.getMessage());
            }
        }
        else
        {
            // very strange, print at least a warning
            System.err.println(
                "WARNING: SwingConnectionManager.valueChanged(ListSelectionEvent)" +
                " called from unknown source"+listSelectionSource);
        }
    }

    /**
     * This method is not part of the public API, it's internally used as
     * the Swing callback method and forwards pCaret to the registered
     * objekt.
     *
     * @param    pCaret event describing the new caret position
     */
    public void caretUpdate(CaretEvent pCaret)
    {
        // get source of this event and check if we know it
        Object caretSource= pCaret.getSource();
        Target target     = null;
        synchronized (mSourceToTarget)
        {
            Source source= new Source(caretSource, CaretEvent.class);
            target= (Target)mSourceToTarget.get(source);
        }
        if (target!=null)
        {
            // we know the target, let's dispatch the call
            try
            {
                Object[] args= new Object[]{pCaret};

                // doit: call the method with the incoming ActionEvent
                target.getMethod().invoke(target.getTarget(), args);
            }
            catch (IllegalAccessException e)
            {
                // map e to a IllegalArgumentException
                throw new IllegalArgumentException(e.getMessage());
            }
            catch (InvocationTargetException e)
            {
                // map the cause of e to a IllegalArgumentException, if it is
                // not a RuntimeException or an Error
                Throwable cause= e.getTargetException();

                if (cause instanceof RuntimeException)
                {
                    throw (RuntimeException) cause;
                }
                if (cause instanceof Error)
                {
                    throw (Error) cause;
                }

                // FIXME: starting with JDK 1.4, we should use the
                //        IllegalArgumentException(cause) constructor
                throw new IllegalArgumentException(cause!=null ? cause.getMessage() : e.getMessage());
            }
        }
        else
        {
            // very strange, print at least a warning
            System.err.println(
                "WARNING: SwingConnectionManager.caretUpdate(CaretEvent)" +
                " called from unknown source"+caretSource);
        }
    }

    /**
     * This method is not part of the public API, it's internally used as
     * the Swing callback method and forwards pFocus to the registered
     * objekt.
     *
     * @param    pFocus event describing the new focus situation
     */
    public void focusGained(FocusEvent pFocus)
    {
        focusLost(pFocus);
    }

    /**
     * This method is not part of the public API, it's internally used as
     * the Swing callback method and forwards pFocus to the registered
     * objekt.
     *
     * @param    pFocus event describing the new focus situation
     */
    public void focusLost(FocusEvent pFocus)
    {
        // get source of this event and check if we know it
        Object focusSource= pFocus.getSource();
        Target target     = null;
        synchronized (mSourceToTarget)
        {
            Source source= new Source(focusSource, FocusEvent.class);
            target= (Target)mSourceToTarget.get(source);
        }
        if (target!=null)
        {
            // we know the target, let's dispatch the call
            try
            {
                Object[] args= new Object[]{pFocus};

                // doit: call the method with the incoming ActionEvent
                target.getMethod().invoke(target.getTarget(), args);
            }
            catch (IllegalAccessException e)
            {
                // map e to a IllegalArgumentException
                throw new IllegalArgumentException(e.getMessage());
            }
            catch (InvocationTargetException e)
            {
                // map the cause of e to a IllegalArgumentException, if it is
                // not a RuntimeException or an Error
                Throwable cause= e.getTargetException();

                if (cause instanceof RuntimeException)
                {
                    throw (RuntimeException) cause;
                }
                if (cause instanceof Error)
                {
                    throw (Error) cause;
                }

                // FIXME: starting with JDK 1.4, we should use the
                //        IllegalArgumentException(cause) constructor
                throw new IllegalArgumentException(cause!=null ? cause.getMessage() : e.getMessage());
            }
        }
        else
        {
            // very strange, print at least a warning
            System.err.println(
                "WARNING: SwingConnectionManager.focusLost/Gained(FocusEvent)" +
                " called from unknown source"+focusSource);
        }
    }

    /**
     * This method is not part of the public API, it's internally used as
     * the Swing callback method and forwards pFocus to the registered
     * objekt.
     *
     * @param    pPropertyChange event describing the new property situation
     */
    public void propertyChange(PropertyChangeEvent pPropertyChange)
    {
        // get source of this event and check if we know it
        Object propertyChangeSource= pPropertyChange.getSource();
        Target target              = null;
        synchronized (mSourceToTarget)
        {
            Source source= new Source(propertyChangeSource, PropertyChangeEvent.class);
            target= (Target)mSourceToTarget.get(source);
        }
        if (target!=null)
        {
            // we know the target, let's dispatch the call
            try
            {
                Object[] args= new Object[]{pPropertyChange};

                // doit: call the method with the incoming ActionEvent
                target.getMethod().invoke(target.getTarget(), args);
            }
            catch (IllegalAccessException e)
            {
                // map e to a IllegalArgumentException
                throw new IllegalArgumentException(e.getMessage());
            }
            catch (InvocationTargetException e)
            {
                // map the cause of e to a IllegalArgumentException, if it is
                // not a RuntimeException or an Error
                Throwable cause= e.getTargetException();

                if (cause instanceof RuntimeException)
                {
                    throw (RuntimeException) cause;
                }
                if (cause instanceof Error)
                {
                    throw (Error) cause;
                }

                // FIXME: starting with JDK 1.4, we should use the
                //        IllegalArgumentException(cause) constructor
                throw new IllegalArgumentException(cause!=null ? cause.getMessage() : e.getMessage());
            }
        }
        else
        {
            // very strange, print at least a warning
            System.err.println(
                "WARNING: SwingConnectionManager.propertyChange(PropertyChangeEvent)" +
                " called from unknown source"+propertyChangeSource);
        }
    }
}

/**************************************************************************/
