package fun.kaituo.tagchurch.state;

import fun.kaituo.gameutils.game.GameState;
import fun.kaituo.tagchurch.TagChurch;
import fun.kaituo.tagchurch.util.ActiveItem;
import fun.kaituo.tagchurch.util.Corpse;
import fun.kaituo.tagchurch.util.Item;
import fun.kaituo.tagchurch.util.PlayerData;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static fun.kaituo.gameutils.util.Misc.spawnFireworks;
import static fun.kaituo.tagchurch.util.Misc.isCharacterHuman;

public class HuntState implements GameState, Listener {
    public static final int HIDE_SECONDS = 20;
    public static final String START_MESSAGE = "§c感受到了§7『§0黑§7』§c的气息...";
    public static final String NO_HUNTER_MESSAGE = "§e鬼不复存在，人类胜利";
    public static final String NO_HUMAN_MESSAGE = "§c人类全灭，鬼胜利";
    public static final String TIME_UP_MESSAGE = "§e时间到，人类胜利";

    public static final HuntState INST = new HuntState();
    private HuntState() {}

    private TagChurch game;
    private int remainingTime = 0;
    private Location start;
    private Location wait;
    private Location waitBlock;
    private Objective remainingTimeObjective;
    private Score remainingTimeScore;
    private final Set<Integer> taskIds = new HashSet<>();
    private final Set<Item> items = new HashSet<>();

    @Getter
    private boolean isEnded = true;
    private final Set<Corpse> corpses = new HashSet<>();

    public void init() {
        game = TagChurch.inst();
        start = game.getLoc("start");
        wait = game.getLoc("wait");
        waitBlock = game.getLoc("waitBlock");
        remainingTimeObjective = game.getTagBoard().registerNewObjective("remainingTime", Criteria.DUMMY, "鬼抓人");
        remainingTimeScore = remainingTimeObjective.getScore("剩余时间");
        initItems();
    }

    @Override
    public void enter() {
        isEnded = false;
        remainingTime = WaitState.INST.getGameTimeMinutes() * 60 * 20;
        remainingTimeObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (Player p : game.getPlayers()) {
            PlayerData data;
            Class<? extends PlayerData> characterClass = game.playerCharacterChoices.get(p.getUniqueId());
            try {
                Constructor<? extends PlayerData> constructor = characterClass.getConstructor(Player.class);
                data = constructor.newInstance(p);
            } catch (Exception e) {
                p.sendMessage("初始化角色" + characterClass.getSimpleName() + "失败");
                throw new RuntimeException(e);
            }
            game.idDataMap.put(p.getUniqueId(), data);
        }
        for (Player p : getHunters()) {
            p.teleport(wait);
        }
        for (Player p : getHumans()) {
            p.teleport(start);
        }
        Bukkit.getPluginManager().registerEvents(this, game);
        enableItems();
        taskIds.add(Bukkit.getScheduler().runTaskLater(game, () -> {
            removePlatform();
            for (Player p : game.getPlayers()) {
                p.sendTitle(START_MESSAGE, "", 10, 30, 20);
            }
        }, HIDE_SECONDS * 20).getTaskId());
    }

    private void enableItems() {
        for (Item item : items) {
            item.enable();
        }
    }

    private void disableItems() {
        for (Item item : items) {
            item.disable();
        }
    }

