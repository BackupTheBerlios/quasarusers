package quasarusers.portal.client;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Stack;
import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import com.sdm.quasar.component.CommandController;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.component.ComponentInternalException;
import com.sdm.quasar.component.StandardCommandControllerManager;
import com.sdm.quasar.component.SynchronousCommandPerformer;
import com.sdm.quasar.component.AbstractCommandController;
import com.sdm.quasar.component.Command;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.util.Debug;
import com.sdm.quasar.util.TraceLevel;
import com.sdm.quasar.view.AbstractView;
import com.sdm.quasar.view.SimpleViewOpener;
import com.sdm.quasar.view.UserInterfaceType;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.ViewCommandPerformer;
import com.sdm.quasar.view.ViewContainer;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.view.ViewServerStarter;
import com.sdm.quasar.view.implementation.swing.SwingDialogManager;
import com.sdm.quasar.view.implementation.swing.WindowContainer;
import com.sdm.quasar.view.quickstart.Portal;
import com.sdm.quasar.view.quickstart.SimplePortalNode;
import com.sdm.quasar.view.sample.multichannel.SimpleView;
import com.sdm.quasar.view.sample.multichannel.server.ServerComplexView;
import com.sdm.quasar.newmodelview.*;
import com.sdm.quasar.businessobject.BusinessObjectException;
import quasarusers.portal.server.QuasarSwingSession;
import quasarusers.portal.server.QuasarSwingSessionHome;
import quasarusers.portal.menue.MenueView;
import quasarusers.portal.client.businessobject.UseCaseManager;
import quasarusers.util.MD5Encoder;
import de.sdm.sia.remis.berechtigung.praesentation.benutzergruppe.auswaehlen.BenutzergruppeDataView;
import de.sdm.sia.remis.berechtigung.praesentation.benutzer.suchen.BenutzerDataView;


/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 7, 2003
 * Time: 1:06:27 PM
 * To change this template use Options | File Templates.
 */
public class SwingClient {
  private ViewManager viewManager;
  private QuasarSwingSession session;

  public static class RemoteCommand extends Command {
      /**
       * Constructs a <code>RemoteCommand</code> with a given symbolic name (passed as a {@link com.sdm.quasar.lang.Symbol}).
       *
       * @param       name  the symbolic name of the new command
       */
      public RemoteCommand(Symbol name) {
          super(name);
      }

      /**
       * Constructs a <code>RemoteCommand</code> with a given symbolic name (passed as a string).
       *
       * @param       name  the symbolic name of the new command
       */
      public RemoteCommand(String name) {
          super(name);
      }

      /**
       * Constructs a <code>RemoteCommand</code> with a given symbolic name (passed as a {@link com.sdm.quasar.lang.Symbol})
       * and a flag that indicates if the command's execution has an effect on the associated command controller's
       * command setup.
       *
       * @param       name                the symbolic name of the new command
       * @param       isAffectingSetup    <code>true</code> if the command affects its command
       *                                  controller's command set
       */
      public RemoteCommand(Symbol name, boolean isAffectingSetup) {
          super(name, isAffectingSetup);
      }

      /**
       * Constructs a <code>RemoteCommand</code> with a given symbolic name (passed as a string) and a flag that indicates
       * if the command's execution has an effect on the associated command controller's command setup.
       *
       * @param       name                the symbolic name of the new command
       * @param       isAffectingSetup    <code>true</code> if the command affects its command controller's command set
       */
      public RemoteCommand(String name, boolean isAffectingSetup) {
          super(name, isAffectingSetup);
      }

