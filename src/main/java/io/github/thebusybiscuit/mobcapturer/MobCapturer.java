package io.github.thebusybiscuit.mobcapturer;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cod;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Illusioner;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Mule;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.Ravager;
import org.bukkit.entity.Salmon;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Stray;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.Turtle;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.Witch;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.thebusybiscuit.mobcapturer.items.MobCannon;
import io.github.thebusybiscuit.mobcapturer.items.MobEgg;
import io.github.thebusybiscuit.mobcapturer.items.MobPellet;
import io.github.thebusybiscuit.mobcapturer.mobs.AnimalsAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.CatAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.ChestedHorseAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.CreeperAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.EndermiteAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.HorseAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.IronGolemAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.LlamaAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.MagicIllagerAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.MooshroomAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.PandaAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.ParrotAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.PhantomAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.PigAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.PufferFishAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.RabbitAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.RaiderAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.SheepAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.ShulkerAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.SkeletonAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.SlimeAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.SnowmanAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.StandardMobAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.TropicalFishAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.UndeadHorseAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.VexAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.WolfAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.ZombieAdapter;
import io.github.thebusybiscuit.mobcapturer.mobs.ZombieVillagerAdapter;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.skull.SkullItem;

public class MobCapturer extends JavaPlugin {

    private final NamespacedKey dataKey = new NamespacedKey(this, "captured_mob");
    private final NamespacedKey inventoryKey = new NamespacedKey(this, "mob_inventory");

    private final Map<EntityType, MobEgg<?>> adapters = new EnumMap<>(EntityType.class);

    private Category category;
    private Research research;
    private RecipeType recipeType;

