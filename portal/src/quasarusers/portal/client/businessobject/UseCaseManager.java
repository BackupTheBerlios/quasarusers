package quasarusers.portal.client.businessobject;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.ViewManager;

import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 16, 2003
 * Time: 10:45:06 AM
 * To change this template use Options | File Templates.
 */
public class UseCaseManager {
    private static UseCaseManager useCaseManager;

    public static UseCaseManager getUseCaseManager() {
        return useCaseManager;
    }

    public UseCaseManager() {
        useCaseManager = this;
    }

    public Object call(Call call, Object[] arguments) {
        Stack callStack = new Stack();

        for (int i = 0; i < arguments.length; i++)
            push(callStack, arguments[i]);

        push(callStack, call);

        return pop(callStack);
    }

    public Object call(Call call, Object argument) {
        Stack callStack = new Stack();

        push(callStack, argument);
        push(callStack, call);

        return pop(callStack);
    }

    public Object call(Call call, Object argument1, Object argument2) {
        Stack callStack = new Stack();

        push(callStack, argument1);
        push(callStack, argument2);
        push(callStack, call);

        return pop(callStack);
    }

    public void push(Stack callStack, Object value) {
        callStack.push(value);
    }

    public Object pop(Stack callStack) {
        if (callStack.size() == 0)
            return null;

        performCall(callStack);

        return callStack.pop();
    }

    public void performCall(Stack callStack) {
        while (callStack.peek() instanceof Call) {
            ((Call)callStack.pop()).perform(this, callStack);

            performCall(callStack);
        }
    }

    public Object[] performRemote(Keywords arguments) {
        return new Object[] { arguments.getValue("functionname") + "", new OpenViewCall() };
    }

    public ViewManager getViewManager() {
        return null;
    }

    public static void main(String[] args) {
        UseCaseManager useCaseManager = new UseCaseManager();

        Object result = useCaseManager.call(new BusinessFunctionCall(), new Object[] { new Keywords("functionname", "testf") });

        System.out.println("Returns: " + result);
    }
}
