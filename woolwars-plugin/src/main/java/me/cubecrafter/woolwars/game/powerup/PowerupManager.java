package me.cubecrafter.woolwars.game.powerup;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PowerupManager {

    private final List<PowerUpData> powerUps = new ArrayList<>();

    public PowerupManager() {
        loadPowerups();
    }

    private void loadPowerups() {
        YamlConfiguration config = WoolWars.getInstance().getFileManager().getPowerUps();
        int loaded = 0;
        for (String id : config.getKeys(false)) {
            ItemStack displayedItem = ItemBuilder.fromConfig(config.getConfigurationSection(id + ".displayed-item")).build();
            List<String> holoLines = config.getStringList(id + ".hologram-lines");
            List<ItemStack> items = new ArrayList<>();
            for (String item : config.getStringList(id + ".items")) {
                ItemStack created = new ItemBuilder(item).build();
                items.add(created);
            }
            List<PotionEffect> effects = new ArrayList<>();
            for (String effect : config.getStringList(id + ".effects")) {
                PotionEffect created = TextUtil.getEffect(effect);
                effects.add(created);
            }
            PowerUpData data = new PowerUpData(displayedItem, holoLines, items, effects);
            powerUps.add(data);
            loaded++;
        }
        TextUtil.info("Loaded " + loaded + " powerup types!");
    }

    public void reload() {
        powerUps.clear();
        loadPowerups();
    }

    public PowerUpData getRandom() {
        return powerUps.get(new Random().nextInt(powerUps.size()));
    }

}
