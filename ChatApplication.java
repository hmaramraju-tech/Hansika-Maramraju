import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ChatApplication {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton favoritesButton;
    private List<String> messages;
    private JLabel charCountLabel;
    private Map<String, Restaurant> favoriteRestaurants;
    
    // Restaurant database
    private Map<String, Restaurant> restaurants;
    
    public static void main(String[] args) {
        ChatApplication app = new ChatApplication();
        app.createAndShowGUI();
    }
    
    public ChatApplication() {
        favoriteRestaurants = new HashMap<>();
        initializeRestaurants();
    }
    
    private void initializeRestaurants() {
        restaurants = new HashMap<>();
        
        // Breakfast places (open early)
        restaurants.put("sunrise cafe", new Restaurant("Sunrise Cafe", "07:00", "15:00", "American", "$$"));
        restaurants.put("bagel world", new Restaurant("Bagel World", "06:00", "14:00", "Breakfast", "$"));
        restaurants.put("pancake house", new Restaurant("Pancake House", "06:30", "16:00", "Breakfast", "$$"));
        
        // Lunch places
        restaurants.put("burger palace", new Restaurant("Burger Palace", "11:00", "22:00", "American", "$$"));
        restaurants.put("pizza corner", new Restaurant("Pizza Corner", "10:00", "23:00", "Italian", "$$"));
        restaurants.put("sandwich spot", new Restaurant("Sandwich Spot", "10:30", "20:00", "Deli", "$"));
        
        // Dinner places
        restaurants.put("italian garden", new Restaurant("Italian Garden", "16:00", "23:00", "Italian", "$$$"));
        restaurants.put("tokyo sushi", new Restaurant("Tokyo Sushi", "17:00", "23:00", "Japanese", "$$$"));
        restaurants.put("steak house", new Restaurant("Prime Steak House", "17:00", "22:00", "Steakhouse", "$$$$"));
        
        // All-day places
        restaurants.put("coffee hub", new Restaurant("Coffee Hub", "05:00", "22:00", "Cafe", "$"));
        restaurants.put("diner 24", new Restaurant("24-Hour Diner", "00:00", "23:59", "American", "$$"));
    }
    
    public void createAndShowGUI() {
        messages = new ArrayList<>();
        messages.add("FoodAI: Welcome! I can recommend restaurants based on time. What meal are you looking for? (breakfast/lunch/dinner)");
        
        frame = new JFrame("FoodAI Restaurant Finder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(new BorderLayout());
        
        // Header
        JLabel headerLabel = new JLabel("FoodAI Restaurant Recommendations", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        frame.add(headerLabel, BorderLayout.NORTH);
        
        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        updateChatArea();
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        inputField = new JTextField();
        
        // Character count label
        charCountLabel = new JLabel("0/200");
        charCountLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        charCountLabel.setForeground(Color.GRAY);
        
        sendButton = new JButton("Send");
        favoritesButton = new JButton("Favorites");
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        buttonPanel.add(sendButton);
        buttonPanel.add(favoritesButton);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        inputPanel.add(bottomPanel, BorderLayout.CENTER);
        inputPanel.add(charCountLabel, BorderLayout.SOUTH);
        frame.add(inputPanel, BorderLayout.SOUTH);
        
        // Event listeners
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        favoritesButton.addActionListener(e -> showFavorites());
        
        // Character count
        inputField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateCount(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateCount(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateCount(); }
            
            private void updateCount() {
                int length = inputField.getText().length();
                charCountLabel.setText(length + "/200");
                if (length > 200) {
                    charCountLabel.setForeground(Color.RED);
                    sendButton.setEnabled(false);
                } else {
                    charCountLabel.setForeground(Color.GRAY);
                    sendButton.setEnabled(true);
                }
            }
        });
        
        frame.setVisible(true);
        inputField.requestFocus();
    }
    
    private void updateChatArea() {
        StringBuilder chatText = new StringBuilder();
        for (String message : messages) {
            chatText.append(message).append("\n\n");
        }
        chatArea.setText(chatText.toString());
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    private void sendMessage() {
        String userMessage = inputField.getText().trim();
        if (!userMessage.isEmpty() && userMessage.length() <= 200) {
            messages.add("You: " + userMessage);
            inputField.setText("");
            charCountLabel.setText("0/200");
            
            processUserMessage(userMessage);
            updateChatArea();
        } else if (userMessage.length() > 200) {
            JOptionPane.showMessageDialog(frame, 
                "Message too long! Please keep under 200 characters.", 
                "Message Limit", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void processUserMessage(String message) {
        String lowerMessage = message.toLowerCase();
        String currentTime = getCurrentTime();
        
        if (lowerMessage.contains("breakfast")) {
            showRestaurantsByMeal("breakfast", currentTime);
        } else if (lowerMessage.contains("lunch")) {
            showRestaurantsByMeal("lunch", currentTime);
        } else if (lowerMessage.contains("dinner")) {
            showRestaurantsByMeal("dinner", currentTime);
        } else if (lowerMessage.contains("favorite") || lowerMessage.contains("save")) {
            saveFavoriteRestaurant(lowerMessage);
        } else if (lowerMessage.contains("time")) {
            messages.add("FoodAI: Current time is: " + currentTime);
        } else {
            messages.add("FoodAI: Please specify if you want breakfast, lunch, or dinner recommendations.");
        }
    }
    
    private void showRestaurantsByMeal(String mealType, String currentTime) {
        List<Restaurant> availableRestaurants = new ArrayList<>();
        
        for (Restaurant restaurant : restaurants.values()) {
            if (isRestaurantOpen(restaurant, currentTime) && 
                isSuitableForMeal(restaurant, mealType)) {
                availableRestaurants.add(restaurant);
            }
        }
        
        if (availableRestaurants.isEmpty()) {
            messages.add("FoodAI: Sorry, no " + mealType + " restaurants are open right now.");
        } else {
            messages.add("FoodAI: Here are open " + mealType + " places:");
            for (Restaurant restaurant : availableRestaurants) {
                String info = String.format("- %s (%s) %s | Open: %s-%s | Type: Save '%s'",
                    restaurant.getName(), restaurant.getPrice(),
                    restaurant.getCuisine(), restaurant.getOpenTime(), 
                    restaurant.getCloseTime(), restaurant.getName().toLowerCase());
                messages.add(info);
            }
        }
    }
    
    private boolean isRestaurantOpen(Restaurant restaurant, String currentTime) {
        return compareTime(currentTime, restaurant.getOpenTime()) >= 0 &&
               compareTime(currentTime, restaurant.getCloseTime()) < 0;
    }
    
    private boolean isSuitableForMeal(Restaurant restaurant, String mealType) {
        String cuisine = restaurant.getCuisine().toLowerCase();
        String name = restaurant.getName().toLowerCase();
        
        switch (mealType) {
            case "breakfast":
                return cuisine.contains("breakfast") || cuisine.contains("cafe") || 
                       name.contains("cafe") || name.contains("bagel") || name.contains("pancake");
            case "lunch":
                return cuisine.contains("american") || cuisine.contains("deli") || 
                       name.contains("burger") || name.contains("pizza") || name.contains("sandwich");
            case "dinner":
                return cuisine.contains("italian") || cuisine.contains("japanese") || 
                       cuisine.contains("steakhouse") || name.contains("garden") || 
                       name.contains("sushi") || name.contains("steak");
            default:
                return true;
        }
    }
    
    private void saveFavoriteRestaurant(String message) {
        for (String restaurantName : restaurants.keySet()) {
            if (message.contains(restaurantName)) {
                Restaurant restaurant = restaurants.get(restaurantName);
                favoriteRestaurants.put(restaurantName, restaurant);
                messages.add("FoodAI: Saved " + restaurant.getName() + " to favorites!");
                return;
            }
        }
        messages.add("FoodAI: Restaurant not found. Please type the exact name.");
    }
    
    private void showFavorites() {
        if (favoriteRestaurants.isEmpty()) {
            messages.add("FoodAI: You have no favorite restaurants yet.");
        } else {
            messages.add("FoodAI: Your favorite restaurants:");
            String currentTime = getCurrentTime();
            for (Restaurant restaurant : favoriteRestaurants.values()) {
                String status = isRestaurantOpen(restaurant, currentTime) ? "OPEN" : "CLOSED";
                messages.add(String.format("- %s: %s (%s-%s) - %s",
                    restaurant.getName(), restaurant.getCuisine(),
                    restaurant.getOpenTime(), restaurant.getCloseTime(), status));
            }
        }
        updateChatArea();
    }
    
    private String getCurrentTime() {
        // Simulate different times for testing
        // return "08:00"; // Breakfast time
        // return "13:00"; // Lunch time
        return "19:00"; // Dinner time
    }
    
    private int compareTime(String time1, String time2) {
        return time1.compareTo(time2);
    }
    
    // Restaurant class
    class Restaurant {
        private String name;
        private String openTime;
        private String closeTime;
        private String cuisine;
        private String price;
        
        public Restaurant(String name, String openTime, String closeTime, String cuisine, String price) {
            this.name = name;
            this.openTime = openTime;
            this.closeTime = closeTime;
            this.cuisine = cuisine;
            this.price = price;
        }
        
        public String getName() { return name; }
        public String getOpenTime() { return openTime; }
        public String getCloseTime() { return closeTime; }
        public String getCuisine() { return cuisine; }
        public String getPrice() { return price; }
    }
}