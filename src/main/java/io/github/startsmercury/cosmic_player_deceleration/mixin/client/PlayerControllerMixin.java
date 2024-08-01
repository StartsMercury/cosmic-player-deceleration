package io.github.startsmercury.cosmic_player_deceleration.mixin.client;

import com.badlogic.gdx.math.Vector3;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
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
    /**
     * Buffer vector before committing to {@code Entity.onceVelocity}.
     *
     * @see Entity#onceVelocity
     */
    @Unique
    private final Vector3 tmpImpulse = new Vector3();

    /**
     * Clear buffers of previous values before using them.
     *
     * @param callback the injector callback
     */
    @Inject(method = "updateMovement(Lfinalforeach/cosmicreach/world/Zone;)V", at = @At(value = "FIELD", ordinal = 0, target = "Lfinalforeach/cosmicreach/entities/Entity;onceVelocity:Lcom/badlogic/gdx/math/Vector3;"))
    private void clearBuffers(final CallbackInfo callback) {
        this.tmpImpulse.setZero();
    }

    /**
     * Replace client delta time with server's. This is essential for this mod
     * as the client delta time will result in fps-dependent movement speed.
     *
     * @param original the original client delta time
     * @return the server delta time
     */
    @ModifyExpressionValue(method = "updateMovement", at = @At(value = "INVOKE", target = "Lcom/badlogic/gdx/Graphics;getDeltaTime()F"))
    private float overrideWithServerDeltaTime(float original) {
        return 1.0F / 20.0F;
    }

    /**
     * Use the buffer instead of directly modifying stuff.
     *
     * @param original the original value
     * @return the buffered value
     */
    @ModifyExpressionValue(
        method = "updateMovement",
        at = @At(value = "FIELD", target = """
            Lfinalforeach/cosmicreach/entities/Entity;onceVelocity:\
            Lcom/badlogic/gdx/math/Vector3;\
        """)
    )
    private Vector3 replaceWithBuffer(final Vector3 original) {
        return this.tmpImpulse;
    }

    /**
     * Commits the impulse buffer.
     *
     * @param callback the injector callback
     * @param entity the player entity
     */
    @Inject(
        method = "updateMovement",
        at = @At(value = "FIELD", ordinal = 2, target = """
            Lfinalforeach/cosmicreach/entities/Entity;onceVelocity:\
            Lcom/badlogic/gdx/math/Vector3;\
        """)
    )
    private void commitImpulse(
        final CallbackInfo callback,
        final @Local(ordinal = 0) Entity entity
    ) {
        if (this.tmpImpulse.isZero()) {
            this.tmpImpulse.set(entity.onceVelocity);
        } else {
            entity.onceVelocity.set(this.tmpImpulse);
        }
    }
}
