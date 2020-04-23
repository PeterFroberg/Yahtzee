package peter;

import javax.swing.*;
import java.awt.*;

public class Yahtzee extends JPanel {
    /**
     * Create a databasehandler to take care of database communication
     */
    private DatabaseHandler databaseHandler = new DatabaseHandler();

    private static JMenuBar menuBar = new JMenuBar();
    private JPanel topPanel = new JPanel(new GridLayout(2,6));
    private JPanel centerPanel = new JPanel();
    private JPanel rightTopPanel = new JPanel();
    private JPanel rightMiddelPanel = new JPanel();
    private JPanel rightBottomPanel = new JPanel();
    private JPanel bottomPanel = new JPanel();


   /* final static boolean shouldFill = true;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;*/


    /**
     * Create GUI components for the application
     */

    //Menu items
    private static JMenuItem menuItemInvitePlayers = new JMenuItem("Invite players");
    private static JMenuItem menuItemScoreBoard = new JMenuItem("My Scoreboard");
    private static JMenuItem menuItemExit = new JMenuItem("Exit");

    private static JTextArea textAreaChatArea = new JTextArea("Detta är chat meddelanden");
    private static JTextArea textAreaScore = new JTextArea("");

    private static JTextField jTextFieldChatInput = new JTextField("Inmatning av chatt");

    private static JTextField jTextFieldDiceResult1 = new JTextField("1");
    private static JCheckBox jCheckBoxDiceResult1 = new JCheckBox();
    private static JTextField jTextFieldDiceResult2 = new JTextField("2");
    private static JCheckBox jCheckBoxDiceResult2 = new JCheckBox();
    private static JTextField jTextFieldDiceResult3 = new JTextField("3");
    private static JCheckBox jCheckBoxDiceResult3 = new JCheckBox();
    private static JTextField jTextFieldDiceResult4 = new JTextField("4");
    private static JCheckBox jCheckBoxDiceResult4 = new JCheckBox();
    private static JTextField jTextFieldDiceResult5 = new JTextField("5");
    private static JCheckBox jCheckBoxDiceResult5 = new JCheckBox();


    private static JButton buttonSendChat = new JButton("Send");
    private static JButton buttonRollAgain = new JButton("Roll dices");
    private static JButton buttonSaveResult = new JButton("Save Result");


    public Yahtzee() {
        //Create Menu
        menuBar = new JMenuBar();
        JMenu menu = new JMenu("Play");
        menuBar.add(menu);

        menu.add(menuItemInvitePlayers);
        menu.add(menuItemScoreBoard);
        menu.add(menuItemExit);





        JPanel outerCenterPanel = new JPanel(new BorderLayout());
        outerCenterPanel.add(centerPanel, BorderLayout.CENTER);
        outerCenterPanel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel outerRightPanel = new JPanel(new BorderLayout());
        outerRightPanel.add(rightTopPanel, BorderLayout.CENTER);
        outerRightPanel.add(rightMiddelPanel, BorderLayout.SOUTH);
        //outerRightPanel.add(rightBottomPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        //Populate toppanel
        topPanel.add(new JLabel("Dices"));
        jTextFieldDiceResult1.setEditable(false);
        jTextFieldDiceResult1.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jTextFieldDiceResult1);

