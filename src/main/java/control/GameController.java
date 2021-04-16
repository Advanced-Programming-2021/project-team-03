package control;

import model.card.Card;
import model.user.User;

enum Phase {
    DRAW,
    STANDBY,
    FIRST_MAIN,
    BATTLE,
    SECOND_MAIN,
}

public class GameController {
    private Card selectedCard;
    private Phase currentPhase;
    public String makeNewDuel(String firstUsername,User SecondUsername,int roundsCount){
            //TODO
            return null;
    }

    public String selectCard(String cardType,int position){
        //TODO
        return null;
    }

    public String deselectCard(String cardType,int position){
        //TODO
        return null;
    }

    private void changePhase(){

    }

    public String summonCard(){
        //TODO
        return null;
    }

    public String setCard(String type){
        //TODO
        return null;
    }

    public String setCardStandFormat(){
        //TODO
        return null;
    }

    public String attackToRivalCard(int targetCardPosition){
        //TODO
        return null;
    }

    public String DirectAttackToRivalHealth(){
        //TODO
        return null;
    }

    public String activateSpellCardInOnwTurn(){
        //TODO
        return null;
    }

    public String activateSpellCardInRivalTurn(){
        //TODO
        return null;
    }

    public String ritualSummon(){
        //TODO
        return null;
    }

    public String specialSummon(){
        //TODO
        return null;
    }

    public String showGraveyard(String whichGraveyard){
        //TODO
        return null;
    }

    private void rewardPlayers(){

    }

    public String surrender(String username){
        //TODO
        return null;
    }
}
