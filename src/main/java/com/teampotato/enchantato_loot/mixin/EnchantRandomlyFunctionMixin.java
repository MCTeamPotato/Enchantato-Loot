package com.teampotato.enchantato_loot.mixin;

import com.teampotato.enchantato_loot.EnchantatoLoot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.functions.EnchantRandomly;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(EnchantRandomly.class)
public abstract class EnchantRandomlyFunctionMixin {

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"))
    private Stream<Enchantment> onRun(@NotNull Stream<Enchantment> instance, Predicate<Enchantment> predicate) {
        return instance.filter(Enchantment::isDiscoverable).filter(EnchantatoLoot::canEnchantLoot);
    }
}
