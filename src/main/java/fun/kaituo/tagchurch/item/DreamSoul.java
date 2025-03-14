package fun.kaituo.tagchurch.item;

import fun.kaituo.tagchurch.util.ActiveItem;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class DreamSoul extends ActiveItem {
    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @Override
    public boolean use(Player p) {
        p.sendMessage("§a获得护盾生命值和隐身效果！");
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, -1, 1, false, false));
        return true;
    }
}
