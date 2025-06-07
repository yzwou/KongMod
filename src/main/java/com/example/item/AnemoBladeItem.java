package com.example.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;


import java.util.List;
import java.util.Objects;

public class AnemoBladeItem extends Item {
    private static final int MAX_CHARGE_TICKS = 40; // ✅ 最长2秒

    public AnemoBladeItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return MAX_CHARGE_TICKS;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) { return UseAction.BOW; }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player) || world.isClient) return;


        int usedTicks = MAX_CHARGE_TICKS - remainingUseTicks;

        if (usedTicks == 1) {
            world.playSound(
                    null,
                    player.getBlockPos(),
                    Registries.SOUND_EVENT.get(Identifier.of("template-mod", "anemo_charge")),
                    player.getSoundCategory(),
                    1.0f,
                    1.0f
            );
        }

        // ✅ 只在最大蓄力 tick 前 1 tick 触发一次 stopUsingItem
        if (usedTicks == MAX_CHARGE_TICKS - 1) {
            System.out.println("最大蓄力即将完成，强制释放！");
            player.stopUsingItem(); // ⚠️ 会触发 onStoppedUsing
            return;
        }

        Vec3d lookVec = player.getRotationVec(1.0F);
        Vec3d focusPoint = player.getPos().add(lookVec.multiply(2.5));

        Box area = new Box(player.getBlockPos()).expand(8);
        List<MobEntity> mobs = world.getEntitiesByClass(MobEntity.class, area, MobEntity::isAlive);

        for (MobEntity mob : mobs) {
            Vec3d dir = focusPoint.subtract(mob.getPos()).normalize();
            mob.setVelocity(dir.x * 0.2, 0.0, dir.z * 0.2); // ✅ 不抬升

            if (usedTicks % 10 == 0) {
                mob.damage(player.getDamageSources().playerAttack(player), 1.0f);
            }

            // ✅ 粒子
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        ParticleTypes.CLOUD,
                        mob.getX(), mob.getY() + 0.5, mob.getZ(),
                        3, 0.2, 0.2, 0.2, 0.01
                );
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;

        int chargeTicks = MAX_CHARGE_TICKS - remainingUseTicks;
        float power = Math.min(chargeTicks / 20.0f, 2.0f); // 最多2秒

        // ✅ 更精准的范围判定
        Box area = new Box(player.getPos().add(-8, -2, -8), player.getPos().add(8, 2, 8));
        List<MobEntity> mobs = world.getEntitiesByClass(MobEntity.class, area, LivingEntity::isAlive);

        for (MobEntity mob : mobs) {
            Vec3d knockDir = mob.getPos().subtract(player.getPos()).normalize();
            mob.addVelocity(knockDir.x * (1.1 * power), 0.4, knockDir.z * (1.1 * power));

            // ✅ 确保实际造成伤害
            boolean result = mob.damage(player.getDamageSources().playerAttack(player), 4.0f * power);
            System.out.println("命中: " + mob.getName().getString() + "，造成伤害: " + result);
        }
        world.playSound(
                null,
                player.getBlockPos(),
                Registries.SOUND_EVENT.get(Identifier.of("template-mod", "anemo_burst")),
                player.getSoundCategory(),
                1.0f,
                1.0f
        );
        int cooldownTicks = (int) (power * 60); // 1秒~2秒蓄力，CD随蓄力时长变化
        player.getItemCooldownManager().set(this, cooldownTicks);
    }
}
