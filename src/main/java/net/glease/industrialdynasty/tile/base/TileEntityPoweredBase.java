package net.glease.industrialdynasty.tile.base;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import net.glease.industrialdynasty.util.ConnectionConfiguration.ConnectionCategory;
import net.glease.industrialdynasty.util.ConnectionConfiguration.IOConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Setup args:
 * <ol>
 * <li>B:lostJobOnStop
 * <li>I:jobAmount
 * <li>I:wakeCheckInterval
 * <li>I:invSize
 * <li>S:invName
 * <li>I:energyCapacity
 * <li>I:interval				# ticks between two work
 * <li>I:energyPerWork			# energy consumed per work
 * </ol>
 * 
 * @author glease
 *
 */
public abstract class TileEntityPoweredBase extends TileEntityInventoryBase
		implements IEnergyReceiver {
	protected EnergyStorage buffer;
	protected int energyCapacity;
	protected int energyPerWork;
	protected int interval;
	protected int ticksSinceLastWork = 0;

	protected TileEntityPoweredBase(Object[] args) {
		super(args);
		this.energyCapacity = (int) args[5];
		interval = (int) args[6];
		energyPerWork = (int) args[7];
		createEnergyStorage();
	}

	// NBT
	// {
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		buffer.readFromNBT(tag.getCompoundTag("buffer"));
		energyCapacity = tag.getInteger("energyCapacity");
		interval = tag.getInteger("interval");
		ticksSinceLastWork = tag.getInteger("ticksSinceLastWork");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		buffer.writeToNBT(tag);
		tag.setInteger("energyCapacity", energyCapacity);
		tag.setInteger("interval", interval);
		tag.setInteger("ticksSinceLastWork", ticksSinceLastWork);
	}

	// }

	// IEnergyReceiver
	// {
	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return connConfig.getSideConnectionConfiguration(from).getEnergyIO() != IOConfig.NO;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive,
			boolean simulate) {
		return connConfig.test(from, 0, IOConfig.IN, ConnectionCategory.ENERGY) ? buffer
				.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return buffer.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return buffer.getMaxEnergyStored();
	}

	// }

	public boolean hasEnergy() {
		return buffer.extractEnergy(energyPerWork, true) == energyPerWork;
	}

	@Override
	public boolean shouldWakeUp() {
		return hasEnergy() && hasRawMaterial();
	}

	@Override
	public int doJob() {
		if (shouldWakeUp())
			return -1;
		buffer.extractEnergy(energyPerWork, false);
		return doWork();
	}

	/**
	 * Determines if has any raw material.
	 * @return true if material is sufficient.
	 */
	protected abstract boolean hasRawMaterial();

	/**
	 * Actual business logic goes in here. No need for further energy check and consumption.
	 * 
	 * @return
	 */
	protected abstract int doWork();

	protected void createEnergyStorage() {
		buffer = new EnergyStorage(energyCapacity);
	}
}
