/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 15.01.2003
 * Time: 13:39:33
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.portal;

import com.sdm.quasar.session.implementation.LocalSessionManager;
import org.wings.RequestURL;
import org.wings.SFlowDownLayout;
import org.wings.SFrame;
import org.wings.SLabel;
import org.wings.SPanel;

import javax.transaction.SystemException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Dies ist eine Status-Seite für den Admin.
 *
 * @author Marco Schmickler
 */
public class StatusFrame extends SFrame {
  private static Date startTime = new Date(System.currentTimeMillis());

  public StatusFrame() {
    RequestURL url = (RequestURL) getSession().getProperty("request.url");
    SPanel panel = new SPanel();

    setTitle("Server Status");

    panel.setLayout(new SFlowDownLayout());
    panel.add(new SLabel("Big brother is watching..."));
    panel.add(new SLabel("Request URL " + url.toString()));

    try {
      int openSessions = LocalSessionManager.getSessionManager().getSessions().length;
      panel.add(new SLabel("Open sessions " + openSessions));
    } catch (SystemException e) {
      e.printStackTrace();
    }

    // Wann wurde der Server zuletzt gestartet
    panel.add(new SLabel("running since " + DateFormat.getInstance().format(startTime)));
    // verbleibenden freien Speicher anzeigen, nach GarbageCollection wegen Netto-Zahlen
    Runtime.getRuntime().gc();
    panel.add(new SLabel("Free memory " + Runtime.getRuntime().freeMemory()));

    /* Server remote herunterfahren...
    SButton exitButton = new SButton("exit");
    panel.add(exitButton);
    exitButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });*/

    // login der angemeldeten Users anzeigen (ohne Gast)
    try {
      listUsers(panel);
    } catch (SystemException e) {
      e.printStackTrace();
    }

    getContentPane().add(panel);
  }

  private void listUsers(SPanel panel) throws SystemException {
    com.sdm.quasar.session.Session[] sessions = LocalSessionManager.getSessionManager().getSessions();

    for (int i = 0; i < sessions.length; i++) {
      com.sdm.quasar.session.Session session = sessions[i];
      SessionBenutzer benutzer = ((SessionBenutzer) session.getLocal("siaUser", null));

      if (benutzer != null)
        if (!"gast".equals(benutzer.getKennung()))
          panel.add(new SLabel(benutzer.getKennung()));
    }
  }
}
