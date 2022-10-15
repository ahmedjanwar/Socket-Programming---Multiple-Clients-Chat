import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;

    //constructor
    public Client(Socket socket, String userName){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = userName;
        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);  
        }
    }
    //Message sender 
    public void sendMessage(){
        try{
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            try (Scanner scanner = new Scanner(System.in)) {
                while(socket.isConnected()){
                    String messagToSend =  scanner.nextLine();
                    bufferedWriter.write(userName + ": " + messagToSend);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }

        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter); 
        }
    }
    // get messages 
    public void listenForMessage(){
        new Thread(new Runnable() {

            @Override
            public void run() {
                String MessageFromGroup;
                while(socket.isConnected()){
                    try{
                        MessageFromGroup = bufferedReader.readLine();
                        System.out.println(MessageFromGroup);
                    }catch(IOException e){
                        closeEverything(socket, bufferedReader, bufferedWriter); 
                    }
                }
                
            }

            
            
        }).start();
    }
    //
    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    public static void main(String[] args) throws UnknownHostException, IOException {
       Scanner scanner = new Scanner(System.in);
            System.out.println("Enter Your Username fro the Group chat: ");
            String userName = scanner.nextLine();
            // connection to port
            Socket socket = new Socket("localhost",5000); // use ipaddress or localhost
            Client client = new Client(socket, userName); 
            client.listenForMessage();
            client.sendMessage();
        
    }
}
