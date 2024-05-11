package bluejayDB;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

class Garment {
    private String name;
    private double priceUSD;
    private String[] sizes;
    private int quantity; // Added to track inventory
    private String checkpointStatus; // Added to track checkpoint status

    public Garment(String name, double priceUSD, String[] sizes) {
        this.name = name;
        this.priceUSD = priceUSD;
        this.sizes = sizes;
        this.quantity = 10; // Default quantity for each garment
        this.checkpointStatus = ""; // Default checkpoint status is empty
    }

    public String getName() {
        return name;
    }

    public double getPriceUSD() {
        return priceUSD;
    }

    public String[] getSizes() {
        return sizes;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPriceUSD(double priceUSD) {
        this.priceUSD = priceUSD;
    }

    public String getCheckpointStatus() {
        return checkpointStatus;
    }

    public void setCheckpointStatus(String checkpointStatus) {
        this.checkpointStatus = checkpointStatus;
    }
}

class OrderItem {
    private Garment garment;
    private String size;
    private int quantity;
    private double totalPrice;

    public OrderItem(Garment garment, String size, int quantity, double totalPrice) {
        this.garment = garment;
        this.size = size;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public Garment getGarment() {
        return garment;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

public class GarmentsProductionSystem extends JFrame {
    private static final String CUSTOMER_USERNAME = "customer";
    private static final String CUSTOMER_PASSWORD = "password";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "adminpassword";
    private static final double EXCHANGE_RATE = 50.0; // Exchange rate: 1 USD = 50 PHP

    private static final Object[][] INITIAL_GARMENTS = {
            { "Tshirt", 7.0, new String[] { "S", "M", "L" } },
            { "Skirt", 13.0, new String[] { "S", "M", "L" } },
            { "Jacket", 35.0, new String[] { "S", "M", "L" } },
            { "Pants", 20.0, new String[] { "S", "M", "L" } },
            { "Dress", 45.0, new String[] { "S", "M", "L" } },
            { "Jeans", 35.0, new String[] { "S", "M", "L" } },
            { "Blouse", 20.0, new String[] { "S", "M", "L" } },
            { "Coat", 11.0, new String[] { "S", "M", "L" } },
            { "Suit", 20.0, new String[] { "S", "M", "L" } },
            { "Gold Rmore", 100.0, new String[] { "S", "M", "L" } }
    };

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea cartTextArea;
    private JLabel priceCounterLabel;
    private double totalPrice;

    private JPanel loginPanel;
    private JPanel customerPanel;
    private JPanel adminPanel;
    private JPanel adminControlPanel;

    private final List<Garment> garments;
    private final List<Garment> inventory;
    private final List<OrderItem> orderItems;

    public GarmentsProductionSystem() {
        setTitle("Garments Production System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        garments = new ArrayList<>();
        inventory = new ArrayList<>();
        orderItems = new ArrayList<>();
        for (Object[] garmentInfo : INITIAL_GARMENTS) {
            String name = (String) garmentInfo[0];
            double price = (double) garmentInfo[1];
            String[] sizes = (String[]) garmentInfo[2];
            garments.add(new Garment(name, price, sizes));
            inventory.add(new Garment(name, price, sizes));
        }

        createLoginWindow();
    }

    private void createLoginWindow() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel());
        loginPanel.add(loginButton);

        add(loginPanel, BorderLayout.NORTH);

        loginButton.addActionListener(this::login);
    }

    private void login(ActionEvent e) {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter both username and password.");
            return;
        }

        if (username.equals(CUSTOMER_USERNAME) && password.equals(CUSTOMER_PASSWORD)) {
            JOptionPane.showMessageDialog(null, "Login successful as Customer. You are now logged in.");
            createCustomerWindow();
        } else if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            JOptionPane.showMessageDialog(null, "Login successful as Admin. You are now logged in.");
            createAdminWindow();
        } else {
            JOptionPane.showMessageDialog(null, "Invalid username or password. Access denied.");
        }
    }

