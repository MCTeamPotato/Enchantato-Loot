package com.teampotato.enchantato_loot.mixin;

import com.teampotato.enchantato_loot.EnchantatoLoot;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantWithLevelsFunction.class)
public abstract class EnchantWithLevelsFunctionMixin {
    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;enchantItem(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;IZ)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack onRun(RandomSource randomsource, ItemStack pStack, int pLevel, boolean pAllowTreasure) {
        return EnchantatoLoot.enchantItem(randomsource, pStack, pLevel, pAllowTreasure);
    }
}
