package io.github.startsmercury.cosmic_player_deceleration.mixin.client;

import com.badlogic.gdx.math.Vector3;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import finalforeach.cosmicreach.entities.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
    /**
     * This entity is on some ground.
     */
    @Shadow
    public boolean isOnGround;

    /**
     * Should block collisions be ignored.
     */
    @Shadow
    public boolean noClip;

    /**
     * Fades out movement impulse in absence of no-clipping.
     *
     * @param onceVelocity
     * @param operation
     * @return
     */
    @WrapOperation(
        method = "updatePositions(Lfinalforeach/cosmicreach/world/Zone;D)V",
        at = @At(value = "INVOKE", ordinal = 1, target = """
            Lcom/badlogic/gdx/math/Vector3;setZero()Lcom/badlogic/gdx/math/Vector3;\
        """)
    )
    private Vector3 fadeImpulseWhenClipped(
        final Vector3 onceVelocity,
        final Operation<Vector3> operation
    ) {
        if (this.noClip) {
            return operation.call(onceVelocity);
        } else {
            final var friction = this.isOnGround ? 0.6F * 0.91F : 0.91F;
            onceVelocity.x *= friction;
            onceVelocity.z *= friction;
            return onceVelocity;
        }
    }
}
