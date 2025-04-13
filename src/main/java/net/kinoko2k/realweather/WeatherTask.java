package net.kinoko2k.realweather;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class WeatherTask {

    private final JavaPlugin plugin;

    public WeatherTask(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void run() {
        try {
            URL url = new URL("https://weather.tsukumijima.net/api/forecast?city=230010");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray forecasts = json.getJSONArray("forecasts");
            JSONObject today = forecasts.getJSONObject(0);
            String text = today.getString("text");

            boolean isClear = text.contains("晴");
            boolean isRain = text.contains("雨") || text.contains("雪");

            ZonedDateTime japanTime = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
            int hour = japanTime.getHour();
            boolean isDay = hour >= 6 && hour < 18;

            Bukkit.getScheduler().runTask(plugin, () -> {
                World world = Bukkit.getWorlds().get(0);
                if (world == null) return;

                if (isRain) {
                    world.setStorm(true);
                    world.setThundering(false);
                } else {
                    world.setStorm(false);
                    world.setThundering(false);
                }

                long time = isDay ? 1000L : 13000L;
                world.setTime(time);

                plugin.getLogger().info("天気と時間を更新しました: " + text + " / " + (isDay ? "昼" : "夜"));
                WeatherCache.todayWeather = text;
            });

        } catch (Exception e) {
            plugin.getLogger().warning("天気データの取得に失敗しました: " + e.getMessage());
        }
    }
}
