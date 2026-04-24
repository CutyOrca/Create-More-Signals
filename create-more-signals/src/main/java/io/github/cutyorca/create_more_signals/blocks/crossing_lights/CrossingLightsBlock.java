package io.github.cutyorca.create_more_signals.blocks.crossing_lights;

import com.mojang.serialization.MapCodec;
import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsBlockEntities;
import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsBlocks;
import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsShapes;
import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsTags;
import io.github.cutyorca.create_more_signals.blocks.GenericSignalPoleHelper;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Predicate;

public class CrossingLightsBlock extends HorizontalDirectionalBlock implements IBE<CrossingLightsBlockEntity>, IWrenchable {

    public static final MapCodec<CrossingLightsBlock> CODEC = simpleCodec(CrossingLightsBlock::new);

    public static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty UPSIDE_DOWN = BooleanProperty.create("upside_down");

    public CrossingLightsBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(UPSIDE_DOWN, false).setValue(POWERED, false));
    }
    
    @Override
    protected MapCodec<? extends CrossingLightsBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING).add(UPSIDE_DOWN).add(POWERED));
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult ray) {
        IPlacementHelper helper = PlacementHelpers.get(CrossingLightsBlock.placementHelperId);

        if (helper.matchesItem(stack)) {
            ItemInteractionResult result = helper.getOffset(player, world, state, pos, ray)
                .placeInWorld(world, (BlockItem) stack.getItem(), player, hand, ray);
            return result.consumesAction() ? result : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if(state==null)
            return null;

        Direction facing = context.getHorizontalDirection().getOpposite();
        boolean upside_down = context.getClickedFace() == Direction.DOWN;

        return state.setValue(FACING,facing).setValue(UPSIDE_DOWN,upside_down).setValue(POWERED, context.getLevel()
			.hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block block, BlockPos fromPos, boolean isMoving) {

        if (level.isClientSide)
            return;
        boolean sidePower = level.hasNeighborSignal(pos);
        boolean belowPower = isPoweredBelow(level, pos, 16);

        boolean shouldBePowered = sidePower || belowPower;

        if (state.getValue(POWERED) != shouldBePowered) {
            level.setBlock(pos, state.setValue(POWERED, shouldBePowered), Block.UPDATE_CLIENTS);
        }
    }

    public static boolean isPoweredBelow(Level level, BlockPos pos, int maxDepth) {
        BlockPos check = pos.below();

        for (int i = 0; i < maxDepth; i++) {
            BlockState state = level.getBlockState(check);

            if (state.isAir())
                return false;

            if (level.hasNeighborSignal(check))
                return true;

            check = check.below();
        }

        return false;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockState rotated;
        boolean upsideDownChanged = false;

        if(context.getClickedFace().getAxis() != Direction.Axis.Y)
        {
            if (context.getClickedFace() == state.getValue(FACING).getOpposite()) {
                rotated = state.cycle(UPSIDE_DOWN);
                upsideDownChanged = true;
            }
            else
                rotated = state.setValue(FACING,context.getClickedFace());
        }else
        {
            rotated = getRotatedBlockState(state, context.getClickedFace());
        }

        if (!rotated.canSurvive(world, context.getClickedPos()))
            return InteractionResult.PASS;


        KineticBlockEntity.switchToBlockState(world, context.getClickedPos(), updateAfterWrenched(rotated, context));


        if (upsideDownChanged) {
            BlockPos currentPos = context.getClickedPos().below();
            for (int i = 0; i < 16; i++) {
                BlockState blockState = world.getBlockState(currentPos);
                if (CreateMoreSignalsBlocks.CROSSING_LIGHTS.has(blockState)) {
                    BlockState rotatedState = blockState.setValue(UPSIDE_DOWN, rotated.getValue(UPSIDE_DOWN));
                    KineticBlockEntity.switchToBlockState(world, currentPos, Block.updateFromNeighbourShapes(rotatedState, world, currentPos));
                } else if (!CreateMoreSignalsTags.AllBlockTags.SIGNAL_POLES.matches(blockState)) {
                    break;
                }
                currentPos = currentPos.below();
            }

            currentPos = context.getClickedPos().above();
            for (int i = 0; i < 16; i++) {
                BlockState blockState = world.getBlockState(currentPos);
                if (CreateMoreSignalsBlocks.CROSSING_LIGHTS.has(blockState)) {
                    BlockState rotatedState = blockState.setValue(UPSIDE_DOWN, rotated.getValue(UPSIDE_DOWN));
                    KineticBlockEntity.switchToBlockState(world, currentPos, Block.updateFromNeighbourShapes(rotatedState, world, currentPos));
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
            if (CreateMoreSignalsBlocks.CROSSING_LIGHTS.has(blockState)) {
                BlockState rotatedState = blockState.setValue(UPSIDE_DOWN, state.getValue(UPSIDE_DOWN));
                KineticBlockEntity.switchToBlockState(world, currentPos, Block.updateFromNeighbourShapes(rotatedState, world, currentPos));
            } else if (!CreateMoreSignalsTags.AllBlockTags.SIGNAL_POLES.matches(blockState)) {
                break;
            }
            currentPos = currentPos.below();
            if (!world.isClientSide) {
            boolean sidePower = world.hasNeighborSignal(pos);
            boolean belowPower = isPoweredBelow(world, pos, 16);

            boolean shouldBePowered = sidePower || belowPower;

            if (state.getValue(POWERED) != shouldBePowered) {
                world.setBlock(pos, state.setValue(POWERED, shouldBePowered), 3);
            }
            world.scheduleTick(pos, this, 2);
        }
    }

        currentPos = pos.above();
        for (int i = 0; i < 16; i++) {
            BlockState blockState = world.getBlockState(currentPos);
            if (CreateMoreSignalsBlocks.CROSSING_LIGHTS.has(blockState)) {
                BlockState rotatedState = blockState.setValue(UPSIDE_DOWN, state.getValue(UPSIDE_DOWN));
                KineticBlockEntity.switchToBlockState(world, currentPos, Block.updateFromNeighbourShapes(rotatedState, world, currentPos));
            } else if (!CreateMoreSignalsTags.AllBlockTags.SIGNAL_POLES.matches(blockState)) {
                break;
            }
            currentPos = currentPos.above();
        }
        
    }
	    @MethodsReturnNonnullByDefault
	    public static class PlacementHelper extends GenericSignalPoleHelper<Direction> {

  		private static final PlacementHelper instance = new PlacementHelper();

		    public static PlacementHelper get() {
			    return instance;
		    }

		    private PlacementHelper() {
    		super(
 				CreateMoreSignalsBlocks.CROSSING_LIGHTS::has,
				    state -> state.getValue(FACING).getAxis(),
				    FACING
			    );
		    }

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return stack ->
                CreateMoreSignalsBlocks.CROSSING_LIGHTS.isIn(stack);
            }
      }

    @Override
    public Class<CrossingLightsBlockEntity> getBlockEntityClass() {
        return CrossingLightsBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CrossingLightsBlockEntity> getBlockEntityType() {
        return CreateMoreSignalsBlockEntities.CROSSING_LIGHTS.get();
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return CreateMoreSignalsShapes.CROSSING_LIGHTS.get(pState.getValue(FACING));
	}
}