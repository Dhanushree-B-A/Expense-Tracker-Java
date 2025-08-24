import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class ExpenseTracker {

    // ---------- Add Expense ----------
    public static void addExpense(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter Category: ");
            String category = sc.nextLine();

            System.out.print("Enter Amount: ");
            double amount = sc.nextDouble();
            sc.nextLine();

            System.out.print("Enter Date (yyyy-mm-dd): ");
            String date = sc.nextLine();

            System.out.print("Enter Description: ");
            String desc = sc.nextLine();

            String query = "INSERT INTO expenses (category, amount, expense_date, description) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, category);
            pstmt.setDouble(2, amount);
            pstmt.setDate(3, Date.valueOf(date));
            pstmt.setString(4, desc);
            pstmt.executeUpdate();

            System.out.println("✅ Expense added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- View Expenses ----------
    public static void viewExpenses(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM expenses");

            System.out.println("\nID | Category | Amount | Date | Description");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " +
                        rs.getString("category") + " | " +
                        rs.getDouble("amount") + " | " +
                        rs.getDate("expense_date") + " | " +
                        rs.getString("description"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- Update Expense ----------
    public static void updateExpense(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter Expense ID to Update: ");
            int id = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter New Category: ");
            String category = sc.nextLine();

            System.out.print("Enter New Amount: ");
            double amount = sc.nextDouble();
            sc.nextLine();

            System.out.print("Enter New Date (yyyy-mm-dd): ");
            String date = sc.nextLine();

            System.out.print("Enter New Description: ");
            String desc = sc.nextLine();

            String query = "UPDATE expenses SET category=?, amount=?, expense_date=?, description=? WHERE id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, category);
            pstmt.setDouble(2, amount);
            pstmt.setDate(3, Date.valueOf(date));
            pstmt.setString(4, desc);
            pstmt.setInt(5, id);

            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("✅ Expense updated successfully!");
            else System.out.println("❌ Expense not found!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- Delete Expense ----------
    public static void deleteExpense(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter Expense ID to Delete: ");
            int id = sc.nextInt();
            sc.nextLine();

            String query = "DELETE FROM expenses WHERE id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("✅ Expense deleted successfully!");
            else System.out.println("❌ Expense not found!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- Category Summary ----------
    public static void categorySummary(Connection conn) {
        try {
            String query = "SELECT category, SUM(amount) AS total FROM expenses GROUP BY category";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\nCategory | Total Amount");
            while (rs.next()) {
                System.out.println(rs.getString("category") + " | " + rs.getDouble("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- Export to CSV ----------
    public static void exportToCSV(Connection conn) {
        try {
            FileWriter writer = new FileWriter("expenses.csv");
            writer.write("ID,Category,Amount,Expense_Date,Description\n");

            String query = "SELECT * FROM expenses";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                writer.write(rs.getInt("id") + "," +
                        rs.getString("category") + "," +
                        rs.getDouble("amount") + "," +
                        rs.getDate("expense_date") + "," +
                        rs.getString("description") + "\n");
            }

            writer.close();
            System.out.println("✅ Expenses exported to expenses.csv successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- Import from CSV ----------
    public static void importFromCSV(Connection conn) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("expenses.csv"));
            String line;
            br.readLine(); // skip header

            String query = "INSERT INTO expenses (category, amount, expense_date, description) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                pstmt.setString(1, values[1]);             // category
                pstmt.setDouble(2, Double.parseDouble(values[2])); // amount
                pstmt.setDate(3, Date.valueOf(values[3])); // expense_date
                pstmt.setString(4, values[4]);             // description

                pstmt.executeUpdate();
            }

            br.close();
            System.out.println("✅ Expenses imported successfully from CSV!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- Main Program ----------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/expense_db",
                    "root", "Dhanumysql@123"
            );

            while (true) {
                System.out.println("\n===== Expense Tracker =====");
                System.out.println("1. Add Expense");
                System.out.println("2. View Expenses");
                System.out.println("3. Update Expense");
                System.out.println("4. Delete Expense");
                System.out.println("5. Category Summary");
                System.out.println("6. Export to CSV");
                System.out.println("7. Import from CSV");
                System.out.println("8. Exit");
                System.out.print("Enter choice: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> addExpense(conn, sc);
                    case 2 -> viewExpenses(conn);
                    case 3 -> updateExpense(conn, sc);
                    case 4 -> deleteExpense(conn, sc);
                    case 5 -> categorySummary(conn);
                    case 6 -> exportToCSV(conn);
                    case 7 -> importFromCSV(conn);
                    case 8 -> {
                        System.out.println("👋 Exiting...");
                        conn.close();
                        return;
                    }
                    default -> System.out.println("❌ Invalid choice!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}