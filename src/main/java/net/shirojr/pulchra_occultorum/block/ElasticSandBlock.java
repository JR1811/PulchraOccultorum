package net.shirojr.pulchra_occultorum.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ElasticSandBlock extends FallingBlock {
    public static final MapCodec<ElasticSandBlock> CODEC = createCodec(ElasticSandBlock::new);

    public ElasticSandBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        if (entity.bypassesLandingEffects()) {
            super.onEntityLand(world, entity);
        } else {
            this.bounce(entity);
        }
    }

    private void bounce(Entity entity) {
        Vec3d vec3d = entity.getVelocity();
        if (vec3d.y < 0.0) {
            double livingEntityMultiplier = entity instanceof LivingEntity ? 1.0 : 0.8;
            double bounceReduction = 0.5;
            entity.setVelocity(vec3d.x, -vec3d.y * livingEntityMultiplier * bounceReduction, vec3d.z);
        }
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.handleFallDamage(fallDistance, 0.4F, world.getDamageSources().fall());
    }

    @Override
    protected MapCodec<? extends FallingBlock> getCodec() {
        return CODEC;
    }
}
