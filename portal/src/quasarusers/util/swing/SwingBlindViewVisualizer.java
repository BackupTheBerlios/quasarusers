package quasarusers.util.swing;

import javax.swing.JPanel;

import com.sdm.quasar.view.implementation.swing.SwingViewVisualizer;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.view.View;
import com.sdm.quasar.lang.Keywords;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Jan 25, 2003
 * Time: 9:28:04 PM
 * To change this template use Options | File Templates.
 */
public class SwingBlindViewVisualizer extends SwingViewVisualizer {
    public SwingBlindViewVisualizer(Keywords arguments) {
        super(arguments);
    }

    public Object makeVisualRepresentation() {
        return new JPanel();
    }
}
