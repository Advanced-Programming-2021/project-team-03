package server.control.databaseController;

import lombok.Data;
import server.model.card.Monster;
import server.model.enums.CardAttributes;
import server.model.enums.MonsterModels;
import server.model.enums.MonsterTypes;

@Data
public class MonsterCSV {
    private String Name;
    private int Level;
    private String Attribute;
    private String MonsterType;
    private String CardType;
    private int Atk;
    private int Def;
    private String Description;
    private int Price;
    private String CardID;

    public static MonsterCSV exportMonsterCSV(Monster monster) {
        MonsterCSV monsterCSV = new MonsterCSV();
        monsterCSV.Name = monster.getCardName();
        monsterCSV.Level = monster.getLevel();
        monsterCSV.Attribute = monster.getAttribute().toString();
        monsterCSV.MonsterType = monster.getModel().toString();
        monsterCSV.CardType = monster.getType().toString();
        monsterCSV.Atk = monster.getBaseAttack();
        monsterCSV.Def = monster.getBaseDefence();
        monsterCSV.Description = monster.getDescription();
        monsterCSV.Price = monster.getPrice();
        monsterCSV.CardID = monster.getCardID();

        return monsterCSV;
    }

    public Monster convert() {
        return new Monster(Name, Level,
                CardAttributes.valueOf(Database.toEnumCase(Attribute)),
                MonsterModels.valueOf(Database.toEnumCase(MonsterType)),
                MonsterTypes.valueOf(Database.toEnumCase(CardType)),
                Atk, Def, Description, Price, CardID
        );
    }
}
