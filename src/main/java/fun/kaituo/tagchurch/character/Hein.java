package fun.kaituo.tagchurch.character;

import fun.kaituo.tagchurch.util.Hunter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class Hein extends Hunter {
    public static final String displayName = "海因";
    public static final String chooseMessage = "这里满溢着大量的黑之魂！太棒了！";
    public static final String color = "§8";

    public Hein(Player p) {
        super(p);
    }

    @Override
    public void applyPotionEffects() {
        super.applyPotionEffects();
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, -1, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 0, false, false));
    }
}
