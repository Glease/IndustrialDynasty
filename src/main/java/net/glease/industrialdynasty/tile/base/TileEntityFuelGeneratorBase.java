package net.glease.industrialdynasty.tile.base;

import net.glease.industrialdynasty.util.CachedFilter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;

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
 * <li>I:power # power generated per generate
 * <li>CachedFilter<T>:filter # filter for fuel filtering.
 * </ol>
 * 
 * Default behavior:
 * <ul>
 * <li>shutdown on insufficent fuel
 * <li>fixed generation rate( defined through <code>args[7]</code> )
 * </ul>
 * 
 * @author glease
 *
 * @param <T>
 *            {@link ItemStack} or {@link FluidStack}, particularly. Other type
 *            is also possible, but that may be tricky.
 */
public abstract class TileEntityFuelGeneratorBase<T> extends
		TileEntityGeneratorBase {
	protected CachedFilter<T> filter;
	protected T[] fuel;
	protected int cachedFuel = 0;
	protected int power;

	@SuppressWarnings("unchecked")
	public TileEntityFuelGeneratorBase(Object[] args) {
		super(args);
		power = (int) args[7];
		if (args[8] instanceof CachedFilter<?>)
			filter = (CachedFilter<T>) args[8];
		else
			filter = new CachedFilter<>(false);
		createFuelHolder();
	}

	// NBT
	// {
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound t = tag.getCompoundTag("Generator");
		readFuelNBT(t.getTagList("Fuel", 10));
		cachedFuel = t.getInteger("cachedFuel");
		power = t.getInteger("power");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound t = new NBTTagCompound();
		t.setTag("Fuel", writeFuelNBT(new NBTTagList()));
		t.setInteger("cachedFuel", cachedFuel);
		t.setInteger("power", power);
	}

	protected abstract void readFuelNBT(NBTTagList tag);

	protected abstract NBTTagList writeFuelNBT(NBTTagList tag);

	// }
	@Override
	public int generate() {
		cachedFuel--;
		if (cachedFuel == 0) {
			cachedFuel += cacheFuel();
			if (cachedFuel < 1)// cacheFuel() may return negative values.
				shutdown();
		}
		return power;
	}

	@Override
	public boolean shouldWakeUp() {
		cachedFuel += cacheFuel();
		return cachedFuel > 0;
	}

	@Override
	public void onBufferFull() {
		setWorking(false);
	}

	/**
	 * Called upon {@link #cachedFuel} is insufficient.
	 * 
	 * @return the amount of fuel cached. Non-positive values results in tile
	 *         shutdown.
	 * @return
	 */
	protected abstract int cacheFuel();

	protected abstract void createFuelHolder();
}
