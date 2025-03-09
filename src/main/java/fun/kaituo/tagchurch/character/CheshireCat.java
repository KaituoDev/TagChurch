package fun.kaituo.tagchurch.character;

import fun.kaituo.tagchurch.util.Human;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class CheshireCat extends Human {
    public static final String displayName = "柴郡猫";
    public static final String chooseMessage = "能和你再说上话真是太好了喵~";
    public static final String color = "§d";

    public CheshireCat(Player p) {
        super(p);
    }
}
