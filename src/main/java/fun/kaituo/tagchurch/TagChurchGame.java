package fun.kaituo.tagchurch;


import fun.kaituo.gameutils.Game;
import fun.kaituo.gameutils.PlayerQuitData;
import fun.kaituo.gameutils.event.PlayerChangeGameEvent;
import fun.kaituo.gameutils.event.PlayerEndGameEvent;
import fun.kaituo.gameutils.utils.ItemStackBuilder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;


public class TagChurchGame extends Game implements Listener {
    private static final TagChurchGame instance = new TagChurchGame((TagChurch) Bukkit.getPluginManager().getPlugin("TagChurch"));
    Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    Scoreboard tag5 = Bukkit.getScoreboardManager().getNewScoreboard();
    TagChurch plugin;
    List<Player> humans = new ArrayList<>();
    List<Player> devils = new ArrayList<>();


    List<Location> chestLocations = new ArrayList<>();
    List<Location> respawnChestLocations = new ArrayList<>();


    long startTime;
    long gameTime;
    Team team;

    Team tag5norden;
    Team tag5cheshirecat;
    Team tag5redhat;
    Team tag5alice;
    Team tag5eunice;
    Team tag5dodo;
    Team tag5faketurtle;
    Team tag5victoria;

    Team tag5miranda;
    Team tag5hein;
    Team tag5lindamayer;
    Team tag5baphomet;

    boolean running = false;
    int countDownSeconds = 10;
    FileConfiguration c;


    HashMap<Player, List<Long>> coolDown = new HashMap<>();
    HashMap<ArmorStand, ArmorStand> armourStandMap = new HashMap<>();
    HashMap<ArmorStand, Player> playerMap = new HashMap<>();

    ItemStack feather = generateItemStack(Material.FEATHER, "§l§b渡渡快跑", new String[]{"可以调整移动速度", "§7模仿那只渡渡的跑法的话，应该就能以势不可挡的步伐冲刺了吧"});
    ItemStack cooked_chicken = generateItemStack(Material.COOKED_CHICKEN, "§l§b渡渡快跑", new String[]{"可以调整移动速度", "§7模仿那只渡渡的跑法的话，应该就能以势不可挡的步伐冲刺了吧"});
    ItemStack glass_bottle = generateItemStack(Material.GLASS_BOTTLE, "§l§7透明身体", new String[]{"将身体变得透明", "§7欺骗敌人"});
    ItemStack nether_star = generateItemStack(Material.NETHER_STAR, "§l§c还魂", new String[]{"使友方复活", "§7已经被消费掉的东西是无法返还的...", "§7是呢，除非回溯时间"});
    ItemStack clock = generateItemStack(Material.CLOCK, "§b§l兔子的怀表", new String[]{"防止一次收到的伤害", "§7被规矩束缚的白兔绝不会在任何一场审判中迟到"});
    ItemStack coal = generateItemStack(Material.COAL, "§l§8污秽的黑之魂", new String[]{"回复所有生命值，但会降低最大生命上限", "§7这个国家洋溢着大量的黑之魂！太棒了！"});
    ItemStack potion = generateItemStack(Material.POTION, "§l§1星水", new String[]{"使用后一段时间内不会受到伤害，但效果结束后会被强制击倒", "§7没有门扉的箱庭会有漂流者顺流而来，其中似乎也混杂着外来者"});
    ItemStack dragon_breath = generateItemStack(Material.DRAGON_BREATH, "§l§d心跳悦动酒", new String[]{"瞬间恢复一定生命值，但会获得两倍回复量的发光时间", "§7梅贝尔最爱喝的酒！"});
    ItemStack honey_bottle = generateItemStack(Material.HONEY_BOTTLE, "§l§6黄金蜂蜜酒", new String[]{"获得短时间的缓降效果", "§7带翼的贵妇人相当喜欢这个蜂蜜酒"});
    ItemStack enchanted_book = generateItemStack(Material.ENCHANTED_BOOK, "§l§8§k爱丽丝·里德尔的誓约", new String[]{"持有者速度永久增加，但无法被复活", "§7§m我将永远爱着你"});
    ItemStack experience_bottle = generateItemStack(Material.EXPERIENCE_BOTTLE, "§l§d梦之魂", new String[] {"获得少量的护盾生命值", "以及隐身效果","§7即使你已忘却，","§7少女的梦幻故事也永远不会完结"});
    ItemStack amethyst_shard = generateItemStack(Material.AMETHYST_SHARD, "§l§8脏液", new String[] {"缓慢回复一定生命值","但同时会获得反胃","§7狂信的患者为了证明医学的正确性，而自行患上疾病"});
    ItemStack enchanted_book2 = generateItemStack(Material.ENCHANTED_BOOK, "§l§9童话绘本", new String[]{"持有者死亡时无法被复活","减少最大生命值上限","§赋予全场友方角色短暂的加速、生命恢复和免伤","§7克缇遗落的绘本","§7某一天，","§7人鱼公主爱上出现在海边的王子。","§7那绝对无法得到回报的恋爱不断膨胀，令海啸袭击王子的国家"});
    ItemStack medium_amethyst_bud = generateItemStack(Material.MEDIUM_AMETHYST_BUD, "§d§l女仆维多利雅的誓约", new String[]{"右键切换至魅魔形态","持有者拥有常驻速度提升和跳跃提升","不会因为污秽的黑之魂减少最大生命值上限","§7§o明明已经说了不要再接近却仍然定下了誓约", "§7§o那就请您做好即使从噩梦中醒来也无法逃脱的心理准备吧","§c§o主人大人"});
    ItemStack large_amethyst_bud = generateItemStack(Material.LARGE_AMETHYST_BUD, "§d§l魅魔维多利雅的誓约", new String[]{"右键切换至女仆形态","持有者拥有常驻抗性提升，并且附近存在角色时(无论敌我)能缓慢回复生命值","§7§o明明已经说了不要再接近却仍然定下了誓约", "§7§o那就请您做好即使从噩梦中醒来也无法逃脱的心理准备吧","§c§o主人大人"});


