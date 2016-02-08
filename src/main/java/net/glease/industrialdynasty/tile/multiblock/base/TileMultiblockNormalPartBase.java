package net.glease.industrialdynasty.tile.multiblock.base;

import net.glease.industrialdynasty.multiblock.BlockDataBundle;
import net.glease.industrialdynasty.multiblock.IMultiblockController;
import net.glease.industrialdynasty.multiblock.IMultiblockPart;
import net.glease.industrialdynasty.tile.base.TileEntityPoweredBase;
import net.minecraft.item.ItemStack;

public abstract class TileMultiblockNormalPartBase<T extends IMultiblockController<T>> extends TileEntityPoweredBase implements IMultiblockPart<T>{
	protected T controller;
	public TileMultiblockNormalPartBase(Object[] args) {
		super(args);
	}

	@Override
	public T getController() {
		return controller;
	}

	@Override
	public void setController(T controller) {
		this.controller = controller;
	}

	@Override
	public BlockDataBundle<T, ? extends IMultiblockPart<T>> getDataBundle() {
		return BlockDataBundle.from(this);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		return null;
	}

	@Override
	public boolean jobDone() {
		return false;
	}

	@Override
	public boolean hasRawMaterial() {
		return false;
	}

	@Override
	protected int doWork() {
		return 0;
	}
}
