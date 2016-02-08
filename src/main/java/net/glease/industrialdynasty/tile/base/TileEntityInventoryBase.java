package net.glease.industrialdynasty.tile.base;

import java.util.Arrays;
import java.util.Iterator;

import cofh.api.inventory.IInventoryConnection;
import net.glease.industrialdynasty.util.ConnectionConfiguration;
import net.glease.industrialdynasty.util.FluidTankScreened;
import net.glease.industrialdynasty.util.InventorySlot;
import net.glease.industrialdynasty.util.NBTTagHelper;
import net.glease.industrialdynasty.util.ConnectionConfiguration.ConnectionCategory;
import net.glease.industrialdynasty.util.ConnectionConfiguration.IOConfig;
import net.glease.industrialdynasty.util.ConnectionConfiguration.SideConnectionConfiguration;
import net.glease.industrialdynasty.util.ConnectionConfiguration.TankIOConfig;
import net.glease.industrialdynasty.util.filters.ItemStackPredicate;
import net.glease.industrialdynasty.util.filters.NBTTagPredicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

/**
 * Setup args:
 * <ol>
 * <li>B:lostJobOnStop
 * <li>I:jobAmount
 * <li>I:wakeCheckInterval
 * <li>I:invSize
 * <li>S:invName
 * </ol>
 * 
 * @author glease
 *
 */
