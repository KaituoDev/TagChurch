package fun.kaituo.tagchurch.item;

import fun.kaituo.tagchurch.util.ActiveItem;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class HoneyBeer extends ActiveItem {
    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    public boolean use(Player p) {
        p.sendMessage("§e获得缓降！");
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 40, 0));
        return true;
    }
}
