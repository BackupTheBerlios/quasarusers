package quasarusers.portal.businessobject;

import com.sdm.quasar.view.quickstart.PortalNode;
import com.sdm.quasar.lang.Keywords;

import java.io.Serializable;
import java.util.Locale;

import quasarusers.util.businessobject.ViewProperty;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 13, 2003
 * Time: 6:33:40 PM
 * To change this template use Options | File Templates.
 */
public class BusinessPortalNode implements PortalNode, Serializable {
    private String label;
    private Object view;
    private PortalNode[] children;


    public BusinessPortalNode(String label, Object viewDescriptor, PortalNode[] children) {
        this.label = label;

        view = viewDescriptor;
        this.children = children;

        if (children == null)
            this.children = new PortalNode[0];
    }

    public BusinessPortalNode(ViewProperty viewProperty) {
        this(viewProperty.getLabel(Locale.getDefault()), viewProperty.getObject(), null);
    }

    public void openView(Keywords arguments) {

    }

    public int getId() {
        return 0;
    }

    public String getLabel() {
        return label;
    }

    public Object getView() {
        return view;
    }

    public PortalNode[] getChildren() {
        return children;
    }

    public boolean isActive() {
        return getView() != null;
    }

    public Object getObject() {
        return null;
    }

    public PortalNode findNode(int id) {
        return null;
    }

    public String toString() {
        return getLabel();
    }
}