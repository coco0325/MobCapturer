package io.github.thebusybiscuit.mobcapturer.items;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import me.mrCookieSlime.Slimefun.Objects.handlers.ItemInteractionHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.google.gson.JsonObject;

import io.github.thebusybiscuit.mobcapturer.InventoryAdapter;
import io.github.thebusybiscuit.mobcapturer.MobAdapter;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.NotPlaceable;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;

public class MobEgg<T extends LivingEntity> extends SimpleSlimefunItem<ItemInteractionHandler> implements NotPlaceable {

    private final NamespacedKey dataKey;
    private final NamespacedKey inventoryKey;
    private final MobAdapter<T> adapter;
    private final SlimefunItemStack Item;

    public MobEgg(Category category, SlimefunItemStack item, NamespacedKey dataKey, NamespacedKey inventoryKey, MobAdapter<T> adapter, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        this.dataKey = dataKey;
        this.inventoryKey = inventoryKey;
        this.adapter = adapter;
        this.Item = item;
    }

    @SuppressWarnings("unchecked")
    public ItemStack getEggItem(T entity) {
        JsonObject json = adapter.saveData(entity);

        ItemStack item = this.Item.clone();
        ItemMeta meta = item.getItemMeta();

        meta.setLore(adapter.getLore(json));
        meta.getPersistentDataContainer().set(dataKey, adapter, json);

        if (adapter instanceof InventoryAdapter) {
            FileConfiguration yaml = new YamlConfiguration();

            for (Map.Entry<String, ItemStack> entry : ((InventoryAdapter<T>) adapter).saveInventory(entity).entrySet()) {
                yaml.set(entry.getKey(), entry.getValue());
            }

            meta.getPersistentDataContainer().set(inventoryKey, PersistentDataType.STRING, yaml.saveToString());
        }

        item.setItemMeta(meta);

        return item;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ItemInteractionHandler getItemHandler() {
        return (e, p, item) -> {
            if (isItem(item)) {
                e.setCancelled(true);

                Block b = e.getClickedBlock();

                if (b != null) {
                    T entity = b.getWorld().spawn(b.getRelative(e.getParentEvent().getBlockFace()).getLocation(), adapter.getEntityClass());

                    PersistentDataContainer container = e.getItem().getItemMeta().getPersistentDataContainer();
                    JsonObject json = container.get(dataKey, adapter);
                    ItemUtils.consumeItem(e.getItem(), false);

                    if (json != null) {
                        adapter.apply(entity, json);

                        if (adapter instanceof InventoryAdapter) {
                            Map<String, ItemStack> inventory = new HashMap<>();

                            try (Reader reader = new StringReader(container.get(inventoryKey, PersistentDataType.STRING))) {
                                FileConfiguration yaml = YamlConfiguration.loadConfiguration(reader);

                                for (String key : yaml.getKeys(true)) {
                                    Object obj = yaml.get(key);

                                    if (obj instanceof ItemStack) {
                                        inventory.put(key, (ItemStack) obj);
                                    }
                                }
                            }
                            catch (IOException x) {
                                x.printStackTrace();
                            }

                            ((InventoryAdapter<T>) adapter).applyInventory(entity, inventory);
                        }
                    }
                }
                return true;
            }
            return false;
        };
    }

}