    List<ItemStack> gadgets = Arrays.asList(feather, glass_bottle, nether_star, clock, potion, honey_bottle, coal, dragon_breath,experience_bottle,amethyst_shard);
    List<Integer> gadgetWeights = Arrays.asList(10, 5, 1, 10, 1, 10, 5, 10, 5, 10);

    int totalWeight;

    private TagChurchGame(TagChurch plugin) {
        this.plugin = plugin;
        initializeGame(plugin, "TagChurch",  "§f教堂", new Location(world, 0.5, 25, -2022.5));
        initializeButtons(new Location(world, 0, 26, -2031), BlockFace.SOUTH,
                new Location(world, 0, 27, -2034), BlockFace.SOUTH);
        initializeGameRunnable();
        players = TagChurch.players;
        tag5.registerNewObjective("tag5", "dummy", "鬼抓人");
        tag5.getObjective("tag5").setDisplaySlot(DisplaySlot.SIDEBAR);
        for (int i : gadgetWeights) {
            totalWeight += i;
        }

        c = plugin.getConfig();
        tag5norden = scoreboard.getTeam("tag5norden");
        tag5cheshirecat = scoreboard.getTeam("tag5cheshirecat");
        tag5redhat = scoreboard.getTeam("tag5redhat");
        tag5alice = scoreboard.getTeam("tag5alice");
        tag5eunice = scoreboard.getTeam("tag5eunice");
        tag5dodo = scoreboard.getTeam("tag5dodo");
        tag5faketurtle = scoreboard.getTeam("tag5faketurtle");
        tag5victoria = scoreboard.getTeam("tag5victoria");

        tag5miranda = scoreboard.getTeam("tag5miranda");
        tag5hein = scoreboard.getTeam("tag5hein");
        tag5lindamayer = scoreboard.getTeam("tag5lindamayer");
        tag5baphomet = scoreboard.getTeam("tag5baphomet");

        ConfigurationSection chestLocationsSection = c.getConfigurationSection("chest-locations");

        for (String key : chestLocationsSection.getKeys(false)) {
            int x = c.getInt("chest-locations." + key + ".x");
            int y = c.getInt("chest-locations." + key + ".y");
            int z = c.getInt("chest-locations." + key + ".z");
            chestLocations.add(new Location(world, x, y, z));
        }

        ConfigurationSection respawnChestLocationsSection = c.getConfigurationSection("respawn-chest-locations");

        for (String key : respawnChestLocationsSection.getKeys(false)) {
            int x = c.getInt("respawn-chest-locations." + key + ".x");
            int y = c.getInt("respawn-chest-locations." + key + ".y");
            int z = c.getInt("respawn-chest-locations." + key + ".z");
            respawnChestLocations.add(new Location(world, x, y, z));
        }
    }

    public static TagChurchGame getInstance() {
        return instance;
    }