    private void createCustomerWindow() {
        customerPanel = new JPanel();
        customerPanel.setLayout(new BorderLayout());

        cartTextArea = new JTextArea();
        cartTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(cartTextArea);
        customerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(garments.size() + 1, 1));
        for (Garment garment : garments) {
            JButton orderButton = new JButton("Order " + garment.getName() + " (Php"
                    + String.format("%.2f", garment.getPriceUSD() * EXCHANGE_RATE) + ")");
            buttonPanel.add(orderButton);
            orderButton.addActionListener(ev -> {
                String size = (String) JOptionPane.showInputDialog(null, "Select size for " + garment.getName(),
                        "Size Selection", JOptionPane.QUESTION_MESSAGE, null, garment.getSizes(),
                        garment.getSizes()[0]);
                if (size != null) {
                    JPanel quantityPanel = new JPanel(new GridLayout(2, 1));
                    JLabel quantityLabel = new JLabel("Available Quantity: " + garment.getQuantity());
                    quantityPanel.add(quantityLabel);

                    // Ensure the spinner respects the available quantity
                    int availableQuantity = garment.getQuantity();
                    int spinnerMin = availableQuantity > 0 ? 1 : 0;
                    int spinnerValue = availableQuantity > 0 ? 1 : 0;
                    int spinnerMax = availableQuantity > 0 ? availableQuantity : 0;

                    SpinnerNumberModel model = new SpinnerNumberModel(spinnerValue, spinnerMin, spinnerMax, 1);
                    JSpinner quantitySpinner = new JSpinner(model);
                    quantityPanel.add(quantitySpinner);

                    int result = JOptionPane.showConfirmDialog(null, quantityPanel,
                            "Enter quantity for " + garment.getName() + " (Size: " + size + "):",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
                        int orderedQuantity = (Integer) quantitySpinner.getValue();

                        double totalPriceForItem = orderedQuantity * garment.getPriceUSD() * EXCHANGE_RATE;
                        JOptionPane.showMessageDialog(null, "You ordered " + orderedQuantity + " " + garment.getName()
                                + " (Size: " + size + "). Price: Php" + String.format("%.2f", totalPriceForItem));
                        orderItems.add(new OrderItem(garment, size, orderedQuantity, totalPriceForItem));
                        updateCartTextArea();
                        totalPrice += totalPriceForItem;
                        updatePriceCounter();
                        garment.setQuantity(garment.getQuantity() - orderedQuantity); // Update inventory
                    }
                }
            });
        }

        JButton viewCartButton = new JButton("View Cart");
        viewCartButton.addActionListener(ev -> viewCart());
        buttonPanel.add(viewCartButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(ev -> {
            loginPanel.setVisible(true);
            customerPanel.setVisible(false);
            cartTextArea.setText("");
            totalPrice = 0.0;
            if (priceCounterLabel != null) {
                priceCounterLabel.setText("Total Price: Php0.00");
            }
        });
        buttonPanel.add(logoutButton);

        priceCounterLabel = new JLabel("Total Price: Php0.00");
        customerPanel.add(priceCounterLabel, BorderLayout.SOUTH);

        customerPanel.add(buttonPanel, BorderLayout.EAST);

        add(customerPanel, BorderLayout.CENTER);
        loginPanel.setVisible(false);
        customerPanel.setVisible(true);
    }

    private void removeItemFromCart(int index) {
        if (index >= 0 && index < orderItems.size()) {
            OrderItem item = orderItems.get(index);
            Garment garment = item.getGarment();
            int quantityToAddBack = item.getQuantity();

            // Update the garment quantity in the inventory
            garment.setQuantity(garment.getQuantity() + quantityToAddBack);

            // Remove the item from the cart
            orderItems.remove(index);
            updateCartTextArea(); // Update the text area after removing the item
            totalPrice -= item.getTotalPrice(); // Adjust the total price
            updatePriceCounter(); // Update the price counter display
            JOptionPane.showMessageDialog(null, "Item removed successfully.");
        } else {
            JOptionPane.showMessageDialog(null, "Invalid item index.");
        }
    }

