import java.net.*;
import java.io.*;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client
  {
    private static BufferedReader in = null;
    private static Socket socket = null;
    private static PrintWriter out = null;
    private static Scanner scanner = new Scanner(System.in);

    private static JTextPane chatFeed = new JTextPane();

    public Client(String address, int port)
    {
      try
      {
        socket = new Socket(address, port);
        System.out.println("Connected");

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
      }
      catch(UnknownHostException u)
      {
        System.out.println(u);
      }
      catch(IOException i)
      {
        System.out.println(i);
      }

      String line = "";

      while (!line.equals("Over"))
      {
        try
        {
          String currentText = chatFeed.getText();
          if(currentText.length() > 0){
            chatFeed.setText(currentText + "\n" + in.readLine());
          }
          else{
            chatFeed.setText(in.readLine());
          }
        }
        catch(IOException i)
        {
          System.out.println(i);
        }
      }
      try
      {
        in.close();
        out.close();
        socket.close();
        scanner.close();
      }
      catch(IOException i)
      {
        System.out.println(i);
      }
    }
    public static void main(String args[])
    {
        JFrame frame = new JFrame("Chat Room");

        frame.setSize(1000,6000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatFeed.setEditable(false);

        frame.getContentPane().add(BorderLayout.CENTER, chatFeed);

        JPanel inputPanel = new JPanel();

        JTextField textField = new JTextField(20);

        JButton sendButton = new JButton("Send");


        Action enterAction = new AbstractAction() {
          @Override

          public void actionPerformed(ActionEvent e){
            Object src = e.getSource();
              out.println(textField.getText());
              textField.setText("");
          }
        };
        Action sendAction = new AbstractAction() {
          @Override

          public void actionPerformed(ActionEvent e){
            Object src = e.getSource();

            if(src == sendButton){
                out.println(textField.getText());
                textField.setText("");
            }
          }
        };

        textField.addActionListener(enterAction);
        sendButton.addActionListener(sendAction);

        inputPanel.add(textField);
        inputPanel.add(sendButton);

        frame.getContentPane().add(BorderLayout.SOUTH, inputPanel);

        frame.setVisible(true);

        Client client = new Client("{IP address of server}", 5000);


    }
  }
  
