package io.github.cutyorca.create_more_signals.blocks.signal_pole;

import com.mojang.serialization.MapCodec;
import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsBlockEntities;
import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsBlocks;
import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsShapes;
import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsTags;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.placement.PoleHelper;

import net.createmod.catnip.ghostblock.GhostBlocks;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Properties;
import java.util.function.Predicate;

public class SignalPoleBlock extends Block implements IWrenchable {

    public static final MapCodec<SignalPoleBlock> CODEC = simpleCodec(SignalPoleBlock::new);

    public static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

    public SignalPoleBlock(Properties pProperties) {
        super(pProperties);
    }
    
    @Override
    protected MapCodec<? extends SignalPoleBlock> codec() {
        return CODEC;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult ray) {
        IPlacementHelper helper = PlacementHelpers.get(SignalPoleBlock.placementHelperId);

        if (helper.matchesItem(stack)) {
            ItemInteractionResult result = helper.getOffset(player, world, state, pos, ray)
                .placeInWorld(world, (BlockItem) stack.getItem(), player, hand, ray);
            return result.consumesAction() ? result : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        boolean changed = false;

        if (changed) {
            BlockPos currentPos = context.getClickedPos().below();
            for (int i = 0; i < 16; i++) {
                BlockState blockState = world.getBlockState(currentPos);
                if (CreateMoreSignalsBlocks.SIGNAL_POLE.has(blockState)) {
                    KineticBlockEntity.switchToBlockState(world, currentPos, Block.updateFromNeighbourShapes(blockState, world, currentPos));
                } else if (!CreateMoreSignalsTags.AllBlockTags.SIGNAL_POLES.matches(blockState)) {
                    break;
                }
                currentPos = currentPos.below();
            }

            currentPos = context.getClickedPos().above();
            for (int i = 0; i < 16; i++) {
                BlockState blockState = world.getBlockState(currentPos);
                if (CreateMoreSignalsBlocks.SIGNAL_POLE.has(blockState)) {
                    KineticBlockEntity.switchToBlockState(world, currentPos, Block.updateFromNeighbourShapes(blockState, world, currentPos));
                } else if (!CreateMoreSignalsTags.AllBlockTags.SIGNAL_POLES.matches(blockState)) {
                    break;
                }
                currentPos = currentPos.above();
            }
        }


        if (world.getBlockState(context.getClickedPos()) != state)
            IWrenchable.playRotateSound(world, context.getClickedPos());

        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        neighborChanged(state, level, pos, null, pos, dynamicShape);
        level.scheduleTick(pos, this, 2);
    }
    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        BlockPos currentPos = pos.below();
        for (int i = 0; i < 16; i++) {
            BlockState blockState = world.getBlockState(currentPos);
            if (CreateMoreSignalsBlocks.SIGNAL_POLE.has(blockState)) {
                KineticBlockEntity.switchToBlockState(world, currentPos, Block.updateFromNeighbourShapes(blockState, world, currentPos));
            } else if (!CreateMoreSignalsTags.AllBlockTags.SIGNAL_POLES.matches(blockState)) {
                break;
            }
        }

        currentPos = pos.above();
        for (int i = 0; i < 16; i++) {
            BlockState blockState = world.getBlockState(currentPos);
            if (CreateMoreSignalsBlocks.SIGNAL_POLE.has(blockState)) {
                KineticBlockEntity.switchToBlockState(world, currentPos, Block.updateFromNeighbourShapes(blockState, world, currentPos));
            } else if (!CreateMoreSignalsTags.AllBlockTags.SIGNAL_POLES.matches(blockState)) {
                break;
            }
            currentPos = currentPos.above();
        }
        
    }

public static class PlacementHelper implements IPlacementHelper {

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return CreateMoreSignalsBlocks.SIGNAL_POLE::isIn;
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return state -> CreateMoreSignalsTags.AllBlockTags.SIGNAL_POLES.matches(state);
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {

        BlockPos above = pos.above();
            if (world.getBlockState(above).canBeReplaced()) {
                return PlacementOffset.success(above, s -> s);
            }

            BlockPos below = pos.below();
            if (world.getBlockState(below).canBeReplaced()) {
                return PlacementOffset.success(below, s -> s);
            }

            return PlacementOffset.fail();
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level,
                               BlockPos pos, CollisionContext ctx) {
        return CreateMoreSignalsShapes.SIGNAL_POLE;
    }
}