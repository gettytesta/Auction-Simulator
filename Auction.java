import java.io.Serializable;

public class Auction implements Serializable {
    private int timeRemaining;
    private double currentBid;
    private String auctionID;
    private String sellerName;
    private String buyerName;
    private String itemInfo;

    /**
     * Empty constructor for the auction class
     */
    public Auction() {
        this(0, 0, "", "", "", "");
    }

    /**
     * Overloaded constructor for the auction class.
     * 
     * @param timeRemaining the time in hours left of the auction
     * @param currentBid    the current bid
     * @param auctionId     the id of the auction
     * @param sellerName    the name of the seller
     * @param buyerName     the name of the buyer
     * @param itemInfo      general info on the item
     */
    public Auction(int timeRemaining, double currentBid, String auctionId, String sellerName, String buyerName,
            String itemInfo) {
        this.timeRemaining = timeRemaining;
        this.currentBid = currentBid;
        this.auctionID = auctionId;
        this.sellerName = sellerName;
        this.buyerName = buyerName;
        this.itemInfo = itemInfo;
    }

    /**
     * Retrieves the remaining time of the auction
     * 
     * @return the remaining time
     */
    public int getTimeRemaining() {
        return timeRemaining;
    }

    /**
     * Retrieves the current bid of the auction
     * 
     * @return the current bid
     */
    public double getCurrentBid() {
        return currentBid;
    }

    /**
     * Retrieves the id of the auction
     * 
     * @return the id
     */
    public String auctionID() {
        return auctionID;
    }

    /**
     * Retrieves the seller name of the auction
     * 
     * @return the seller name
     */
    public String getSellerName() {
        return sellerName;
    }

    /**
     * Retrieves the buyer name of the auction
     * 
     * @return the buyer name
     */
    public String getBuyerName() {
        return buyerName;
    }

    /**
     * Retrieves the info for the auction
     * 
     * @return the item info
     */
    public String getItemInfo() {
        return itemInfo;
    }

    /**
     * Decrements the remaining time of the auction by the input. Sets any negative
     * time to 0.
     * 
     * @param time the time to decrement the remaining time by
     */
    public void decrementTimeRemaining(int time) {
        timeRemaining -= time;
        if (timeRemaining < 0) {
            timeRemaining = 0;
        }
    }

    /**
     * Makes a new bid on the auction. If the bid is higher than the current
     * bid, the new bidder becomes the highest bidder for the auction.
     * 
     * @param bidderName the name of the new bidder
     * @param bidAmt     the amount of the new bid
     * @return true if the bid went through, false otherwise
     * @throws ClosedAuctionException if the time remaining is 0
     */
    public boolean newBid(String bidderName, double bidAmt) throws ClosedAuctionException {
        if (timeRemaining == 0) {
            throw new ClosedAuctionException();
        }
        if (bidAmt > currentBid) {
            buyerName = bidderName;
            currentBid = bidAmt;
            return true;
        }
        return false;
    }

    /**
     * Returns a formatted string representation of the auction
     * 
     * @return the formatted string
     */
    public String toString() {
        String formattedAuction = "";
        formattedAuction += String.format("%11s | $ %8.2f | %-21s |  %-21s  |%4s hours | %.43s", auctionID, currentBid,
                sellerName, buyerName, timeRemaining, itemInfo);
        return formattedAuction;
    }

    /**
     * Prints out the info on whether or not the auction is closed and the
     * highest bidder
     * 
     * @return true if the auciton is open, false otherwise
     */
    public boolean printBidAmount() {
        if (timeRemaining == 0) {
            System.out.printf("Auction %s is CLOSED\n", auctionID);
            System.out.printf("    Current Bid: $ %.2f\n\n", currentBid);
            return false;
        } else {
            System.out.printf("Auction %s is OPEN\n", auctionID);
            System.out.printf("    Current Bid: $ %.2f\n\n", currentBid);
            return true;
        }
    }
}