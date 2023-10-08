package com.teampotato.enchantato_loot.mixin;

import com.teampotato.enchantato_loot.EnchantatoLoot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(EnchantWithLevelsFunction.class)
public abstract class EnchantWithLevelsFunctionMixin {
    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;enchantItem(Ljava/util/Random;Lnet/minecraft/world/item/ItemStack;IZ)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack onRun(Random enchantmentinstance, ItemStack pRandom, int pStack, boolean pLevel) {
        return EnchantatoLoot.enchantItem(enchantmentinstance, pRandom, pStack, pLevel);
    }
}
