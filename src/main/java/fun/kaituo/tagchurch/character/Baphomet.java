package fun.kaituo.tagchurch.character;

import fun.kaituo.tagchurch.TagChurch;
import fun.kaituo.tagchurch.state.HuntState;
import fun.kaituo.tagchurch.util.Hunter;
import fun.kaituo.tagchurch.util.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@SuppressWarnings("unused")
public class Baphomet extends Hunter {
    public static final String displayName = "巴风特";
    public static final String chooseMessage = "我也没办法啦，希望你们能理解一下~";
    public static final String color = "§8";

    public Baphomet(Player p) {
        super(p);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!e.getDamager().equals(player)) {
            return;
        }
        if (!(e.getEntity() instanceof Player victim)) {
            return;
        }
        if (!HuntState.INST.getHumans().contains(victim)) {
            return;
        }
        PlayerData victimData = TagChurch.inst().idDataMap.get(victim.getUniqueId());
        assert victimData != null;
        if (victimData.getClass().equals(Norden.class)) {
            victim.sendMessage("§a成功免疫最大生命减少效果！");
            return;
        }
        if (victim.getMaxHealth() > 3) {
            victim.sendMessage("§c被巴风特攻击，最大生命值减少！");
            victim.setMaxHealth(victim.getMaxHealth() - 3);
        } else {
            victim.sendMessage("§a生命上限过低，无法继续减少！");
        }
    }
}
