package server.control.databaseController;

import lombok.Data;
import server.model.card.SpellAndTrap;
import server.model.enums.CardAttributes;
import server.model.enums.SpellAndTrapIcon;

@Data
public class SpellAndTrapCSV {
    private String Name;
    private String Type;
    private String Icon;
    private String Description;
    private String Status;
    private String CardID;
    private int Price;

    public static SpellAndTrapCSV exportSpellAndTrapCSV(SpellAndTrap spellAndTrap) {
        SpellAndTrapCSV spellAndTrapCSV = new SpellAndTrapCSV();

        spellAndTrapCSV.Name = spellAndTrap.getCardName();
        spellAndTrapCSV.Type = spellAndTrap.getAttribute().toString();
        spellAndTrapCSV.Icon = spellAndTrap.getIcon().toString();
        spellAndTrapCSV.Description = spellAndTrap.getDescription();
        spellAndTrapCSV.Price = spellAndTrap.getPrice();
        spellAndTrapCSV.CardID = spellAndTrap.getCardID();
        spellAndTrapCSV.Status = "";

        return spellAndTrapCSV;
    }

    public SpellAndTrap convert() {
        return new SpellAndTrap(Name,
                CardAttributes.valueOf(Database.toEnumCase(Type)),
                Description, Price, CardID,
                SpellAndTrapIcon.valueOf(Database.toEnumCase(Icon))
        );
    }
}
