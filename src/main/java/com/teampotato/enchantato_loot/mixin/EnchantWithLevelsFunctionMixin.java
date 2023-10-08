package com.teampotato.enchantato_loot.mixin;

import com.teampotato.enchantato_loot.EnchantatoLoot;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.functions.EnchantWithLevels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(EnchantWithLevels.class)
public abstract class EnchantWithLevelsFunctionMixin {
    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;enchantItem(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Lnet/minecraft/item/ItemStack;"))
    private ItemStack onRun(Random enchantmentdata, ItemStack pRandom, int pStack, boolean pLevel) {
        return EnchantatoLoot.enchantItem(enchantmentdata, pRandom, pStack, pLevel);
    }
}
