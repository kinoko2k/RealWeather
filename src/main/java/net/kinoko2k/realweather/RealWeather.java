package net.kinoko2k.realweather;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RealWeather extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("[RealWeather]");
        getServer().getPluginManager().registerEvents(this, this);

        new BukkitRunnable() {
            @Override
            public void run() {
                new WeatherTask(RealWeather.this).run();
            }
        }.runTaskTimerAsynchronously(this, 0L, 20L * 60 * 60);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§a今日の天気は、《§f" + WeatherCache.todayWeather + "§a》です。");
    }
}