        jTextFieldDiceResult2.setEditable(false);
        jTextFieldDiceResult2.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jTextFieldDiceResult2);

        jTextFieldDiceResult3.setEditable(false);
        jTextFieldDiceResult3.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jTextFieldDiceResult3);

        jTextFieldDiceResult4.setEditable(false);
        jTextFieldDiceResult4.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jTextFieldDiceResult4);

        jTextFieldDiceResult5.setEditable(false);
        jTextFieldDiceResult5.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jTextFieldDiceResult5);

        topPanel.add(buttonRollAgain);

        topPanel.add(new JLabel("Save this dice:"));
        topPanel.add(jCheckBoxDiceResult1);
        jCheckBoxDiceResult1.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jCheckBoxDiceResult2);
        jCheckBoxDiceResult2.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jCheckBoxDiceResult3);
        jCheckBoxDiceResult3.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jCheckBoxDiceResult4);
        jCheckBoxDiceResult4.setHorizontalAlignment(JCheckBox.CENTER);
        topPanel.add(jCheckBoxDiceResult5);
        jCheckBoxDiceResult5.setHorizontalAlignment(JCheckBox.CENTER);

        topPanel.add(buttonSaveResult);
        //populate Right top panel
        rightTopPanel.add(textAreaChatArea);

        //Populate rightmiddel panel
        rightMiddelPanel.add(jTextFieldChatInput);
        rightMiddelPanel.add(buttonSendChat);

        add(topPanel,BorderLayout.NORTH);
        add(outerRightPanel, BorderLayout.EAST);
        //add(rightPanel, BorderLayout.EAST);
        add(outerCenterPanel, BorderLayout.CENTER);


        topPanel.setMinimumSize(new Dimension(0,50));
        outerRightPanel.setMinimumSize(new Dimension(180, 0));
        rightTopPanel.setMinimumSize(new Dimension(170,50));
        rightMiddelPanel.setMinimumSize(new Dimension(170, 80));
        bottomPanel.setMinimumSize(new Dimension(0, 350));

        topPanel.setPreferredSize(topPanel.getMinimumSize());
        outerRightPanel.setPreferredSize(outerRightPanel.getMinimumSize());
        rightTopPanel.setPreferredSize(rightTopPanel.getMinimumSize());
        rightMiddelPanel.setPreferredSize((rightMiddelPanel.getMinimumSize()));
        bottomPanel.setPreferredSize(bottomPanel.getMinimumSize());

        topPanel.setBorder(BorderFactory.createLineBorder(Color.orange));
        outerRightPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        rightTopPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        rightMiddelPanel.setBorder(BorderFactory.createLineBorder(Color.cyan));
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.green));

        /*new Timer(DELAY, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dimen += DELTA_DIMEN;
                if (dimen < MAX_DIMEN) {
                    centerPanel.setPreferredSize(new Dimension(dimen, dimen));
                    SwingUtilities.getWindowAncestor(EmielGui.this).pack();

                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        }).start();*/
    }

   /* public static void addComponentsToPane(Container pane){

        *//**
         * Setting up GUI space, by adding GUI components
         *//*

        pane.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        if (shouldFill){
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        }
        if(shouldWeightX){
            gridBagConstraints.weightx = 0.5;
        }
        //Dices
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new Insets(10,0,0,0);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridy = 0;
        pane.add(jTextFieldDiceResult,gridBagConstraints);
        //Scoreboard
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 300;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5,0,0,0);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridy = 1;
        pane.add(textAreaScore,gridBagConstraints);

        //Chat textarea
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 300;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5,0,0,5);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridy = 1;
        pane.add(textAreaChatArea,gridBagConstraints);

        //Chat writefield
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5,0,0,0);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridy = 2;
        pane.add(jTextFieldChatInput,gridBagConstraints);

        //Send Button
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.anchor = GridBagConstraints.PAGE_END;
        gridBagConstraints.insets = new Insets(10,0,0,0);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridy = 3;

        pane.add(buttonSendChat, gridBagConstraints);

//        //NORTH
//        JPanel jPanelNorth = new JPanel();
//        jPanelNorth.setLayout(new GridBagLayout());
//        GridBagConstraints gridBagConstraints = new GridBagConstraints();
//
//        if (shouldFill) {
//            //natural height, maximum width
//            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//        }
//
//        jPanelNorth.add(jTextFieldDiceResult);
//        //EAST
//        JPanel jPanelEast = new JPanel();
//        jPanelEast.setLayout(new GridLayout(4,1));
//        jPanelEast.add(new JLabel("Game chatroom"));
//        jPanelEast.add(this.textAreaChatArea);
//        jPanelEast.add(this.jTextFieldChatInput);
//
//        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.ipady = 0;
//        gridBagConstraints.weighty = 1.0;
//        gridBagConstraints.anchor = GridBagConstraints.PAGE_END;
//        gridBagConstraints.insets = new Insets(10,0,0,0);
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridwidth = 2;
//        gridBagConstraints.gridy = 2;
//
//        jPanelEast.add(this.buttonSendChat);





    }*/

    private static void createAndShowGUI(){

        Yahtzee mainPanel = new Yahtzee();

        JFrame frame = new JFrame("EmielGui");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.setSize(800,600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

       /* JFrame frame = new JFrame("Yahtzee");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Set upo the Content pane
        addComponentsToPane(frame.getContentPane());

        //Display the window
        frame.pack();
        frame.setSize(640,480);
        frame.setVisible(true);*/
    }
    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
