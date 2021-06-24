package model.card;

import control.databaseController.Database;
import model.enums.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import static model.enums.MonsterTypes.EFFECT;

public class Monster extends Card {
    private final int level;
    private final int baseAttack;
    private final int baseDefence;
    private final MonsterModels model; // monster model is the model of the card for example warrior or spell caster or ..
    private IMonsterEffect monsterEffect; // this interface contains the special monster effect if exists
    private final MonsterTypes type;

    /* two enums below contains the position of the monster card in the game board */
    private AttackingFormat attackingFormat;
    private FaceUpSituation faceUpSituation;

    private final ArrayList<Integer> attackSupplier; // contains all game effects which determine the attacking power of the monster
    private final ArrayList<Integer> defensiveSupplies;

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
        // monster type is the effect type of the card which could be normal, effect or ritual
        if (monsterType.equals(EFFECT))
            monsterEffect = AllMonsterEffects.getInstance().getEffectByID(cardID);
        attackSupplier = new ArrayList<>();
        defensiveSupplies = new ArrayList<>();
    }


    @Override
    public String toString() {
        return "Name: " + this.cardName + "\n" +
                "Level: " + this.level + "\n" +
                "Type: " + this.model + "\n" +
                "ATK: " + this.baseAttack + "\n" +
                "DEF: " + this.baseDefence + "\n" +
                "Description: " + this.description + "\n";
    }

    public IMonsterEffect getMonsterEffect() {
        return monsterEffect;
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

    public ArrayList<Integer> getDefensiveSupplies() {
        return defensiveSupplies;
    }

    public Monster cloneForDeck() {
        Monster clone = new Monster(cardName, level, attribute, model, type, baseAttack, baseDefence, description, price, cardID);
        clone.attackingFormat = this.attackingFormat;
        clone.faceUpSituation = this.faceUpSituation;
        return clone;
    }
}
