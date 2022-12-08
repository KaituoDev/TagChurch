package fun.kaituo.tag5;


import fun.kaituo.GameUtils;
import fun.kaituo.event.PlayerChangeGameEvent;
import fun.kaituo.tag5.Tag5Game;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

import static fun.kaituo.GameUtils.unregisterGame;
import static fun.kaituo.GameUtils.world;

public class Tag5 extends JavaPlugin implements Listener {
    static List<Player> players;
    static long gameTime;
    Scoreboard scoreboard;
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
    List<Team> teams;
    BoundingBox box = new BoundingBox(-200, -64, -1800, 200, 256, -2200);

    public static Tag5Game getGameInstance() {
        return Tag5Game.getInstance();
    }

    @EventHandler
    public void displayChest(InventoryClickEvent ice) {
        if (ice.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (ice.getInventory().getHolder() instanceof Chest) {
            Location location = ((Chest) ice.getInventory().getHolder()).getBlock().getLocation();
            long x = location.getBlockX(); long y = location.getBlockY(); long z = location.getBlockZ();
            if (x == -1 && y == 25 && z == -2035) {
                ice.setCancelled(true);
            } else if (x == 1 && y == 25 && z == -2035) {
                ice.setCancelled(true);
            }
        }
    }

    /*
    @EventHandler
    public void preventClickingTrapDoor(PlayerInteractEvent pie) {
        if (!pie.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!(pie.getClickedBlock().getBlockData() instanceof TrapDoor) && !(pie.getClickedBlock().getBlockData() instanceof Gate)) {
            return;
        }
        if (!pie.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            if (box.contains(pie.getClickedBlock().getLocation().toVector())) {
                pie.setCancelled(true);
            }
        }
    }

     */
    @EventHandler
    public void onButtonClicked(PlayerInteractEvent pie) {
        if (!pie.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!pie.getClickedBlock().getType().equals(Material.OAK_BUTTON)) {
            return;
        }
        if (pie.getClickedBlock().getLocation().equals(new Location(world,0,26,-2031))) {
            Tag5Game.getInstance().startGame();
        }
    }
    @EventHandler
    public void setGameTime(PlayerInteractEvent pie) {
        Player player = pie.getPlayer();
        if (pie.getClickedBlock() == null) {
            return;
        }
        Location location = pie.getClickedBlock().getLocation();
        long x = location.getBlockX(); long y = location.getBlockY(); long z = location.getBlockZ();
        if (x == 0 && y == 25 && z == -2031) {
            switch ((int)gameTime) {
                case 3600:
                case 4800:
                case 6000:
                case 7200:
                case 8400:
                case 9600:
                case 10800:
                case 12000:
                case 13200:
                case 14400:
                case 15600:
                case 16800:
                    gameTime += 1200;
                    break;
                case 18000:
                    gameTime = 3600;
                    break;
                default:
                    break;
            }
            Sign sign = (Sign) pie.getClickedBlock().getState();
            sign.setLine(2,"当前时间为 " + gameTime/1200 + " 分钟");
            sign.update();
        }
        if (Tag5Game.getInstance().running) {
            return;
        }
        if (x == 2 && y == 25 && z == -2031) {
            if (tag5norden.hasPlayer(player)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "诺登", "§f");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag5norden " + player.getName());
            player.sendMessage("§f诺登§f： 欢迎回来，" + player.getName() + "大人");
        } else if (x == 3 && y == 25 && z == -2031) {
            if (tag5cheshirecat.hasPlayer(player)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "柴郡猫", "§d");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag5cheshirecat " + player.getName());
            player.sendMessage("§d柴郡猫§f： 能和你再说上话真是太好喵");
        } else if (x == 4 && y == 25 && z == -2031) {
            if (tag5redhat.hasPlayer(player)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "小红帽", "§c");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag5redhat " + player.getName());
            player.sendMessage("§c小红帽§f： .......好了,我们出发吧");
        } else if (x == 5 && y == 25 && z == -2031) {
            if (tag5alice.hasPlayer(player)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "爱丽丝", "§b");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag5alice " + player.getName());
            player.sendMessage("§b爱丽丝§f： 去寻觅爱的浪漫吧~☆");
        } else if (x == -4 && y == 25 && z == -2031) {
            if (tag5hein.hasPlayer(player)) {
                return;
            }
            broadcastDevilChoiceMessage(player, "海因", "§8");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag5hein " + player.getName());
            player.sendMessage("§8海因§f： 这里满溢着大量的黑之魂！太棒了！");
        } else if (x == -5 && y == 25 && z == -2031) {
            if (tag5baphomet.hasPlayer(player)) {
                return;
            }
            broadcastDevilChoiceMessage(player, "巴风特", "§8");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag5baphomet " + player.getName());
            player.sendMessage("§8巴风特§f： 我也没办法啦，希望你们能理解一下~");
        } else if (x == -2 && y == 25 && z == -2031) {
            if (tag5lindamayer.hasPlayer(player)) {
                return;
            }
            broadcastDevilChoiceMessage(player, "琳达梅尔", "§8");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag5lindamayer " + player.getName());
            player.sendMessage("§8琳达梅尔§f： 我要重建新的黒之裁判，将盘踞于大地之上的罪人处刑！");
        } else if (x == -3 && y == 25 && z == -2031) {
            if (tag5miranda.hasPlayer(player)) {
                return;
            }
            broadcastDevilChoiceMessage(player, "米兰达", "§8");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag5miranda " + player.getName());
            player.sendMessage("§8米兰达§f： 总有一天，这个虚假的世界会迎来崩坏的时刻......");
        }else if (x == 6 && y == 25 && z == -2031) {
            if (tag5eunice.hasPlayer(player)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "尤妮丝", "§f");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag5eunice " + player.getName());
            player.sendMessage("§f尤妮丝§f： 很好，让我们一起守护平等而纯洁的世界吧");
        }else if (x == 7 && y == 25 && z == -2031) {
            if (tag5dodo.hasPlayer(player)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "渡渡", "§7");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag5dodo " + player.getName());
            player.sendMessage("§7渡渡§f： 哼哼——！看来你的心已被吾辈俘获，是这么回事吧？");
        }else if (x == 8 && y == 25 && z == -2031) {
            if (tag5faketurtle.hasPlayer(player)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "假海龟", "§3");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag5faketurtle " + player.getName());
            player.sendMessage("§3假海龟§f： 是呢...只要有你在就没什么可怕的了......");
        }else if (x == 9 && y == 25 && z == -2031) {
            if (tag5victoria.hasPlayer(player)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "维多利雅", "§d");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag5victoria " + player.getName());
            player.sendMessage("§d维多利雅§f： 欢迎回来,主人大人");
        }
    }
    public void onEnable() {
        saveDefaultConfig();
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        players = new ArrayList<>();
        tag5norden = scoreboard.getTeam("tag5norden");
        tag5baphomet = scoreboard.getTeam("tag5baphomet");
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
        teams = List.of(tag5norden,tag5cheshirecat, tag5redhat,tag5alice,tag5eunice,tag5dodo,tag5faketurtle,tag5victoria,tag5miranda,tag5hein,tag5lindamayer,tag5baphomet);
        Bukkit.getPluginManager().registerEvents(this, this);
        gameTime = 3600;
        Sign sign = (Sign) world.getBlockAt(0, 25, -2031).getState();
        sign.setLine(2,"当前时间为 " + gameTime/1200 + " 分钟");
        sign.update();
        GameUtils.registerGame(getGameInstance());
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll((Plugin)this);
        if (players.size() > 0) {
            for (Player p : players) {
                p.teleport(new Location(world, 0.5,89.0,0.5));
                Bukkit.getPluginManager().callEvent(new PlayerChangeGameEvent(p, getGameInstance(), null));
            }
        }
        unregisterGame(getGameInstance());
    }

    private void broadcastHumanChoiceMessage(Player player, String role, String color) {
        for (Team team: teams) {
            for (String entryName : team.getEntries()) {
                Player p = Bukkit.getPlayer(entryName);
                if (p != null) {
                    if (p.isOnline()) {
                        p.sendMessage(color + player.getName()+" §r誓约了 " + color + role);
                    }
                }
            }
        }
    }
    private void broadcastDevilChoiceMessage(Player player, String role, String color) {
        for (Team team: teams) {
            for (String entryName : team.getEntries()) {
                Player p = Bukkit.getPlayer(entryName);
                if (p != null) {
                    if (p.isOnline()) {
                        p.sendMessage(color + player.getName()+" §r选择成为 " + color + role);
                    }
                }
            }
        }
    }
}