      /**
       * Performs the actual functionality of a <code>RemoteCommand</code>. Whereas this method is to be overridden
       * for 'normal' <code>Command</code>s (as their behaviour is not predetermined), a <code>RemoteCommand</code>
       * overrides this method itself in order to implement the communication between a {@link RemoteCommandController}
       * and an original {@link CommandController}. It does so by assuming that there is a <code>Command</code>
       * of the same name as the <code>RemoteCommand</code> registered with the original controller; the execution of
       * the <code>RemoteCommand</code> leads to the execution of the original controller's command with the same
       * arguments, and the result of the latter call is returned as the result of the <code>RemoteCommand</code>.
       *
       * @param       arguments   any keyword arguments expected by the functionality of the the original command
       * @return      always <code>null</code> (meaningless since a <code>RemoteCommand</code> is continuation-based
       *              and therefore delivers its result by means of a <code>Continuation</code>)
       */
      public final Object perform(Keywords arguments) throws ComponentException {
          RemoteCommandController controller = (RemoteCommandController)getCommandController();
          Serializable receiver = controller.getOriginalCommandController();

          if (Assertion.CHECK)
              Assertion.checkNotNull(receiver, "receiver");

          return ((EJBCommandControllerManager)controller.getCommandControllerManager()).performRemoteCommand(controller, receiver, this, arguments);
      }
  }

  public static class RemoteCommandController extends AbstractCommandController {
      private Symbol[] remoteCommands;
      private String originalControllerClass;
      private Serializable originalController;

      /**
       * Constructs a <code>StandardRemoteCommandController</code>.
       *
       * @throws      ComponentException  if an error occurred during creation
       */
      public RemoteCommandController() throws ComponentException {
          this(new Keywords());
      }

      /**
       * Constructs a <code>StandardRemoteCommandController</code> with the supplied keyword arguments.
       *
       * @param       arguments  variable arguments
       *              <ul>
       *              <li>{@link com.sdm.quasar.lang.Symbol}[] <code>commands</code> -
       *                      the symbolic names of the remote commands of this remote command controller (required);
       *                      for each name an instance of {@link com.sdm.quasar.component.communication.RemoteCommand} will be automatically created</li>
       *              <li>{@link java.lang.String} <code>originalControllerClass</code> -
       *                      the fully qualified name of the class of the original command controller that is to be
       *                      started for this remote controller (optional); if not supplied, the ID of an existing
       *                      original command controller must be provided by keyword "originalController"</li>
       *              <li>{@link java.io.Serializable} <code>originalController</code> -
       *                      the ID of an existing original command controller (optional); if provided, the remote
       *                      command controller does not start a further original command controller</li>
       *              </ul>
       * @throws      ComponentException  if an error occurred during creation
       */
      public RemoteCommandController(Keywords arguments) throws ComponentException {
          super(arguments);
      }

      protected void initialize(Keywords arguments) {
          super.initialize(arguments);

          remoteCommands = (Symbol[])arguments.getValue("commands");
          originalControllerClass = (String)arguments.getValue("originalControllerClass");
          originalController = (Serializable)arguments.getValue("originalController");
      }

      /**
       * Returns the names of this command controller's remote commands.
       *
       * @return  the names of the remote commands
       */
      protected Symbol[] getRemoteCommands() {
          return remoteCommands;
      }

      /**
       * Returns the name of the class of the original command controller.
       *
       * @return  the name of the class of the original command controller or <code>null</code> if no name was specified.
       */
      public String getOriginalCommandControllerClass() {
          return originalControllerClass;
      }

      /**
       * Returns the ID of the original command controller.
       *
       * @return  the ID of the original command controller or <code>null</code> if there is none yet
       */
      public Serializable getOriginalCommandController() {
          return originalController;
      }

      /**
       * Updates the internal state of this <code>StandardRemoteCommandController</code>. The new state
       * is derived from the specified keyword arguments.
       *
       * @param       arguments  variable arguments
       *              <ul>
       *              <li>{@link java.io.Serializable} <code>originalController</code> -
       *                      the unique identifier of the original command controller of this remote command controller
       *                      (optional)
       *              </ul>
       * @throws      ComponentException  if an error occurred
       */
      public void updateState(Keywords arguments) throws ComponentException {
          Object originalController = arguments.getValue("originalController", this);

          if (originalController != this)
              this.originalController = (Serializable)originalController;
      }

