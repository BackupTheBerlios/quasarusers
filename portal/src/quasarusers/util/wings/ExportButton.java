package quasarusers.util.wings;

import org.wings.SButton;
import org.wings.DynamicResource;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

//todo dpk: 23/01/03 -> MC kommentieren

public class ExportButton extends SButton {
  public ExportButton(String text, final DynamicResource resource) {
    super(text);

    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          getSession().getServletResponse().sendRedirect(resource.getURL().toString());
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    });
  }
}
