package fun.kaituo.tagchurch.util;

import fun.kaituo.tagchurch.TagChurch;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Item implements Listener {
    private final ItemStack item;
    public enum Rarity {
        COMMON, RARE, LEGENDARY
    }

    public Item() {
        item = TagChurch.inst().getItem(this.getClass().getSimpleName());
    }

    public abstract Rarity getRarity();

    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, TagChurch.inst());
    }

    public void disable() {
        HandlerList.unregisterAll(this);
    }

    public boolean use(Player p) {
        return false;
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!TagChurch.inst().getPlayers().contains(p)) {
            return;
        }
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }
        ItemStack handItem = p.getInventory().getItemInMainHand().clone();
        if (!handItem.isSimilar(item)) {
            return;
        }
        if (!use(p)) {
            return;
        }
        if (handItem.getAmount() == 1) {
            p.getInventory().setItemInMainHand(null);
        } else {
            handItem.setAmount(handItem.getAmount() - 1);
            p.getInventory().setItemInMainHand(handItem);
        }
    }
}
