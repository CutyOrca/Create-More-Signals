package io.github.cutyorca.create_more_signals.blocks.three_color_light_signal;

import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ThreeColorLightSignalItem extends BlockItem {
    public ThreeColorLightSignalItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext) {

        //return super.place(pContext);
        IPlacementHelper placementHelper = PlacementHelpers.get(ThreeColorLightSignalBlock.placementHelperId);

        Level world = pContext.getLevel();
        Player player = pContext.getPlayer();
        BlockPos pos = pContext.getClickedPos();
        if(!pContext.replacingClickedOnBlock())
        {
            pos = pos.offset(pContext.getClickedFace().getOpposite().getNormal());
        }
        BlockState state = world.getBlockState(pos);
        Vec3 eyePos = player.getEyePosition();
        Vec3 viewVec = player.getViewVector(0);
        Vec3 endPos = eyePos.add(viewVec.x, viewVec.y, viewVec.z);
        BlockHitResult ray = pContext.getLevel().clip(new ClipContext(eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
//        BlockHitResult ray = (BlockHitResult)Minecraft.getInstance().hitResult;
        if(!placementHelper.matchesState(state))
            return super.place(pContext);

        ItemInteractionResult result = placementHelper.getOffset(player, world, state, pos, ray)
                .placeInWorld(world, this, player, pContext.getHand(), ray);

        if(result.consumesAction())
            return InteractionResult.sidedSuccess(world.isClientSide);
        else
            return super.place(pContext);
    }
}
