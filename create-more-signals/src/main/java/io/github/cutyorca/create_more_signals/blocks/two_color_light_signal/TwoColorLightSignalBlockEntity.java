package io.github.cutyorca.create_more_signals.blocks.two_color_light_signal;

import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsTags;
import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;

public class TwoColorLightSignalBlockEntity extends SmartBlockEntity {
    private WeakReference<SignalBlockEntity> cachedSignalTE;
    public SignalBlockEntity.SignalState signalState;
    public final LerpedFloat lightPosition;
    public boolean isValid = false;
    public boolean isDistantSignal=false;
    protected boolean cachedWasUpsideDown = false;
    private int overrideLastingTicks = -1;

    public TwoColorLightSignalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        cachedSignalTE = new WeakReference<>(null);
        lightPosition = LerpedFloat.linear()
                .startWithValue(0);
        setLazyTickRate(10);
    }

    public boolean wasCachedSearchingUpsideDown() {
        return cachedWasUpsideDown;
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider lookupProvider, boolean clientPacket) {
        super.read(tag, lookupProvider, clientPacket);
        cachedWasUpsideDown = tag.getBoolean("CachedWasUpsideDown");
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider lookupProvider, boolean clientPacket) {
        super.write(tag, lookupProvider, clientPacket);
        tag.putBoolean("CachedWasUpsideDown", cachedWasUpsideDown);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }
    @Override
    public void tick() {

        super.tick();
        if (!level.isClientSide)
            return;

        if (overrideLastingTicks > 0) {
            overrideLastingTicks--;
            SignalBlockEntity signalBE = cachedSignalTE.get();
            if (signalBE != null && !signalBE.isRemoved()) {
                signalState = signalBE.getState();
            }
            isValid = signalState != SignalBlockEntity.SignalState.INVALID;
            boolean isActive = false;
            if (isValid)
                isActive = (signalState == SignalBlockEntity.SignalState.YELLOW && !isDistantSignal) || signalState == SignalBlockEntity.SignalState.GREEN;

            float currentTarget = lightPosition.getChaseTarget();
            int target = isActive ? 1 : 0;
            if (target != currentTarget) {
                lightPosition.setValue(currentTarget);
                lightPosition.chase(target, 0.05f, LerpedFloat.Chaser.LINEAR);
            }

            lightPosition.tickChaser();
            return;
        } else if (overrideLastingTicks == 0) {
            overrideLastingTicks--;
            cachedSignalTE.clear();
            signalState = null;
            updateSignalConnection();
        }



        SignalBlockEntity signalBlockEntity = cachedSignalTE.get();




        // whether the arm is up (i.e. signalling that a train can pass)
        boolean isActive=false;

        if (signalBlockEntity != null && !signalBlockEntity.isRemoved() && isValid) {
            signalState = signalBlockEntity.getState();

            if(signalState == SignalBlockEntity.SignalState.INVALID)
                isValid=false;
            else
                isActive = (signalState == SignalBlockEntity.SignalState.YELLOW && !isDistantSignal) || signalState == SignalBlockEntity.SignalState.GREEN;
        }

        float currentTarget = lightPosition.getChaseTarget();
        int target = isActive ? 1 : 0;
        if (target != currentTarget) {
            lightPosition.setValue(currentTarget);
            lightPosition.chase(target, 0.05f, LerpedFloat.Chaser.LINEAR);
        }

        lightPosition.tickChaser();




    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (overrideLastingTicks > 0) return;
        signalState = null;

        updateSignalConnection();
    }

    boolean updateSignalConnection() {
        if (updateSignalConnection(cachedWasUpsideDown)) {
            return true;
        } else if (updateSignalConnection(!cachedWasUpsideDown)) {
            cachedWasUpsideDown = !cachedWasUpsideDown;
            return true;
        }
        return false;
    }

    boolean updateSignalConnection(boolean upsideDown)
    {
        isValid=false;
        isDistantSignal=false;
        BlockPos currentPos = upsideDown?worldPosition.above():worldPosition.below();
        int twoColorLightSignalsBelow = 0;
        boolean constantOrder = !(getBlockState().getValue(TwoColorLightSignalBlock.UPSIDE_DOWN));
        //count downwards from the two_color_light_signal along the pole blocks, until a signal is reached
        for (int i = 0; i < 16; i++) {
            BlockState blockState = level.getBlockState(currentPos);
            BlockEntity blockEntity = level.getBlockEntity(currentPos);
            if (blockEntity instanceof SignalBlockEntity signal) {
                signalState = signal.getState();
                cachedSignalTE = new WeakReference<>(signal);
                isValid = true;
                SignalBlock.SignalType stateType = blockState.getValue(SignalBlock.TYPE);


                if (twoColorLightSignalsBelow == 0 != (upsideDown && constantOrder)) {
                    currentPos = upsideDown?(constantOrder?currentPos:worldPosition).below():worldPosition.above();
                    //if the signal is a cross-signal, and this two_color_light_signal is at the bottom of the stack,
                    //count upwards to find other two_color_light_signals. if one is found this two_color_light_signal becomes caution-type
                    for (int j = i + 1; j < 16; j++) {
                        blockState = level.getBlockState(currentPos);
                        blockEntity = level.getBlockEntity(currentPos);
                        if (blockEntity instanceof TwoColorLightSignalBlockEntity two_color_light_signal && two_color_light_signal.wasCachedSearchingUpsideDown() == this.wasCachedSearchingUpsideDown()) {
                            isDistantSignal = true;
                            break;
                        }
                        if (!CreateMoreSignalsTags.AllBlockTags.SIGNAL_POLES.matches(blockState)) {
                            break;
                        }
                        currentPos = upsideDown?currentPos.below():currentPos.above();
                    }
                }
                //the two_color_light_signal is valid as a danger-type two_color_light_signal
                // if it has exactly one other two_color_light_signal below,
                //or if no signal was found above
                break;

            }
            if(blockEntity instanceof TwoColorLightSignalBlockEntity)
            {
                twoColorLightSignalsBelow++;
                if(twoColorLightSignalsBelow>1)
                    break;
            }else if(!CreateMoreSignalsTags.AllBlockTags.SIGNAL_POLES.matches(blockState))
            {
                break;
            }
            currentPos = upsideDown?currentPos.above():currentPos.below();
        }
        return isValid;
    }

}