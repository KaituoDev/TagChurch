package fun.kaituo.tagchurch.item;

import fun.kaituo.tagchurch.util.Item;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class PocketWatch extends Item {
    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @Override
    public boolean use(Player p) {
        p.sendMessage("你使用了怀表");
        p.sendMessage("稀有道具");
        return true;
    }
}
