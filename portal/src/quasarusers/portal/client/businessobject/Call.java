package quasarusers.portal.client.businessobject;

import java.io.Serializable;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 16, 2003
 * Time: 11:34:29 AM
 * To change this template use Options | File Templates.
 */
public abstract class Call implements Serializable {
    public abstract void perform(UseCaseManager useCaseManager, Stack callStack);
}