    @EventHandler
    public void clearCoolDown(PlayerDeathEvent pde) {
        clearCoolDown(pde.getEntity());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent ede) { //受击特效
        if (!(ede.getEntity() instanceof Player)) {
            return;
        }
        if (ede.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            if ((((EntityDamageByEntityEvent) ede).getDamager() instanceof Player)) {
                if (humans.contains(((EntityDamageByEntityEvent) ede).getDamager())) {
                    return;
                }
            }
        }//友军不能触发特效

        Player p = (Player) ede.getEntity();
        if (tag5cheshirecat.hasPlayer(p)) {
            if (ede.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, c.getInt("cheshirecat.speed-duration"), c.getInt("cheshirecat.speed-amplifier"), true, false));
                ede.setCancelled(true);
            } else if (p.getInventory().contains(Material.CLOCK, 1)) {
                removeItem(p, Material.CLOCK);
                ede.setCancelled(true);
            }
        } else if (p.getInventory().contains(Material.CLOCK, 1)) {
            removeItem(p, Material.CLOCK);
            ede.setCancelled(true);
        }


        //以下是自带天赋
        if (tag5dodo.hasPlayer(p)) {
            p.sendMessage("获得加速！");
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, c.getInt("dodo.speed-duration"), 1));
        } else if (tag5redhat.hasPlayer(p)) {
            p.sendMessage("获得生命恢复与发光！");
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, c.getInt("redhat.duration"), c.getInt("redhat.regeneration-amplifier"), false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, c.getInt("redhat.duration"), 0, false, false));
        }
    }

    private void initializePlayer(Player p) {
        switch (scoreboard.getPlayerTeam(p).getName()) {
            case "tag5hein" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000000, 1, false, false));
            }
            case "tag5miranda", "tag5baphomet", "tag5lindamayer" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000000, 0, false, false));
            }
            case "tag5norden" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 10000000, 4, true, false));
            }
            case "tag5redhat" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10000000, c.getInt("redhat.resistance-amplifier"), false, false));
            }
            case "tag5dodo" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 0, false, false));
            }
            case "tag5faketurtle" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10000000, c.getInt("faketurtle.resistance-amplifier"), false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10000000,  c.getInt("faketurtle.slowness-amplifier"), false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000000,  c.getInt("faketurtle.jump-amplifier"), false, false));
            }
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 0, false, false));
    }

    private Player revivePlayer(ArmorStand s) {
        if ((armourStandMap.get(s) == null)) {
            return null;
        }
        if (!playerMap.containsKey(s)) {
            return null;
        }
        Player p = playerMap.get(s);
        if (!(players.contains(p) && !humans.contains(p))) {
            return null;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location l = s.getLocation();
            l.setY(l.getY() + 1.4);
            humans.add(p);
            p.teleport(l);
            p.setGameMode(GameMode.ADVENTURE);
            initializePlayer(p);
        }, 1);
        armourStandMap.get(s).remove();
        s.remove();
        armourStandMap.remove(s);
        playerMap.remove(s);
        return p;
    }

    @EventHandler
    public void armorStandOperation(PlayerInteractAtEntityEvent piaee) {
        if (!piaee.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            return;
        }
        if (!(players.contains(piaee.getPlayer()))) {
            return;
        }
        if (!(piaee.getRightClicked() instanceof ArmorStand)) {
            return;
        }
        if (piaee.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            piaee.setCancelled(true);
        }

        ItemStack item = piaee.getPlayer().getInventory().getItemInMainHand();
        if (item.getType().equals(Material.NETHER_STAR)) {
            revivePlayer((ArmorStand) piaee.getRightClicked());
            item.setAmount(item.getAmount() - 1);
        }
    }

    @EventHandler
    public void destroyChest(PlayerInteractEvent pie) {
        if (!pie.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }// 不是右键方块
        Player executor = pie.getPlayer();
        if (!(devils.contains(executor))) {
            return;
        } //不是鬼
        Block block = pie.getClickedBlock();
        if (block.getType().equals(Material.TRAPPED_CHEST)) {
            if (pie.getItem() == null) {
                return;
            }//没有物品
            if (!pie.getItem().getType().equals(Material.POPPED_CHORUS_FRUIT)) {
                return;
            }
            pie.setCancelled(true);
            if (((Chest) (block.getState())).getBlockInventory().isEmpty()) {
                executor.sendMessage("§c这个箱子是空的！");
            } else if (checkCoolDown(executor, 600)) {
                ((Chest) (block.getState())).getBlockInventory().clear();
                executor.sendMessage("§a成功破坏箱子内的所有道具！");
            }
        }
    }

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent epee) {
        if (!epee.getCause().equals(EntityPotionEffectEvent.Cause.PLUGIN)) {
            return;
        }
        if (epee.getNewEffect() == null) {
            return;
        }
        if (!epee.getNewEffect().getType().equals(PotionEffectType.GLOWING)) {
            return;
        }
        if (!players.contains(epee.getEntity())) {
            return;
        }
        for (Player p : devils) {
            if (tag5lindamayer.hasPlayer(p)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, epee.getNewEffect().getDuration(), 1, true, false));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent pie) { //右键特效
        if (!pie.getAction().equals(Action.RIGHT_CLICK_AIR) && !pie.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }// 不是右键
        Player executor = pie.getPlayer();
        if (!players.contains(executor)) {
            return;
        }//不在tag5里
        if (devils.contains(executor)) { //是鬼
            if (pie.getItem() == null) {
                return;
            }//没有物品
            //这里开始添加内容

            switch (pie.getItem().getType()) {
                case NETHERITE_SWORD -> {
                    if (checkCoolDown(executor, 3600)) {
                        for (Player p: humans) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0, true, false));
                        }
                        for (Player p : devils) {
                            if (tag5hein.hasPlayer(p)) {
                                for (Player victim: humans) {
                                    victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 0, true, false));
                                }
                                break;
                            }
                        }
                        for (Player p : players) {
                            p.sendMessage("§8琳达梅尔§c发动了群体发光！");
                        }
                    }
                }
            }
        } else if (humans.contains(executor)) { //是人
            if (pie.getClickedBlock() != null) {
                if (pie.getClickedBlock().getType().equals(Material.TRAPPED_CHEST)) {
                    if (!pie.getPlayer().isSneaking()) {
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60, 0));
                        return;
                    }
                }
            }
            if (pie.getItem() == null) {
                return;
            }//没有物品
            //这里开始添加内容

            switch (pie.getItem().getType()) {
                case BOOK -> {
                    ArmorStand target = null;
                    for (Entity e : world.getNearbyEntities(executor.getLocation(), 200,200,200, e ->  playerMap.containsKey(e))) {
                        if (target == null) {
                            target = (ArmorStand) e;
                        } else {
                            if (executor.getLocation().distance(e.getLocation()) < executor.getLocation().distance(target.getLocation())) {
                                target = (ArmorStand) e;
                            }
                        }
                    }
                    if (target == null) {
                        executor.sendMessage("§c没有可以复活的友方角色！");
                    } else {
                        Player p = revivePlayer(target);
                        if (p != null) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0, false, false));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 3, false, false));
                        }
                        pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                        executor.getInventory().addItem(enchanted_book);
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 0));
                    }
                }


                case MEDIUM_AMETHYST_BUD -> {
                    if (checkCoolDown(executor, 50)) {
                        pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                        executor.getInventory().addItem(large_amethyst_bud);
                        executor.sendMessage("§d切换至魅魔形态！");
                    }
                }

                case LARGE_AMETHYST_BUD -> {
                    if (checkCoolDown(executor, 50)) {
                        pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                        executor.getInventory().addItem(medium_amethyst_bud);
                        executor.sendMessage("§d切换至女仆形态！");
                    }
                }

                case COAL -> {
                    if (tag5norden.hasPlayer(executor) || tag5victoria.hasPlayer(executor)) {
                        executor.setHealth(executor.getMaxHealth());
                        executor.sendMessage("§c生命全部恢复！");
                        pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    } else {
                        if (executor.getMaxHealth() > 3) {
                            executor.sendMessage("§c最大生命值减少，生命全部恢复！");
                            executor.setMaxHealth(executor.getMaxHealth() - 3);
                            executor.setHealth(executor.getMaxHealth());

                            pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                        } else {
                            executor.sendMessage("§c生命上限过低，无法使用！");
                        }
                    }
                }
                case POTION -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§c30秒内无敌，30秒后将被强制击倒！");
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 254, false, false));
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        executor.setHealth(0);
                    }, 600);
                }
                case GLASS_BOTTLE -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§c隐身10秒！");
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0, false, false));
                }
                case DRAGON_BREATH -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§c回复一半最大生命值，获得与回复量相同的发光时长！");
                    if (executor.getHealth() * 2 < executor.getMaxHealth()) {
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, (int) (executor.getMaxHealth() * 10), 0, false, false));
                        executor.setHealth(executor.getHealth() + executor.getMaxHealth() / 2);

                    } else {
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, (int) ((executor.getMaxHealth() - executor.getHealth()) * 20), 0, false, false));
                        executor.setHealth(executor.getMaxHealth());

                    }
                }
                case HONEY_BOTTLE -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§e获得缓降！");
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 40, 0, false, false));
                }
                case FEATHER -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.getInventory().addItem(cooked_chicken);
                    executor.sendMessage("§b获得加速！");
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0, false, false));
                    if ((tag5dodo.hasPlayer(executor))) {
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 0));
                    }
                }
                case COOKED_CHICKEN -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§b获得加速！");
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false, false));
                }
                case BEETROOT_SOUP -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§a恢复一半最大生命，获得隐身！");
                    if (executor.getHealth() * 2 < executor.getMaxHealth()) {
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, c.getInt("faketurtle.invisibility-duration"), 0, false, false));
                        executor.setHealth(executor.getHealth() + executor.getMaxHealth() / 2);

                    } else {
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0, false, false));
                        executor.setHealth(executor.getMaxHealth());
                    }
                }
                case EXPERIENCE_BOTTLE -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    pie.setCancelled(true);
                    executor.sendMessage("§a获得护盾生命值和隐身效果！");
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0, false, false));
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 10000000, 1, false, false));
                }
                case AMETHYST_SHARD -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§a获得生命恢复和§c反胃效果！");
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,  150, 1, false, false));
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,  300, 0, true, false));
                }
            }
        }
    }

    @EventHandler
    public void cancelItemMovement(InventoryClickEvent ice) {
        if (!(ice.getWhoClicked() instanceof Player)) {
            return;
        }
        if (ice.getCurrentItem() == null) {
            return;
        }
        Player p = (Player) ice.getWhoClicked();
        if (devils.contains(p)) {
            ice.setCancelled(true);
        } else if (humans.contains(p)) {
            if (ice.getCurrentItem().getType().equals(Material.BOOK)
                    || ice.getCurrentItem().getType().equals(Material.ENCHANTED_BOOK)
                    || ice.getCurrentItem().getType().equals(Material.RABBIT_FOOT)
                    || ice.getCurrentItem().getType().equals(Material.STRING)
                    || ice.getCurrentItem().getType().equals(Material.RED_DYE)
                    || ice.getCurrentItem().getType().equals(Material.POPPED_CHORUS_FRUIT)
                    || ice.getCurrentItem().getType().equals(Material.END_ROD)
                    || ice.getCurrentItem().getType().equals(Material.TURTLE_EGG)
                    || ice.getCurrentItem().getType().equals(Material.SWEET_BERRIES)
                    || ice.getCurrentItem().getType().equals(Material.MEDIUM_AMETHYST_BUD)
                    || ice.getCurrentItem().getType().equals(Material.IRON_SWORD)
                    || ice.getCurrentItem().getType().equals(Material.CHARCOAL)
                    || ice.getCurrentItem().getType().equals(Material.NETHERITE_AXE)
                    || ice.getCurrentItem().getType().equals(Material.NETHERITE_SWORD)
                    || ice.getCurrentItem().getType().equals(Material.NETHERITE_HOE)) {
                ice.setCancelled(true);
            }
        }
    }

    public void removeItem(Player p, Material material) {
        p.getInventory().all(material).get(0);
        for (Map.Entry entry : p.getInventory().all(material).entrySet()) {
            ItemStack i = (ItemStack) entry.getValue();
            i.setAmount(i.getAmount() - 1);
            break;
        }
    }

    public void clearCoolDown(Player p) {
        p.setLevel(0);
        p.setExp(0);
        if (coolDown.get(p) != null) {
            coolDown.get(p).set(0, 0L);
            coolDown.get(p).set(1, 0L);
        }
    }

    private void setCoolDown(Player p, long remainingCoolDown, long totalCoolDown) {
        coolDown.get(p).set(0, remainingCoolDown);
        coolDown.get(p).set(1, totalCoolDown);
    }

    private boolean checkCoolDown(Player p, long cd) {
        if (coolDown.get(p).get(0) == 0) {
            coolDown.get(p).set(0, cd);
            coolDown.get(p).set(1, cd);
            return true;
        } else {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§4§l技能冷却中！"));
            return false;
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent pde) {
        if (!players.contains(pde.getEntity())) {
            return;
        }
        devils.remove(pde.getEntity());
        humans.remove(pde.getEntity());
        pde.getEntity().setGameMode(GameMode.SPECTATOR);
        for (Player p : players) {
            p.sendMessage("§f" + pde.getEntity().getName() + " §c 重伤倒地！");
        }
    }

    @EventHandler
    public void freezeGui(EntityDamageByEntityEvent edbee) {
        if (!(edbee.getDamager() instanceof Player)) {
            return;
        }
        if (!(edbee.getEntity() instanceof Player)) {
            return;
        }
        Player victim = (Player) edbee.getEntity();
        Player damager = (Player) edbee.getDamager();
        if (humans.contains(damager) && devils.contains(victim)) {
            if (tag5eunice.hasPlayer(damager)) {
                if (damager.getInventory().getItemInMainHand().getType().equals(Material.IRON_SWORD)) {
                    if (checkCoolDown(damager,  c.getInt("eunice.cd"))) {
                        damager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, c.getInt("eunice.duration"), 1, false, false));
                        damager.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,  150, 1, false, false));
                        damager.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,  c.getInt("eunice.duration"), 0, false, false));
                        damager.sendMessage("§c获得加速、恢复和隐身！");
                    }
                }
            } else {
                edbee.setCancelled(true);
            }
        } else if (humans.contains(damager) && humans.contains(victim)) {
            edbee.setCancelled(true);
        } else if (devils.contains(damager) && humans.contains(victim)) {
            if (tag5baphomet.hasPlayer(damager)) {
                if (tag5norden.hasPlayer(victim)) {
                    victim.sendMessage("§a成功免疫最大生命减少效果！");
                } else {
                    if (victim.getMaxHealth() > 3) {
                        victim.sendMessage("§c被巴风特攻击，最大生命值减少！");
                        victim.setMaxHealth(victim.getMaxHealth() - 3);
                    } else {
                        victim.sendMessage("§a生命上限过低，无法继续减少！");
                    }
                }
            }
            int freezeTime = 60;
            if (tag5miranda.hasPlayer(damager)) { //米兰达
                freezeTime = 110;
            } else if (tag5baphomet.hasPlayer(damager)) { //巴风特
                freezeTime = 50;
            }
            Location l = damager.getLocation().clone();
            int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                damager.teleport(l);
            }, 1, 1);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.getScheduler().cancelTask(id);
            }, freezeTime);
            damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, freezeTime, 254, false, false));
        }
    }

    public ItemStack generateItemStack(Material material, String name, String[] lore) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Arrays.asList(lore.clone()));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @EventHandler
    public void summonCorpse(PlayerDeathEvent pde) {
        if (!(players.contains(pde.getEntity()))) {
            return;
        }
        if ((devils.contains(pde.getEntity()))) {
            return;
        }
        if (pde.getEntity().getInventory().contains(Material.ENCHANTED_BOOK, 1)) {
            if (tag5alice.hasPlayer(pde.getEntity())) {
                for (Player p : players) {
                    p.sendMessage("§f" + pde.getEntity().getName() + " §c 被逐出了箱庭！");
                }
                return;
            }
        } else if (pde.getEntity().getKiller() != null) {
            if (tag5baphomet.hasPlayer(pde.getEntity().getKiller())) {
                for (Player p : players) {
                    p.sendMessage("§f" + pde.getEntity().getName() + " §c 被逐出了箱庭！");
                }
                return;
            }
        }
        Location l = pde.getEntity().getLocation().clone();

        ArmorStand ice = (ArmorStand) world.spawnEntity(l, EntityType.ARMOR_STAND);
        ice.setBasePlate(false);
        ice.setInvisible(true);
        ice.setVelocity(new Vector(0, -50, 0));

        l.setY(0);

        ArmorStand head = (ArmorStand) world.spawnEntity(l, EntityType.ARMOR_STAND);
        ItemStack headItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) headItem.getItemMeta();
        skullMeta.setOwningPlayer(pde.getEntity());
        headItem.setItemMeta(skullMeta);
        head.setBasePlate(false);
        head.setSmall(true);
        head.getEquipment().setHelmet(headItem);
        head.setGravity(false);
        head.setCustomName(pde.getEntity().getName());
        head.setCustomNameVisible(true);
        head.setInvisible(true);
        EulerAngle angle = new EulerAngle(Math.PI, 0, 0);
        head.setLeftLegPose(angle);
        head.setRightLegPose(angle);

        armourStandMap.put(ice, head);
        playerMap.put(ice, pde.getEntity());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ice.setGravity(false);
            Location iceLocation = ice.getLocation().clone();
            iceLocation.setY(iceLocation.getY() - 1.4);
            ice.teleport(iceLocation);
            iceLocation.setY(iceLocation.getY() + 0.75);
            head.teleport(iceLocation);
        }, 5);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ice.getEquipment().setHelmet(new ItemStack(Material.BROWN_STAINED_GLASS));
            head.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 594, 0));
        }, 6);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (head.isValid()) {
                head.remove();
                ice.remove();
                for (Player p : players) {
                    p.sendMessage("§f" + pde.getEntity().getName() + " §c 被逐出了箱庭！");
                }
            }
        }, 600);
    }

    @EventHandler
    public void preventDroppingItem(PlayerDropItemEvent pdie) {
        if (!(players.contains(pdie.getPlayer()))) {
            return;
        }
        if (!pdie.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            return;
        }
        switch (pdie.getItemDrop().getItemStack().getType()) {
            case RABBIT_FOOT, BOOK, STRING, ENCHANTED_BOOK, RED_DYE, POPPED_CHORUS_FRUIT,
                    END_ROD, FEATHER, TURTLE_EGG, MEDIUM_AMETHYST_BUD,LARGE_AMETHYST_BUD, BEETROOT_SOUP,
                    IRON_SWORD, NETHERITE_AXE, NETHERITE_HOE, NETHERITE_SWORD, CHARCOAL-> pdie.setCancelled(true);
            default -> {
            }
        }
    }

    @EventHandler
    public void preventRegen(EntityRegainHealthEvent erhe) {
        if (!(erhe.getEntity() instanceof Player)) {
            return;
        }
        if (!(players.contains(erhe.getEntity()))) {
            return;
        }
        if (!((Player) erhe.getEntity()).getGameMode().equals(GameMode.ADVENTURE)) {
            return;
        }
        if (erhe.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
            erhe.setCancelled(true);
        } else if (erhe.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.EATING)) {
            erhe.setCancelled(true);
        }
    }

    @Override
    protected void quit(Player p) {
        if (!players.contains(p)) {
            return;
        }
        PlayerQuitData quitData = new PlayerQuitData(p, this, gameUUID);
        quitData.getData().put("team", whichGroup(p));
        gameUtils.setPlayerQuitData(p.getUniqueId(), quitData);
        players.remove(p);
        humans.remove(p);
        devils.remove(p);
    }

    @Override
    protected boolean rejoin(Player p) {
        if (!running) {
            p.sendMessage("§c游戏已经结束！");
            return false;
        }
        if (!gameUtils.getPlayerQuitData(p.getUniqueId()).getGameUUID().equals(gameUUID)) {
            p.sendMessage("§c游戏已经结束！");
            return false;
        }
        PlayerQuitData pqd = gameUtils.getPlayerQuitData(p.getUniqueId());
        pqd.restoreBasicData(p);
        players.add(p);
        team.addPlayer(p);
        p.setScoreboard(tag5);
        if (pqd.getData().get("team") != null) {
            ((List<Player>) pqd.getData().get("team")).add(p);
        }
        gameUtils.setPlayerQuitData(p.getUniqueId(), null);
        return true;
    }

    @Override
    protected boolean join(Player p) {
        p.setBedSpawnLocation(hubLocation, true);
        p.teleport(hubLocation);
        scoreboard.getTeam("tag5norden").addPlayer(p);
        return true;
    }

    @Override
    protected void forceStop() {
        if (running) {
            endGame("§c游戏被强制终止", new ArrayList<>());
        }
    }

    private List<Player> whichGroup(Player p) {
        if (humans.contains(p)) {
            return humans;
        } else if (devils.contains(p)) {
            return devils;
        } else {
            return null;
        }
    }

    private void endGame(String msg, List<Player> winningPlayers) {
        List<Player> winningPlayersCopy = new ArrayList<>(winningPlayers);
        List<Player> playersCopy = new ArrayList<>(players);
        for (Player p : winningPlayersCopy) {
            spawnFireworks(p);
        }
        for (Player p : playersCopy) {
            p.sendTitle(msg, null, 5, 50, 5);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                p.teleport(new Location(world, 0.5, 25, -2022.5, 180, 0));
                Bukkit.getPluginManager().callEvent(new PlayerEndGameEvent(p, this));
            }, 100);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                scoreboard.getTeam("tag5norden").addPlayer(p);
            }, 101);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Entity e : world.getNearbyEntities(new Location(world, 0, 128, -2000), 200, 200, 200)) {
                if (e instanceof Item) {
                    e.remove();
                }
            }
            clearChests();
            for (Map.Entry<ArmorStand, ArmorStand> entry : armourStandMap.entrySet()) {
                entry.getKey().remove();
                entry.getValue().remove();
            }
            armourStandMap.clear();
            playerMap.clear();
            removeSpectateButton();
            placeStartButton();
            HandlerList.unregisterAll(this);
            tag5.getObjective("tag5").getScore("剩余人数").setScore(0);
            tag5.getObjective("tag5").getScore("剩余时间").setScore(0);
        }, 100);
        players.clear();
        humans.clear();
        devils.clear();
        team.unregister();
        running = false;
        gameUUID = UUID.randomUUID();
        cancelGameTasks();
    }

    private void initializeGameRunnable() {
        gameRunnable = () -> {
            gameTime = TagChurch.gameTime;
            team = tag5.registerNewTeam("tag5");
            team.setNameTagVisibility(NameTagVisibility.NEVER);
            team.setCanSeeFriendlyInvisibles(false);
            team.setAllowFriendlyFire(true);
            for (Player p : getPlayersNearHub(50, 50, 50)) {
                if (scoreboard.getPlayerTeam(p) == null) {
                    continue;
                }
                switch (scoreboard.getPlayerTeam(p).getName()) {
                    case "tag5cheshirecat", "tag5norden", "tag5redhat", "tag5alice" ,  "tag5eunice", "tag5dodo", "tag5faketurtle", "tag5victoria"-> {
                        humans.add(p);
                    }
                    case "tag5hein", "tag5miranda", "tag5lindamayer","tag5baphomet" -> {
                        devils.add(p);
                    }
                }
                players.add(p);
                team.addPlayer(p);
            }
            if (players.size() < 2) {
                for (Player p : players) {
                    p.sendMessage("§c至少需要2人才能开始游戏！");
                }
                players.clear();
                humans.clear();
                team.unregister();

            } else if (humans.size() == 0) {
                for (Player p : players) {
                    p.sendMessage("§c至少需要1个人类才能开始游戏！");
                }
                players.clear();
                humans.clear();
                team.unregister();
            } else if (devils.size() == 0) {
                for (Player p : players) {
                    p.sendMessage("§c至少需要1个鬼才能开始游戏！");
                }
                players.clear();
                humans.clear();
                team.unregister();
            } else {
                startTime = getTime(world) + countDownSeconds * 20L + 400;
                running = true;
                removeStartButton();
                startCountdown(countDownSeconds);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (Player p : players) {
                        p.getInventory().clear();
                    }
                });
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Bukkit.getPluginManager().registerEvents(this, plugin);
                    for (Player p : humans) {
                        p.teleport(new Location(world, 0.5,70,-1994.5, 180, 0));
                    }
                    for (Player p : players) {
                        initializePlayer(p);
                        p.setScoreboard(tag5);
                    }

                }, countDownSeconds * 20L);
                for (int i = 0; i < 5; i++) {
                    int finalI = i;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        for (Player p : players) {
                            p.sendTitle("§a" + (5 - finalI), null, 2, 16, 2);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1f);
                        }
                    }, countDownSeconds * 20L + 300 + i * 20);
                }

                taskIds.add(
                        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, ()-> {
                            for (Player p : players) {
                                if (coolDown.get(p) == null) {
                                    List<Long> l = new ArrayList<>(2);
                                    l.add(0,0L);
                                    l.add(1,0L);
                                    coolDown.put(p, l);
                                } else {
                                    List<Long> l = coolDown.get(p);
                                    //p.sendMessage(String.valueOf(l.get(0)));
                                    if (l.get(0) > 0) {
                                        l.set(0, l.get(0) - 1);
                                    }
                                    if (l.get(1) != 0) {
                                        p.setLevel((int) Math.ceil(((float)l.get(0)) / 20));
                                        p.setExp(((float)(l.get(1) - l.get(0))) / l.get(1));
                                    }

                                }
                            }
                        }, 1,1 ));

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    placeSpectateButton();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "function tag5:go");
                    for (Player p : devils) {
                        p.teleport(new Location(world, 0.5,70,-1994.5, 180, 0));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 4, false, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0, false, false));
                        ItemStack skull = new ItemStackBuilder(Material.WITHER_SKELETON_SKULL).addEnchantment(Enchantment.BINDING_CURSE, 1).build();
                        ItemStack chestPlate = new ItemStackBuilder(Material.NETHERITE_CHESTPLATE).addEnchantment(Enchantment.BINDING_CURSE, 1).setUnbreakable(true).build();
                        ItemStack leggings = new ItemStackBuilder(Material.NETHERITE_LEGGINGS).addEnchantment(Enchantment.BINDING_CURSE, 1).setUnbreakable(true).build();
                        ItemStack boots = new ItemStackBuilder(Material.NETHERITE_BOOTS).addEnchantment(Enchantment.BINDING_CURSE, 1).setUnbreakable(true).build();
                        p.getInventory().setItem(EquipmentSlot.HEAD, skull);
                        p.getInventory().setItem(EquipmentSlot.CHEST, chestPlate);
                        p.getInventory().setItem(EquipmentSlot.LEGS, leggings);
                        p.getInventory().setItem(EquipmentSlot.FEET, boots);
                    }
                    for (Player p : players) {
                        if (tag5lindamayer.hasPlayer(p)) {
                            setCoolDown(p, 600, 3600);
                        }
                        p.setHealth(20);
                        p.sendTitle("§e游戏开始！", null, 2, 16, 2);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 2f);
                    }

                }, countDownSeconds * 20L + 400);


                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    for (Player p : players) {
                        if (devils.contains(p)) {
                            for (Player victim : players) {
                                if (humans.contains(victim)) {
                                    if (p.getLocation().distance(victim.getLocation()) < 10) {
                                        victim.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.BLOCKS, 2f, 0f);
                                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                            victim.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.BLOCKS, 2f, 0f);
                                        }, 3);
                                    }
                                    if (p.getLocation().distance(victim.getLocation()) < 5) {
                                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                            victim.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.BLOCKS, 2f, 0f);
                                        }, 10);
                                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                            victim.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.BLOCKS, 2f, 0f);
                                        }, 13);
                                    }

                                }
                            }
                        }
                    }
                }, countDownSeconds * 20L + 400, 20));

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Location loc : respawnChestLocations) {
                        ((Chest) (world.getBlockAt(loc).getState())).getBlockInventory().addItem(nether_star);
                    }
                }, countDownSeconds * 20L + 400);

                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    for (Player p : players) {
                        p.sendMessage("§a道具已刷新！");
                    }
                    for (Location loc : chestLocations) {
                        double spawnChance = random.nextDouble();
                        if (spawnChance <= 0.5) {//overall chance
                            int spawnNo = random.nextInt(totalWeight);
                            int counter = 0;
                            for (int i = 0; i < gadgets.size(); i++) {
                                counter += gadgetWeights.get(i);
                                if (spawnNo < counter) {
                                    ((Chest) (world.getBlockAt(loc).getState())).getBlockInventory().addItem(gadgets.get(i));
                                    break;
                                }
                            }
                        }
                    }
                }, countDownSeconds * 20L + 400 + 600, 1200));

                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    for (Player p : devils) {
                        if (tag5miranda.hasPlayer(p)) {
                            int counter = 0;
                            for (ItemStack i : p.getInventory().all(Material.ENDER_PEARL).values()) {
                                counter += i.getAmount();
                            }
                            if (counter < 1) {
                                p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                                p.sendMessage("§a获得末影珍珠！");
                            }
                        }
                    }
                }, countDownSeconds * 20L + 400 + 200, 1200));

                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    for (Player p : humans) {
                        if (tag5eunice.hasPlayer(p)) {
                            int counter = 0;
                            for (ItemStack i : p.getInventory().all(Material.CLOCK).values()) {
                                counter += i.getAmount();
                            }
                            if (counter < 1) {
                                p.getInventory().addItem(clock);
                                p.sendMessage("§a获得兔子的怀表！");
                            }
                        }
                    }
                }, countDownSeconds * 20L + 400 + 600, 1200));

                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    for (Player p : humans) {
                        if (tag5victoria.hasPlayer(p)) {
                            if (p.getInventory().contains(Material.MEDIUM_AMETHYST_BUD)) {
                                //p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 55, 0, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 55, 0, false, false));
                            } else if (p.getInventory().contains(Material.LARGE_AMETHYST_BUD)) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 55, 1, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 55, 0, false, false));
                                for (Entity e : p.getNearbyEntities(c.getInt("victoria.radiusX"),c.getInt("victoria.radiusY"),c.getInt("victoria.radiusZ"))) {
                                    if (players.contains(e)) {
                                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 50, 0, false, false));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }, countDownSeconds * 20L + 400, 50));

                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    long time = getTime(world);
                    if (time - startTime > gameTime) {
                        endGame("§e时间到，人类获胜！", humans);
                        return;
                    }
                    if (humans.isEmpty()) {
                        endGame("§e无人幸存，鬼获胜！", devils);
                        return;
                    }
                    if (devils.isEmpty()) {
                        endGame("§e鬼不复存在，人类获胜！", humans);
                        return;
                    }
                    tag5.getObjective("tag5").getScore("剩余人数").setScore(humans.size());
                    tag5.getObjective("tag5").getScore("剩余时间").setScore((int) ((gameTime - (time - startTime)) / 20));
                }, countDownSeconds * 20L + 400, 1));

            }
        };
    }

    public void clearChests() {
        for (Location l : chestLocations) {
            if (world.getBlockAt(l).getType() != Material.CHEST && world.getBlockAt(l).getType() != Material.TRAPPED_CHEST) {
                Bukkit.broadcastMessage("x " + l.getX() + " y " + l.getY() + " z " + l.getZ() + " 不是箱子！");
            }
            ((Chest) (world.getBlockAt(l).getState())).getBlockInventory().clear();
        }
        for (Location l : respawnChestLocations) {
            if (world.getBlockAt(l).getType() != Material.CHEST && world.getBlockAt(l).getType() != Material.TRAPPED_CHEST) {
                Bukkit.broadcastMessage("x " + l.getX() + " y " + l.getY() + " z " + l.getZ() + " 不是箱子！");
            }
            ((Chest) (world.getBlockAt(l).getState())).getBlockInventory().clear();
        }
    }
}

