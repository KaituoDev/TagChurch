package fun.kaituo.tagchurch.util;

import fun.kaituo.tagchurch.TagChurch;
import fun.kaituo.tagchurch.state.HuntState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Corpse implements Listener {
    public static final int CORPSE_REMAIN_TICKS = 30 * 20;
    private final PlayerData data;
    private final ArmorStand head;
    private final ArmorStand block;
    private final Set<Integer> taskIds = new HashSet<>();


    public Corpse(PlayerData data) {
        data.save();
        this.data = data;
        Player p = data.getPlayer();
        Location l = p.getLocation().clone();
        Bukkit.getPluginManager().registerEvents(this, TagChurch.inst());

        block = (ArmorStand) p.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
        block.setBasePlate(false);
        block.setInvisible(true);
        block.setVelocity(new Vector(0, -50, 0));

        head = (ArmorStand) p.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
        ItemStack headItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) headItem.getItemMeta();
        skullMeta.setOwningPlayer(p);
        headItem.setItemMeta(skullMeta);
        head.setBasePlate(false);
        head.setSmall(true);
        head.getEquipment().setHelmet(headItem);
        head.setGravity(false);
        head.setCustomName(p.getName());
        head.setCustomNameVisible(true);
        head.setInvisible(true);
        EulerAngle angle = new EulerAngle(Math.PI, 0, 0);
        head.setLeftLegPose(angle);
        head.setRightLegPose(angle);

        taskIds.add(Bukkit.getScheduler().runTaskLater(TagChurch.inst(), () -> {
            block.setGravity(false);
            Location iceLocation = block.getLocation().clone();
            iceLocation.setY(iceLocation.getY() - 1.4);
            block.teleport(iceLocation);
            iceLocation.setY(iceLocation.getY() + 0.75);
            head.teleport(iceLocation);
        }, 5).getTaskId());
        taskIds.add(Bukkit.getScheduler().runTaskLater(TagChurch.inst(), () -> {
            block.getEquipment().setHelmet(new ItemStack(Material.COMPOSTER));
            head.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, -1, 0));
        }, 6).getTaskId());
        taskIds.add(Bukkit.getScheduler().runTaskLater(TagChurch.inst(), () -> {
            for (Player player : TagChurch.inst().getPlayers()) {
                player.sendMessage("§f" + p.getName() + " §c 被逐出了箱庭！");
            }
            destroy();
        }, CORPSE_REMAIN_TICKS).getTaskId());
    }

    public void destroy() {
        if (head.isValid()) {
            head.remove();
            block.remove();
        }
        for (int id : taskIds) {
            Bukkit.getScheduler().cancelTask(id);
        }
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void revive(PlayerInteractAtEntityEvent e) {
        if (HuntState.INST.isEnded()) {
            return;
        }
        if (!e.getRightClicked().equals(block)) {
            return;
        }
        UUID targetId = data.getPlayerId();
        Player target = Bukkit.getPlayer(targetId);
        if (target == null) {
            return;
        }
        data.onRejoin();
        TagChurch.inst().idDataMap.put(targetId, data);
        target.setHealth(20);
        Location reviveLocation = block.getLocation().clone();
        reviveLocation = reviveLocation.add(0, 1.4, 0);
        target.teleport(reviveLocation);
        target.setGameMode(GameMode.ADVENTURE);
        destroy();
    }
}
