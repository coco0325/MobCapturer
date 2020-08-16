package io.github.thebusybiscuit.mobcapturer.items;

import me.mrCookieSlime.Slimefun.Objects.handlers.ItemInteractionHandler;
import me.mrCookieSlime.Slimefun.SlimefunPlugin;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import io.github.thebusybiscuit.mobcapturer.MobCapturer;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class MobCannon extends SimpleSlimefunItem<ItemInteractionHandler> {

	public static final int MAX_USES = 25;

	private static final NamespacedKey usageKey = new NamespacedKey(SlimefunPlugin.instance, "mobcannon_usage");

	private final MobCapturer plugin;
	private final MobPellet pellet;
	
	public MobCannon(MobCapturer plugin, Category category, SlimefunItemStack item, MobPellet pellet, RecipeType recipeType, ItemStack[] recipe) {
		super(category, item, recipeType, recipe);
		
		this.plugin = plugin;
		this.pellet = pellet;
	}

	@Override
	public ItemInteractionHandler getItemHandler() {
		return (e, p, item) -> {
			if (isItem(item)) {
				e.setCancelled(true);
				if (consumeAmmo(e.getPlayer(), pellet)) {
					ItemMeta itemMeta = item.getItemMeta();
					int currentUses = itemMeta.getPersistentDataContainer()
							.getOrDefault(usageKey, PersistentDataType.INTEGER, MAX_USES);
					if (currentUses == 1) {
						p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
						item.setAmount(0);
					}
					else {
						itemMeta.getPersistentDataContainer().set(
								usageKey, PersistentDataType.INTEGER, --currentUses
						);
						List<String> itemLore = itemMeta.getLore();
						itemLore.set(3, ChatColor.translateAlternateColorCodes('&',
								"&7剩餘使用次數 &e" + currentUses));
						itemMeta.setLore(itemLore);
						item.setItemMeta(itemMeta);
					}
					Snowball projectile = e.getPlayer().launchProjectile(Snowball.class);
					projectile.setMetadata("mob_capturing_cannon", new FixedMetadataValue(plugin, e.getPlayer().getUniqueId()));
				}
				return true;
			}
			return false;
		};
	}

	private boolean consumeAmmo(Player p, MobPellet pellet) {
		if (p.getGameMode() == GameMode.CREATIVE) {
			return true;
		}
		
		for (ItemStack item : p.getInventory()) {
			if (pellet.isItem(item)) {
				ItemUtils.consumeItem(item, false);
				return true;
			}
		}
		
		return false;
	}
	/*
	private static final NamespacedKey usageKey = new NamespacedKey(SlimefunPlugin.instance, "stormstaff_usage");

	public StormStaff(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
		super(category, item, recipeType, recipe, getCraftedOutput());
	}

	private static ItemStack getCraftedOutput() {
		ItemStack item = SlimefunItems.STAFF_STORM.clone();
		ItemMeta im = item.getItemMeta();
		List<String> lore = im.getLore();

		lore.set(4, ChatColor.translateAlternateColorCodes('&', "&7剩餘使用次數 &e" + MAX_USES));

		im.setLore(lore);
		item.setItemMeta(im);
		return item;
	}

	@Override
	public ItemInteractionHandler getItemHandler() {
		return (e, p, item) -> {
			if (isItem(item)) {
				if (!item.hasItemMeta()) return false;
				ItemMeta itemMeta = item.getItemMeta();
				if (!itemMeta.hasLore()) return false;
				List<String> itemLore = itemMeta.getLore();

				ItemStack sfItem = getItem();
				ItemMeta sfItemMeta = sfItem.getItemMeta();
				List<String> sfItemLore = sfItemMeta.getLore();

				// Index 1 and 3 in SlimefunItems.STAFF_STORM has lores with words and stuff so we check for them.
				if (itemLore.size() < 6 && itemLore.get(1).equals(sfItemLore.get(1)) && itemLore.get(3).equals(sfItemLore.get(3))) {
					if (p.getFoodLevel() >= 4 || p.getGameMode() == GameMode.CREATIVE) {
						// Get a target block with max. 30 blocks of distance
						Location loc = p.getTargetBlock(null, 30).getLocation();

						if (loc.getWorld() != null && loc.getChunk().isLoaded() && loc.getBlock().getType() != Material.AIR) {
							if (loc.getWorld().getPVP() && SlimefunPlugin.getProtectionManager().hasPermission(p, loc, ProtectableAction.BREAK_BLOCK)) {
								loc.getWorld().strikeLightning(loc);

								if (p.getInventory().getItemInMainHand().getType() != Material.SHEARS && p.getGameMode() != GameMode.CREATIVE) {
									FoodLevelChangeEvent event = new FoodLevelChangeEvent(p, p.getFoodLevel() - 4);
									Bukkit.getPluginManager().callEvent(event);
									p.setFoodLevel(event.getFoodLevel());
								}

								int currentUses = itemMeta.getPersistentDataContainer()
									.getOrDefault(usageKey, PersistentDataType.INTEGER, MAX_USES);

								e.setCancelled(true);
								if (currentUses == 1) {
									p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
									item.setAmount(0);
								}
								else {
									itemMeta.getPersistentDataContainer().set(
										usageKey, PersistentDataType.INTEGER, --currentUses
									);
									itemLore.set(4, ChatColor.translateAlternateColorCodes('&',
											"&7剩餘使用次數 &e" + currentUses));
									itemMeta.setLore(itemLore);
									item.setItemMeta(itemMeta);
								}
								return true;
							}
							else {
								SlimefunPlugin.getLocal().sendMessage(p, "messages.no-pvp", true);
							}
						}
					}
					else {
						SlimefunPlugin.getLocal().sendMessage(p, "messages.hungry", true);
					}
					return true;
				}
			}
			return false;
		};
	}
	 */
}
