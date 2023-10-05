package com.teampotato.enchantato_loot.mixin;

import com.teampotato.enchantato_loot.EnchantatoLoot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantRandomlyFunction.class)
public abstract class EnchantRandomlyMixin {

    @Dynamic
    @Redirect(method = {"lambda$run$0", "m_80433_"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;canEnchant(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean onEnchant(Enchantment enchantment, ItemStack arg) {
        return EnchantatoLoot.onEnchant(enchantment, arg);
    }
}
