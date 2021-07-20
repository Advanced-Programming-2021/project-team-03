package server.model.user;

import server.control.databaseController.DatabaseException;
import server.model.card.Card;


public class Auction {
    private final int id;
    private final String owner;
    private final String cardName;
    private final int startingPrice;
    private int highestBid;
    private String highestBidder;
    private final long startingTime;

    public Auction(String owner, String cardName, int startingPrice, int id) throws DatabaseException {
        this.id = id;
        this.owner = owner;
        this.cardName = cardName;
        this.startingPrice = startingPrice;
        initializeTheAuction();
        startingTime = System.currentTimeMillis();
    }

    private void initializeTheAuction() throws DatabaseException {
        User.getByUsername(owner).removeCard(Card.getCardByName(cardName));
    }

    public int getId() {
        return id;
    }

    public int getStartingPrice() {
        return startingPrice;
    }

    public int getHighestBid() {
        return highestBid;
    }

    public String getOwner() {
        return owner;
    }

    public String getCardName() {
        return cardName;
    }

    public String getHighestBidder() {
        return highestBidder;
    }

    public void setHighestBid(int highestBid) {
        this.highestBid = highestBid;
    }

    public void setHighestBidder(String highestBidder) {
        this.highestBidder = highestBidder;
    }

    public long getStartingTime() {
        return startingTime;
    }

    public void finished() throws DatabaseException {
        if (highestBidder != null) {
            User.getByUsername(highestBidder).addCard(Card.getCardByName(cardName));
        } else {
            User.getByUsername(owner).addCard(Card.getCardByName(cardName));
        }
    }
}
