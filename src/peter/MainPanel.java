package peter;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
    private JTextField jTextFieldDiceResult1 = new JTextField("-");
    private JCheckBox jCheckBoxDiceResult1 = new JCheckBox();
    private JTextField jTextFieldDiceResult2 = new JTextField("-");
    private JCheckBox jCheckBoxDiceResult2 = new JCheckBox();
    private JTextField jTextFieldDiceResult3 = new JTextField("-");
    private JCheckBox jCheckBoxDiceResult3 = new JCheckBox();
    private JTextField jTextFieldDiceResult4 = new JTextField("-");
    private JCheckBox jCheckBoxDiceResult4 = new JCheckBox();
    private JTextField jTextFieldDiceResult5 = new JTextField("-");
    private JCheckBox jCheckBoxDiceResult5 = new JCheckBox();

    public JButton buttonRollDices = new JButton("Roll dices");
    public JButton buttonSaveResult = new JButton("Save Result");

    public MainPanel(){
        setLayout(new GridLayout(2,6));
        add(new JLabel("Dices"));
//        ImageIcon dice1Icon = new ImageIcon("C:/Users/Peter/IdeaProjects/Yahtzee/src/peter/aec.png");
//        mainTopPanel.add(new JLabel(dice1Icon));

        jTextFieldDiceResult1.setEditable(false);
        jTextFieldDiceResult1.setHorizontalAlignment(JTextField.CENTER);
        add(jTextFieldDiceResult1);

        jTextFieldDiceResult2.setEditable(false);
        jTextFieldDiceResult2.setHorizontalAlignment(JTextField.CENTER);
        add(jTextFieldDiceResult2);

        jTextFieldDiceResult3.setEditable(false);
        jTextFieldDiceResult3.setHorizontalAlignment(JTextField.CENTER);
        add(jTextFieldDiceResult3);

        jTextFieldDiceResult4.setEditable(false);
        jTextFieldDiceResult4.setHorizontalAlignment(JTextField.CENTER);
        add(jTextFieldDiceResult4);

        jTextFieldDiceResult5.setEditable(false);
        jTextFieldDiceResult5.setHorizontalAlignment(JTextField.CENTER);
        add(jTextFieldDiceResult5);

        buttonRollDices.setEnabled(false);
        add(buttonRollDices);

        add(new JLabel("Save this dice:"));
        add(jCheckBoxDiceResult1);
        jCheckBoxDiceResult1.setHorizontalAlignment(JTextField.CENTER);
        add(jCheckBoxDiceResult2);
        jCheckBoxDiceResult2.setHorizontalAlignment(JTextField.CENTER);
        add(jCheckBoxDiceResult3);
        jCheckBoxDiceResult3.setHorizontalAlignment(JTextField.CENTER);
        add(jCheckBoxDiceResult4);
        jCheckBoxDiceResult4.setHorizontalAlignment(JCheckBox.CENTER);
        add(jCheckBoxDiceResult5);
        jCheckBoxDiceResult5.setHorizontalAlignment(JCheckBox.CENTER);

        setMinimumSize(new Dimension(0, 50));
        setPreferredSize(this.getMinimumSize());
        setBorder(BorderFactory.createLineBorder(Color.lightGray));

        buttonSaveResult.setEnabled(false);
        add(buttonSaveResult);
    }

    public void displayNewDices(String[] dicesStrings) {
        //Display dices in GUI
        if (!jCheckBoxDiceResult1.isSelected()) {
            jTextFieldDiceResult1.setText(dicesStrings[0]);
        }
        if (!jCheckBoxDiceResult2.isSelected()) {
            jTextFieldDiceResult2.setText(dicesStrings[1]);
        }
        if (!jCheckBoxDiceResult3.isSelected()) {
            jTextFieldDiceResult3.setText(dicesStrings[2]);
        }
        if (!jCheckBoxDiceResult4.isSelected()) {
            jTextFieldDiceResult4.setText(dicesStrings[3]);
        }
        if (!jCheckBoxDiceResult5.isSelected()) {
            jTextFieldDiceResult5.setText(dicesStrings[4]);
        }
    }

    public void uncheckDices() {
        jCheckBoxDiceResult1.setSelected(false);
        jCheckBoxDiceResult2.setSelected(false);
        jCheckBoxDiceResult3.setSelected(false);
        jCheckBoxDiceResult4.setSelected(false);
        jCheckBoxDiceResult5.setSelected(false);
    }

}
