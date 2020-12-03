package org.sharo.fencejumper

import net.minecraft.block.Block
import net.minecraft.client.entity.player.ClientPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.World
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.api.distmarker.Dist

@EventBusSubscriber
class ClientEventHandler {
    companion object {
        @JvmStatic
        @SubscribeEvent
        public fun onPlayerJump(event: LivingEvent.LivingJumpEvent) {
            val entity = event.getEntity()
            if (entity is ClientPlayerEntity) {
                if (
                    entity.movementInput.jump
                    && isPlayerNextToFence(entity)
                ) {
                    entity.motion = entity.motion.add(Vector3d(.0, .05, .0))
                }
            }
        }

        @JvmStatic
        private fun isPlayerNextToFence(player: ClientPlayerEntity): Boolean {
            val pos = Vector3d(
                player.prevPosX - 1,
                player.prevPosY,
                player.prevPosZ - 1
            )
            for (i in 0..3) {
                for (j in 0..3) {
                    if (
                        i != pos.x.toInt()
                        || j != pos.z.toInt()
                    ) {
                        val block = getBlock(
                            player.entityWorld,
                            BlockPos(pos.x + i, pos.y, pos.z + j)
                        )
                        if (
                            block is net.minecraft.block.FenceBlock
                            || block is net.minecraft.block.WallBlock
                        ) {
                            return true
                        }
                    }
                }
            }
            return false
        }

        @JvmStatic
        private fun getBlock(world: World, pos: BlockPos): Block {
            return world.getBlockState(pos).block
        }
    }
}
