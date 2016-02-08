package net.glease.industrialdynasty;

import java.io.IOException;

import cofh.core.CoFHProps;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.mod.BaseMod;
import cofh.mod.updater.UpdateManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.glease.industrialdynasty.client.gui.IEGUIHandler;
import net.glease.industrialdynasty.proxy.IEProxy;

@Mod(modid = IndustrialEmipre.modId, name = IndustrialEmipre.modName, dependencies = IndustrialEmipre.dependencies)
public class IndustrialEmipre extends BaseMod {

	public static final String modId = "industrialempire";
	public static final String modName = "IndustrialEmpire";
	public static final String modNetworkChannel = "IndustrialEmpire";
	public static final String dependencies = CoFHProps.DEPENDENCIES
			+ ";required-after:CoFHCore@[" + CoFHProps.VERSION + ",)";
	public static final String version = "1.7.10A0.1";

	public static final String prefix = "gleasecraft:";
	public static final String modelFolder = prefix + "models/";
	public static final String textureFolder = prefix + "textures/";
	public static final String armorTextureFolder = textureFolder + "armor/";
	public static final String guiFolder = textureFolder + "gui/";
	public static final String hudFolder = textureFolder + "hud/";
	public static final String modelTextureFolder = textureFolder
			+ "itemmodels/";
	public static final String mobTextureFolder = textureFolder + "mob/";
	public static final String tileEntityFolder = textureFolder + "tileentity/";
	public static final String villagerFolder = textureFolder + "villager/";
	// static{DepLoader.load();}

	@Mod.Instance
	public static IndustrialEmipre INSTANCE;

	public SimpleNetworkWrapper network = NetworkRegistry.INSTANCE
			.newSimpleChannel(modNetworkChannel);

	@SidedProxy(clientSide = "net.glease.industrialempire.proxy.IEClientProxy", serverSide = "net.glease.industrialempire.proxy.IEProxy")
	public IEProxy proxy;

	@EventHandler
	public void handleIMC(IMCEvent e) {
		for (IMCMessage m : e.getMessages()) {
			if (!IMCHandler.onMessage(m))
				_log.warn(String.format(
						"Received an unrecognized IMC from %s.", m.getSender()));
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		// MinecraftForge.EVENT_BUS.register(new EntityHandler());

		proxy.init();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new IEGUIHandler());

		// WorldHandler.instance.registerFeature(new IEWorldGen());

		UpdateManager.registerUpdater(new UpdateManager(this, null,
				CoFHProps.DOWNLOAD_URL));
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) throws IOException {
		setConfigFolderBase(evt.getModConfigurationDirectory());
		/*
		 * machineBlocks.put(0, new BlockFactoryMachine(0));
		 * machineBlocks.put(1, new BlockFactoryMachine(1));
		 * machineBlocks.put(2, new BlockFactoryMachine(2));
		 */
		IEConfig.loadClientConfig(getClientConfig());
		IEConfig.loadCommonConfig(getCommonConfig());

		loadLang();

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		/*
		 * for (Vanilla e : recipeSets) e.registerRecipes();
		 */
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent evt) {
		OreDictionaryArbiter.initialize();
		_log.info("Load Complete.");
	}

	@Override
	public String getModId() {
		return modId;
	}

	@Override
	public String getModName() {
		return modName;
	}

	@Override
	public String getModVersion() {
		return version;
	}
}
