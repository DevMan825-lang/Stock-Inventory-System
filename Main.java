import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// 📦 Product Class
class Product {
    private String name;
    private int quantity;
    private double price;
    private String lastUpdated;

    public Product(String name, int quantity, double price, String lastUpdated) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.lastUpdated = lastUpdated;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public double getTotalValue() {
        return quantity * price;
    }

    @Override
    public String toString() {
        String lowStockWarning = quantity <= 5 ? " ⚠ Low Stock!" : "";
        return String.format("Product: %-15s | Qty: %-5d | Price: ₹%-8.2f | Total: ₹%-8.2f | Last Updated: %s%s",
                name, quantity, price, getTotalValue(), lastUpdated, lowStockWarning);
    }

    public String toCSV() {
        return name + "," + quantity + "," + price + "," + lastUpdated;
    }
}

// 📂 Inventory Class
class Inventory {
    private ArrayList<Product> products = new ArrayList<>();
    private static final String FILE_NAME = "inventory.txt";
    private static final String LOW_STOCK_FILE = "low_stock_report.txt";
    private static final String CSV_FILE = "inventory_report.csv";
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Inventory() {
        loadFromFile();
        autoLowStockAlert();
    }

    // ➕ Add Product
    public void addProduct(String name, int quantity, double price) {
        String dateTime = LocalDateTime.now().format(dtf);
        products.add(new Product(name, quantity, price, dateTime));
        System.out.println("✅ Product added successfully!");
        saveToFile();
    }

    // 🔄 Update Stock
    public void updateStock(String name, int quantity, double price) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(name)) {
                p.setQuantity(quantity);
                p.setPrice(price);
                p.setLastUpdated(LocalDateTime.now().format(dtf));
                System.out.println("🔄 Stock updated successfully!");
                saveToFile();
                return;
            }
        }
        System.out.println("❌ Product not found.");
    }

    // 🗑 Delete Product
    public void deleteProduct(String name) {
        boolean removed = products.removeIf(p -> p.getName().equalsIgnoreCase(name));
        if (removed) {
            System.out.println("🗑 Product deleted successfully!");
            saveToFile();
        } else {
            System.out.println("❌ Product not found.");
        }
    }

    // 🔍 Search Product
    public void searchProduct(String name) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(name)) {
                System.out.println("🔍 Found: " + p);
                return;
            }
        }
        System.out.println("❌ Product not found.");
    }

    // 📜 Full Report
    public void generateReport() {
        if (products.isEmpty()) {
            System.out.println("📭 Inventory is empty.");
            return;
        }
        System.out.println("\n📦 Inventory Report:");
        System.out.println("-------------------------------------------------------------");
        double totalInventoryValue = 0;
        for (Product p : products) {
            System.out.println(p);
            totalInventoryValue += p.getTotalValue();
        }
        System.out.println("-------------------------------------------------------------");
        System.out.printf("💰 Total Inventory Value: ₹%.2f\n", totalInventoryValue);
    }

    // ⚠ Low Stock Report
    public void lowStockReport() {
        boolean found = false;
        System.out.println("\n⚠ Low Stock Report (Qty ≤ 5):");
        System.out.println("-------------------------------------------------------------");
        for (Product p : products) {
            if (p.getQuantity() <= 5) {
                System.out.println(p);
                found = true;
            }
        }
        if (!found) {
            System.out.println("✅ No products are low on stock.");
        }
    }

    // 📄 Save Low Stock Report (Append Mode)
    public void saveLowStockReportToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOW_STOCK_FILE, true))) {
            bw.write("📅 Low Stock Report - " + LocalDateTime.now().format(dtf));
            bw.newLine();
            bw.write("-------------------------------------------------------------");
            bw.newLine();
            boolean found = false;
            for (Product p : products) {
                if (p.getQuantity() <= 5) {
                    bw.write(p.toString());
                    bw.newLine();
                    found = true;
                }
            }
            if (!found) {
                bw.write("✅ No products are low on stock.");
                bw.newLine();
            }
            bw.write("-------------------------------------------------------------");
            bw.newLine();
            bw.newLine();
            System.out.println("📄 Low stock report appended to " + LOW_STOCK_FILE);
        } catch (IOException e) {
            System.out.println("⚠ Error writing low stock file: " + e.getMessage());
        }
    }

    // 📊 Export Full Inventory to CSV
    public void exportToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE))) {
            bw.write("Product Name,Quantity,Price,Last Updated,Total Value");
            bw.newLine();

            double totalInventoryValue = 0;
            for (Product p : products) {
                bw.write(p.getName() + "," + p.getQuantity() + "," + p.getPrice() + "," +
                        p.getLastUpdated() + "," + p.getTotalValue());
                bw.newLine();
                totalInventoryValue += p.getTotalValue();
            }

            bw.newLine();
            bw.write("Total Inventory Value,,,,Rs" + String.format("%.2f", totalInventoryValue));
            bw.newLine();

            System.out.println("📊 Inventory exported to " + CSV_FILE + " with summary row.");
        } catch (IOException e) {
            System.out.println("⚠ Error exporting CSV: " + e.getMessage());
        }
    }

    // 🔠 Sort by Name
    public void sortByName() {
        products.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
        System.out.println("🔠 Products sorted by name.");
        saveToFile();
    }

    // 💰 Sort by Value
    public void sortByValue() {
        products.sort((p1, p2) -> Double.compare(p2.getTotalValue(), p1.getTotalValue()));
        System.out.println("💰 Products sorted by total value.");
        saveToFile();
    }

    // 📂 Save to File
    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Product p : products) {
                bw.write(p.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("⚠ Error saving file: " + e.getMessage());
        }
    }

    // 📥 Load from File
    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists())
            return;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    products.add(new Product(data[0], Integer.parseInt(data[1]),
                            Double.parseDouble(data[2]), data[3]));
                }
            }
        } catch (IOException e) {
            System.out.println("⚠ Error loading file: " + e.getMessage());
        }
    }

    // 🚨 Auto Low Stock Alert on Startup
    private void autoLowStockAlert() {
        System.out.println("\n🚨 Checking for low stock items...");
        lowStockReport();
        saveLowStockReportToFile();
    }
}