    private void viewCart() {
        if (!orderItems.isEmpty()) {
            StringBuilder cartInfo = new StringBuilder();
            for (int i = 0; i < orderItems.size(); i++) {
                OrderItem item = orderItems.get(i);
                cartInfo.append(i + 1).append(". ").append(item.getGarment().getName())
                        .append(" (Size: ").append(item.getSize())
                        .append(", Quantity: ").append(item.getQuantity())
                        .append(", Total Price: Php").append(String.format("%.2f", item.getTotalPrice()))
                        .append(")\n");
            }
            JOptionPane.showMessageDialog(null, cartInfo.toString(), "Cart", JOptionPane.INFORMATION_MESSAGE);

            String input = JOptionPane.showInputDialog("Enter the number of the item to remove, or 'cancel' to exit:");
            if (input == null || input.equalsIgnoreCase("cancel")) {
                return;
            }
            try {
                int itemNumber = Integer.parseInt(input) - 1; // Convert to zero-based index
                removeItemFromCart(itemNumber);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Your cart is empty.", "Cart", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateCartTextArea() {
        cartTextArea.setText(""); // Clear the existing text
        for (OrderItem item : orderItems) {
            cartTextArea.append(item.getGarment().getName() + " (Size: " + item.getSize() + "). Price: Php" +
                    String.format("%.2f", item.getTotalPrice()) + " (Quantity: " + item.getQuantity() + ")\n");
        }
    }

    private void updatePriceCounter() {
        if (priceCounterLabel != null) {
            priceCounterLabel.setText("Total Price: Php" + String.format("%.2f", totalPrice));
        }
    }

    private void createAdminWindow() {
        adminPanel = new JPanel();
        adminPanel.setLayout(new BorderLayout());

        adminControlPanel = new JPanel();
        adminControlPanel.setLayout(new GridLayout(5, 1));

        JButton addButton = new JButton("Add Product");
        JButton removeButton = new JButton("Remove Product");
        JButton updateButton = new JButton("Update Product");
        JButton checkpointButton = new JButton("Mark Checkpoint");
        JButton viewAuditTrailButton = new JButton("View Audit Trail");
        JButton logoutButton = new JButton("Logout");

        addButton.addActionListener(ev -> addProduct());
        removeButton.addActionListener(ev -> removeProduct());
        updateButton.addActionListener(ev -> updateProduct());
        checkpointButton.addActionListener(ev -> markCheckpoint());
        viewAuditTrailButton.addActionListener(ev -> viewAuditTrail());
        logoutButton.addActionListener(ev -> {
            loginPanel.setVisible(true);
            adminPanel.setVisible(false);
        });

        adminControlPanel.add(addButton);
        adminControlPanel.add(removeButton);
        adminControlPanel.add(updateButton);
        adminControlPanel.add(checkpointButton);
        adminControlPanel.add(viewAuditTrailButton);
        adminControlPanel.add(logoutButton);

        adminPanel.add(adminControlPanel, BorderLayout.CENTER);

        add(adminPanel, BorderLayout.CENTER);
        loginPanel.setVisible(false);
        adminPanel.setVisible(true);
    }

    private void addProduct() {
        String productName = JOptionPane.showInputDialog("Enter product name:");
        if (productName == null || productName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Product name cannot be empty.");
            return;
        }
        double priceUSD;
        try {
            priceUSD = Double.parseDouble(JOptionPane.showInputDialog("Enter product price (USD):"));
            if (priceUSD <= 0) {
                JOptionPane.showMessageDialog(null, "Invalid price. Please enter a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid price format. Please enter a valid number.");
            return;
        }
        inventory.add(new Garment(productName, priceUSD, new String[] { "S", "M", "L" }));
        JOptionPane.showMessageDialog(null, "Product added successfully.");

        // Update garments list in customer panel
        garments.add(new Garment(productName, priceUSD, new String[] { "S", "M", "L" }));
    }

    private void removeProduct() {
        String productName = JOptionPane.showInputDialog("Enter name of the product to remove:");
        if (productName == null || productName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Product name cannot be empty.");
            return;
        }
        boolean found = false;
        Iterator<Garment> iterator = inventory.iterator();
        while (iterator.hasNext()) {
            Garment garment = iterator.next();
            if (garment.getName().equals(productName)) {
                iterator.remove(); // Safely remove the garment from inventory
                found = true;
                break;
            }
        }
        if (!found) {
            JOptionPane.showMessageDialog(null, "Product not found.");
            return;
        }

        // Update garments list in customer panel
        iterator = garments.iterator();
        while (iterator.hasNext()) {
            Garment garment = iterator.next();
            if (garment.getName().equals(productName)) {
                iterator.remove(); // Safely remove the garment from garments list
            }
        }

        // Update the cart text area to reflect the removal
        updateCartTextArea();
        JOptionPane.showMessageDialog(null, "Product removed successfully.");
    }

    private void updateProduct() {
        String productName = JOptionPane.showInputDialog("Enter name of the product to update:");
        if (productName == null || productName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Product name cannot be empty.");
            return;
        }
        Garment foundGarment = null;
        for (Garment garment : inventory) {
            if (garment.getName().equals(productName)) {
                foundGarment = garment;
                break;
            }
        }
        if (foundGarment == null) {
            JOptionPane.showMessageDialog(null, "Product not found.");
            return;
        }
        double newPriceUSD;
        try {
            newPriceUSD = Double
                    .parseDouble(JOptionPane.showInputDialog("Enter new price (USD) for " + productName + ":"));
            if (newPriceUSD <= 0) {
                JOptionPane.showMessageDialog(null, "Invalid price. Please enter a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid price format. Please enter a valid number.");
            return;
        }
        foundGarment.setPriceUSD(newPriceUSD);
        JOptionPane.showMessageDialog(null, "Product updated successfully.");

        // Update garments list in customer panel
        for (Garment garment : garments) {
            if (garment.getName().equals(productName)) {
                garment.setPriceUSD(newPriceUSD);
                break;
            }
        }
    }

    private void markCheckpoint() {
        String productName = JOptionPane.showInputDialog("Enter name of the garment to mark checkpoint:");
        if (productName == null || productName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Product name cannot be empty.");
            return;
        }
        boolean found = false;
        for (Garment garment : inventory) {
            if (garment.getName().equals(productName)) {
                garment.setCheckpointStatus("Checked");
                JOptionPane.showMessageDialog(null, "Checkpoint marked for " + productName);
                found = true;
                break;
            }
        }
        if (!found) {
            JOptionPane.showMessageDialog(null, "Product not found.");
        }
    }

    private void viewAuditTrail() {
        StringBuilder auditTrail = new StringBuilder();
        // Iterate through the order items and append audit information
        for (OrderItem item : orderItems) {
            auditTrail.append("Ordered: ").append(item.getGarment().getName())
                    .append(", Size: ").append(item.getSize())
                    .append(", Quantity: ").append(item.getQuantity())
                    .append(", Total Price: Php").append(String.format("%.2f", item.getTotalPrice())).append("\n");
        }
        // Show the audit trail in a dialog
        JOptionPane.showMessageDialog(null, auditTrail.toString(), "Audit Trail", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GarmentsProductionSystem system = new GarmentsProductionSystem();
            system.setVisible(true);
        });
    }
}