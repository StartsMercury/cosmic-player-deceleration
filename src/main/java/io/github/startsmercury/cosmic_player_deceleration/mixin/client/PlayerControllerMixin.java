package io.github.startsmercury.cosmic_player_deceleration.mixin.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.entities.PlayerController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(PlayerController.class)
public abstract class PlayerControllerMixin {
    @Unique
    private float retainedMovementX = 0.0f;

    @Unique
    private float retainedMovementZ = 0.0f;

    @WrapOperation(
        method = "updateMovement(Lfinalforeach/cosmicreach/world/Zone;)V",
        at = @At(
            value = "INVOKE",
            slice = "z",
            target = "Lcom/badlogic/gdx/math/Vector3;set(FFF)Lcom/badlogic/gdx/math/Vector3;",
            ordinal = 0
        )
    )
    private Vector3 persistPreviousMovement(
        final Vector3 instance,
        final float x,
        final float y,
        final float z,
        final Operation<Vector3> original,
        final @Local(ordinal = 0) Entity entity,
        final @Local(ordinal = 1) float playerSpeed
    ) {
        if (entity.isOnGround || entity.noClip) {
            return original.call(instance, x, y, z);
        } else {
            return instance.set(this.retainedMovementX, 0.0F, this.retainedMovementZ);
        }
    }

    @ModifyExpressionValue(
        method = "updateMovement(Lfinalforeach/cosmicreach/world/Zone;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/badlogic/gdx/math/Vector3;nor()Lcom/badlogic/gdx/math/Vector3;",
            ordinal = 1
        )
    )
    private Vector3 captureNormalizedHorizontalMovement(
        final Vector3 original,
        final @Local(ordinal = 0) Entity entity
    ) {
        if (!entity.noClip) {
            this.retainedMovementX = original.x;
            this.retainedMovementZ = original.z;
        }
        return original;
    }

    @WrapOperation(
        method = "updateMovement(Lfinalforeach/cosmicreach/world/Zone;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/badlogic/gdx/math/Vector3;scl(F)Lcom/badlogic/gdx/math/Vector3;",
            ordinal = 3
        )
    )
    private Vector3 captureHorizontalMovement(
        final Vector3 instance,
        final float scalar,
        final Operation<Vector3> original,
        final @Local(ordinal = 0) Entity entity
    ) {
        if (!entity.noClip) {
            this.retainedMovementX = instance.x;
            this.retainedMovementZ = instance.z;
        }
        return original.call(instance, scalar);
    }

    @Inject(
        method = "updateMovement(Lfinalforeach/cosmicreach/world/Zone;)V",
        at = @At(
            value = "INVOKE",
            target = "Lfinalforeach/cosmicreach/entities/Entity;isInFluid()Z",
            ordinal = 0
        )
    )
    private void applyOffGroundDrag(
        final CallbackInfo callback,
        final @Local(ordinal = 0) Entity entity
    ) {
        if (!entity.noClip) {
            final var x = this.retainedMovementX;
            final var z = this.retainedMovementZ;

            final var len2 = x * x + z * z;
            if (len2 > 0.0F) {
                final var factor = (float) Math.pow(0.1, Gdx.graphics.getDeltaTime());

                if (len2 * factor * factor > 0.01F) {
                    this.retainedMovementX = x * factor;
                    this.retainedMovementZ = z * factor;
                    return;
                }
            }
        }

        // Fall-through: Apply Defaults
        this.retainedMovementX = 0.0F;
        this.retainedMovementZ = 0.0F;
    }
}