      protected void buildCommands() {
          super.buildCommands();

          if (remoteCommands != null)
              for (int i = 0; i < remoteCommands.length; i++)
                  addCommand(new RemoteCommand(remoteCommands[i], false));
      }

      public Keywords startOriginalCommandController(Keywords arguments) throws ComponentException {
          if (arguments.getValue("controllerClass") == null)
              arguments.addValue("controllerClass", originalControllerClass);

          Keywords result = ((EJBCommandControllerManager)getCommandControllerManager()).startOriginalCommandController(this, arguments);

          updateState(result);

          result.removeValue("originalController");

          return result;
      }

      public void stopOriginalCommandController(Keywords arguments) throws ComponentException {
          ((EJBCommandControllerManager)getCommandControllerManager()).stopOriginalCommandController(this, arguments);
      }

      public void shutdown(Keywords arguments) {
          super.shutdown(arguments);

          try {
              stopOriginalCommandController(arguments);
          }
          catch (ComponentException e) {
              if (Debug.DEBUG)
                  if (Debug.isTraced("com.sdm.quasar.component", TraceLevel.LOW))
                      e.printStackTrace(Debug.getPrintStream());
          }
      }
  }

  public static class EJBCommandControllerManager extends StandardCommandControllerManager {
    private QuasarSwingSession session;

    public EJBCommandControllerManager(Keywords arguments) {
            super(arguments);
        }

        public EJBCommandControllerManager(QuasarSwingSession session) {
            super();
          this.session = session;
        }

        protected void initialize(Keywords arguments) {
            super.initialize(arguments);
        }

        protected void finalize(Keywords arguments) {
            super.finalize(arguments);
        }

        public Keywords startOriginalCommandController(final RemoteCommandController remoteCommandController,
                                                       final Keywords arguments) throws ComponentException {
            if (Assertion.CHECK)
                Assertion.check(arguments.getValue("controllerClass") instanceof String,
                                "arguments.getValue(\"controllerClass\") instanceof String");

            try {
                return session.startViewServer(arguments);
            }
            catch (RemoteException e) {
                throw new ComponentInternalException(e);
            }
        }


        public void stopOriginalCommandController(final RemoteCommandController remoteCommandController,
                                                  final Keywords arguments) throws ComponentException {
          arguments.addValue("originalController", remoteCommandController.getOriginalCommandController());

          try {
            session.stopViewServer(arguments);
          }
          catch (RemoteException e) {
            throw new ComponentInternalException(e);
          }
        }

        public Object performRemoteCommand(RemoteCommandController sender, Serializable receiver,
                                         RemoteCommand command, Keywords arguments) throws ComponentException {
            arguments.addValue("receiver", receiver);
            arguments.addValue("command", command.getName());

          try {
            return session.performCommand(arguments);
          }
          catch (RemoteException e) {
            throw new ComponentInternalException(e);
          }
        }

    }

  public static class CommunicationViewServerStarter implements ViewServerStarter {
      /**
       * Constructs a <code>CommunicationViewServerStarter</code>.
       */
      public CommunicationViewServerStarter() {
          super();
      }

      public Keywords startViewServer(View view, Keywords arguments) throws ComponentException {
          if (arguments.getValue("view") == null)
              arguments.addValue("view", view.getID());

          String serverClass = (String)arguments.getValue("serverClass");

          arguments.removeValue("serverClass");

          RemoteCommandController remoteController =
              (RemoteCommandController)view.getViewManager().getDelegate().makeCommandController(
                  SwingClient.RemoteCommandController.class, new Keywords("commands", view.getViewServerCommands(),
                                                                      "originalControllerClass", serverClass,
                                                                      "leadManager", Boolean.FALSE));

          RemoteCommandController parent = (RemoteCommandController)arguments.getValue("parent");

          if (parent != null)
              arguments.setValue("parent", parent.getOriginalCommandController());

          Keywords result = remoteController.startOriginalCommandController(arguments);

          result.removeValue("originalController");
          result.addValue("server", remoteController);

          return result;
      }

