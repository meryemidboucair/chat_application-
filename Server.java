
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Base64;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class Server extends JFrame implements ActionListener {
    private JTextField textField;
    private JTextArea chatArea;
    private BufferedReader in;
    private PrintWriter out;
    private SecretKeySpec secretKeySpec;
    
    public Server() {
    	byte[] key = "mysecretpassword".getBytes();
        secretKeySpec = new SecretKeySpec(key, "AES");
        
        textField = new JTextField(30);
        textField.addActionListener(this);
        chatArea = new JTextArea(20, 30);
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatArea.setBackground(Color.gray);
        JButton sendButton = new JButton("Send Your msg");
        sendButton.addActionListener(this);
        sendButton.setBackground(Color.black);
        sendButton.setForeground(Color.cyan);
        textField.setBackground(Color.lightGray);
        JPanel panel = new JPanel();
        
Border border = new LineBorder(Color.BLACK, 1);
textField.setBorder(border);

     
        panel.setBackground(Color.lightGray);
        panel.add(textField);
        panel.add(sendButton);
     
        add(panel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);

        
        setUpNetworking();

        
        setSize(600, 400);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Interface Sever");
    }

    private void setUpNetworking() {
        try {
            ServerSocket serverSocket = new ServerSocket(5050); 
            chatArea.append("Server is waiting for client...\n");
            Socket socket = serverSocket.accept(); 
            chatArea.append("Client connected.\n");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
            out = new PrintWriter(socket.getOutputStream(), true); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String encrypt(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encrypted = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Decrypt the message using AES algorithm
    private String decrypt(String encryptedMessage) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decoded = Base64.getDecoder().decode(encryptedMessage);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }
    
    public void actionPerformed(ActionEvent event) {
        String message = textField.getText();
        try {
            String encryptedMessage = encrypt(message);
            out.println(encryptedMessage); // Send the encrypted message
            chatArea.append("Server: " + message + "\n"); 
            textField.setText(""); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String encryptedMessage;
        try {
            while ((encryptedMessage = in.readLine()) != null) { 
                String message = decrypt(encryptedMessage);
                chatArea.append("From Client : " + message + "\n"); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}