package peter;

import javax.swing.*;
import java.awt.*;

public class Yahtzee extends JPanel {
    /**
     * Create a databasehandler to take care of database communication
     */
    private DatabaseHandler databaseHandler = new DatabaseHandler();

    private static JMenuBar menuBar = new JMenuBar();
    private JPanel topPanel = new JPanel();
    private JPanel centerPanel = new JPanel();
    private JPanel rightPanel = new JPanel();
    private JPanel bottomPanel = new JPanel();


   /* final static boolean shouldFill = true;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;*/


    /**
     * Create GUI components for the application
     */
    private static JTextArea textAreaChatArea = new JTextArea("");
    private static JTextArea textAreaScore = new JTextArea("");

    private static JTextField jTextFieldChatInput = new JTextField("Inmatning av chatt");
    private static JTextField jTextFieldDiceResult = new JTextField("1,2,3,4");


    private static JButton buttonSendChat = new JButton("Send");


    public Yahtzee() {
        //Create Menu
        menuBar = new JMenuBar();
        JMenu menu = new JMenu("Play");

        menuBar.add(menu);




        JPanel outerCenterPanel = new JPanel(new BorderLayout());
        outerCenterPanel.add(centerPanel, BorderLayout.CENTER);
        outerCenterPanel.add(bottomPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        //Populate toppanel
        topPanel.add(jTextFieldDiceResult);

        add(topPanel,BorderLayout.NORTH);
        add(rightPanel, BorderLayout.EAST);
        add(outerCenterPanel, BorderLayout.CENTER);


        topPanel.setMinimumSize(new Dimension(0,50));
        rightPanel.setMinimumSize(new Dimension(150, 0));
        bottomPanel.setMinimumSize(new Dimension(0, 350));

        topPanel.setPreferredSize(topPanel.getMinimumSize());
        rightPanel.setPreferredSize(rightPanel.getMinimumSize());
        bottomPanel.setPreferredSize(bottomPanel.getMinimumSize());

        topPanel.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.red));
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
