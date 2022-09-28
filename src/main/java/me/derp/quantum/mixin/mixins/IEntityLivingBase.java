package me.derp.quantum.mixin.mixins;

import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={EntityLivingBase.class})
public interface IEntityLivingBase {
    @Invoker(value="getArmSwingAnimationEnd")
    public int getArmSwingAnimationEnd();
}