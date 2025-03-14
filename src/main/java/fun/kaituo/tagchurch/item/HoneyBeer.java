package fun.kaituo.tagchurch.item;

import fun.kaituo.tagchurch.util.Item;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class HoneyBeer extends Item {
    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    public boolean use(Player p) {
        p.sendMessage("你使用了蜂蜜啤酒");
        p.sendMessage("普通道具");
        return true;
    }
}
