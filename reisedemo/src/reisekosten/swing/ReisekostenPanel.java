/*
 * Copyright (c) 2001, 2002. software design & management AG
 * All rights reserved.
 * This file is made available under the terms of the license
 * agreement that accompanies this distribution.
 *
 * $Revision: 1.1 $, last modified $Date: 2003/03/26 19:59:52 $ by $Author: schmickler $
 */
package reisekosten.swing;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.widget.fields.swing.TextInputField;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

/**
 * Swing-Repräsentation der Einstiegsmaske.
 *
 * Verwendet GridBagLayout. Sollte am besten mit einem GUI-Builder bearbeitet werden!
 *
 * @author Martin Fritz
 */
public class ReisekostenPanel extends JPanel {
    GridBagLayout panelLayout = new GridBagLayout();
    JLabel mitarbeiterLabel = new JLabel();
    JTextField mitarbeiterTF = new JTextField();
    JLabel datumVonLabel = new JLabel();
    TextInputField datumVonTF = new TextInputField();
    JLabel datumBisLabel = new JLabel();
    TextInputField datumBisTF = new TextInputField();
    JLabel kostenstelleLabel = new JLabel();
    TextInputField kostenstelleTF = new TextInputField();
    JLabel beschreibungLabel = new JLabel();
    JTextField beschreibungTF = new JTextField();
    JPanel suchenPanel = new JPanel();
    FlowLayout suchenLayout = new FlowLayout();
    JButton suchenBtn = new JButton();
    JButton resetBtn = new JButton();
    JScrollPane ergebnisPane = new JScrollPane();
    JTable ergebnisTable = new JTable();
    JPanel bearbeitenPanel = new JPanel();
    FlowLayout bearbeitenLayout = new FlowLayout();
    JButton neuBtn = new JButton();
    JButton bearbeitenBtn = new JButton();
    JButton loeschenBtn = new JButton();

    public ReisekostenPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {

        mitarbeiterLabel.setPreferredSize(new Dimension(120, 17));
        mitarbeiterLabel.setText("Mitarbeiter:");
        mitarbeiterTF.setMinimumSize(new Dimension(200, 21));
        mitarbeiterTF.setPreferredSize(new Dimension(200, 21));

        datumVonLabel.setText("Anfangsdatum von:");
        datumVonLabel.setPreferredSize(new Dimension(120, 17));
        datumVonTF.setMinimumSize(new Dimension(80, 21));
        datumVonTF.setPreferredSize(new Dimension(80, 21));
        datumVonTF.setType(Date.class);

        datumBisLabel.setText("Bis:");
        datumBisTF.setMinimumSize(new Dimension(80, 21));
        datumBisTF.setPreferredSize(new Dimension(80, 21));
        datumBisTF.setType(Date.class);

        kostenstelleLabel.setText("Kostenstelle:");
        kostenstelleTF.setMinimumSize(new Dimension(80, 21));
        kostenstelleTF.setPreferredSize(new Dimension(80, 21));
        kostenstelleTF.setType(Integer.class, new Keywords("precision", new Integer(4)));

        beschreibungLabel.setText("Beschreibung:");
        beschreibungTF.setMinimumSize(new Dimension(200, 21));
        beschreibungTF.setPreferredSize(new Dimension(200, 21));

        suchenBtn.setPreferredSize(new Dimension(120, 27));
        suchenBtn.setText("Suchen");
        resetBtn.setPreferredSize(new Dimension(120, 27));
        resetBtn.setText("Zurücksetzen");
        suchenLayout.setAlignment(FlowLayout.LEFT);
        suchenPanel.setLayout(suchenLayout);
        suchenPanel.setMinimumSize(new Dimension(300, 37));
        suchenPanel.add(suchenBtn, null);
        suchenPanel.add(resetBtn, null);

        ergebnisPane.getViewport().add(ergebnisTable, null);
        ergebnisPane.setMinimumSize(new Dimension(400, 200));

        neuBtn.setPreferredSize(new Dimension(120, 27));
        neuBtn.setText("Neu");
        bearbeitenBtn.setPreferredSize(new Dimension(120, 27));
        bearbeitenBtn.setText("Bearbeiten");
        loeschenBtn.setPreferredSize(new Dimension(120, 27));
        loeschenBtn.setText("Löschen");
        bearbeitenLayout.setAlignment(FlowLayout.LEFT);
        bearbeitenPanel.setLayout(bearbeitenLayout);
        bearbeitenPanel.setPreferredSize(new Dimension(400, 37));
        bearbeitenPanel.setMinimumSize(new Dimension(400, 37));
        bearbeitenPanel.add(neuBtn, null);
        bearbeitenPanel.add(bearbeitenBtn, null);
        bearbeitenPanel.add(loeschenBtn, null);

        this.setMinimumSize(new Dimension(500, 400));
        this.setPreferredSize(new Dimension(500, 400));
        this.setLayout(panelLayout);

        this.add(mitarbeiterLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 10));
        this.add(mitarbeiterTF, new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(datumVonLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 10));
        this.add(datumVonTF, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(datumBisLabel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 10, 0));
        this.add(datumBisTF, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(kostenstelleLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 10));
        this.add(kostenstelleTF, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(beschreibungLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 10));
        this.add(beschreibungTF, new GridBagConstraints(1, 3, 3, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(suchenPanel, new GridBagConstraints(0, 4, 4, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
        this.add(ergebnisPane, new GridBagConstraints(0, 5, 4, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 91, 0));
        this.add(bearbeitenPanel, new GridBagConstraints(0, 6, 5, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    }

    public JTable getErgebnisTable() {
        return ergebnisTable;
    }

    public JScrollPane getErgebnisPane() {
        return ergebnisPane;
    }

    public JTextField getMitarbeiterTF() {
        return mitarbeiterTF;
    }

    public TextInputField getKostenstelleTF() {
        return kostenstelleTF;
    }

    public JTextField getBeschreibungTF() {
        return beschreibungTF;
    }

    public TextInputField getDatumVonTF() {
        return datumVonTF;
    }

    public TextInputField getDatumBisTF() {
        return datumBisTF;
    }

    public JButton getSuchenButton() {
        return suchenBtn;
    }

    public JButton getResetButton() {
        return resetBtn;
    }

    public JButton getNeuButton() {
        return neuBtn;
    }

    public JButton getBearbeitenButton() {
        return bearbeitenBtn;
    }

    public JButton getLoeschenButton() {
        return loeschenBtn;
    }
}