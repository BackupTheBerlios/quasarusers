package quasarusers.portal;

import com.sdm.quasar.communication.CommunicationException;
import com.sdm.quasar.communication.CommunicationManager;
import com.sdm.quasar.communication.channel.Channel;
import com.sdm.quasar.communication.channel.ChannelEvent;
import com.sdm.quasar.communication.channel.ChannelListener;
import com.sdm.quasar.communication.message.MessageController;
import com.sdm.quasar.communication.message.implementation.StandardMessageController;
import com.sdm.quasar.runtime.client.LoginDialog;
import com.sdm.quasarx.configuration.CommunicationConfiguration;
import com.sdm.quasarx.configuration.Configuration;
import com.sdm.quasarx.configuration.ConfigurationModule;
import com.sdm.quasarx.configuration.ModelViewConfiguration;
import com.sdm.quasarx.configuration.ViewConfiguration;
import quasarusers.util.MD5Encoder;

public class ClientMain {

    /**
     * Method main.
     *
     * todo Fehlerbehandlung
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Starting client...");

        try {
            Configuration configuration =
                    new Configuration(
                            ClientMain.class.getClassLoader().getResource(
                                    "de/sdm/sia/remis/portal/clientconfig.xml"),
                            new ConfigurationModule[] {
                                new CommunicationConfiguration(),
                                new ViewConfiguration(),
                                new ModelViewConfiguration()});

            CommunicationManager communicationManager =
                    (CommunicationManager) configuration.getSingleton(CommunicationManager.class);

//            ((ServerRegistry)configuration.getSingleton(ServerRegistry.class)).putAttribute("testserver:SOCKET:CONNECT", "testserver:SOCKET:192.168.1.9:3000");

            Channel channel = null;
            try {
                /** todo Servernamen konfigurierbar */
                channel = communicationManager.connect("testserver");
            } catch (CommunicationException e) {
                e.printStackTrace();
                throw new RuntimeException("Verbindung zum Server fehlgeschlagen");
            }

            channel.addChannelListener(new ChannelListener() {
                public void channelOpened(ChannelEvent channelEvent) {
                }

                public void channelClosed(ChannelEvent channelEvent) {
                    // todo: cleanup

                    System.exit(0);
                }
            });

            StandardMessageController standardMessageController =
                    new StandardMessageController(channel);

            configuration.registerSingleton(
                    MessageController.class,
                    standardMessageController);

            LoginDialog dialog = new LoginDialog();

//            dialog.show();
//
//            String userName = dialog.getName();
//            String userPassword = dialog.getPassword();

            String userName = "admin";
            String userPassword = "Ariodante";

            standardMessageController.call("login", new Object[] { userName, new MD5Encoder().encode(userPassword) });
//                standardMessageController.call("login", new Object[] { "gast", "XXXXXXXXXXXXXXXX" });

        } catch (Throwable t) {
            t.printStackTrace();

            System.exit(-1);
        }
    }
}
