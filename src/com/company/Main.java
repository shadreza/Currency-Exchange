package com.company;

//importing for scanner
import java.util.Scanner;

//importing for file
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;

public class Main {

//    scanner initializing defaults
    static Scanner inp = new Scanner(System.in);

//    name of notes and their initial count
    static String[] notes = {"1000", "500", "200", "100", "50", "20", "10", "5", "2", "1"};
    static int[] amountsPerNote = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

//  name of the currencies and their initial value against BDT
    static String[] currencies = {"BDT - Bangladeshi Taka", "USD - US Dollar", "RS - Indian Rupee", "EUR - Euro", "SAR - Saudi Arabian Riyal", "KWD - Kuwaiti Dinar"};
    static double[] currencyRate = {1.00, 84.95, 1.14, 99.38, 22.65, 282.33};

//    function for knowing whether a file exists or not return boolean -> true (file found) or false (file not found)
    static boolean doesFileExist (String fileName) {
        boolean fileExistsOrNot = false;
        try {
            File myObj = new File(fileName);
            if (myObj.createNewFile()) {
                fileExistsOrNot = false;
            } else {
                fileExistsOrNot = true;
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return  fileExistsOrNot;
    }

//    this function calculates the number of notes in which amount is required and the additional 2 indexes are for the total-transactionary-amount and whether-the-transaction-is-possible-or-not (1-> possible / 0-> not possible)
//    returns the array with all the information
    static int[] notesRequired (int amount) {

        int [] countsOfNotes = new int[notes.length + 2];

        int tempAmount = amount;
        int totalCash = 0;

        for(int i = 0 ; i < notes.length ; i++) {

            int note = Integer.parseInt(notes[i]);
            int count = amountsPerNote[i];

            int howMuchNotes = tempAmount / note;

            if(howMuchNotes > count) {
                howMuchNotes = count;
            }
            totalCash = totalCash + (howMuchNotes * note);
            tempAmount = tempAmount - (howMuchNotes * note);
            countsOfNotes[i] = howMuchNotes;

        }

        countsOfNotes[countsOfNotes.length - 2] = totalCash;

        if(tempAmount == 0 && totalCash == amount) {
            countsOfNotes[countsOfNotes.length - 1] = 1;
        } else {
            countsOfNotes[countsOfNotes.length - 1] = 0;
        }

        return countsOfNotes;
    }


//    this updates the balance file meaning keeping the file connected securely
    static void updateAmountsPerNote () {
        try {
            FileWriter myWriter = new FileWriter("Balance.txt");

            for(int i = 0 ; i < notes.length ; i ++) {
                myWriter.write(notes[i] + "\n");
                myWriter.write(String.valueOf(amountsPerNote[i]));
                if(i == notes.length -1) {

                } else {
                    myWriter.write("\n");
                }
            }

            myWriter.close();
            System.out.println("Successfully updated the Balance file.");
        } catch (IOException e) {
            System.out.println("Something went wrong while updating the Balance file.");
            e.printStackTrace();
        }
    }

//    this is a sub part of the goForExchange function. here the paramemter is the index of the currency
//    takes amount as input and goes from there
    static void enterAmountAndGoFromThere (int indexOfCurrency) {

        int input;
        System.out.println("Here you need to enter the amount which must be positive integer. To close this enter -1");
        System.out.println();
        System.out.println("Enter your amount");
        input = inp.nextInt();
        if (input == -1) {
            System.out.println("Exiting transaction");
            return;
        } else if(input < 0) {
            System.out.println("Please input a valid and non negative integer number!");
            enterAmountAndGoFromThere(indexOfCurrency);
        } else {

            int amount = input;
            int balance = (int) getTheBalance();
            amount = (int) (amount * currencyRate[indexOfCurrency]);

            if(amount > balance) {
                System.out.println("Sorry we don't have sufficient balance. Please try a smaller transaction.");
            } else {
                int [] returnNotesArray = notesRequired(amount);
                if( returnNotesArray[returnNotesArray.length - 1 ] == 1 && returnNotesArray[returnNotesArray.length - 2 ] == amount ) {
                    System.out.println("You will be getting total " + amount + " BDT.");
                    System.out.println("The rate is 1 " + currencies[indexOfCurrency] + " = " + currencyRate[indexOfCurrency] + " BDT" );
                    System.out.println("Press 1 to proceed to transaction");
                    input = inp.nextInt();
                    if(input == 1) {

                        System.out.println("Thank you for your transaction.");
                        System.out.println("You are getting");
                        for (int i = 0 ; i < returnNotesArray.length-2 ; i++) {
                            if(returnNotesArray[i] == 0) {
                                continue;
                            } else {
                                System.out.println("-> " + returnNotesArray[i] + " x " + notes[i] + " BDT notes");
                                amountsPerNote[i] = amountsPerNote[i] - returnNotesArray[i];
                            }
                        }
                        System.out.println();
                        System.out.println("In total you got " + amount + " BDT");
                        System.out.println("Thanks again for staying with us! Good day!");

                        updateAmountsPerNote();

                    } else {
                        System.out.println("Closing Transaction");
                        return;
                    }
                } else {
                    System.out.println("Sorry ");
                }

            }

        }
    }

//    this is the function that makes the exchange happen
    static void goForExchange () {

        System.out.println();
        System.out.println("Please select your currency first");

        boolean otherCurrencies = loadCurrencies();

        if(otherCurrencies == true) {
            for( int i = 0 ; i < currencies.length ; i++) {
                System.out.println((i + 1) + ". " + currencies[i]);
            }
            System.out.println((currencies.length + 1) + ". Exit");
            int input = inp.nextInt();

            if (input < 1 || input >currencies.length + 1) {
                System.out.println("Invalid input.");
            } else if ( input == currencies.length + 1) {
                System.out.println("No more exchange");
                return;
            } else {
                enterAmountAndGoFromThere(input-1);
            }
        } else {
            enterAmountAndGoFromThere(0);
        }

    }

//    this function loads the currencies that are on the Currency file
    static boolean loadCurrencies () {

        boolean result = false;

        if (doesFileExist("Currencies.txt") == true) {

            try {
                File myObj = new File("Currencies.txt");
                Scanner myReader = new Scanner(myObj);

                String baseCurrency = "BDT (Bangladeshi Taka)";
                String foreignCurrency = "";
                double foreignCurrencyToBaseCurrency = 0.00;

                if (myReader.hasNextLine() == false) {
                    System.out.println("The file is empty. So can not show any chart for the currencies!");
                    result = false;
                    return  result;
                }

                while (myReader.hasNextLine()) {

                    String data = myReader.nextLine();

                    foreignCurrency = data;
                    foreignCurrencyToBaseCurrency = Double.parseDouble(myReader.nextLine());

                    for(int i=0; i<currencies.length; i++) {
                        String tmpData = foreignCurrency.trim();
                        if( tmpData.compareTo(currencies[i].trim()) == 0) {
                            currencyRate[i] = foreignCurrencyToBaseCurrency;
                        }
                    }

                }
                myReader.close();
                result = true;
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

        } else {
            result = false;
        }

        return result;
    }

//    this function shows the currencies and their rates in respect to BDT
    static void showTheCurrencyChart () {

        System.out.println();

        if(loadCurrencies() == true) {
            System.out.println("The Currency Chart");
            System.out.println();
            for(int i = 0 ; i < currencies.length ; i++) {
                System.out.println("1 " + currencies[i] + " = " + currencyRate[i] + " BDT");
            }
        } else {
            System.out.println("There is some problem with the Currency File. So we can not show the chart now!");
        }

    }

//    this function calculates the balance as per the Balance file. and return the total balance
    static double getTheBalance () {

        double balance = 0.00;

        if (doesFileExist("Balance.txt") == true) {

            try {
                File myObj = new File("Balance.txt");
                Scanner myReader = new Scanner(myObj);

                if (myReader.hasNextLine() == false) {
                    balance = 0.00;
                } else {

                    while (myReader.hasNextLine()) {

                        String dataNote = myReader.nextLine();
                        String noteCount = myReader.nextLine();

                        for(int i = 0 ; i < notes.length ; i++) {
                            if (Integer.parseInt(dataNote) == Integer.parseInt(notes[i])) {
                                amountsPerNote[i] = Integer.parseInt(noteCount);
                            }
                        }

                    }

                    balance = 0.00;

                    for(int i = 0 ; i < notes.length ; i++) {
                        balance += ((Integer.parseInt(notes[i])) * 1.00 * amountsPerNote[i]);
                    }

                    if(balance <= 0) {
                        balance = 0.00;
                    }

                }

                myReader.close();

            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        } else {
            balance = 0.00;
        }

        return balance;

    }

//    this function shows the balance to the console
    static void checkBalance () {

        System.out.println();
        double balance = getTheBalance();
        System.out.println("Remaining Balance : " + balance + " BDT");
        System.out.println();
        System.out.println("The notes and their amounts are ...");
        for (int i = 0 ; i < notes.length ; i++) {
            System.out.println("There are " + amountsPerNote[i] + " number of " + notes[i] + " Taka note.");
        }

    }

//    this function shows the initial menus or options in the console
    static void showInitialOptions() {

        System.out.println();

        System.out.println("Please select your desired option");
        System.out.println();
        System.out.println("1. Go for exchange");
        System.out.println("2. See the currency chart");
        System.out.println("3. Check Balance");
        System.out.println("4. Exit");

        int input = inp.nextInt();
        if (input == 1) {

//            go for exchange
            goForExchange();

        } else if (input == 2) {

//            go for currency chart
            showTheCurrencyChart();

        } else if (input == 3) {

//            go for the balance check
            checkBalance();

        } else if (input == 4) {

//            go for termination
            System.out.println();
            System.out.println("Terminating the program");
            return;

        } else {
//            invalid input
            System.out.println("Invalid Input.");
        }

//        For making the option looping we will call the showInitialOptions recursively
        showInitialOptions();

    }

//    this is the main function and here all starts by the greeting and the option showing
    public static void main(String[] args) {

//        greetings msg
        System.out.println("Welcome to Currency Converter!");

//        options that will be looped
        showInitialOptions();

        System.out.println();
        System.out.println();

    }
}