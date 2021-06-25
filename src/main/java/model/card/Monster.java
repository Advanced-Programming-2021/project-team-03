package model.card;

import control.databaseController.Database;
import model.enums.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Monster extends Card {
    private final int level;
    private int baseAttack;
    private final int baseDefence;
    private final MonsterModels model; // monster model is the model of the card for example warrior or spell caster or ..
    private final MonsterTypes type;

    /* two enums below contains the position of the monster card in the game board */
    private AttackingFormat attackingFormat;
    private FaceUpSituation faceUpSituation;

    private ArrayList<Integer> attackSupplier; // contains all game effects which determine the attacking power of the monster
    private ArrayList<Integer> defensiveSupplies;

    //equip card
    private SpellAndTrap equipment;

    private static HashMap<String, Monster> allMonsters;

    public static void initialize() {
        try {
            allMonsters = Database.updateMonsters();
        } catch (IOException e) {
            System.out.println("Couldn't find monsters database files");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Monster(String cardName, int level, CardAttributes attribute, MonsterModels model, MonsterTypes monsterType,
                   int baseAttack, int baseDefence, String description, int price, String cardID) {
        super(cardName, cardID, description, price, attribute);
        this.level = level;
        this.baseAttack = baseAttack;
        this.baseDefence = baseDefence;
        this.model = model;
        this.type = monsterType;
        attackSupplier = new ArrayList<>();
        defensiveSupplies = new ArrayList<>();
    }


    @Override
    public String toString() {
        return "Name: " + this.cardName + "\n" +
                "Level: " + this.level + "\n" +
                "Type: " + this.model + "\n" +
                "ATK: " + this.baseAttack + " + " + (getAttackingPower() - baseAttack) + "\n" +
                "DEF: " + this.baseDefence + " + " + (getDefensivePower() - baseDefence) + "\n" +
                "Description: " + this.description + "\n";
    }

    public AttackingFormat getAttackingFormat() {
        return attackingFormat;
    }

    public void setAttackingFormat(AttackingFormat attackingFormat) {
        this.attackingFormat = attackingFormat;
    }

    public FaceUpSituation getFaceUpSituation() {
        return faceUpSituation;
    }

    public void setFaceUpSituation(FaceUpSituation faceUpSituation) {
        this.faceUpSituation = faceUpSituation;
    }

    public void addToAttackSupplier(int power) {
        attackSupplier.add(power);
    }

    public void addToDefensiveSupply(int power) {
        defensiveSupplies.add(power);
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getBaseDefence() {
        return baseDefence;
    }

    public int getAttackingPower() {
        int power = baseAttack;
        power += attackSupplier.stream().mapToInt(p -> p).sum();
        return power;
    }

    public int getDefensivePower() {
        int power = baseDefence;
        power += defensiveSupplies.stream().mapToInt(p -> p).sum();
        return power;
    }

    public int getLevel() {
        return this.level;
    }

    public static Monster getMonsterByName(String name) {
        return allMonsters.get(name);
    }

    public static HashMap<String, Monster> getAllMonsters() {
        return allMonsters;
    }

    public MonsterTypes getType() {
        return type;
    }

    public void setAttackSupplier(ArrayList<Integer> attackSupplier) {
        this.attackSupplier = attackSupplier;
    }

    public void setDefensiveSupplies(ArrayList<Integer> defensiveSupplies) {
        this.defensiveSupplies = defensiveSupplies;
    }

    public MonsterModels getModel() {
        return model;
    }

    public Monster cloneForDeck() {
        Monster clone = new Monster(cardName, level, attribute, model, type, baseAttack, baseDefence, description, price, cardID);
        clone.attackingFormat = this.attackingFormat;
        clone.faceUpSituation = this.faceUpSituation;
        return clone;
    }

    public SpellAndTrap getEquipment() {
        return equipment;
    }

    public void setEquipment(SpellAndTrap equipment) {
        this.equipment = equipment;
    }

    public void setBaseAttack(int baseAttack) {
        this.baseAttack = baseAttack;
    }
}
