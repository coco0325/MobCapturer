package io.github.thebusybiscuit.mobcapturer.items;

import me.mrCookieSlime.Slimefun.Objects.handlers.ItemInteractionHandler;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.NotPlaceable;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

public class MobPellet extends SimpleSlimefunItem<ItemInteractionHandler> implements NotPlaceable {

	public MobPellet(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
		super(category, item, recipeType, recipe, new CustomItem(item, 2));
	}

	@Override
	public ItemInteractionHandler getItemHandler() {
		return (e, p, item) -> {
			if (isItem(item)) {
				e.getParentEvent().setCancelled(true);
				return true;
			}
			return false;
		};
	}

}
