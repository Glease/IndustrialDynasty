package net.glease.industrialdynasty.tile.multiblock.base;

import net.glease.industrialdynasty.multiblock.BlockDataBundle;
import net.glease.industrialdynasty.multiblock.IMultiblockPart;
import net.glease.industrialdynasty.tile.base.TileEntityGeneratorBase;
import net.glease.industrialempire.multiblock.IMultiblockGeneratorController;
import net.minecraft.item.ItemStack;

public abstract class TileMultiblockGeneratingPartBase<T extends IMultiblockGeneratorController<T>> extends TileEntityGeneratorBase implements IMultiblockPart<T> {
	protected T controller;
	protected TileMultiblockGeneratingPartBase(Object[] args) {
		super(args);
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
	public int generate() {
		return controller.generate(this);
	}

	@Override
	public boolean shouldWakeUp() {
		return controller.shouldWakeUp(this);
	}

}
