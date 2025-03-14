package fun.kaituo.tagchurch.util;

import fun.kaituo.tagchurch.TagChurch;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

import static fun.kaituo.tagchurch.util.Misc.isCharacterHuman;

public abstract class Item implements Listener {
    public enum Rarity {
        COMMON, RARE, LEGENDARY
    }
    protected final ItemStack item;
    protected final Set<Integer> taskIds = new HashSet<>();

    public Item() {
        item = TagChurch.inst().getItem(this.getClass().getSimpleName());
    }

    public abstract Rarity getRarity();

    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, TagChurch.inst());
    }

    public void disable() {
        HandlerList.unregisterAll(this);
        taskIds.forEach(Bukkit.getScheduler()::cancelTask);
        taskIds.clear();
    }

    @EventHandler
    public void preventHunterPickUp(PlayerPickItemEvent e) {
        Player p = e.getPlayer();
        if (!TagChurch.inst().playerIds.contains(p.getUniqueId())) {
            return;
        }
        PlayerData data = TagChurch.inst().idDataMap.get(p.getUniqueId());
        if (data == null) {
            return;
        }
        if (!isCharacterHuman(data.getClass())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void preventHunterClick(InventoryClickEvent e) {
        HumanEntity entity = e.getWhoClicked();
        if (!TagChurch.inst().playerIds.contains(entity.getUniqueId())) {
            return;
        }
        PlayerData data = TagChurch.inst().idDataMap.get(entity.getUniqueId());
        if (data == null) {
            return;
        }
        if (!isCharacterHuman(data.getClass())) {
            e.setCancelled(true);
        }
    }
}
