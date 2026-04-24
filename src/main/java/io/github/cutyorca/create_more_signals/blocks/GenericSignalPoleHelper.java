package io.github.cutyorca.create_more_signals.blocks;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.infrastructure.config.AllConfigs;

import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsBlocks;
import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsTags;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
public abstract class GenericSignalPoleHelper<T extends Comparable<T>> implements IPlacementHelper {
    protected final Predicate<BlockState> statePredicate;
    protected final Property<T> property;
    protected final Function<BlockState, Direction.Axis> axisFunction;

    public GenericSignalPoleHelper(Predicate<BlockState> statePredicate, Function<BlockState, Direction.Axis> axisFunction, Property<T> property) {
        this.statePredicate = statePredicate;
        this.axisFunction = axisFunction;
        this.property = property;
    }

    public boolean matchesAxis(BlockState state, Direction.Axis axis) {
        if (!statePredicate.test(state))
            return false;

        return axisFunction.apply(state) == axis;
    }

    public int attachedPoles(Level world, BlockPos pos, Direction direction) {
        BlockPos checkPos = pos.relative(direction);
        BlockState state = world.getBlockState(checkPos);
        int count = 0;
        while (matchesAxis(state, direction.getAxis())) {
            count++;
            checkPos = checkPos.relative(direction);
            state = world.getBlockState(checkPos);
        }
        return count;
    }

    @Override
    public Predicate<BlockState> getStatePredicate() {
        return state -> CreateMoreSignalsBlocks.CROSSING_LIGHTS.has(state) || CreateMoreSignalsTags.AllBlockTags.SIGNAL_POLES.matches(state);
    }

    @Override
    public PlacementOffset getOffset(Player player, Level world,
                                     BlockState state, BlockPos pos,
                                     BlockHitResult ray) {

        Direction[] directions = new Direction[] {Direction.UP, Direction.DOWN};

        for (Direction dir : directions) {

			int poles = attachedPoles(world, pos, dir);
            BlockPos newPos = pos.relative(dir, poles +1);
            BlockState newState = world.getBlockState(newPos);

			int range = AllConfigs.server().equipment.placementAssistRange.get();
			if (player != null) {
				AttributeInstance reach = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
				if (reach != null && reach.hasModifier(ExtendoGripItem.singleRangeAttributeModifier.id()))
					range += 4;
			}
			if (poles >= range)
				continue;
            if (!world.isEmptyBlock(newPos) && !newState.canBeReplaced())
                continue;
            if (newState.canBeReplaced()) {

                Direction facing = ray.getDirection();
                if(facing.getAxis()== Direction.Axis.Y)
                    return PlacementOffset.fail();
            }
            return PlacementOffset.success(newPos, placedState -> {

            if (placedState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {

                if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                    return placedState.setValue(
                        BlockStateProperties.HORIZONTAL_FACING,
                        state.getValue(BlockStateProperties.HORIZONTAL_FACING)
                    );
                }

                Direction playerFacing = player.getDirection().getOpposite();
                return placedState.setValue(
                    BlockStateProperties.HORIZONTAL_FACING,
                    playerFacing
                );
            }

            return placedState;
            });
        }

        return PlacementOffset.fail();
    }
}