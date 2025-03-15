package fun.kaituo.tagchurch.util;

import fun.kaituo.tagchurch.TagChurch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static fun.kaituo.gameutils.util.ItemUtils.removeItem;

public abstract class ActiveItem extends Item{
    public abstract boolean use(Player p);

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!TagChurch.inst().playerIds.contains(p.getUniqueId())) {
            return;
        }
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }
        ItemStack handItem = p.getInventory().getItemInMainHand().clone();
        if (!handItem.isSimilar(item)) {
            return;
        }
        // Prevent player from using items such as experience bottles
        e.setCancelled(true);
        if (p.hasCooldown(item.getType())) {
            return;
        }
        if (use(p)) {
            removeItem(p.getInventory(), item);
            p.setCooldown(item.getType(), 10);
        }
    }
}
