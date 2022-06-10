package me.cubecrafter.woolwars.game.kits;

import com.cryptomorin.xseries.particles.ParticleDisplay;
import com.cryptomorin.xseries.particles.XParticle;
import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.game.arena.GamePhase;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Ability {

    private final static List<UUID> cooldown = new ArrayList<>();

    private final String name;
    private final AbilityType abilityType;
    private final ItemStack item;
    private final int slot;
    private final List<PotionEffect> effects = new ArrayList<>();

    public Ability(YamlConfiguration kitConfig) {
        name = kitConfig.getString("ability.displayname");
        abilityType = AbilityType.valueOf(kitConfig.getString("ability.type"));
        item = ItemBuilder.fromConfig(kitConfig.getConfigurationSection("ability.item")).setTag("ability-item").build();
        slot = kitConfig.getInt("ability.item.slot");
        if (abilityType.equals(AbilityType.EFFECT)) {
            for (String effect : kitConfig.getStringList("ability.effects")) {
                effects.add(TextUtil.getEffect(effect));
            }
        }
    }

    public void use(Player player) {
        if (!ArenaUtil.getArenaByPlayer(player).getGamePhase().equals(GamePhase.ACTIVE_ROUND)) {
            player.sendMessage(TextUtil.color("&cYou can't use your ability yet!"));
            return;
        }
        if (cooldown.contains(player.getUniqueId())) {
            player.sendMessage(TextUtil.color("&cYou have already used your ability!"));
            return;
        }
        cooldown.add(player.getUniqueId());
        switch (abilityType) {
            case EFFECT:
                for (PotionEffect effect : effects) {
                    player.addPotionEffect(effect);
                }
                break;
            case KNOCKBACK_TNT:
                TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
                tnt.setMetadata("woolwars", new FixedMetadataValue(WoolWars.getInstance(), "knockback-tnt"));
                tnt.setIsIncendiary(false);
                tnt.setFuseTicks(20);
                break;
            case STEP_BACK:
                player.setVelocity(player.getLocation().getDirection().multiply(-1.5));
                ArenaUtil.playSound(player, "ENTITY_ENDERMAN_TELEPORT");
                break;
            case GOLDEN_SHELL:
                player.getInventory().setHelmet(new ItemBuilder("GOLDEN_HELMET").build());
                player.getInventory().setChestplate(new ItemBuilder("GOLDEN_CHESTPLATE").build());
                player.getInventory().setLeggings(new ItemBuilder("GOLDEN_LEGGINGS").build());
                player.getInventory().setBoots(new ItemBuilder("GOLDEN_BOOTS").build());
                Bukkit.getScheduler().runTaskLater(WoolWars.getInstance(), () -> {
                    player.getInventory().setArmorContents(null);
                }, 100L);
                break;
        }
        TextUtil.sendMessage(player, "&aYou used your keystone ability: " + name);
    }

    public static void removeCooldown(UUID uuid) {
        cooldown.remove(uuid);
    }

    private enum AbilityType {
        EFFECT,
        KNOCKBACK_TNT,
        STEP_BACK,
        GOLDEN_SHELL
    }

}
