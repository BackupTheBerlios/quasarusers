package quasarusers.portal;

import com.sdm.quasar.communication.CommunicationManager;
import com.sdm.quasar.communication.channel.Channel;
import com.sdm.quasar.communication.channel.ChannelEvent;
import com.sdm.quasar.communication.channel.ChannelException;
import com.sdm.quasar.communication.channel.ChannelListener;
import com.sdm.quasar.communication.channel.ServiceHandler;
import com.sdm.quasar.communication.channel.implementation.SocketChannelServer;
import com.sdm.quasar.communication.message.MessageHandler;
import com.sdm.quasar.communication.message.MessageServiceRegistry;
import com.sdm.quasar.communication.message.implementation.AbstractMessageService;
import com.sdm.quasar.communication.message.implementation.StandardMessageController;
import com.sdm.quasar.communication.proxy.ProxyServer;
import com.sdm.quasar.communication.proxy.implementation.ProxyHandler;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.modelview.proxy.ProxyAdapter;
import com.sdm.quasar.modelview.server.ModelViewServerManager;
import com.sdm.quasar.runtime.Loadable;
import com.sdm.quasar.session.Session;
import com.sdm.quasar.session.SessionManager;
import com.sdm.quasar.session.SessionType;
import com.sdm.quasar.view.UserInterfaceType;
import com.sdm.quasar.view.server.SimpleViewDescription;
import com.sdm.quasar.view.server.ViewDescription;
import com.sdm.quasar.view.server.ViewServerManager;
import com.sdm.quasarx.configuration.CommunicationConfiguration;
import com.sdm.quasarx.configuration.Configuration;
import quasarusers.util.businessobject.NodeGenerator;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Jan 24, 2003
 * Time: 8:38:26 PM
 * To change this template use Options | File Templates.
 */
public class ServerMain implements Loadable {
	private static final Symbol channelSession =
		Symbol.forName("com.sdm.quasar.runtime.server.ChannelSession");
	private static final Symbol channelProxyServer =
		Symbol.forName("com.sdm.quasar.runtime.server.ProxyServer");
	private static final Symbol channelController =
		Symbol.forName("com.sdm.quasar.runtime.server.ChannelController");

    public void load(String arguments) throws Exception {
		System.out.println("Starting server...");

	    final Configuration configuration = ConfigurableRuntime.getRuntime().getConfiguration();


		final CommunicationManager communicationManager =
			(CommunicationManager) configuration.getSingleton(CommunicationManager.class);

		final SessionManager sessionManager =
			(SessionManager) configuration.getSingleton(SessionManager.class);
		final ViewServerManager viewServerManager =
			(ViewServerManager) configuration.getSingleton("communicationserver");

		viewServerManager.registerSingleton(
			ModelViewServerManager.class,
			configuration.getSingleton(ModelViewServerManager.class));

        final ProxyAdapter proxyAdapter = (ProxyAdapter)configuration.getSingleton(CommunicationConfiguration.RuntimeProxyAdapter.class);

        MessageServiceRegistry serviceRegistry =
      ((MessageHandler)communicationManager.getServiceHandler(MessageHandler.class)).getMessageServiceRegistry();

        serviceRegistry.addMessageService(new AbstractMessageService("login") {
            public Object perform(Object parameters[]) throws Exception {
                Channel channel = communicationManager.getChannel();

                sessionManager.open((String)parameters[0], // Benutzerkennung
                                    (String)parameters[1], // Das verschlüsselte Passwort
                                    SessionType.INTERACTIVE);

                Session session = sessionManager.getSession();

                channel.setLocal(channelSession, session);

                final StandardMessageController controller = new StandardMessageController(channel);

                session.setLocal(
                    channelController,
                        controller);
                session.setLocal(
                    channelProxyServer,
                    (ProxyServer) channel.getChannelState(
                        ProxyHandler.class));
                session.setLocal(
                    "sessionViewServerManager",
                    viewServerManager);

                session.setLocal(
                    "proxyAdapter",
                    proxyAdapter);

                final ViewDescription portalView = new SimpleViewDescription("menue",
                                                             "Menue",
                                                             "",
                                                             quasarusers.portal.menue.MenueView.class.getName(),
                                                             quasarusers.portal.menue.MenueViewServer.class.getName());

                viewServerManager.openView(portalView, new NodeGenerator(UserInterfaceType.SWING).makeSystemNodes(), new Keywords());

                return null;
            }
        });

		communicationManager.addChannelListener(new ChannelListener() {
			public void channelOpened(ChannelEvent channelEvent) {
                System.out.println("Connected");
			}

			public void channelClosed(ChannelEvent channelEvent) {
				try {
					Session session =
						(Session) channelEvent.getChannel().getLocal(channelSession);
					sessionManager.suspend();
					sessionManager.resume(session);

					session.close();
				} catch (Exception e) {
					e.printStackTrace();
					//To change body of catch statement use Options | File Templates.
				}
			}
		});

		communicationManager.accept(arguments);
	}
}
