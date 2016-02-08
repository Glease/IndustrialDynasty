package net.glease.industrialdynasty.tile.base;

import net.glease.industrialempire.common.delegates.JobController;
import net.glease.industrialempire.common.delegates.JobController.JobControlled;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Setup args:
 * <ol>
 * <li>B:lostJobOnStop
 * <li>I:jobAmount
 * <li>I:wakeCheckInterval
 * </ol>
 * NBT tag should be structured into something like this:
 * (root)
 * ©À Job
 * ©À Energy
 * ©À ...
 * ¡£
 * ¡£
 * ¡£
 * @author glease
 *
 */
public abstract class TileEntityBase extends TileEntity implements JobControlled{

	protected JobController job;

	public TileEntityBase(Object[] args) {
		job.setUp((boolean) args[0], (int) args[1], (int) args[2]);
	}

	@Override
	public void updateEntity() {
		if(worldObj.isRemote)
			job.update();
	}

	public boolean isWorking() {
		return job.isWorking();
	}

	public void wakeUp() {
		job.wakeUp();
	}

	public void shutdown() {
		job.shutdown();
	}

	public void setWorking(boolean working) {
		job.setWorking(working);
	}

	public void resetJob() {
		job.resetJob();
	}

	public int getJobDone() {
		return job.getJobDone();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		job.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		job.writeToNBT(tag);
	}
}
