package com.snowcom.plugin;

import org.bukkit.configuration.file.FileConfiguration;

public class SnowComConfig {

    private final int range;
    private final int amount;
    private final double speed;
    private final int lifeTimeSeconds;
    private final int cooldownSeconds;
    private final boolean particles;

    public SnowComConfig(FileConfiguration config) {
        this.range = config.getInt("range", 40);
        this.amount = config.getInt("amount", 25);
        this.speed = config.getDouble("speed", 1.2);
        this.lifeTimeSeconds = config.getInt("life-time-seconds", 4);
        this.cooldownSeconds = config.getInt("cooldown-seconds", 5);
        this.particles = config.getBoolean("particles", true);
    }

    public int getRange() { return range; }
    public int getAmount() { return amount; }
    public double getSpeed() { return speed; }
    public int getLifeTimeSeconds() { return lifeTimeSeconds; }
    public int getCooldownSeconds() { return cooldownSeconds; }
    public boolean isParticles() { return particles; }
}
