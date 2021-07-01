/*** This is a multi-Threaded Dictionary Server Gui for COMP90015 2021 S1 Assignment1
 * @author Kaixun Yang, a student of Unimelb (Master of Information Technology), student id: 1040203
 * @version 15/04/2021
 */
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGui {
    private static JFrame frame;
    private static JTextArea showArea;
    private static JButton clearButton;

    public static void setGui() {
        frame = new JFrame("Dictionary System Server");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        placeComponents(panel);
        frame.add(panel);
        frame.setVisible(true);
        //Handle the clear button
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearButton.setEnabled(false);
                showArea.setText("");
                showArea.append("This is the server of dictionary system!\n");
                showArea.append("Waiting for clients to connect: \n");
                clearButton.setEnabled(true);
            }
        });
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);
        showArea = new JTextArea();
        clearButton = new JButton("Clear");
        JScrollPane Scroll=new JScrollPane(showArea);
        Scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        Scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        Scroll.setBounds(100,50,600,400);
        clearButton.setBounds(360, 520, 80,20);
        panel.add(Scroll);
        panel.add(clearButton);
    }

    public static void showMessage(String msg) {
        showArea.append(msg + '\n');
    }

    public static JFrame getFrame() {
        return frame;
    }
}
