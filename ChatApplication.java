import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ChatApplication {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private List<String> messages;
    
    public static void main(String[] args) {
        // Create and show the chat application
        ChatApplication app = new ChatApplication();
        app.createAndShowGUI();
    }
    
    public void createAndShowGUI() {
        messages = new ArrayList<>();
        messages.add("AI: Welcome to Chatbox. Tell us what you want to eat.");
        
        // Create main frame
        frame = new JFrame("FoodAI Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());
        
        // Create chat area (read-only)
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(Color.WHITE);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        updateChatArea();
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        // Create input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(0, 122, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);
        
        // Add event listeners
        sendButton.addActionListener(new SendButtonListener());
        inputField.addActionListener(new SendButtonListener());
        
        // Show the frame
        frame.setVisible(true);
    }
    
    private void updateChatArea() {
        StringBuilder chatText = new StringBuilder();
        for (String message : messages) {
            chatText.append(message).append("\n\n");
        }
        chatArea.setText(chatText.toString());
        
        // Scroll to bottom
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    private class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String userMessage = inputField.getText().trim();
            if (!userMessage.isEmpty()) {
                // Add user message
                messages.add("You: " + userMessage);
                inputField.setText("");
                
                // Simulate AI response
                simulateAIResponse(userMessage);
                
                // Update chat area
                updateChatArea();
            }
        }
    }
    
    private void simulateAIResponse(String userMessage) {
        String aiResponse;
        
        if (userMessage.toLowerCase().contains("pizza")) {
            aiResponse = "AI: Great! I recommend Mario's Pizza downtown. They have amazing margherita pizza!";
        } else if (userMessage.toLowerCase().contains("burger")) {
            aiResponse = "AI: Burger Palace is fantastic! Try their classic cheeseburger with fries.";
        } else if (userMessage.toLowerCase().contains("sushi")) {
            aiResponse = "AI: Tokyo Sushi has the best rolls in town! Their dragon roll is incredible.";
        } else if (userMessage.toLowerCase().contains("taco")) {
            aiResponse = "AI: Taco Fiesta has authentic Mexican tacos. The al pastor is my favorite!";
        } else {
            aiResponse = "AI: Thanks for your message! I recommend trying Italian Garden - they have great pasta.";
        }
        
        // Add a small delay to simulate thinking
        Timer timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                messages.add(aiResponse);
                updateChatArea();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
}