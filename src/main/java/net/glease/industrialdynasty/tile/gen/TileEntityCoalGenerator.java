package net.glease.industrialdynasty.tile.gen;

import cpw.mods.fml.common.registry.GameRegistry;
import net.glease.industrialdynasty.tile.base.TileEntityFuelGeneratorBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * Slot mapping:
 * <ol>
 * <li>Coal/charcoal position.
 * <li>Battery to charge.
 * </ol>
 * 
 * @author glease
 * 
 * @deprecated this is only a test class. Possibly too powerful.
 *
 */
@SuppressWarnings("dep-ann")
public class TileEntityCoalGenerator extends
		TileEntityFuelGeneratorBase<ItemStack> {

	public TileEntityCoalGenerator(Object[] args) {
		super(args);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		return null;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean jobDone() {
		throw new IllegalStateException("Impossible.");
	}

	@Override
	protected void readFuelNBT(NBTTagList tag) {
		fuel[0].readFromNBT(tag.getCompoundTagAt(0));
	}

	@Override
	protected NBTTagList writeFuelNBT(NBTTagList tag) {
		tag.appendTag(fuel[0].writeToNBT(new NBTTagCompound()));
		return tag;
	}

	@Override
	protected int cacheFuel() {
		if (fuel[0] == null || fuel[0].stackSize < 1)
			return 0;
		fuel[0].stackSize--;
		return GameRegistry.getFuelValue(fuel[0]);
	}

	@Override
	protected void createFuelHolder() {
		fuel = new ItemStack[1];
	}

}