// 🖥 Main Class
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Inventory inv = new Inventory();
        int choice;

        do {
            System.out.println("\n====== Stock Inventory Management System ======");
            System.out.println("1. Add Product");
            System.out.println("2. Update Stock");
            System.out.println("3. Delete Product");
            System.out.println("4. Search Product");
            System.out.println("5. Generate Full Report");
            System.out.println("6. Low Stock Report");
            System.out.println("7. Sort by Name");
            System.out.println("8. Sort by Value");
            System.out.println("9. Export Inventory to CSV");
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter product name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter quantity: ");
                    int quantity = sc.nextInt();
                    System.out.print("Enter price: ");
                    double price = sc.nextDouble();
                    inv.addProduct(name, quantity, price);
                    break;
                case 2:
                    System.out.print("Enter product name to update: ");
                    String updateName = sc.nextLine();
                    System.out.print("Enter new quantity: ");
                    int newQuantity = sc.nextInt();
                    System.out.print("Enter new price: ");
                    double newPrice = sc.nextDouble();
                    inv.updateStock(updateName, newQuantity, newPrice);
                    break;
                case 3:
                    System.out.print("Enter product name to delete: ");
                    String deleteName = sc.nextLine();
                    inv.deleteProduct(deleteName);
                    break;
                case 4:
                    System.out.print("Enter product name to search: ");
                    String searchName = sc.nextLine();
                    inv.searchProduct(searchName);
                    break;
                case 5:
                    inv.generateReport();
                    break;
                case 6:
                    inv.lowStockReport();
                    break;
                case 7:
                    inv.sortByName();
                    break;
                case 8:
                    inv.sortByValue();
                    break;
                case 9:
                    inv.exportToCSV();
                    break;
                case 10:
                    System.out.println("👋 Exiting Inventory Management System. Goodbye!");
                    break;
                default:
                    System.out.println("⚠ Invalid choice. Try again.");
            }
        } while (choice != 10);

        sc.close();
    }
}