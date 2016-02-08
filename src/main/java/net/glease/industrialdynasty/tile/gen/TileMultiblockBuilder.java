package net.glease.industrialdynasty.tile.gen;

import java.util.EnumMap;
import java.util.ListIterator;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import net.glease.industrialdynasty.multiblock.MultiblockLayout;
import net.glease.industrialdynasty.multiblock.BlockDataBundle.Fake;
import net.glease.industrialdynasty.tile.base.TileEntityPoweredBase;
import net.glease.industrialdynasty.util.BlockPositionUtil;
import net.glease.industrialdynasty.util.ConnectionConfiguration;
import net.glease.industrialdynasty.util.FluidTankScreened;
import net.glease.industrialdynasty.util.ConnectionConfiguration.SideConnectionConfiguration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;

/**
 * Slot mapping:
 * <ul>
 * <li>#0: ItemMultiblockLayout</li>
 * <li>#1-#30: raw materials</li>
 * <ul>
 * 
 * @author glease
 *		
 */
public class TileMultiblockBuilder extends TileEntityPoweredBase {
	private FakePlayer fakePlayer = null;
	private MultiblockLayout layout;
	private ListIterator<Fake<?, ?>> iter;
	
	public TileMultiblockBuilder(Object[] args) {
		super(args);
	}
	
	public MultiblockLayout getLayout() {
		return layout;
	}
	
	/**
	 * Do world related stuff here
	 */
	public void setup() {
		fakePlayer = getFakePlayer(worldObj);
	}
	
	public void setLayout(MultiblockLayout layout) {
		this.layout = layout;
		iter = layout.listIterator();
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
		int i = seekRawMaterialSlot();
		if (i == -1)
			return false;
		Fake<?, ?> next = iter.next();
		if (!BlockPositionUtil.tryPlaceItemIntoWorld(s[i].decrStackSize(1), fakePlayer, worldObj, next.getBp()))
			return true;
		// roll back
		iter.previous();
		s[i].increaseStackSize();
		return false;
	}
	
	@Override
	public boolean shouldWakeUp() {
		return iter != null && iter.hasNext() && super.shouldWakeUp();
	}
	
	@Override
	public boolean hasRawMaterial() {
		return seekRawMaterialSlot() != -1;
	}
	
	private int seekRawMaterialSlot() {
		Fake<?, ?> f = layout.get(iter.nextIndex());
		Item i = Item.getItemFromBlock(f.getBlock());
		int metadata = f.getMetadata();
		return findItemStack(new ItemStack(i,1,metadata), 1);
	}
	
	@Override
	protected int doWork() {
		return 1;
	}
	
	@Override
	protected void createConnectionConfiguration() {
		EnumMap<ForgeDirection, Long> sides = Maps.newEnumMap(ForgeDirection.class);
		long c = SideConnectionConfiguration.Defaults.ENERGY_IN.c | SideConnectionConfiguration.Defaults.ITEM_IN.c;
		for (ForgeDirection d : ForgeDirection.values()) {
			sides.put(d, c);
		}
		sides.put(ForgeDirection.UP, c | SideConnectionConfiguration.Defaults.ITEM_OUT.c);
		connConfig = ConnectionConfiguration.of(sides);
	}
	
	@Override
	protected void createTanks() {
		tanks = new FluidTankScreened[5];
		for (int i = 0; i < tanks.length; i++) {
			tanks[i] = new FluidTankScreened(5 * FluidContainerRegistry.BUCKET_VOLUME);
		}
	}
	
	private static final GameProfile FAKEPLAYER_PROFILE = new GameProfile(UUID.randomUUID(),
			"[IndustrialCraft-MultiblockBuilder]");
			
	/**
	 * @param world
	 * @return null if client side, or a corresponding {@link FakePlayer} on the
	 *         server side.
	 */
	private static FakePlayer getFakePlayer(World world) {
		if (world.isRemote)
			return FakePlayerFactory.get((WorldServer) world, FAKEPLAYER_PROFILE);
		return null;
	}
	
}
