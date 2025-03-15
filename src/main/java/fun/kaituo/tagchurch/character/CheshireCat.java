package fun.kaituo.tagchurch.character;

import fun.kaituo.tagchurch.util.Human;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class CheshireCat extends Human {
    public static final String displayName = "柴郡猫";
    public static final String chooseMessage = "能和你再说上话真是太好了喵~";
    public static final String color = "§d";

    private final int speedDuration;
    private final int speedAmplifier;

    public CheshireCat(Player p) {
        super(p);
        speedDuration = getConfigInt("speed-duration");
        speedAmplifier = getConfigInt("speed-amplifier");
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!e.getEntity().equals(player)) {
            return;
        }
        if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            e.setCancelled(true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, speedDuration, speedAmplifier));
            player.sendMessage("§a获得加速！");
        }
    }
}
