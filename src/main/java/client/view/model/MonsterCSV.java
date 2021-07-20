package client.view.model;

import lombok.Data;

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
}
