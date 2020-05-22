package peter;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class MainRightPanel extends JPanel {

    public JButton buttonSendChat = new JButton("Send");
    private JTextArea jTextAreaChatInput = new JTextArea("Inmatning av chatt", 20, 15);
    private static JTextPane jTextPaneChatArea = new JTextPane();
    JScrollPane jScrollPaneChatArea = new JScrollPane(jTextPaneChatArea);

    public MainRightPanel(){
        setLayout(new BorderLayout());

        JPanel rightTopPanel = new JPanel(new BorderLayout());
        JPanel rightMiddelPanel = new JPanel(new BorderLayout());
        JPanel rightBottomPanel = new JPanel();

        //JPanel mainRightPanel = new JPanel(new BorderLayout());
        add(rightTopPanel, BorderLayout.NORTH);
        add(rightMiddelPanel, BorderLayout.CENTER);
        add(rightBottomPanel, BorderLayout.SOUTH);


        //jTextPaneChatArea.setEditable(false);
        //jTextPaneChatArea.getCaret().setVisible(true);
        //jTextAreaChatArea.setLineWrap(true);
        //jTextAreaChatArea.setWrapStyleWord(true);

        DefaultCaret caret = (DefaultCaret) jTextPaneChatArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        jScrollPaneChatArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        rightTopPanel.add(jScrollPaneChatArea);

        //Populate rightmiddel panel

        jTextAreaChatInput.setLineWrap(true);
        jTextAreaChatInput.setWrapStyleWord(true);
        rightMiddelPanel.add(jTextAreaChatInput);

        //Populate rightbottom panel

        buttonSendChat.setEnabled(false);
        rightBottomPanel.add(buttonSendChat);

        setMinimumSize(new Dimension(180, 0));
        rightTopPanel.setMinimumSize(new Dimension(170, 350));
        rightMiddelPanel.setMinimumSize(new Dimension(170, 80));
        rightBottomPanel.setMinimumSize(new Dimension(170, 40));

        setPreferredSize(getMinimumSize());
        rightTopPanel.setPreferredSize(rightTopPanel.getMinimumSize());
        rightMiddelPanel.setPreferredSize((rightMiddelPanel.getMinimumSize()));
        rightBottomPanel.setPreferredSize(rightBottomPanel.getMinimumSize());

        setBorder(BorderFactory.createLineBorder(Color.lightGray));
        rightTopPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        rightMiddelPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        rightBottomPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
    }

    public String getNewChatMessage(){
        return jTextAreaChatInput.getText();
    }

    public void appendNewChattMessage(String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = jTextPaneChatArea.getDocument().getLength();
        jTextPaneChatArea.setCaretPosition(len);
        jTextPaneChatArea.setCharacterAttributes(aset, false);
        jTextPaneChatArea.replaceSelection(msg);
    }

}
