package fun.kaituo.tagchurch.character;

import fun.kaituo.tagchurch.util.Human;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class FakeTurtle extends Human {
    public static final String displayName = "假海龟";
    public static final String chooseMessage = "是呢...只要有你在就没什么可怕的了......";
    public static final String color = "§3";

    public FakeTurtle(Player p) {
        super(p);
    }
}
