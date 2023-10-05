package com.teampotato.enchantato_loot;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Mod("enchantato_loot")
public class EnchantatoLoot {
    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ENCHANTMENT_LIST;
    public static ForgeConfigSpec.BooleanValue INVERTED_MODE;

    static {
        ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
        CONFIG_BUILDER.push("Enchantato-Loot");
        INVERTED_MODE = CONFIG_BUILDER.comment("If you enable this, the 'DisabledEnchantments' will become 'AllowedEnchantments'").define("InvertedMode", false);
        ENCHANTMENT_LIST = CONFIG_BUILDER.defineList("DisabledEnchantments", new ObjectArrayList<>(), o -> true);
        CONFIG_BUILDER.pop();
        COMMON_CONFIG = CONFIG_BUILDER.build();
    }

    public EnchantatoLoot() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG, "enchantato-loot.toml");
    }

    public static ItemStack enchantItem(RandomSource pRandom, ItemStack pStack, int pLevel, boolean pAllowTreasure) {
        List<EnchantmentInstance> list = selectEnchantment(pRandom, pStack, pLevel, pAllowTreasure);
        boolean flag = pStack.is(Items.BOOK);
        if (flag) {
            pStack = new ItemStack(Items.ENCHANTED_BOOK);
        }

        for(EnchantmentInstance enchantmentinstance : list) {
            if (flag) {
                EnchantedBookItem.addEnchantment(pStack, enchantmentinstance);
            } else {
                pStack.enchant(enchantmentinstance.enchantment, enchantmentinstance.level);
            }
        }

        return pStack;
    }

    public static @NotNull List<EnchantmentInstance> selectEnchantment(RandomSource pRandom, @NotNull ItemStack pItemStack, int pLevel, boolean pAllowTreasure) {
        List<EnchantmentInstance> list = new ObjectArrayList<>();
        Item item = pItemStack.getItem();
        int i = pItemStack.getEnchantmentValue();
        if (i <= 0) {
            return list;
        } else {
            pLevel += 1 + pRandom.nextInt(i / 4 + 1) + pRandom.nextInt(i / 4 + 1);
            float f = (pRandom.nextFloat() + pRandom.nextFloat() - 1.0F) * 0.15F;
            pLevel = Mth.clamp(Math.round((float)pLevel + (float)pLevel * f), 1, Integer.MAX_VALUE);
            List<EnchantmentInstance> list1 = getAvailableEnchantmentResults(pLevel, pItemStack, pAllowTreasure);
            if (!list1.isEmpty()) {
                WeightedRandom.getRandomItem(pRandom, list1).ifPresent(list::add);

                while(pRandom.nextInt(50) <= pLevel) {
                    if (!list.isEmpty()) {
                        EnchantmentHelper.filterCompatibleEnchantments(list1, Util.lastOf(list));
                    }

                    if (list1.isEmpty()) {
                        break;
                    }

                    WeightedRandom.getRandomItem(pRandom, list1).ifPresent(list::add);
                    pLevel /= 2;
                }
            }

            return list;
        }
    }

    public static @NotNull List<EnchantmentInstance> getAvailableEnchantmentResults(int pLevel, @NotNull ItemStack pStack, boolean pAllowTreasure) {
        List<EnchantmentInstance> list = new ObjectArrayList<>();
        boolean flag = pStack.is(Items.BOOK);

        for(Enchantment enchantment : BuiltInRegistries.ENCHANTMENT) {
            if ((!enchantment.isTreasureOnly() || pAllowTreasure) && enchantment.isDiscoverable() && (onEnchant(enchantment, pStack) || (flag && enchantment.isAllowedOnBooks()))) {
                for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                    if (pLevel >= enchantment.getMinCost(i) && pLevel <= enchantment.getMaxCost(i)) {
                        list.add(new EnchantmentInstance(enchantment, i));
                        break;
                    }
                }
            }
        }

        return list;
    }

    @SuppressWarnings("DataFlowIssue")
    public static boolean onEnchant(Enchantment instance, ItemStack arg) {
        if (!EnchantatoLoot.INVERTED_MODE.get()) {
            if (EnchantatoLoot.ENCHANTMENT_LIST.get().contains(ForgeRegistries.ENCHANTMENTS.getKey(instance).toString())) return false;
            return instance.canEnchant(arg);
        } else {
            return instance.canEnchant(arg) && EnchantatoLoot.ENCHANTMENT_LIST.get().contains(ForgeRegistries.ENCHANTMENTS.getKey(instance).toString());
        }
    }
}
