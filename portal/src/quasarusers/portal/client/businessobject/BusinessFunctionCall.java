package quasarusers.portal.client.businessobject;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.DialogManager;

import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 16, 2003
 * Time: 11:53:38 AM
 * To change this template use Options | File Templates.
 */
public class BusinessFunctionCall extends Call {
    public void perform(UseCaseManager useCaseManager, Stack callStack) {
        System.out.println("performFunction");

        Keywords arguments = (Keywords)useCaseManager.pop(callStack);

        Object[] results = useCaseManager.performRemote(arguments);

        if (results instanceof String[]) {
            DialogManager dialogManager = useCaseManager.getViewManager().getDialogManager();

            String function = dialogManager.openChoiceDialog("Choose", (String[])results, ((String[])results)[0]);

            arguments.removeValue("functionSelector");
            arguments.setValue("functionName", function);

            results = useCaseManager.performRemote(arguments);
        }

        for (int i = 0; i < results.length; i++)
            useCaseManager.push(callStack, results[i]);
   }
}
