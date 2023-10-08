package com.teampotato.enchantato_loot;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

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

    public static ItemStack enchantItem(Random pRandom, ItemStack pStack, int pLevel, boolean pAllowTreasure) {
        List<EnchantmentData> list = selectEnchantment(pRandom, pStack, pLevel, pAllowTreasure);
        boolean flag = pStack.getItem() == Items.BOOK;
        if (flag) {
            pStack = new ItemStack(Items.ENCHANTED_BOOK);
        }

        for(EnchantmentData enchantmentdata : list) {
            if (flag) {
                EnchantedBookItem.addEnchantment(pStack, enchantmentdata);
            } else {
                pStack.enchant(enchantmentdata.enchantment, enchantmentdata.level);
            }
        }

        return pStack;
    }

    public static @NotNull List<EnchantmentData> selectEnchantment(Random pRandom, @NotNull ItemStack pItemStack, int pLevel, boolean pAllowTreasure) {
        List<EnchantmentData> list = new ObjectArrayList<>();
        int i = pItemStack.getItemEnchantability();
        if (i > 0) {
            pLevel = pLevel + 1 + pRandom.nextInt(i / 4 + 1) + pRandom.nextInt(i / 4 + 1);
            float f = (pRandom.nextFloat() + pRandom.nextFloat() - 1.0F) * 0.15F;
            pLevel = MathHelper.clamp(Math.round((float) pLevel + (float) pLevel * f), 1, Integer.MAX_VALUE);
            List<EnchantmentData> list1 = getAvailableEnchantmentResults(pLevel, pItemStack, pAllowTreasure);
            if (!list1.isEmpty()) {
                list.add(WeightedRandom.getRandomItem(pRandom, list1));

                while (pRandom.nextInt(50) <= pLevel) {
                    EnchantmentHelper.filterCompatibleEnchantments(list1, Util.lastOf(list));
                    if (list1.isEmpty()) {
                        break;
                    }

                    list.add(WeightedRandom.getRandomItem(pRandom, list1));
                    pLevel /= 2;
                }
            }

        }
        return list;
    }

    public static @NotNull List<EnchantmentData> getAvailableEnchantmentResults(int pLevel, @NotNull ItemStack pStack, boolean pAllowTreasure) {
        List<EnchantmentData> list = new ObjectArrayList<>();
        boolean flag = pStack.getItem() == Items.BOOK;

        for(Enchantment enchantment : Registry.ENCHANTMENT) {
            if ((!enchantment.isTreasureOnly() || pAllowTreasure) && canEnchantLoot(enchantment) && (enchantment.canApplyAtEnchantingTable(pStack) || (flag && enchantment.isAllowedOnBooks()))) {
                for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                    if (pLevel >= enchantment.getMinCost(i) && pLevel <= enchantment.getMaxCost(i)) {
                        list.add(new EnchantmentData(enchantment, i));
                        break;
                    }
                }
            }
        }

        return list;
    }

    @SuppressWarnings("DataFlowIssue")
    public static boolean canEnchantLoot(Enchantment enchantment) {
        if (!INVERTED_MODE.get()) {
            if (ENCHANTMENT_LIST.get().contains(enchantment.getRegistryName().toString())) return false;
            return enchantment.isDiscoverable();
        } else {
            return enchantment.isDiscoverable() && ENCHANTMENT_LIST.get().contains(enchantment.getRegistryName().toString());
        }
    }
}
