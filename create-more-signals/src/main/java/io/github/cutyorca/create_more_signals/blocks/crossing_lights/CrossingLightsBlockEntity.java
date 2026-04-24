package io.github.cutyorca.create_more_signals.blocks.crossing_lights;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CrossingLightsBlockEntity extends SmartBlockEntity {

    public CrossingLightsBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.state = LightState.INVALID;
		this.lastReportedPower = false;
        setLazyTickRate(10);
	}

    private LightState state;
	private boolean lastReportedPower;

	public static enum LightState {
	    POWERED, INVALID;

    	public boolean isRightLight(float renderTime) {
        	if (this != POWERED)
            	return false;

	        float time = AnimationTickHolder.getRenderTime();
    	    return (time % 20) < 10;
    	}

    	public boolean isLeftLight(float renderTime) {
    	    if (this != POWERED)
    	        return false;

	        float time = AnimationTickHolder.getRenderTime();
    	    return ((time + 10) % 20) < 10;
    	}

    	public boolean isOff() {
    	    return this == INVALID;
    	}
	}

    @Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		NBTHelper.writeEnum(tag, "State", state);
		tag.putBoolean("Power", lastReportedPower);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		state = NBTHelper.readEnum(tag, "State", LightState.class);
		lastReportedPower = tag.getBoolean("Power");
		invalidateRenderBoundingBox();
	}

	@Override
	public void tick() {
    	super.tick();

    	if (level.isClientSide)
    	    return;

    	boolean powered = getBlockState().getValue(CrossingLightsBlock.POWERED);
    	if (lastReportedPower == powered)
        	return;

    	lastReportedPower = powered;
    	state = powered ? LightState.POWERED : LightState.INVALID;
	
    	setChanged();
    	notifyUpdate();
	}

    public boolean isPowered() {
        return getBlockState().getValue(CrossingLightsBlock.POWERED);
    }

	public LightState getState() {
	return state;
	}

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
}