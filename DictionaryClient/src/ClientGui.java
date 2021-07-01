/*** This is a multi-Threaded Dictionary Client Gui for COMP90015 2021 S1 Assignment1
 * @author Kaixun Yang, a student of Unimelb (Master of Information Technology), student id: 1040203
 * @version 15/04/2021
 */

import javax.swing.*;
public class ClientGui {
    private static JFrame frame;
    private static JLabel actionLabel;
    private static JLabel wordLabel;
    private static JLabel meaningsLabel;
    private static JTextArea responseArea;
    private static JTextField wordField;
    private static JTextField meaningsField;
    private static JComboBox<String> actionComboBox;
    private static JButton yesButton;

    public static void setGui() {
        frame = new JFrame("Dictionary System Client");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);
        responseArea = new JTextArea();
        JScrollPane Scroll=new JScrollPane(responseArea);
        Scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        Scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        Scroll.setBounds(100,50,600,300);
        responseArea.append("Welcome to use the dictionary!\n");
        responseArea.append("Please choose the action.\n");
        responseArea.append("For Query and Remove, you need fill the Word area only.\n");
        responseArea.append("For Add and Update, you need fill the Word and Meanings area.\n");
        responseArea.append("If there are multiple meanings, please use '@' to separate.\n");
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        actionLabel = new JLabel("Action:");
        wordLabel = new JLabel("Word:");
        meaningsLabel = new JLabel("Meanings:");
        wordField = new JTextField();
        meaningsField = new JTextField();
        actionComboBox = new JComboBox<>(new String[] { "Query", "Add", "Remove", "Update" });
        actionLabel.setBounds(100, 400, 70,20);
        actionComboBox.setBounds(170, 400, 100, 20);
        wordLabel.setBounds(100, 440, 70,20);
        wordField.setBounds(170, 440, 200, 20);
        meaningsLabel.setBounds(100, 480, 70,20);
        meaningsField.setBounds(170, 480, 530,20);
        yesButton = new JButton("Yes");
        yesButton.setBounds(360, 520, 80,20);
        panel.add(Scroll);
        panel.add(actionLabel);
        panel.add(actionComboBox);
        panel.add(wordLabel);
        panel.add(wordField);
        panel.add(meaningsLabel);
        panel.add(meaningsField);
        panel.add(yesButton);
    }

    public static void showMsg(String msg){
        responseArea.append(msg + '\n');
    }

    public static JFrame getFrame() {
        return frame;
    }

    public static JTextArea getResponseArea() {
        return responseArea;
    }

    public static JTextField getWordField() {
        return wordField;
    }

    public static JTextField getMeaningsField() {
        return meaningsField;
    }

    public static JComboBox<String> getActionComboBox() {
        return actionComboBox;
    }

    public static JButton getYesButton() {
        return yesButton;
    }
}
