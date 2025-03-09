package fun.kaituo.tagchurch.util;

import fun.kaituo.tagchurch.TagChurch;
import fun.kaituo.tagchurch.state.HuntState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Human extends PlayerData{
    public Human(Player p) {
        super(p);
        taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(TagChurch.inst(), this::heartbeat, 20, 20));
    }

    @Override
    public void onRejoin() {
        super.onRejoin();
    }

    private void heartbeat() {
        double minDistance = 9999;
        for (Player hunter : HuntState.INST.getHunters()) {
            double distance = hunter.getLocation().distance(player.getLocation());
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        if (minDistance < 10) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.PLAYERS, 2f, 0f);
            Bukkit.getScheduler().runTaskLater(TagChurch.inst(), () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.PLAYERS, 2f, 0f), 3);
        }
        if (minDistance < 5) {Bukkit.getScheduler().runTaskLater(TagChurch.inst(), () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.PLAYERS, 2f, 0f), 10);
            Bukkit.getScheduler().runTaskLater(TagChurch.inst(), () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.PLAYERS, 2f, 0f), 13);
        }
    }


    @EventHandler
    public void noFriendlyFire(EntityDamageByEntityEvent e) {
        if (!e.getDamager().getUniqueId().equals(playerId)) {
            return;
        }
        PlayerData victimData = TagChurch.inst().idDataMap.get(e.getEntity().getUniqueId());
        if (victimData == null) {
            return;
        }
        if (victimData instanceof Human) {
            e.setCancelled(true);
        }
    }
}
