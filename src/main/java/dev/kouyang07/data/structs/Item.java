package dev.kouyang07.data.structs;

import dev.kouyang07.data.structs.scraper.Wear;
import lombok.Data;

@Data
public class Item {
    private String weapon;
    private String skin;
    private Wear wear;
    private boolean statTrak;

    public Item(String weapon, String skin, Wear wear, boolean statTrak) {
        this.weapon = weapon;
        this.skin = skin;
        this.wear = wear;
        this.statTrak = statTrak;
    }

    public Item(String weapon, String skin, Wear wear) {
        this.weapon = weapon;
        this.skin = skin;
        this.wear = wear;
        this.statTrak = false;
    }

    public Item(String weapon, String skin) {
        this.weapon = weapon;
        this.skin = skin;
        this.wear = Wear.FACTORY_NEW;
        this.statTrak = false;
    }

    public Item(String weapon, String skin, boolean statTrak) {
        this.weapon = weapon;
        this.skin = skin;
        this.wear = Wear.FACTORY_NEW;
        this.statTrak = statTrak;
    }

    public String generateShortName(){
        return weapon + " | " + skin;
    }

    public String generateURL() {
        StringBuilder sb = new StringBuilder();
        sb.append("https://csgoskins.gg/items/");
        sb.append(weapon.replaceAll(" ", "-").toLowerCase()).append("-").append(skin.replaceAll(" ", "-").toLowerCase());
        sb.append("/");
        if(statTrak){
            sb.append("stattrak-").append(wear.getName());
        }else{
            sb.append(wear.getName());
        }
        return sb.toString().replaceAll("---", "-");
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Item objItem){
            return this.weapon.equals(objItem.weapon) && (this.skin.equals(objItem.skin) && this.wear.equals(objItem.wear) && (this.statTrak == objItem.statTrak));
        }
        return false;
    }
}