      public void stopViewServer(View view, Keywords arguments) throws ComponentException {
          ((RemoteCommandController)view.getViewServer()).stopOriginalCommandController(arguments);
      }

      public CommandController getViewServer(View view, Serializable viewServerId) throws ComponentException {
          return view.getViewManager().getDelegate().makeCommandController(
              SwingClient.RemoteCommandController.class, new Keywords("commands", view.getViewServerCommands(),
                                                                  "originalController", viewServerId,
                                                                  "leadManager", Boolean.FALSE));
      }
  }


  protected void initializeManagers() throws Exception {

      final ViewManager viewManager = this.viewManager =
              new ViewManager(new Keywords(new Object[] {
                                              "dialogManager", new SwingDialogManager(),
                                              "delegate", new EJBCommandControllerManager(session),
                                              "commandPerformer", new Object[] { null, new SynchronousCommandPerformer(),
                                                                                 AbstractView.class, new ViewCommandPerformer(),
                                                                                 ObjectView.class, new SimpleObjectViewCommandPerformer()},
                                              "viewOpener", new SimpleViewOpener(),
                                              "userInterfaceType", UserInterfaceType.SWING,
                                              "viewServerStarter", new CommunicationViewServerStarter() }));

      viewManager.setDefaultContainer(
          null, Keywords.NONE,
          new ViewManager.ContainerGenerator() {
              public ViewContainer makeContainer(Keywords arguments) throws ComponentException {
                  return viewManager.makeViewContainer(
                      WindowContainer.class,
                      new Keywords(new Object[] { "viewMenus", Boolean.TRUE, "viewTools", Boolean.TRUE,
                                                  "activators", Boolean.TRUE }));
              }
      });

      new ModelViewManager(viewManager) {
            public ViewCollectionController makeViewCollectionController(CollectionView collectionView, Object object) {
                return new ProxyCollectionController(collectionView);
            }
        };
  }

  public void startServer() throws NamingException, RemoteException, CreateException {
    InitialContext jndiContext = new InitialContext();
    System.out.println("Got context");

    // Get a reference to the Interest Bean
    Object ref  = jndiContext.lookup("quasar/QuasarSwingSession");
    System.out.println("Got reference");

    // Get a reference from this to the Bean's Home interface
    QuasarSwingSessionHome home = (QuasarSwingSessionHome)
    PortableRemoteObject.narrow(ref, QuasarSwingSessionHome.class);

    // Create an Interest object from the Home interface
    session = home.create();

    new UseCaseManager() {
        public Object[] performRemote(Keywords arguments) {
            try {
                return session.performUseCase(arguments);
            } catch (RemoteException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            } catch (BusinessObjectException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }

            return null;
        }

        public ViewManager getViewManager() {
            return SwingClient.this.viewManager;
        }
    };
  }

  public void runSample() throws Exception {
                String userName = "admin";
            String userPassword = "Ariodante";

    try {
      session.login(userName, new MD5Encoder().encode(userPassword));
    }
    catch (RemoteException e) {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }

    viewManager.openView(MenueView.class, new Keywords());
//    new Portal(makePortalNodes(viewManager), viewManager).open();
  }

  private static SimplePortalNode[] makePortalNodes(ViewManager viewManager) {
      return new SimplePortalNode[] {
          // a top level node for all demo views
          new SimplePortalNode("Berechtigung", (Class)null, viewManager, new SimplePortalNode[] {
              // leaf node for the ServerComplexView
              new SimplePortalNode("Benutzer", BenutzerDataView.class, viewManager, null),
              new SimplePortalNode("Benutzergruppe", BenutzergruppeDataView.class, viewManager, null),
//              // leaf node for the SimpleView
//              new SimplePortalNode("Demo2", SimpleView.class, viewManager, null) {
//                  public Object getObject() {
//                      return new Object[] { "Ebene1", new Object[0]};
//                  }
//              }
          })
      };
  }

  public static void main(String[] args) throws Exception
  {
    SwingClient main = new SwingClient();

    main.startServer();
    main.initializeManagers();

    main.runSample();
  }
}