public abstract class TileEntityInventoryBase extends TileEntityBase implements
		ISidedInventory, IFluidHandler, IInventoryConnection {
	protected InventorySlot[] s;
	/**
	 * I believe that I won't switch to some other implementation of
	 * {@link IFluidTank}
	 */
	protected FluidTankScreened[] tanks;

	protected String invName;

	protected ConnectionConfiguration connConfig;

	public TileEntityInventoryBase(Object[] args) {
		super(args);
		setup((int) args[3], (String) args[4]);
	}

	private void setup(int invSize, String invName) {
		s = new InventorySlot[invSize];
		for (int i = 0; i < s.length; i++)
			s[i] = new InventorySlot(i, this);
		this.invName = invName;
		createTanks();
		createConnectionConfiguration();
	}

	// NBT
	// {

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound t = tag.getCompoundTag("Inventory");
		invName = t.getString("invName");
		NBTTagList l = t.getTagList("Tanks", 10);
		setup(l.tagCount(), invName);
		for (int i = 0; i < l.tagCount(); i++)
			tanks[i].readFromNBT(l.getCompoundTagAt(i));
		l = t.getTagList("slots", 10);
		for (int i = 0; i < l.tagCount(); i++)
			s[i].readFromNBT(l.getCompoundTagAt(i));
		connConfig = ConnectionConfiguration.readFromNBT(t
				.getCompoundTag("ConnectionConfiguration"));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound t = new NBTTagCompound();
		tag.setTag("Inventory", t);
		t.setString("invName", invName);
		t.setTag("slots", NBTTagHelper.asList(Arrays.asList(s)));
		t.setTag("Tanks",NBTTagHelper.asList(Arrays.asList(tanks)));
		t.setTag("ConnectionConfiguration",
				connConfig.writeToNBT(new NBTTagCompound()));
	}

	// }

	// ISidedInventory
	// {
	@Override
	public int getSizeInventory() {
		return s.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return s[i].getStack();
	}

	@Override
	public ItemStack decrStackSize(int i, int count) {
		return s[i].decrStackSize(count);
	}
	
	/**
	 * Drop everything since in most cases we don't need to keep it.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		return s[index].getStack();
	}
	
	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (itemstack != null) {
			if (itemstack.stackSize > getInventoryStackLimit())
				itemstack.stackSize = getInventoryStackLimit();
			else if (itemstack.stackSize < 0)
				itemstack = null;
		}
		s[i].setStack(itemstack);
		markDirty();
	}

	@Override
	public String getInventoryName() {
		return invName;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		int start = getFirstSlotSided(ForgeDirection.UNKNOWN);
		if (slot < start
				|| slot > (start + getSizeInventorySided(ForgeDirection.UNKNOWN)))
			return false;
		if (itemstack == null)
			return true;
		if (itemstack.stackSize > Math.min(itemstack.getMaxStackSize(),
				getInventoryStackLimit()))
			return false;
		ItemStack slotContent = this.getStackInSlot(slot);
		return slotContent == null
				|| ItemStack.areItemStacksEqual(itemstack, slotContent);
	}

	// Renamed to fit my brain.
	protected int getFirstSlotSided(@SuppressWarnings("unused") ForgeDirection unknown) {
		return 0;
	}

	// Renamed to fit my brain.
	/**
	 * @param direction  
	 */
	protected int getSizeInventorySided(ForgeDirection direction) {
		return getSizeInventory();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (isInvalid()
				|| worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		}
		return entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D,
				zCoord + 0.5D) <= 64D;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		int start = getFirstSlotSided(ForgeDirection.getOrientation(side));
		int size = getSizeInventorySided(ForgeDirection.getOrientation(side));

		int[] slots = new int[size];
		for (int i = 0; i < size; i++) {
			slots[i] = i + start;
		}
		return slots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		return itemstack == null
				|| this.isItemValidForSlot(slot, itemstack)
				|| !connConfig.test(ForgeDirection.getOrientation(side), 0,
						IOConfig.IN, ConnectionCategory.ITEM);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
		return connConfig.test(ForgeDirection.getOrientation(side), 0,
				IOConfig.OUT, ConnectionCategory.ITEM);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public ConnectionType canConnectInventory(ForgeDirection from) {
		return connConfig.test(from, 0, IOConfig.NO, ConnectionCategory.ITEM) ? ConnectionType.DENY
				: ConnectionType.DEFAULT;
	}

	// }

	public int getComparatorOutput(int side) {
		IFluidTank[] tanks = getTanks();
		IFluidTank tank = null;
		if (tanks.length > 0) // just in case
			tank = tanks[0];
		float tankPercent = 0, invPercent = 0;
		boolean hasTank = false, hasInventory = false;
		if (tank != null) {
			hasTank = true;
			if (tank.getFluid() != null) {
				tankPercent = ((float) tank.getFluid().amount)
						/ tank.getCapacity();
			}
		}
		int[] accSlots = getAccessibleSlotsFromSide(side);
		if (accSlots.length > 0) {
			hasInventory = true;
			int[] slots = accSlots;
			int len = 0;
			float ret = 0;
			for (int i = slots.length; i-- > 0;) {
				if (canInsertItem(slots[i], null, side)) {
					ItemStack stack = getStackInSlot(slots[i]);
					if (stack != null) {
						float maxStack = Math.min(stack.getMaxStackSize(),
								getInventoryStackLimit());
						ret += Math.max(
								Math.min(stack.stackSize / maxStack, 1), 0);
					}
					++len;
				}
			}
			invPercent = ret / len;
		}
		float mult = hasTank & hasInventory ? (tankPercent + invPercent) / 2
				: hasTank ? tankPercent : hasInventory ? invPercent : 0f;
		return (int) Math.ceil(15 * mult);
	}

	// Fluid tanks
	public FluidTankScreened[] getTanks() {
		return tanks;
	}

	// IFluidHandler
	// sub-classes are encouraged to implement their own, for performance
	// reason.
	// {
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		int left = resource.amount;
		for (Iterator<TankIOConfig> iter = connConfig
				.getSideConnectionConfiguration(from).avaliableTankIterator(
						IOConfig.IN); iter.hasNext();) {
			FluidTankScreened t = tanks[iter.next().getId()];
			if (t.isFluidAccepted(resource)) {
				left -= t.fill(new FluidStack(resource, left), doFill);
				if (left == 0)
					break;
			}
		}
		return left;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		int left = resource.amount;
		for (Iterator<TankIOConfig> iter = connConfig
				.getSideConnectionConfiguration(from).avaliableTankIterator(
						IOConfig.OUT); iter.hasNext();) {
			FluidTankScreened t = tanks[iter.next().getId()];
			if (t.isFluidAccepted(resource)
					&& t.getFluid().isFluidEqual(resource)) {
				left -= t.drain(left, doDrain).amount;
				if (left == 0)
					break;
			}
		}
		return new FluidStack(resource, left);
	}

	/**
	 * {@inheritDoc} <br/>
	 * This method will always and only drain the first available tank. Since we
	 * may have multiple tanks inside, it's better not to use this method, thus
	 * it will log debug info on invocation.
	 */
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		return getTanks()[connConfig
				.getNextAvaliableTank(from, 0, IOConfig.OUT)].drain(maxDrain,
				doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if (from != ForgeDirection.UNKNOWN) {
			SideConnectionConfiguration c = connConfig
					.getSideConnectionConfiguration(from);
			for (Iterator<TankIOConfig> iter = c
					.avaliableTankIterator(IOConfig.IN); iter.hasNext();)
				if (getTanks()[iter.next().getId()].isFluidAccepted(fluid))
					return true;
		}
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		if (from != ForgeDirection.UNKNOWN) {
			SideConnectionConfiguration c = connConfig
					.getSideConnectionConfiguration(from);
			for (Iterator<TankIOConfig> iter = c
					.avaliableTankIterator(IOConfig.OUT); iter.hasNext();)
				if (getTanks()[iter.next().getId()].isFluidAccepted(fluid))
					return true;
		}
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		FluidTankInfo[] info = new FluidTankInfo[getTanks().length];
		for (int i = 0; i < getTanks().length; i++)
			info[i] = getTanks()[i].getInfo();
		return info;
	}

	// sub-classes should override these, if necessary.
	// {
	/**
	 * Create the tanks.
	 */
	protected void createTanks() {
		tanks = new FluidTankScreened[0];
	}

	/**
	 * Create the {@link ConnectionConfiguration}.
	 * 
	 */
	protected void createConnectionConfiguration() {
		connConfig = ConnectionConfiguration.ALL_DENIED;
	}

	protected int findItemStack(ItemStack stack,int offset) {
		return findItemStack(ItemStackPredicate.buildExactMatch(stack),offset);
	}

	protected int findItemStack(Item stack,int meta, NBTTagPredicate<NBTTagCompound> predicate, int offset) {
		return findItemStack(new ItemStackPredicate(stack, meta, 64, 1, predicate), offset);
	}
	
	protected int findItemStack(ItemStackPredicate filter,int offset) {
		if(filter==null)
			return -1;
		if(offset<0|| offset >= s.length)
			return -1;
		for (int i = offset; i< s.length; i++){
			if(s[i].isSlotEmpty())
				continue;
			if(filter.apply(s[i].getStack()))
				return i;
		}
		return -1;
	}
}
