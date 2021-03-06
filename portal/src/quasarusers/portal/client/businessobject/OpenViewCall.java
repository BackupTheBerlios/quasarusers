package quasarusers.portal.client.businessobject;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.component.ComponentException;

import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 16, 2003
 * Time: 11:53:54 AM
 * To change this template use Options | File Templates.
 */
public class OpenViewCall extends Call {
    public void perform(UseCaseManager useCaseManager, Stack callStack) {
        Keywords keywords = (Keywords)useCaseManager.pop(callStack);

        String viewClassName = (String)keywords.getValue("view");

        try {
            useCaseManager.getViewManager().openView(viewClassName, keywords);
        } catch (ComponentException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }

        System.out.println("openView" + keywords);

        useCaseManager.push(callStack, null);
    }
}
