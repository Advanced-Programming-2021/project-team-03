package control.databaseController;

import lombok.Data;
import model.card.Monster;
import model.enums.CardAttributes;
import model.enums.MonsterModels;
import model.enums.MonsterTypes;

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

    public Monster convert() {
        return new Monster(Name, Level,
                CardAttributes.valueOf(Database.toEnumCase(Attribute)),
                MonsterModels.valueOf(Database.toEnumCase(MonsterType)),
                MonsterTypes.valueOf(Database.toEnumCase(CardType)),
                Atk, Def, Description, Price, CardID
        );
    }
}
