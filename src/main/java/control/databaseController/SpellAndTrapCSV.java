package control.databaseController;

import lombok.Data;
import model.card.SpellAndTrap;
import model.enums.CardAttributes;
import model.enums.SpellAndTrapIcon;

@Data
public class SpellAndTrapCSV {
    private String Name;
    private String Type;
    private String Icon;
    private String Description;
    private String Status;
    private String CardID;
    private int Price;

    public SpellAndTrap convert() {
        return new SpellAndTrap(Name,
                CardAttributes.valueOf(Database.toEnumCase(Type)),
                Description, Price, CardID,
                SpellAndTrapIcon.valueOf(Database.toEnumCase(Icon))
        );
    }
}
