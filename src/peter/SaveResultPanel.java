/**
 * @author  Peter Fröberg, pefr7147@student.su.se
 * @version 1.0
 * @since   2020-06-04
 */

package peter;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class SaveResultPanel extends JPanel {

    private JComboBox<String> comboBoxSaveOptions = new JComboBox<>(new String[]{});
    private JComboBox<String> comboBoxStikeOutOptions = new JComboBox<>(new String[]{});

    public SaveResultPanel(){
        JPanel leftSaveResultPanel = new JPanel();
        leftSaveResultPanel.setLayout(new GridLayout(3, 2, 5, 5));
        leftSaveResultPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftSaveResultPanel.add(new JLabel("Choose combination to save: "));
        leftSaveResultPanel.add(new JLabel("If no, choose combination to strike out ( 0 points)"));

        JPanel centerSaveResultPanel = new JPanel();
        centerSaveResultPanel.setLayout(new GridLayout(3, 2, 5, 5));
        centerSaveResultPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        comboBoxSaveOptions.addAncestorListener(new RequestFocusListener());
        centerSaveResultPanel.add(comboBoxSaveOptions);
        centerSaveResultPanel.add(comboBoxStikeOutOptions);

        add(leftSaveResultPanel);
        add(centerSaveResultPanel);
    }

    public void addToSaveOptions(String newItem){
        comboBoxSaveOptions.addItem(newItem);
    }

    public void addToStrikeOutOptions(String newItem){
        comboBoxStikeOutOptions.addItem(newItem);
    }

    public String getSelectedSaveOption(){
        return Objects.requireNonNull(comboBoxSaveOptions.getSelectedItem()).toString();
    }

    public String getSelectedStrikeOutOption(){
        return Objects.requireNonNull(comboBoxStikeOutOptions.getSelectedItem()).toString();
    }

    public void clearAllSaveOptions(){
        comboBoxSaveOptions.removeAllItems();
        comboBoxStikeOutOptions.removeAllItems();
    }

    public boolean checkSaveOptions(){
        if(comboBoxSaveOptions.getItemCount() == 0){
            return false;
        }
        return true;
    }

}
