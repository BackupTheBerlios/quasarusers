package quasarusers.portal;

import java.util.Locale;

import javax.transaction.SystemException;

import com.sdm.quasar.session.AccessTicket;
import com.sdm.quasar.session.SessionAccessDeniedException;
import com.sdm.quasar.session.SessionManager;
import com.sdm.quasar.session.implementation.AccessController;
import com.sdm.quasar.persistence.model.PropertyDescriptor;
import com.sdm.quasar.persistence.model.TypeDescriptor;
import com.sdm.quasar.persistence.model.StandardPersistenceModelManager;
import com.sdm.quasar.persistence.AccessMode;
import com.sdm.quasar.businessobject.BusinessSystem;
import com.sdm.quasar.businessobject.BusinessModule;
import com.sdm.quasar.businessobject.BusinessObject;
import com.sdm.quasar.businessobject.BusinessObjectProperty;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import quasarusers.portal.AuthorizationConfiguration;

/**
 * Dummy-Anbindung des Berechtigungssystems für Testzwecke.
 * Gewährt alle Berechtigungen.
 *
 * @author Marco Schmickler
 */
public final class SimpleAuthorizationConfiguration extends AuthorizationConfiguration {
    protected AccessController makeSessionAccessController() {
        return new SessionAccessController();
    }

    protected com.sdm.quasar.persistence.model.AccessController makePersistenceAccessController() {
        return new PersistenceAccessController();
    }

    protected com.sdm.quasar.businessobject.AccessController makeBusinessObjectAcessController(SessionManager sessionManager) {
        return new BusinessObjectAccessController();
    }

    protected static class SessionAccessController implements AccessController {
        public AccessTicket authorize (final String user, String password) throws SessionAccessDeniedException {
            return new AccessTicket() {
                public String getUser() {
                    return user;
                };

                public Locale getLocale() {
                    return Locale.getDefault();
                };

            };
        };

        public boolean isAccessRestricted(String type, Object object, String operation,
                                          Object argument) throws IllegalStateException, SystemException {
            return false;
        };

        public boolean isAccessPermitted(AccessTicket ticket, String type, Object object, String operation,
                                         Object argument) throws IllegalStateException, SystemException {
            return true;
        };
    }

    protected class PersistenceAccessController implements com.sdm.quasar.persistence.model.AccessController {
        public boolean isAccessControlled(PropertyDescriptor propertyDescriptor, AccessMode accessMode) {
          return false;
        }

        public boolean isAccessControlled(TypeDescriptor typeDescriptor, AccessMode accessMode) {
          return false;
        }

        public boolean isAccessPermitted(PropertyDescriptor propertyDescriptor, AccessMode accessMode) {
          return true;
        }

        public boolean isAccessPermitted(TypeDescriptor typeDescriptor, AccessMode accessMode) {
          return true;
        }

        public void setPersistenceModelManager(StandardPersistenceModelManager modelManager) {
        }
    }

    protected static class BusinessObjectAccessController implements com.sdm.quasar.businessobject.AccessController {
        public boolean isAccessPermitted(BusinessSystem system) {
            return true;
        }

        public boolean isAccessPermitted(BusinessModule module) {
            return true;
        }

        public boolean isAccessPermitted(BusinessObject object) {
            return true;
        }

        public boolean isAccessPermitted(BusinessObject object, BusinessObjectProperty property) {
            return true;
        }

        public void setBusinessObjectManager(BusinessObjectManager manager) {
        }
    }
}
