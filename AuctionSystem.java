import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AuctionSystem implements Serializable {
    public String username;
    public AuctionTable auctionTable;

    /**
     * Allows the user to interact with the database by listing open auctions,
     * make bids on open auctions, and create new auctions for different items.
     * In addition, it provides the functionality to load a saved (serialized)
     * AuctionTable or create a new one if a saved table does not exist.
     * 
     * @param args the command line arguments for the program
     */
    public static void main(String[] args) {
        boolean isTerminated = false;
        Scanner inputScanner = new Scanner(System.in);

        System.out.println("Starting...");
        AuctionSystem auctionSystem = new AuctionSystem();

        ObjectInputStream inStream = null;
        try {
            FileInputStream file = new FileInputStream("auction.obj");
            inStream = new ObjectInputStream(file);

            auctionSystem.auctionTable = (AuctionTable) inStream.readObject();
            System.out.println("Loading previous Auction Table...");
        } catch (FileNotFoundException fnfe) {
            System.out.println("No previous auction table detected.");
            System.out.println("Creating new table...");
            auctionSystem.auctionTable = new AuctionTable();
        } catch (IOException ioe) {
            System.out.println("ERROR: Could not open existing auction table!");
            System.out.println("Creating new table...");
            auctionSystem.auctionTable = new AuctionTable();
        } catch (ClassNotFoundException cnfe) {
            System.out.println("ERROR: AuctionTable class does not exist!");
        }

        System.out.print("\nPlease select a username: ");
        auctionSystem.username = inputScanner.nextLine();

        while (!isTerminated) {
            System.out.println();
            printMenuHeader();

            String userInput = inputScanner.nextLine();
            System.out.println();

            if (userInput.toUpperCase().equals("D")) {
                System.out.print("Please enter a URL: ");
                userInput = inputScanner.nextLine();
                try {
                    System.out.println("Loading...");
                    auctionSystem.auctionTable = AuctionTable.buildFromURL(userInput);
                    System.out.println("Auction data loaded successfully!");
                } catch (IllegalArgumentException iae) {
                    System.out.println("ERROR: Invalid URL.");
                }
            } else if (userInput.toUpperCase().equals("A")) {
                System.out.printf("Creating new Auction as %s.\n", auctionSystem.username);
                System.out.print("Please enter an Auction ID: ");
                String userID = inputScanner.nextLine();
                System.out.print("Please enter an Auction time (hours): ");
                int userTime;
                try {
                    userTime = inputScanner.nextInt();
                    inputScanner.nextLine();
                } catch (InputMismatchException ime) {
                    System.out.println("ERROR: Invalid time!");
                    inputScanner.nextLine();
                    continue;
                }
                System.out.print("Please enter some item info: ");
                String userInfo = inputScanner.nextLine();

                try {
                    Auction newUserAuction = new Auction(userTime, 0, userID, auctionSystem.username, "", userInfo);
                    auctionSystem.auctionTable.putAuction(userID, newUserAuction);
                    System.out.printf("Auction %s inserted into table.\n", userID);
                } catch (IllegalArgumentException iae) {
                    System.out.printf("ERROR: Auction %s already exists.\n", userID);
                }

            } else if (userInput.toUpperCase().equals("B")) {
                System.out.print("Please enter an Auction ID: ");
                userInput = inputScanner.nextLine();
                Auction searchedAuction = auctionSystem.auctionTable.getAuction(userInput);
                if (searchedAuction == null) {
                    System.out.printf("ERROR: Auction %s does not exist!\n", userInput);
                } else {
                    if (!searchedAuction.printBidAmount()) {
                        System.out.println("You can no longer bid on this item.");
                    } else {
                        try {
                            System.out.print("What would you like to bid?: ");
                            double bidInput = inputScanner.nextDouble();
                            inputScanner.nextLine();
                            if (searchedAuction.newBid(auctionSystem.username, bidInput)) {
                                System.out.println("Bid accepted.");
                            } else {
                                System.out.println("Bid was not accepted.");
                            }
                        } catch (InputMismatchException ime) {
                            System.out.println("ERROR: Invalid bid!");
                        } catch (ClosedAuctionException cae) {
                            System.out.println("ERROR: Auction is closed!");
                        }
                    }
                }
            } else if (userInput.toUpperCase().equals("I")) {
                System.out.print("Please enter an Auction ID: ");
                userInput = inputScanner.nextLine();
                Auction searchedAuction = auctionSystem.auctionTable.getAuction(userInput);

                if (searchedAuction == null) {
                    System.out.printf("Auction %s does not exist!\n", userInput);
                } else {
                    System.out.printf("    Seller: %s\n", searchedAuction.getSellerName());
                    System.out.printf("    Buyer: %s\n", searchedAuction.getBuyerName());
                    System.out.printf("    Time: %d hours\n", searchedAuction.getTimeRemaining());
                    System.out.printf("    Info: %s\n", searchedAuction.getItemInfo());
                }
            } else if (userInput.toUpperCase().equals("P")) {
                auctionSystem.auctionTable.printTable();
            } else if (userInput.toUpperCase().equals("R")) {
                System.out.println("Removing expired auctions...");
                auctionSystem.auctionTable.removeExpiredAuctions();
                System.out.println("All expired auctions removed.");
            } else if (userInput.toUpperCase().equals("T")) {
                System.out.print("How many hours should pass: ");
                try {
                    int userTime = inputScanner.nextInt();
                    inputScanner.nextLine();
                    auctionSystem.auctionTable.letTimePass(userTime);
                } catch (InputMismatchException ime) {
                    System.out.println("ERROR: Invalid time!");
                    continue;
                } catch (IllegalArgumentException iae) {
                    System.out.println("ERROR: Cannot be a negative time!");
                    continue;
                }
                System.out.println("Time passing...");
                System.out.println("Auction times updated.");
            } else if (userInput.toUpperCase().equals("Q")) {
                isTerminated = true;
            } else {
                System.out.println("ERROR: Invalid input.");
            }
        }

        inputScanner.close();

        try {
            System.out.println("Writing Auction Table to file...");
            FileOutputStream file = new FileOutputStream("auction.obj");
            ObjectOutputStream outStream = new ObjectOutputStream(file);
            outStream.writeObject(auctionSystem.auctionTable);
            System.out.println("Done!");

            outStream.close();
            if (inStream != null) {
                inStream.close();
            }
        } catch (IOException ioe) {
            System.out.println("ERROR: Auction table couldn't be written to file.");
        }

        System.out.println("Goodbye.");
    }

    /**
     * Prints the header for the menu
     */
    public static void printMenuHeader() {
        System.out.println("Menu:\n    (D) - Import Data from URL\n    "
                + "(A) - Create a New Auction\n    (B) - Bid on an Item\n    "
                + "(I) - Get Info on Auction\n    (P) - Print All Auctions\n    "
                + "(R) - Remove Expired Auctions\n    (T) - Let Time Pass\n    "
                + "(Q) - Quit");
    }
}
