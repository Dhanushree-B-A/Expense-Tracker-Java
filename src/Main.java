import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String URL = "jdbc:mysql://localhost:3306/expense_db";
    private static final String USER = "root";   // change to your MySQL username
    private static final String PASS = "your_password"; // change to your MySQL password

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Add Expense
    private static void addExpense(Scanner sc) {
        try (Connection con = getConnection()) {
            System.out.print("Enter amount: ");
            double amount = sc.nextDouble();
            sc.nextLine(); // consume newline

            System.out.print("Enter category: ");
            String category = sc.nextLine();

            System.out.print("Enter description: ");
            String description = sc.nextLine();

            System.out.print("Enter date (yyyy-mm-dd): ");
            String date = sc.nextLine();

            String query = "INSERT INTO expenses(amount, category, description, expense_date) VALUES (?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setDouble(1, amount);
            ps.setString(2, category);
            ps.setString(3, description);
            ps.setDate(4, Date.valueOf(date));

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Expense added successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // View All Expenses
    private static void viewExpenses() {
        try (Connection con = getConnection()) {
            String query = "SELECT * FROM expenses";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            System.out.println("\n=== All Expenses ===");
            System.out.printf("%-5s %-10s %-15s %-25s %-12s%n", "ID", "Amount", "Category", "Description", "Date");
            System.out.println("---------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d %-10.2f %-15s %-25s %-12s%n",
                        rs.getInt("expense_id"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getDate("expense_date").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Monthly Summary
    private static void monthlySummary(Scanner sc) {
        try (Connection con = getConnection()) {
            System.out.print("Enter month (1-12): ");
            int month = sc.nextInt();
            System.out.print("Enter year: ");
            int year = sc.nextInt();

            String query = "SELECT SUM(amount) AS total FROM expenses WHERE MONTH(expense_date)=? AND YEAR(expense_date)=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, month);
            ps.setInt(2, year);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double total = rs.getDouble("total");
                System.out.println("💰 Total spent in " + month + "/" + year + " = " + total);
            } else {
                System.out.println("No expenses found for this period.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Category Analysis
    private static void categoryAnalysis() {
        try (Connection con = getConnection()) {
            String query = "SELECT category, SUM(amount) AS total FROM expenses GROUP BY category ORDER BY total DESC";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            System.out.println("\n=== Category Analysis ===");
            System.out.printf("%-15s %-10s%n", "Category", "Total");
            System.out.println("-------------------------------");

            while (rs.next()) {
                System.out.printf("%-15s %-10.2f%n",
                        rs.getString("category"),
                        rs.getDouble("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Main Menu
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== Expense Tracker Menu ===");
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. Monthly Summary");
            System.out.println("4. Category Analysis");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1: addExpense(sc); break;
                case 2: viewExpenses(); break;
                case 3: monthlySummary(sc); break;
                case 4: categoryAnalysis(); break;
                case 5: System.out.println("Exiting..."); break;
                default: System.out.println("Invalid choice!");
            }
        } while (choice != 5);

        sc.close();
    }
}
