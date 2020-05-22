package peter;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static peter.Yatzy.LABELS;

public class ScoreBoard extends JPanel {
    private Map<String, JTextField> scoreBoardMap = new HashMap<String, JTextField>();

    public ScoreBoard() {
        //populate the panel.
        setLayout(new SpringLayout());

        JLabel h = new JLabel("Player: ", JLabel.CENTER);
        JLabel h1 = new JLabel("You", JLabel.CENTER);
        JLabel h2 = new JLabel("P 2", JLabel.CENTER);
        JLabel h3 = new JLabel("P 3", JLabel.CENTER);
        JLabel h4 = new JLabel("P 4", JLabel.CENTER);

        add(h);
        add(h1);
        add(h2);
        add(h3);
        add(h4);

        for (String label : LABELS) {
            JLabel l = new JLabel(label + ": ", JLabel.CENTER);
            add(l);

            JTextField textField = new JTextField();
            textField.setHorizontalAlignment(SwingConstants.CENTER);
            textField.setBackground(Color.white);
            textField.setEditable(false);
            scoreBoardMap.put("P1" + label, textField);

            JTextField textField1 = new JTextField();
            textField1.setHorizontalAlignment(SwingConstants.CENTER);
            textField1.setEditable(false);
            scoreBoardMap.put("P2" + label, textField1);

            JTextField textField2 = new JTextField();
            textField2.setHorizontalAlignment(SwingConstants.CENTER);
            textField2.setEditable(false);
            scoreBoardMap.put("P3" + label, textField2);

            JTextField textField3 = new JTextField();
            textField3.setHorizontalAlignment(SwingConstants.CENTER);
            textField3.setEditable(false);
            scoreBoardMap.put("P4" + label, textField3);

            add(textField);
            add(textField1);
            add(textField2);
            add(textField3);
        }

        SpringUtilities.makeGrid(this, LABELS.length + 1, 5, 6, 6, 6, 6);
    }

    public void updateScoreBoard(int playerNumber, String choice, int score) {
        JTextField textFieldToUpdate = scoreBoardMap.get("P" + playerNumber + choice);
        textFieldToUpdate.setText(String.valueOf(score));
        updateTotals(playerNumber, choice, score);
    }

    private void updateTotals(int playerNumber, String choice, int score) {
        int i;
        for (i = 0; i < LABELS.length; i++) {
            if (choice.equals(LABELS[i])) {
                break;
            }
        }
        if (i < 6) {
            updateUpperTotals(playerNumber, score);
        } else {
            updateLowerTotals(playerNumber, score);
        }
    }

    private void updateUpperTotals(int playerNumber, int score) {
        JTextField upperScoreTextField = scoreBoardMap.get("P" + playerNumber + "Upper score");
        if (upperScoreTextField.getText().equals("")) {
            upperScoreTextField.setText("0");
        }
        int currentTotal = Integer.parseInt(upperScoreTextField.getText()) + score;
        upperScoreTextField.setText(String.valueOf(currentTotal));
        if (currentTotal > 62) {
            JTextField bonusTextField = scoreBoardMap.get("P" + playerNumber + "Upper Bonus");
            bonusTextField.setText("50");
            currentTotal += 50;
        }
        JTextField upperTotal = scoreBoardMap.get("P" + playerNumber + "Upper total");
        upperTotal.setText(String.valueOf(currentTotal));
        updateLowerTotals(playerNumber, score);
    }

    private void updateLowerTotals(int playerNumber, int score) {
        JTextField grandTotal = scoreBoardMap.get("P" + playerNumber + "Grand total");
        if (grandTotal.getText().equals("")) {
            grandTotal.setText("0");
        }
        int currentTotal = Integer.parseInt(grandTotal.getText()) + score;
        grandTotal.setText(String.valueOf(currentTotal));
    }

    public void resetScoreBoard(int numberOfPlaters) {
        for (String l : LABELS) {
            for (int i = 1; i <= numberOfPlaters; i++) {
                JTextField temp = scoreBoardMap.get("P" + i + l);
                temp.setText("");
            }
        }
    }

    public int getScoreFieldValue(String scoreField) {
        return Integer.parseInt(scoreBoardMap.get(scoreField).getText());
    }
}