    @Override
    public void onEnable() {

        new PelletListener(this);
        category = new Category(new CustomItem(SkullItem.fromHash("d429ff1d2015cb11398471bb2f895f7b4c3ccec201e4ad7a86ff24b744878c"), "&d生物捕捉"));
        research = new Research(32652, "生物捕捉", 45);

        SlimefunItemStack cannon = new SlimefunItemStack("MOB_CANNON", Material.BLAZE_ROD, "&6生物捕捉槍", "", "&e右鍵發射 &b捕捉膠囊", "", "&7剩餘使用次數 &e25" );
        SlimefunItemStack pellet = new SlimefunItemStack("MOB_CAPTURING_PELLET", "983b30e9d135b05190eea2c3ac61e2ab55a2d81e1a58dbb26983a14082664", "&b捕捉膠囊", "&7生物捕捉槍的彈藥");

        MobPellet mobPellet = new MobPellet(category, pellet, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] { SlimefunItems.COPPER_WIRE, SlimefunItems.SYNTHETIC_DIAMOND, SlimefunItems.COPPER_WIRE, SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.EGG), SlimefunItems.MAGIC_LUMP_2, SlimefunItems.COPPER_WIRE, SlimefunItems.SYNTHETIC_DIAMOND, SlimefunItems.COPPER_WIRE });

        research.addItems(mobPellet);
        mobPellet.register();

        MobCannon mobCannon = new MobCannon(this, category, cannon, mobPellet, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] { null, SlimefunItems.STEEL_INGOT, SlimefunItems.HOOK, SlimefunItems.STEEL_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.STEEL_INGOT, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.STEEL_INGOT, null });

        research.addItems(mobCannon);
        mobCannon.register();

        recipeType = new RecipeType(new CustomItem(cannon, "&6生物捕捉槍", "&7使用 &6生物捕捉槍", "&7捕捉該生物"));

        // Animals
        register("牛", EntityType.COW, new AnimalsAdapter<>(Cow.class), "9419f15ff54dae5d040f9b9d8eb2a8989e676710922a0ca164da613ca61e9");
        register("雞", EntityType.CHICKEN, new AnimalsAdapter<>(Chicken.class), "d429ff1d2015cb11398471bb2f895f7b4c3ccec201e4ad7a86ff24b744878c");
        register("豬", EntityType.PIG, new PigAdapter(), "527ad51dd773b72dca1c13e6f3547a83181aad91165282999bbdf13a3b3c9");
        register("蘑菇牛", EntityType.MUSHROOM_COW, new MooshroomAdapter(), "41b3b02e36ae9850df25aa09c2ca5a17b9c6616ce53e0b141ad360b6c67c");
        register("兔子", EntityType.RABBIT, new RabbitAdapter(), "63e06ed8809243e317393f6162679b2c1fe6911eda2d30cb99cfc82d347cb");
        register("綿羊", EntityType.SHEEP, new SheepAdapter(), "ff481f77347fe59c083665c9efbb49071d493ea2027454aee87735d63bf3b");
        register("海龜", EntityType.TURTLE, new AnimalsAdapter<>(Turtle.class), "15a45e24cadc18f305291af45a22fc8b3607a675baa31ed583d3a56b15223c5c");
        register("北極熊", EntityType.POLAR_BEAR, new AnimalsAdapter<>(PolarBear.class), "291abcab7a20b28195c0f1786db28c7670c2979243de71703b04e9d93f59aa8d");
        register("熊貓", EntityType.PANDA, new PandaAdapter(), "1ab24611bb37ce3971fdbf01ba3f11bd2e4c72f5d40b6d8d8d536d69e695cd0c");

        // Mobs
        register("史萊姆", EntityType.SLIME, new SlimeAdapter<>(Slime.class), "9330af17f8512ed3b49e78bca7ef2d83f2dc1e598a8cb542ecc3b6becee9f57");
        register("蜘蛛", EntityType.SPIDER, new StandardMobAdapter<>(Spider.class), "5d59aa78cb7e9b6ca6fee4121329059dd68afddc0c8b53a906b7953994e8a76");
        register("洞穴蜘蛛", EntityType.CAVE_SPIDER, new StandardMobAdapter<>(CaveSpider.class), "16617131250e578333a441fdf4a5b8c62163640a9d06cd67db89031d03accf6");
        register("苦力怕", EntityType.CREEPER, new CreeperAdapter(), "87c63d9079b75f90979783cf07ca726f65e3024415ac622a7c906cd25082af");
        register("夜魅", EntityType.PHANTOM, new PhantomAdapter(), "9381dfeac8a050d330fde058bad5f5e6f228f28cd379761c1147e17c4ed605b");
        register("蠹魚", EntityType.SILVERFISH, new StandardMobAdapter<>(Silverfish.class), "d06310a8952b265c6e6bed4348239ddea8e5482c8c68be6fff981ba8056bf2e");
        register("蝙蝠", EntityType.BAT, new StandardMobAdapter<>(Bat.class), "93c8aa3fde295fa9f9c27f734bdbab11d33a2e43e855accd7465352377413b");

        // Water Mobs
        register("魷魚", EntityType.SQUID, new StandardMobAdapter<>(Squid.class), "449088861fc1e14b605a5154d79fa7dd65e041a5c635d24744b3e152535");
        register("深海守衛", EntityType.GUARDIAN, new StandardMobAdapter<>(Guardian.class), "fe119aaa4999648a75b978efafa97edab1cdca1ff1d8301ba61cdc2db1606e22");
        register("遠古深海守衛", EntityType.ELDER_GUARDIAN, new StandardMobAdapter<>(ElderGuardian.class), "9a839d8256c81cf1db8da8ff3f7b80cce2f865b80c9f66aea5340e697ea3e219");
        register("海豚", EntityType.DOLPHIN, new StandardMobAdapter<>(Dolphin.class), "2480cd9577e2173e1c9de5e41318bd859696215a0a7de9242f01c01b8e6c06bf");

        // Tameables
        register("狼", EntityType.WOLF, new WolfAdapter(), "4399c973d6496d1d258492c28d4c95956ac3a253762bf15f7644af1f5728dd");
        register("野貓", EntityType.OCELOT, new AnimalsAdapter<>(Ocelot.class), "c579a743b66bd0b4d559898ed1b9857a49f1127d9d237bed3dc97bceb9379a5");
        register("貓貓", EntityType.CAT, new CatAdapter(), "f2154e385f9ccb187b53cf290fe9a24f98c7d583c368986c7806c312c3f29d79");
        register("鸚鵡", EntityType.PARROT, new ParrotAdapter(), "6eabd8fffc0371877e88ffc3cd0315200534dd2fcc6034588000736fb80033e5");

        // Nether
        register("烈焰使者", EntityType.BLAZE, new StandardMobAdapter<>(Blaze.class), "533acae6e075a578ccfc7dc2d5a15dbccfa8f59c609f9703889ef54c742c56");
        register("熔岩史萊姆", EntityType.MAGMA_CUBE, new SlimeAdapter<>(MagmaCube.class), "1185657c38acdd8f95e1d2cd1115bb0f11139ad2b3ce442267e69706d916e");
        register("EOE", EntityType.GHAST, new StandardMobAdapter<>(Ghast.class), "c442c228f099fdfc1c6b46dfc80b252d81f7fb1739deb16ee7a597c17f7c9");

        // Ender things
        register("界伏蚌", EntityType.SHULKER, new ShulkerAdapter(), "d04252216231b3f744c9ff4ace7084ae9f4164f8b384c65410848a19617af4d");
        register("終界蟎", EntityType.ENDERMITE, new EndermiteAdapter(), "3beac501e97db1cc035287d068a8eb538e55ef802f5cca25683933a243136c");

        // Golems
        register("雪人", EntityType.SNOWMAN, new SnowmanAdapter(), "2e4385d58fe46dd96422f31d35bbd1568e5819bbdb7a196c9f113424582cf977");
        register("鐵巨人", EntityType.IRON_GOLEM, new IronGolemAdapter(), "c442c228f099fdfc1c6b46dfc80b252d81f7fb1739deb16ee7a597c17f7c9");

        // Illagers
        register("女巫", EntityType.WITCH, new RaiderAdapter<>(Witch.class), "afbdceef773d959b49ddd9615f4269c176e23154d45752667428dc4e3fd4d");
        register("衛道士", EntityType.VINDICATOR, new RaiderAdapter<>(Vindicator.class), "8e8e3de7718a54553dd2fc5b2415a08b05f2339b772fe181b65c507fda4e34c1");
        register("掠奪者", EntityType.PILLAGER, new RaiderAdapter<>(Pillager.class), "8fd4983e30b277f0b97b7d8c6f8a0358201be226a2c55e2a0d390c3942ec2df5");
        register("劫毀獸", EntityType.RAVAGER, new RaiderAdapter<>(Ravager.class), "79b625b80cfb0baf04eebbd2cb1ff9f1010b02f4df21b3baf86eb812ab7eba8b");
        register("喚魔者", EntityType.EVOKER, new MagicIllagerAdapter<>(Evoker.class), "ff1eeb387d55b0886a69b6ec62a6e69706f32aba2547e10583060b976341f9be");
        register("幻術師", EntityType.ILLUSIONER, new MagicIllagerAdapter<>(Illusioner.class), "ff1eeb387d55b0886a69b6ec62a6e69706f32aba2547e10583060b976341f9be");
        register("惱鬼", EntityType.VEX, new VexAdapter(), "dc7eb861fd9999bf87a300e3ddd03c57313ddfba221d1c7d4bd62cef31446ca1");

        // Fish
        register("鱈魚", EntityType.COD, new StandardMobAdapter<>(Cod.class), "bd29b25579f9d3a67b612ae8ef96b31feca6c9e7e6c70ac81156d778cbe7db9d");
        register("鮭魚", EntityType.SALMON, new StandardMobAdapter<>(Salmon.class), "5c46c568e8b5c55853a92869ea19c00b7720c328a2f16c5950b9e2e897fc27a1");
        register("河豚", EntityType.PUFFERFISH, new PufferFishAdapter(), "5d5e7d191478efafe23a654de802760f42a0dd83dfc9817f87d460fcf32978df");
        register("熱帶魚", EntityType.TROPICAL_FISH, new TropicalFishAdapter(), "2e4385d58fe46dd96422f31d35bbd1568e5819bbdb7a196c9f113424582cf977");

        // Horses
        register("馬", EntityType.HORSE, new HorseAdapter(), "5c6d5abbf68ccb2386bf16af25ac38d8b77bb0e043152461bd97f3f630dbb8bc");
        register("驢子", EntityType.DONKEY, new ChestedHorseAdapter<>(Donkey.class), "db522f6d77c0696c9d1f2ad49bfa3cb8205a5e623af1c420bd740dc471914e97");
        register("騾子", EntityType.MULE, new ChestedHorseAdapter<>(Mule.class), "e4ad78f7ada7c6376449ef949c9c87fdece882b5a2f14cfbf8eac6fea657f4c7");
        register("殭屍馬", EntityType.ZOMBIE_HORSE, new UndeadHorseAdapter<>(ZombieHorse.class), "ec5b6f8ef1d75f73a5290c9367d2b9b823bc963de2a366fd6550bcace2751205");
        register("骷髏馬", EntityType.SKELETON_HORSE, new UndeadHorseAdapter<>(SkeletonHorse.class), "9dc084b7874268973006c897a03d8906cc9b3df8c39bce93d87ec0df507bbe0d");
        register("羊駝", EntityType.LLAMA, new LlamaAdapter<>(Llama.class), "5cbc6bd92728d79cfa6d8f23cbae9d912f495920b9e95ef691a1967fef8a4453");
        register("商駝", EntityType.TRADER_LLAMA, new LlamaAdapter<>(TraderLlama.class), "5cbc6bd92728d79cfa6d8f23cbae9d912f495920b9e95ef691a1967fef8a4453");

        // Skeletons
        register("骷髏", EntityType.SKELETON, new SkeletonAdapter<>(Skeleton.class), "377055cadacbb0f8f35c1d18acc2ed86e0bcc6d73dda71e4c59f7ea28b7b27b6");
        register("凋零骷髏", EntityType.WITHER_SKELETON, new SkeletonAdapter<>(WitherSkeleton.class), "337223d01906ab63af1a15988343b8637e85930b905c35125b545b398c59e1c5");
        register("流髑", EntityType.STRAY, new SkeletonAdapter<>(Stray.class), "5b45aae241779f0617ffaff468f3f2cf666d2f8a803002f9ae1ba0f14ed79fdd");

        // Zombies
        register("殭屍", EntityType.ZOMBIE, new ZombieAdapter<>(Zombie.class), "77f844bfea25429d45e1fcf96ef6654dfaaa6fc902dc1b6b68c0abc1343447");
        register("沉屍", EntityType.DROWNED, new ZombieAdapter<>(Drowned.class), "13a29552b3abaf388145be8a9067e5ea4d1af945e7bf5e2ca852cec87fd1b17b");
        register("屍殼", EntityType.HUSK, new ZombieAdapter<>(Husk.class), "40fd8d86e7057aae33f5d79dcb03685c88c8137cfcb2349bc874b9f87e934276");
        register("殭屍村民", EntityType.ZOMBIE_VILLAGER, new ZombieVillagerAdapter(), "364b4c94dda909ecc778db93572b847a5df51c4a49894d6a9df59b8df97842c5");

        research.register();
    }

    public <T extends LivingEntity> void register(String name, EntityType type, MobAdapter<T> adapter, String eggTexture) {
        SlimefunItemStack itemstack = new SlimefunItemStack("MOB_EGG_" + type.toString(), eggTexture, "&a生物膠囊 &7(" + name + ")", "", "&7對方塊右鍵", "&r釋放膠囊內的生物");

        MobEgg<T> egg = new MobEgg<>(category, itemstack, dataKey, inventoryKey, adapter, recipeType, new ItemStack[] { null, null, null, null, new CustomItem(SkullItem.fromHash(eggTexture), "&r" + name), null, null, null, null });

        egg.register();

        if (!egg.isDisabled()) {
            research.addItems(egg);
            adapters.put(type, egg);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Optional<ItemStack> capture(LivingEntity entity) {
        MobEgg egg = adapters.get(entity.getType());

        if (egg != null) {
            ItemStack item = egg.getEggItem(entity);
            return Optional.of(item);
        }
        else {
            return Optional.empty();
        }
    }

}
