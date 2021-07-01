/*** This is a multi-Threaded Dictionary Client for COMP90015 2021 S1 Assignment1
 * @author Kaixun Yang, a student of Unimelb (Master of Information Technology), student id: 1040203
 * @version 15/04/2021
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Client {
    private static int port;
    private static String serverAddress;
    private static Socket clientSocket;
    private static BufferedReader in;
    private static BufferedWriter out;

    public static void main(String[] args) throws Exception {
        //Handle args from commend line.
        try {
            handleArgs(args);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }

        //Handle the connection to Server.
        try {
            clientSocket = new Socket(serverAddress, port);
        } catch (UnknownHostException e) {
            System.out.println("Error: Invalid hostname for server, please try other serverAddress or port.");
            return;
        } catch (ConnectException e) {
            System.out.println("Error: Connection refused, please try it later or try other serverAddress or port.");
            return;
        }

        //Handle the I/O.
        try {
            //Obtain the bytes stream.
            InputStream byteStreamIn = clientSocket.getInputStream();
            OutputStream byteStreamOut = clientSocket.getOutputStream();
            //Convert bytes stream into characters stream.
            InputStreamReader characterStreamIn = new InputStreamReader(byteStreamIn, StandardCharsets.UTF_8);
            OutputStreamWriter characterStreamOut = new OutputStreamWriter(byteStreamOut, StandardCharsets.UTF_8);
            //Convert characters stream into text Lines.
            in = new BufferedReader(characterStreamIn);
            out = new BufferedWriter(characterStreamOut);
        } catch (IOException e) {
            System.out.println("Error: Can't get I/O streams, please try it later.");
            return;
        }
        //Start the gui.
        startGui();
    }

    private static void handleArgs(String[] args) throws Exception{
        //Handle the length of args are not 2.
        if (args.length != 2){
            throw new Exception("Error: Wrong number of arguments, need two arguments Port IP address of server and Post number.");
        }
        //Handle the types of args are wrong.
        try {
            serverAddress = args[0];
            port = Integer.parseInt(args[1]);
        } catch (Exception e){
            throw new Exception("Error: Wrong type of arguments, need IP address of server(String) and Port number(Integer).");
        }
        //Handle the value of port is not in the domain.
        if (port < 1024 || port > 65535){
            throw new Exception("Error: Wrong value of Port number, need Port number(Integer) to be between 1024 and 65535.");
        }
    }

    private static void startGui(){
        ClientGui.setGui();
        //Handle the Yes Button
        ClientGui.getYesButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    ClientGui.getYesButton().setEnabled(false);
                    //Remove the start spaces and end spaces
                    String action = ClientGui.getActionComboBox().getSelectedItem().toString().trim();
                    String word = ClientGui.getWordField().getText().trim();
                    String meanings = ClientGui.getMeaningsField().getText().trim();
                    //Handle empty word
                    if(word.equals("")) {
                        ClientGui.showMsg("Error: Word is Empty, please fill the Word area.");
                        ClientGui.getYesButton().setEnabled(true);
                        return;
                    }
                    //Handle not empty meanings for Query and Remove
                    if( (action.equals("Query") || action.equals("Remove")) && !meanings.equals("")){
                        ClientGui.showMsg("Error: For Query or Remove, please keep the Meanings area empty.");
                        ClientGui.getYesButton().setEnabled(true);
                        return;
                    }
                    //Handle empty meanings for Add and Update
                    if( (action.equals("Add") || action.equals("Update")) && meanings.equals("")){
                        ClientGui.showMsg("Error: For Add or Update, please fill the Meanings area.");
                        ClientGui.getYesButton().setEnabled(true);
                        return;
                    }

                    String msg = action + "&" + word + "&" + meanings;
                    String response = sendMsg(msg);
                    //Special case for Query response, because multi-meanings.
                    if(action.equals("Query")){
                        if (!response.contains("Error: ")) {
                            ClientGui.showMsg("Success: the meanings of " + word + " are:");
                            String[] meaning = response.split("@");
                            for (int i = 0; i < meaning.length; i++) {
                                int count = i + 1;
                                ClientGui.showMsg(count + ". " + meaning[i]);
                            }
                        }else ClientGui.showMsg(response);
                    } else ClientGui.showMsg(response);

                    ClientGui.getYesButton().setEnabled(true);
                    //Clear text fields when success.
                    ClientGui.getMeaningsField().setText("");
                    ClientGui.getWordField().setText("");
                } catch (Exception e){
                    //Handle closed Server.
                    System.out.println("Error: Something wrong with the connection, please try it later!");
                    System.exit(0);
                }
            }
        });

        //Handle closing the gui.
        ClientGui.getFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                super.windowClosing(event);
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    //Send message to Server and get the response.
    private static String sendMsg(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
            return in.readLine();
        } catch (IOException e) {
            return "Error: Can't get I/O streams, please try it later.";
        }
    }
}
