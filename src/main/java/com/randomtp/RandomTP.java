package com.randomtp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class RandomTP extends JavaPlugin implements Listener {

    boolean gameRunning = false;
    List<Player> players = new ArrayList<>();

    int count = 0;
    int taskId;
    int last3sec;
    int count3sec = 0;
    int def_location = 29999983;

    @Override
    public void onEnable() {

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                p.sendMessage("사용법 : /randomtp start & stop & def");
            } else {
                if (args[0].equalsIgnoreCase("start")) {
                    if (gameRunning) {
                        p.sendMessage(ChatColor.RED + "게임이 이미 시작되어 있습니다.");
                    } else {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "게임이 시작되었습니다!");
                        gameRunning = true;
                        players.add(p);
                        count = 296;
                        startRandomTeleport();
                    }
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (!gameRunning) {
                        p.sendMessage(ChatColor.RED + "게임이 이미 중단되어 있습니다.");
                    } else {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "게임이 중단되었습니다!");
                        gameRunning = false;
                        players.clear();
                        Bukkit.getScheduler().cancelTask(taskId);
                        Bukkit.getScheduler().cancelTask(last3sec);
                    }
                } else if (args[0].equalsIgnoreCase("def")) {
                    if (gameRunning) {
                        p.sendMessage(ChatColor.RED + "게임 중에는 변경하실 수 없습니다.");
                    } else if (args.length < 2) {
                        p.sendMessage("사용법 : /randomtp def <location>");
                        p.sendMessage("location 적용 범위 \n X : -{location} ~ {location} \n Z : -{location} ~ {location}");
                    } else {
                        int arguments = Integer.parseInt(args[1]);
                        if (arguments < 0) {
                            p.sendMessage("음수 값은 입력할 수 없습니다.");
                        } else if (arguments > 29999983) {
                            p.sendMessage("29999983보다 큰 값은 입력할 수 없습니다.");
                        } else {
                            def_location = arguments;
                            p.sendMessage(ChatColor.GREEN + "텔레포트 반경이 " + def_location + "으로 설정되었습니다.");
                        }
                    }
                }
            }
        } else {
            if (args.length == 0) {
                System.out.println("사용법 : /randomtp start & stop");
            } else {
                if (args[0].equalsIgnoreCase("start")) {
                    if (gameRunning) {
                        System.out.println(ChatColor.RED + "게임이 이미 시작되어 있습니다.");
                    } else {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "게임이 시작되었습니다!");
                        System.out.println(ChatColor.GREEN + "게임이 시작되었습니다!");
                        gameRunning = true;
                        players.addAll(Bukkit.getOnlinePlayers());
                        count = 0;
                        startRandomTeleport();
                    }
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (!gameRunning) {
                        System.out.println(ChatColor.RED + "게임이 이미 중단되어 있습니다.");
                    } else {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "게임이 중단되었습니다!");
                        System.out.println(ChatColor.GREEN + "게임이 중단되었습니다!");
                        gameRunning = false;
                        players.clear();
                        Bukkit.getScheduler().cancelTask(taskId);
                        Bukkit.getScheduler().cancelTask(last3sec);
                    }
                } else if (args[0].equalsIgnoreCase("def")) {
                    if (gameRunning) {
                        System.out.println(ChatColor.RED + "게임 중에는 변경하실 수 없습니다.");
                    } else if (args.length < 2) {
                        System.out.println("사용법 : /randomtp def <location>");
                        System.out.println("location 적용 범위 \n X : -{location} ~ {location} \n Z : -{location} ~ {location}");
                    } else {
                        int arguments = Integer.parseInt(args[1]);
                        if (arguments < 0) {
                            System.out.println("음수 값은 입력할 수 없습니다.");
                        } else if (arguments > 29999983) {
                            System.out.println("29999983보다 큰 값은 입력할 수 없습니다.");
                        } else {
                            def_location = arguments;
                            System.out.println(ChatColor.GREEN + "텔레포트 반경이 " + def_location + "으로 설정되었습니다.");
                        }
                    }
                }
            }
        }
        return super.onCommand(sender, command, label, args);
    }

    void sendTitletoPlayer() {
        last3sec = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (count3sec == 0) {
                Bukkit.getScheduler().cancelTask(last3sec);
                return;
            }
            for (Player player : players) {
                player.sendTitle("", String.valueOf(count3sec), 0, 21, 0);
            }
            count3sec--;
        }, 0, 20);
    }

    void startRandomTeleport() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            count++;

            if (count == 297) {
                count3sec = 3;
                sendTitletoPlayer();
            }
            if (count >= 300) {
                Random random = new Random();
                int minX = def_location * -1;
                int maxX = def_location;
                int minZ = def_location * -1;
                int maxZ = def_location;

                for (Player player : players) {
                    if (player.isOnline()) {
                        double x = minX + (maxX - minX) * random.nextDouble();
                        double z = minZ + (maxZ - minZ) * random.nextDouble();
                        double y = player.getWorld().getHighestBlockYAt((int) x, (int) z) + 1;
                        Location location = new Location(player.getWorld(), x, y, z);
                        player.teleport(location);

                        player.setBedSpawnLocation(location, true);
                    }
                }

                count = 0;
            }
        }, 0, 20);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(taskId);
        Bukkit.getScheduler().cancelTask(last3sec);
    }
}
