package fun.kaituo.tagchurch.util;

import fun.kaituo.gameutils.util.GameInventory;
import fun.kaituo.tagchurch.TagChurch;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public abstract class PlayerData implements Listener {
    @Getter
    protected final UUID playerId;
    @Getter
    protected Player player;

    protected Location location;
    protected final Collection<PotionEffect> potionEffects = new ArrayList<>();
    protected double health;
    protected double maxHealth;
    protected GameInventory inventory;
    protected long maxCoolDownTicks;
    protected long coolDownTicks;

    protected final Set<Integer> taskIds = new HashSet<>();

    public PlayerData(Player player) {
        playerId = player.getUniqueId();
        this.player = player;
        maxCoolDownTicks = getConfigLong("cd");
        coolDownTicks = 0;
        applyInventory();
        applyPotionEffects();
        player.setHealth(20);
        Bukkit.getPluginManager().registerEvents(this, TagChurch.inst());
        taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(TagChurch.inst(), this::tick, 1, 1));
    }

    public void resetPlayer() {
        player.getInventory().clear();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(5);
        player.setExp(0);
        player.setLevel(0);
    }

    public void save() {
        location = player.getLocation();
        potionEffects.clear();
        potionEffects.addAll(player.getActivePotionEffects());
        maxHealth = player.getMaxHealth();
        health = player.getHealth();
        inventory = new GameInventory(player);
    }

    public void onDestroy() {
        resetPlayer();
        HandlerList.unregisterAll(this);
        for (int i : taskIds) {
            Bukkit.getScheduler().cancelTask(i);
        }
        taskIds.clear();
        player = null;
    }

    public void applyPotionEffects() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, -1, 0, false, false));
    }

    public void applyInventory() {
        GameInventory inv = TagChurch.inst().getInv(this.getClass().getSimpleName());
        if (inv != null) {
            inv.apply(player);
        } else {
            player.sendMessage("§cCharacter inventory " + this.getClass().getSimpleName() + " not found!");
        }
    }

    public void tick() {
        if (maxCoolDownTicks == 0) {
            player.setLevel(0);
            player.setExp(0);
            return;
        }
        if (coolDownTicks > 0) {
            coolDownTicks -= 1;
        }
        player.setLevel((int) Math.ceil ((double) coolDownTicks / 20));
        player.setExp((1f - (float) coolDownTicks / maxCoolDownTicks));
    }

    public void onQuit() {
        save();
        resetPlayer();
        HandlerList.unregisterAll(this);
        for (int i : taskIds) {
            Bukkit.getScheduler().cancelTask(i);
        }
        taskIds.clear();
        player = null;
    }

    public void onRejoin() {
        Player p = Bukkit.getPlayer(playerId);
        assert p != null;
        this.player = p;
        p.teleport(location);
        p.addPotionEffects(potionEffects);
        p.setMaxHealth(maxHealth);
        p.setHealth(health);
        inventory.apply(p);
        Bukkit.getPluginManager().registerEvents(this, TagChurch.inst());
        taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(TagChurch.inst(), this::tick, 1, 1));
    }

    @EventHandler
    public void onPlayerTryCastSkill(PlayerInteractEvent e) {
        if (!e.getPlayer().getUniqueId().equals(playerId)) {
            return;
        }
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }
        ItemStack item = e.getItem();
        if (item == null) {
            return;
        }
        if (item.getItemMeta() == null) {
            return;
        }
        // We use fortune enchantment to identify skill items
        if (!item.getItemMeta().hasEnchant(Enchantment.FORTUNE)) {
            return;
        }
        tryCastSkill();
    }

    public void tryCastSkill() {
        if (maxCoolDownTicks == 0) {
            player.sendMessage("§c你没有技能！");
            return;
        }
        if (coolDownTicks > 0) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c§l技能冷却中！"));
            return;
        }
        if (castSkill()) {
            coolDownTicks = maxCoolDownTicks;
        }
    }

    public boolean castSkill() {
        return false;
    }

    public String getConfigPrefix() {
        return "characters." + this.getClass().getSimpleName() + ".";
    }

    @SuppressWarnings("unused")
    public String getConfigString(String key) {
        return TagChurch.inst().getConfig().getString(getConfigPrefix() + key);
    }

    @SuppressWarnings("unused")
    public int getConfigInt(String key) {
        return TagChurch.inst().getConfig().getInt(getConfigPrefix() + key);
    }

    @SuppressWarnings("unused")
    public long getConfigLong(String key) {
        return TagChurch.inst().getConfig().getLong(getConfigPrefix() + key);
    }

    @SuppressWarnings("unused")
    public double getConfigDouble(String key) {
        return TagChurch.inst().getConfig().getDouble(getConfigPrefix() + key);
    }
}