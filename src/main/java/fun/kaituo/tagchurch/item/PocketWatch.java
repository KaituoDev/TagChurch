package fun.kaituo.tagchurch.item;

import fun.kaituo.tagchurch.TagChurch;
import fun.kaituo.tagchurch.util.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

import static fun.kaituo.gameutils.util.ItemUtils.containsItem;
import static fun.kaituo.gameutils.util.ItemUtils.removeItem;

@SuppressWarnings("unused")
public class PocketWatch extends Item {
    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (!(e.getEntity() instanceof Player p)) {
            return;
        }
        if (!TagChurch.inst().playerIds.contains(p.getUniqueId())) {
            return;
        }
        if (e.getDamage() < 7) {
            return;
        }
        if (containsItem(p.getInventory(), item)) {
            p.sendMessage("§b怀表抵消了本次伤害！");
            e.setCancelled(true);
            removeItem(p.getInventory(), item);
        }
    }
}
