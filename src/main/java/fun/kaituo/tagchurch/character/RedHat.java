package fun.kaituo.tagchurch.character;

import fun.kaituo.tagchurch.util.Human;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class RedHat extends Human {
    public static final String displayName = "小红帽";
    public static final String chooseMessage = ".......好了,我们出发吧";
    public static final String color = "§c";

    public RedHat(Player p) {
        super(p);
    }
}
