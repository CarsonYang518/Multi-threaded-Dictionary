/*** This is a multi-Threaded Dictionary Server for COMP90015 2021 S1 Assignment1
 * @author Kaixun Yang, a student of Unimelb (Master of Information Technology), student id: 1040203
 * @version 15/04/2021
 */

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
    private static int port;
    private static String dictPath;
    private static HashMap<String, String> dictionary;
    private static int clientCount = 0;

    public static void main(String[] args) {
        try {
            handleArgs(args);
            loadDictionary();
            startGui();
        } catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.getInetAddress();
            //Thread-per-connection
            while(true) {
                Socket clientSocket = serverSocket.accept();
                //Use thread lambda syntax (without a Runnable) to serve multi-clients.
                Thread newThread = new Thread(() -> clientThread(clientSocket));
                newThread.start();
            }
        } catch (BindException e){
            System.out.println("Error: Port number has been used, need to change another one");
            System.exit(0);
        } catch (IOException e){
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    private static void handleArgs(String[] args) throws Exception{
        //Handle the length of args is not two.
        if (args.length != 2) {
            throw new Exception("Error: Wrong number of arguments, need two arguments Port number and Path of dictionary file(txt).");
        }
        //Handle the types of args are wrong.
        try {
            port = Integer.parseInt(args[0]);
            dictPath = args[1];
        } catch (Exception e) {
            throw new Exception("Error: Wrong type of arguments, need Port number to be Integer and Path of dictionary file(txt) to be String.");
        }
        //Handle the value of port is not in the domain.
        if (port < 1024 || port > 65535) {
            throw new Exception("Error: Wrong value of Port number, need Port number to be between 1024 and 65535.");
        }
    }

    private static void startGui(){
        //Start the Gui for Server.
        ServerGui.setGui();
        ServerGui.showMessage("This is the server of dictionary system!");
        ServerGui.showMessage("Waiting for clients to connect.");
        // When we are closing gui, we save the dictionary file in local.
        ServerGui.getFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                saveDictionary();
            }
        });
    }

    private static void loadDictionary(){
        File file = new File(dictPath);
        //If there are no local dictionary file, the dictionary is a empty hash map.
        if(!file.exists()) {
            dictionary = new HashMap<>();
            return;
        }
        try {
            //Obtain the input bytes from the file.
            FileInputStream fileInput = new FileInputStream(dictPath);
            //Restore the original data previously serialized using ObjectOutputStream as an object.
            ObjectInputStream objInput = new ObjectInputStream(fileInput);
            //Obtain the hash map object.
            dictionary = (HashMap<String, String>) objInput.readObject();
            fileInput.close();
            objInput.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    private static void saveDictionary(){
        File file = new File(dictPath);
        try {
            // if dictionary file doesn't exist, create a new one.
            if(!file.exists()) {
                file.createNewFile();
            }
            //Write the output bytes to the file.
            FileOutputStream fileOutput = new FileOutputStream(dictPath);
            //Serialized the original data using ObjectOutputStream to an object.
            ObjectOutputStream objOutput = new ObjectOutputStream(fileOutput);
            //Write the hash map object.
            objOutput.writeObject(dictionary);
            objOutput.close();
            fileOutput.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
    private static void clientThread(Socket clientSocket) {
        int clientNum = ++clientCount;
        ServerGui.showMessage("Client " + clientNum + " has already connected.");
        try {
            //Obtain the bytes stream.
            InputStream byteStreamIn = clientSocket.getInputStream();
            OutputStream byteStreamOut = clientSocket.getOutputStream();
            //Convert bytes stream into characters stream.
            InputStreamReader characterStreamIn = new InputStreamReader(byteStreamIn, StandardCharsets.UTF_8);
            OutputStreamWriter characterStreamOut = new OutputStreamWriter(byteStreamOut, StandardCharsets.UTF_8);
            //Convert characters stream into text Lines.
            BufferedReader in = new BufferedReader(characterStreamIn);
            BufferedWriter out = new BufferedWriter(characterStreamOut);
            String msg;
            while((msg = in.readLine()) != null){
                out.write(useDictionary(clientNum, msg) + "\n");
                out.flush();
            }
            in.close();
            out.close();
            clientSocket.close();
            ServerGui.showMessage("Client " + clientNum + " has already disconnected.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    //Used synchronized to ensure the data consistency.
    private static synchronized String useDictionary(int clientNum , String msg){
        String[] splitMsg = msg.split("&");
        String action = splitMsg[0];

        if((action.equals("Query") || action.equals("Remove")) && splitMsg.length < 2) {
            return "Error: Word is empty, need a word to Query or Remove";
        }
        //Ensure that LowerCase and UpperCase of a word is the same.
        String word = splitMsg[1].toLowerCase();

        if(action.equals("Query")) {
            if(!dictionary.containsKey(word)) {
                ServerGui.showMessage("Client "+ clientNum + " wants to query the word: " + word + ", but can't find the word to query.");
                return "Error: Can't find the word: " + word + " , you can try to add the word into the dictionary system.";
            }
            ServerGui.showMessage("Client "+ clientNum + " queried the word: " + word + " successfully.");
            return dictionary.get(word);
        }

        if(action.equals("Remove")) {
            if(!dictionary.containsKey(word)) {
                ServerGui.showMessage("Client "+ clientNum + " wants to remove the word: " + word + ", but can't find the word to remove.");
                return "Error: Can't remove the word: " + word + " , it doesn't exist.";
            }
            dictionary.remove(word);
            ServerGui.showMessage("Client "+ clientNum + " removed the word: " + word + " successfully.");
            return "Success: Remove word: " + word + " successfully";
        }

        if((action.equals("Add") || action.equals("Update")) && splitMsg.length < 3) {
            return "Error: Word or/and meanings are empty, need a word or/and meanings to Add or Update";
        }
        String meanings = splitMsg[2];

        if(action.equals("Add")) {
            if(dictionary.containsKey(word)) {
                ServerGui.showMessage("Client "+ clientNum + " wants to add the word: " + word + ", but the word has already existed.");
                return "Error: " + word + " has already exits, you can try to update the word.";
            }
            dictionary.put(word, meanings);
            ServerGui.showMessage("Client "+ clientNum + " added the word: " + word + " successfully.");
            return "Success: Add word: " + word + " successfully";
        }

        if(action.equals("Update")) {
            if(!dictionary.containsKey(word)) {
                ServerGui.showMessage("Client "+ clientNum + " wants to update the word: " + word + ", but can't find the word to update.");
                return "Error: Can't find the word: " + word + " to update, you can try to add the word.";
            }
            dictionary.remove(word);
            dictionary.put(word, meanings);
            ServerGui.showMessage("Client "+ clientNum + " updated the word: " + word + " successfully.");
            return "Success: Update word: " + word + " successfully";
        }

        return null;
    }
}
