package quasarusers.util.swing;

import javax.swing.JPopupMenu;
import javax.swing.JComponent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopupManager extends MouseAdapter
{
    /// our Swing popup menu
    private JPopupMenu mPopup;

    // to make the handling of a JPopupMenu easy, we store ourself in our popup widget's properties
    public static final String POPUPMANAGER_PROPERTY = "popupmanager";

    /**
     * constructor, creates a
     */
    PopupManager(String pTitle)
    {
        mPopup= new JPopupMenu(pTitle);
        // store the manager in the popup, so we can access it later when we
        // only have access to the popup widget
        mPopup.putClientProperty(POPUPMANAGER_PROPERTY, this);
    }

    /**
     * Return Swing popup widget.
     * @return the JPopupMenu managed by this manager
     */
    public JPopupMenu getPopup()
    {
        return mPopup;
    }

    public void addManagedComponent(JComponent pComponent)
    {
        pComponent.addMouseListener(this);
    }

    public void removeManagedComponent(JComponent pComponent)
    {
        pComponent.removeMouseListener(this);
    }

    public void mousePressed(MouseEvent e)
    {
        handleMouseEvent(e);
    }

    public void mouseReleased(MouseEvent e)
    {
        handleMouseEvent(e);
    }

    private void handleMouseEvent(MouseEvent e)
    {
        if (e.isPopupTrigger())
        {
            // XXX: we should support "context sensitivity" by enabling/removing entries
            //     from our popup depending on information about the component that
            //     triggered the MouseEvent
            mPopup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
