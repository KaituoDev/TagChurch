package fun.kaituo.tagchurch.character;

import fun.kaituo.tagchurch.util.Human;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class Alice extends Human {
    public static final String displayName = "爱丽丝";
    public static final String chooseMessage = "去寻觅爱的浪漫吧~☆";
    public static final String color = "§b";

    public Alice(Player p) {
        super(p);
    }
}
