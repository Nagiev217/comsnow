package com.snowcom.plugin;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class SnowComCommand implements CommandExecutor, TabCompleter {

    private final SnowComPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public SnowComCommand(SnowComPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Эта команда только для игроков!");
            return true;
        }

        if (!player.hasPermission("com.use")) {
            player.sendMessage(ChatColor.RED + "У тебя нет прав на использование этой команды!");
            return true;
        }

        SnowComConfig cfg = plugin.getPluginConfig();

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(uuid)) {
            long elapsed = now - cooldowns.get(uuid);
            long cooldownMs = cfg.getCooldownSeconds() * 1000L;
            if (elapsed < cooldownMs) {
                long remaining = (cooldownMs - elapsed) / 1000 + 1;
                player.sendMessage(ChatColor.YELLOW + "Подожди ещё " + remaining + " сек. перед следующим выстрелом!");
                return true;
            }
        }
        cooldowns.put(uuid, now);

        fireSnowBeam(player, cfg);
        return true;
    }

    private void fireSnowBeam(Player player, SnowComConfig cfg) {
        World world = player.getWorld();
        Vector direction = player.getEyeLocation().getDirection().normalize();
        Location origin = player.getEyeLocation().clone().subtract(0, 0.6, 0);

        int amount = cfg.getAmount();
        int range = cfg.getRange();
        double speed = cfg.getSpeed();
        double stepSize = (double) range / amount;

        List<FallingBlock> spawnedBlocks = new ArrayList<>();
        world.playSound(origin, Sound.ENTITY_SNOWBALL_THROW, 1.5f, 0.8f);

        for (int i = 0; i < amount; i++) {
            final int index = i;
            double distance = stepSize * i;
            long delayTicks = (long) (i * 0.5);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) return;

                    Vector dir = direction.clone();
                    Location spawnLoc = origin.clone().add(dir.clone().multiply(distance));

                    @SuppressWarnings("deprecation")
                    FallingBlock fb = world.spawnFallingBlock(
                            spawnLoc,
                            Material.SNOW_BLOCK.createBlockData()
                    );

                    fb.setDropItem(false);
                    fb.setHurtEntities(false);
                    fb.setGravity(false);
                    fb.setVelocity(dir.clone().multiply(speed));
                    spawnedBlocks.add(fb);

                    if (cfg.isParticles()) {
                        spawnSnowflakeParticles(world, spawnLoc);
                    }

                    scheduleRemoval(fb, cfg.getLifeTimeSeconds());
                }
            }.runTaskLater(plugin, delayTicks);
        }
    }

    private void spawnSnowflakeParticles(World world, Location loc) {
        world.spawnParticle(Particle.SNOWFLAKE, loc, 6, 0.2, 0.2, 0.2, 0.02);
    }

    private void scheduleRemoval(FallingBlock fb, int lifeTimeSeconds) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (fb != null && !fb.isDead()) {
                    fb.remove();
                }
            }
        }.runTaskLater(plugin, lifeTimeSeconds * 20L);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
