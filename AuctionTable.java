import java.io.Serializable;
import java.util.HashMap;
import big.data.*;

public class AuctionTable implements Serializable {
    private HashMap<String, Auction> auctionHash = new HashMap<String, Auction>();

    /**
     * Empty constructor for the auction table class
     */
    public AuctionTable() {

    }

    /**
     * Uses the BigData library to construct an AuctionTable from a remote
     * data source
     * Preconditions: URL represents a data source which can be connected
     * to using the BigData library. The data source has proper syntax.
     * 
     * @param URL String representing the URL fo the remote data source.
     * @return The AuctionTable constructed from the remote data source.
     * @throws IllegalArgumentException if the URL isn't valid
     */
    public static AuctionTable buildFromURL(String URL) throws IllegalArgumentException {
        try {
            DataSource ds = DataSource.connectXML(URL).load();

            // Retrieves and processes all values here, couldn't do it in the auction class
            // :/
            String[] sellerList = ds.fetchStringArray("listing/seller_info/seller_name");
            String[] bidStringList = ds.fetchStringArray("listing/auction_info/current_bid");
            double[] bidList = new double[bidStringList.length];
            for (int i = 0; i < bidStringList.length; i++) {
                bidStringList[i] = bidStringList[i].replace("$", "");
                bidStringList[i] = bidStringList[i].replace(",", "");
                bidList[i] = Double.parseDouble(bidStringList[i]);
            }
            String[] timeStringList = ds.fetchStringArray("listing/auction_info/time_left");
            int[] timeList = new int[timeStringList.length];
            for (int i = 0; i < timeStringList.length; i++) {
                String[] splitTimes = timeStringList[i].split(" ");
                for (int j = 1; j < splitTimes.length; j += 2) {
                    if (splitTimes[j].charAt(0) == 'd') {
                        timeList[i] += 24 * Integer.parseInt(splitTimes[j - 1]);
                    } else if (splitTimes[j].charAt(0) == 'h') {
                        timeList[i] += Integer.parseInt(splitTimes[j - 1]);
                    }
                }
            }
            String[] idList = ds.fetchStringArray("listing/auction_info/id_num");
            String[] buyerList = ds.fetchStringArray("listing/auction_info/high_bidder/bidder_name");
            String[] memoryList = ds.fetchStringArray("listing/item_info/memory");
            String[] driveList = ds.fetchStringArray("listing/item_info/hard_drive");
            String[] cpuList = ds.fetchStringArray("listing/item_info/cpu");

            AuctionTable listingTable = new AuctionTable();

            for (int i = 0; i < sellerList.length; i++) {
                String infoString = cpuList[i] + " - " + memoryList[i] + " - " + driveList[i];
                Auction newListing = new Auction(timeList[i], bidList[i], idList[i], sellerList[i], buyerList[i],
                        infoString);
                listingTable.auctionHash.put(idList[i], newListing);
            }

            return listingTable;
        } catch (DataSourceException dse) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Manually posts an auction to the auction table
     * 
     * @param auctionID the id for the new auction
     * @param auction   the new action to be added to the table
     * @throws IllegalArgumentException if the id is already taken
     */
    public void putAuction(String auctionID, Auction auction) throws IllegalArgumentException {
        if (auctionHash.containsKey(auctionID)) {
            throw new IllegalArgumentException();
        }
        auctionHash.put(auctionID, auction);
    }

    /**
     * Retrieves the auction with the input id. Returns null if not found.
     * 
     * @param auctionID the id to check
     * @return the auction or null
     */
    public Auction getAuction(String auctionID) {
        return auctionHash.get(auctionID);
    }

    /**
     * Simulates the passing of time. Decrease the timeRemaining of all
     * Auction objects by the amount specified. The value cannot go below 0.
     * 
     * @param numHours the amount of hours to decrease the auctions by
     * @throws IllegalArgumentException if the number of hours is negative
     */
    public void letTimePass(int numHours) throws IllegalArgumentException {
        if (numHours < 0) {
            throw new IllegalArgumentException();
        }
        for (Auction listing : auctionHash.values()) {
            listing.decrementTimeRemaining(numHours);
        }
    }

    /**
     * Iterates through the auction table and removes any auctions with
     * no time left.
     */
    public void removeExpiredAuctions() {
        String[] removeIDs = new String[auctionHash.size()];
        int counter = 0;

        for (String id : auctionHash.keySet()) {
            if (auctionHash.get(id).getTimeRemaining() == 0) {
                removeIDs[counter] = id;
                counter++;
            }
        }
        for (int i = 0; i < removeIDs.length; i++) {
            auctionHash.remove(removeIDs[i]);
        }
    }

    /**
     * Prints a representation of the auction table in tabular format
     */
    public void printTable() {
        System.out.println(" Auction ID |      Bid   |        Seller         "
                + "|          Buyer          |    Time   |  Item Info");
        System.out.println("==============================================="
                + "========================================================"
                + "============================");
        for (Auction auction : auctionHash.values()) {
            System.out.println(auction.toString());
        }
    }

    /**
     * Retrieves the hashmap used to store the auctions
     * 
     * @return the hashmap holding the auctions
     */
    public HashMap<String, Auction> getAuctionHashMap() {
        return auctionHash;
    }
}
