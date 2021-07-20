package server.control;

import com.google.gson.JsonArray;
import org.json.JSONObject;
import server.control.databaseController.DatabaseException;
import server.model.user.Auction;
import server.model.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuctionController {
    private static AuctionController auctionController;

    private AuctionController() {
        allAuctions = new HashMap<>();
    }

    public static AuctionController getInstance() {
        if (auctionController == null)
            auctionController = new AuctionController();
        return auctionController;
    }

    private final HashMap<Integer, Auction> allAuctions;

    public void addAuction(String owner, String cardName, int startingPrice) throws DatabaseException {
        Auction auction = new Auction(owner, cardName, startingPrice, allAuctions.size() + 1);
        allAuctions.put(auction.getId(), auction);
    }


    public JsonArray getAllAuctions() {
        JsonArray array = new JsonArray();
        for (Auction auction : allAuctions.values()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Id", auction.getId());
            jsonObject.put("Owner", auction.getOwner());
            jsonObject.put("Card Name", auction.getCardName());
            jsonObject.put("Highest Bid", auction.getHighestBid());
            jsonObject.put("Highest Bidder", auction.getHighestBidder());
            jsonObject.put("Starting Price", auction.getStartingPrice());
            jsonObject.put("Starting Time", auction.getStartingTime());
            array.add(String.valueOf(jsonObject));
        }
        return array;
    }

    public boolean containsId(int auctionId) {
        return allAuctions.containsKey(auctionId);
    }

    public boolean bidderDoesNotHaveEnoughMoney(String username, int price) {
        return User.getByUsername(username).getBalance() < price;
    }

    public String bidForAuction(String username, int auctionId, int price) {
        if (canBidForTheAuction(auctionId, price)) {
            try {
                User.getByUsername(username).increaseBalance(-price);
                Auction auction = allAuctions.get(auctionId);
                auction.setHighestBid(price);
                auction.setHighestBidder(username);
                return "your bid placed successful";
            } catch (DatabaseException e) {
                return "an error occurred";
            }

        } else {
            return "you have to bid higher for this card";
        }
    }

    private boolean canBidForTheAuction(int auctionId, int price) {
        return allAuctions.get(auctionId).getHighestBid() < price;
    }

    public void checkAllAuctions() {
        List<Auction> iterator = new ArrayList<>(allAuctions.values());
        for (Auction auction : iterator) {
            if (Math.abs(auction.getStartingTime() - System.currentTimeMillis()) > 600000) {
                allAuctions.remove(auction.getId());
                try {
                    auction.finished();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
