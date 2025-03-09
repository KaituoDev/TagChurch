package fun.kaituo.tagchurch.character;

import fun.kaituo.tagchurch.util.Human;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class Dodo extends Human {
    public static final String displayName = "渡渡";
    public static final String chooseMessage = "哼哼——！看来你的心已被吾辈俘获，是这么回事吧？";
    public static final String color = "§7";

    public Dodo(Player p) {
        super(p);
    }
}
