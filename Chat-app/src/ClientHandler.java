import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    // keep track of all clients, when a messager is sent loop it to each client
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();//allow us to communicate and send meeage (Broadcast a message)
    private Socket socket; //establish connection between client and sever

    private BufferedReader bufferedReader;// read messages from clients
    private BufferedWriter bufferedWriter;// send data (message) to clients

    private String clientuserName; // show the user name of each client

    public ClientHandler(Socket socket){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientuserName = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER "+ clientuserName +" has enterd the chat!");
        }catch(IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    //method to vbroadcast a meesage
    private void broadcastMessage(String messageToSend) {
        for(ClientHandler clientHandler/*name of the obj */ : clientHandlers/*anme of ArrayList */){ // for each clienthandler represent clientHandlers
            try{
                if(!clientHandler.clientuserName.equals(clientuserName)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush(); // manual flush or remove the buffer or cleaning the buffer

                }
            }catch (IOException e){
              closeEverything(socket, bufferedReader, bufferedWriter);  
            }
        }
    }
    //close everything method
    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
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

    // remove user (wehen user leave the chat)
    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER: "+ clientuserName + "has left the chat!");
    }



    @Override
    //everything we run here will be running in separate thread
    public void run() {
        //blcking operation
        String messageFromClient;

        while(socket.isConnected()){
            try{
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            }catch(IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
     
    
}
