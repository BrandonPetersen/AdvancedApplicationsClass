import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;
  
public class ThreadServer 
{
    public static void main(String[] args) throws IOException 
    {
        ServerSocket ss = new ServerSocket(5000);
        ArrayList<String> clients = new ArrayList<>();
        ArrayList<Integer> num = new ArrayList<>();
        ArrayList<PrintWriter> outputs = new ArrayList<>();
        num.add(1);

        while (true) 
        {
            Socket s = null;
              
            try 
            {
                s = ss.accept();
                
                String hostname = s.getInetAddress().getHostName();
                clients.add(hostname);

                System.out.println("A new client is connected : " + hostname);
                  
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                outputs.add(out);
                  
                System.out.println("Assigning new thread for this client");
  
                Thread t = new ClientHandler(s, in, out, clients, num, outputs);
  
                t.start();
                  
            }
            catch (Exception e){
                s.close();
                e.printStackTrace();
            }
        }
    }
}
  
class ClientHandler extends Thread 
{
    final BufferedReader in;
    final PrintWriter out;
    final Socket s;
    ArrayList<String> clientList = new ArrayList<>();
    ArrayList<Integer> num = new ArrayList<>();
    ArrayList<PrintWriter> outputs;
      
  
    public ClientHandler(Socket s, BufferedReader in, PrintWriter out, ArrayList<String> clientList, ArrayList<Integer> num, ArrayList<PrintWriter> outputs) 
    {
        this.s = s;
        this.in = in;
        this.out = out;
        this.clientList = clientList;
        this.num = num;
        this.outputs = outputs;
        
    }
  
    @Override
    public void run() 
    {
        String received;
        String name;
        int wow = num.get(0);
        num.set(0, wow +1);
        try{            
            out.println("What is your name?");
            name = in.readLine();
            clientList.set(wow -1, name);
            out.println("Welcome " + clientList.get(wow -1) + ", continue typing to chat.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        while (true) 
        {
            try {
     
                received = in.readLine();
                  
                if(received.equals("Exit"))
                { 
                    System.out.println("Client " + clientList.get(wow-1) + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.s.close();
                    System.out.println("Connection closed");
                    break;
                }
                else if(received.contains("/dm")){
                    String[] parts = received.split(" ", 3);
                    String recipient = parts[1];
                    String message = parts[2];
                    int index = clientList.indexOf(recipient);
                    PrintWriter write = outputs.get(index);
                    out.println("[DM to " + clientList.get(index) + "]" + ": " + message);
                    write.println("[DM from " + clientList.get(wow-1) + "]" + ": " + message);
                }
                else{
                    for(PrintWriter output: outputs){
                    output.println(clientList.get(wow-1) + ": " + received);
                    }
                }
                  
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
          
        try
        {
            this.in.close();
            this.out.close();
              
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}