    private void initItems() {
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .acceptPackages("fun.kaituo.tagchurch.item") // 指定扫描的包
                .scan()) {

            Set<Class<? extends Item>> itemClasses = new HashSet<>(scanResult
                    .getSubclasses(Item.class.getName())
                    .loadClasses(Item.class))
                    .stream()
                    .filter(clazz -> !clazz.equals(ActiveItem.class))
                    .collect(Collectors.toSet());

            for (Class<? extends Item> itemClass : itemClasses) {
                Constructor<? extends Item> constructor = itemClass.getConstructor();
                Item item = constructor.newInstance();
                items.add(item);
            }
        } catch (Exception e) {
            game.getLogger().warning("Failed to register");
            throw new RuntimeException(e);
        }
    }

    private void removePlatform() {
        for (int x = waitBlock.getBlockX() - 1; x <= waitBlock.getBlockX() + 1; x++) {
            for (int z = waitBlock.getBlockZ() - 1; z <= waitBlock.getBlockZ() + 1; z++) {
                waitBlock.getWorld().getBlockAt(x, waitBlock.getBlockY(), z).setType(Material.AIR);
            }
        }
    }

    private void restorePlatform() {
        for (int x = waitBlock.getBlockX() - 1; x <= waitBlock.getBlockX() + 1; x++) {
            for (int z = waitBlock.getBlockZ() - 1; z <= waitBlock.getBlockZ() + 1; z++) {
                waitBlock.getWorld().getBlockAt(x, waitBlock.getBlockY(), z).setType(Material.BLACK_CONCRETE);
            }
        }
    }

    private void clearCorpses() {
        for (Corpse corpse : corpses) {
            corpse.destroy();
        }
        corpses.clear();
    }

    @Override
    public void exit() {
        remainingTimeObjective.setDisplaySlot(null);
        for (UUID id : game.playerIds) {
            PlayerData data = game.idDataMap.get(id);
            if (data != null) {
                data.onDestroy();
            }
        }
        game.idDataMap.clear();
        HandlerList.unregisterAll(this);
        disableItems();
        for (int id : taskIds) {
            Bukkit.getScheduler().cancelTask(id);
        }
        restorePlatform();
        clearCorpses();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        if (!game.playerIds.contains(p.getUniqueId())) {
            return;
        }
        PlayerData data = game.idDataMap.get(p.getUniqueId());
        if (data == null) {
            return;
        }
        corpses.add(new Corpse(data));
        data.onDestroy();
        game.idDataMap.remove(p.getUniqueId());
        p.setGameMode(GameMode.SPECTATOR);
    }

    private void updateRemainingTime() {
        if (remainingTime > 0) {
            remainingTime -= 1;
        }
        remainingTimeScore.setScore((int) Math.ceil((double)remainingTime / 20));
    }

    public Set<Player> getPlayersAlive() {
        Set<Player> players = new HashSet<>();
        for (UUID id : game.playerIds) {
            PlayerData data = game.idDataMap.get(id);
            if (data != null) {
                players.add(data.getPlayer());
            }
        }
        return players;
    }

    public Set<Player> getHumans() {
        Set<Player> humans = new HashSet<>();
        for (Player p : getPlayersAlive()) {
            if (isCharacterHuman(game.idDataMap.get(p.getUniqueId()).getClass())) {
                humans.add(p);
            }
        }
        return humans;
    }

    public Set<Player> getHunters() {
        Set<Player> hunters = new HashSet<>();
        for (Player p : getPlayersAlive()) {
            if (!isCharacterHuman(game.idDataMap.get(p.getUniqueId()).getClass())) {
                hunters.add(p);
            }
        }
        return hunters;
    }

    private void end(Collection<Player> winners, String message) {
        isEnded = true;
        Set<Player> players = game.getPlayers();
        for (Player p : winners) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 4, false, false));
            taskIds.addAll(spawnFireworks(p, game));
        }
        for (Player p : players) {
            p.sendTitle(message, "", 10, 30, 20);
        }
        taskIds.add(Bukkit.getScheduler().runTaskLater(game, () -> {
            for (Player p : game.getPlayers()) {
                p.removePotionEffect(PotionEffectType.RESISTANCE);
                p.setGameMode(GameMode.ADVENTURE);
            }
            game.setState(WaitState.INST);
        }, 60).getTaskId());
    }

    private void checkForEnd() {
        Set<Player> humans = getHumans();
        Set<Player> hunters = getHunters();
        if (hunters.isEmpty()) {
            end(getHumans(), NO_HUNTER_MESSAGE);
            return;
        }
        if (humans.isEmpty()) {
            end(getHunters(), NO_HUMAN_MESSAGE);
            return;
        }
        if (remainingTime <= 0) {
            end(getHumans(), TIME_UP_MESSAGE);
        }
    }

    @Override
    public void tick() {
        updateRemainingTime();
        if (!isEnded) {
            checkForEnd();
        }
    }

    @Override
    public void addPlayer(Player p) {
        PlayerData data = game.idDataMap.get(p.getUniqueId());
        if (data != null) {
            data.onRejoin();
        } else {
            p.setGameMode(GameMode.SPECTATOR);
            p.teleport(start);
        }
    }

    @Override
    public void removePlayer(Player p) {
        PlayerData data = game.idDataMap.get(p.getUniqueId());
        if (data != null) {
            data.onQuit();
        } else {
            p.setGameMode(GameMode.ADVENTURE);
        }
    }

    @Override
    public void forceStop() {
        isEnded = true;
        for (Player p : game.getPlayers()) {
            p.removePotionEffect(PotionEffectType.RESISTANCE);
            p.setGameMode(GameMode.ADVENTURE);
        }
        game.setState(WaitState.INST);
    }
}
