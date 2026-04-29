package com.jfinance;

import java.util.Scanner;
import java.util.InputMismatchException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;


public class Main {
    static Scanner scanner = new Scanner(System.in);

    private static Connection connection;
    private static final String url = "jdbc:postgresql:jfinance";
    private static final String user = "fellipe";
    private static final String password = "root";

    public static void main(String[] args) throws SQLException {
        try{
            connection = DriverManager.getConnection(url, user, password);
            createDB();
        } catch (SQLException e) {
            System.out.println("Database setup failed.");
            e.printStackTrace();
            System.exit(0);
        }

        int menuMin = 1;
        int menuMax = 3;
        showMainMenu();


        while (true) {
            int option = validateNumericInput(menuMin, menuMax);
            switch(option) {
                case 1:
                    addTransaction();
                    break;
                case 2:
                    showTransactions();
                    break;
                case 3:
                    System.out.println("Application finished.");
                    System.exit(0);
            }
            option = 0;
            showMainMenu();
        }
    }

    static void showMainMenu() {
        String mainMenu =
                  "====== JFinance ======="
                + "\n Type an option"
                + "\n 1. Add transaction"
                + "\n 2. Read transactions"
                + "\n 3. Exit";
        System.out.println(mainMenu);
    }

    static void addTransaction() {
        System.out.println("Type the name of the transaction:");
        String transactionName = scanner.nextLine();
        System.out.println("Type the value of the transaction:");
        String transactionValue = scanner.nextLine();
        System.out.println("This transaction is a gain (1) or a expense(2)?");
        String transactionType = String.valueOf(validateNumericInput(1, 2));
        if (transactionType.equals("1")) {
            transactionType = "gain";
        } else {
            transactionType = "expense";
        }
        System.out.println("Type the date of the transaction (yyyy-mm-dd):");
        String transactionDate = scanner.nextLine();

        String insertTransaction = "INSERT INTO Transactions(name, amount, type, transactionDate) VALUES (?, CAST(? AS MONEY), ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(insertTransaction)) {
            connection.setAutoCommit(false);
            statement.setString(1, transactionName);
            statement.setObject(2, transactionValue);
            statement.setString(3, transactionType);
            statement.setDate(4, Date.valueOf(transactionDate));
            statement.executeUpdate();
            System.out.println("Transaction added successfully.");
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Insert failed. Transaction is being rolled back.");
            connection.rollback();
            e.printStackTrace();
        }
    }

    static void showTransactions() {
        try(Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Transactions ORDER BY transactionDate;");

            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                Double amount = resultSet.getDouble("amount");
                String type = resultSet.getString("type");
                Date date = resultSet.getDate("transactionDate");
                System.out.println("-------------------------------------------");
                System.out.printf(" %d | %s | %.2f | %s | %s\n", id, date.toString(), amount, type, name);
            }
        } catch (SQLException e) {
            System.err.println("Query failed.");
            System.err.println(e.getMessage());
        }
    }

    private static int validateNumericInput(int min, int max) {
        int option = 0;

        while(option < min || option > max) {
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (InputMismatchException e) {
                System.out.printf("Invalid option. It must be a number from %d to %d.\n", min, max);
            }
        }
        return option;
    }

    public static void createDB() throws SQLException{
        String dbCreationString =
                "CREATE TABLE IF NOT EXISTS Transactions(id SERIAL PRIMARY KEY, " +
                        "name VARCHAR(30) NOT NULL, " +
                        "amount MONEY NOT NULL, " +
                        "type CHAR(7) NOT NULL, " +
                        "transactionDate DATE);";

        try(Statement statement = connection.createStatement()) {
            statement.execute(dbCreationString);
        }
    }
}