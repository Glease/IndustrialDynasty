package net.glease.industrialdynasty.tile.base;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import net.glease.industrialdynasty.util.ConnectionConfiguration.ConnectionCategory;
import net.glease.industrialdynasty.util.ConnectionConfiguration.IOConfig;
import net.glease.industrialempire.common.delegates.GeneratorController;
import net.glease.industrialempire.common.delegates.GeneratorController.Generator;
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
 * <li>I:interval # ticks between two generate
 * </ol>
 * 
 * @author glease
 *
 */
public abstract class TileEntityGeneratorBase extends TileEntityInventoryBase
		implements IEnergyProvider, Generator {

	protected EnergyStorage buffer;
	protected int energyCapacity;
	protected GeneratorController generator;
	
	protected TileEntityGeneratorBase(Object[] args) {
		super(args);
		generator = new GeneratorController(this);
		energyCapacity = (int) args[5];
		generator.setInterval((int) args[6]);
		createBuffer();
	}
	
	// NBT
	// {
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		generator.readFromNBT(tag);
		NBTTagCompound t = tag.getCompoundTag("Generator");
		energyCapacity = t.getInteger("energyCapacity");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound t = generator.writeToNBT(tag).getCompoundTag("Generator");
		t.setInteger("energyCapacity", energyCapacity);
	}

	// }

	// IEnergyProvider
	// {

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract,
			boolean simulate) {
		return connConfig
				.test(from, 0, IOConfig.OUT, ConnectionCategory.ENERGY) ? buffer
				.extractEnergy(maxExtract, simulate) : 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return connConfig
				.test(from, 0, IOConfig.OUT, ConnectionCategory.ENERGY);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return connConfig
				.test(from, 0, IOConfig.OUT, ConnectionCategory.ENERGY) ? buffer
				.getEnergyStored() : 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return connConfig
				.test(from, 0, IOConfig.OUT, ConnectionCategory.ENERGY) ? buffer
				.getMaxEnergyStored() : 0;
	}

	// }

	@Override
	public int doJob() {
		super.updateEntity();
		return generator.doJob();
	}

	/**
	 * Create the buffer here. Overrides it to change the vanilla behavior.
	 */
	protected void createBuffer() {
		buffer = new EnergyStorage(energyCapacity);
	}

	@Override
	public EnergyStorage getBuffer() {
		return buffer;
	}
}
