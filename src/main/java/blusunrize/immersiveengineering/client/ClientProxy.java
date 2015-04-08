package blusunrize.immersiveengineering.client;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import blusunrize.immersiveengineering.api.IManualPage;
import blusunrize.immersiveengineering.client.gui.GuiBlastFurnace;
import blusunrize.immersiveengineering.client.gui.GuiCokeOven;
import blusunrize.immersiveengineering.client.gui.GuiRevolver;
import blusunrize.immersiveengineering.client.gui.manual.GuiManual;
import blusunrize.immersiveengineering.client.gui.manual.ManualPages;
import blusunrize.immersiveengineering.client.gui.manual.ManualPages.PositionedItemStack;
import blusunrize.immersiveengineering.client.render.BlockRenderMetalDecoration;
import blusunrize.immersiveengineering.client.render.BlockRenderMetalDevices;
import blusunrize.immersiveengineering.client.render.BlockRenderStoneDevices;
import blusunrize.immersiveengineering.client.render.BlockRenderWoodenDecoration;
import blusunrize.immersiveengineering.client.render.BlockRenderWoodenDevices;
import blusunrize.immersiveengineering.client.render.EntityRenderRevolvershot;
import blusunrize.immersiveengineering.client.render.ItemRenderRevolver;
import blusunrize.immersiveengineering.client.render.TileRenderConnectorHV;
import blusunrize.immersiveengineering.client.render.TileRenderConnectorLV;
import blusunrize.immersiveengineering.client.render.TileRenderConnectorMV;
import blusunrize.immersiveengineering.client.render.TileRenderPost;
import blusunrize.immersiveengineering.client.render.TileRenderRelayHV;
import blusunrize.immersiveengineering.client.render.TileRenderTransformer;
import blusunrize.immersiveengineering.client.render.TileRenderWatermill;
import blusunrize.immersiveengineering.client.render.TileRenderWindmill;
import blusunrize.immersiveengineering.client.render.TileRenderWindmillAdvanced;
import blusunrize.immersiveengineering.common.CommonProxy;
import blusunrize.immersiveengineering.common.Config;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.metal.BlockMetalDevices;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityConnectorHV;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityConnectorLV;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityConnectorMV;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityRelayHV;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityTransformer;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityTransformerHV;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityBlastFurnace;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityCokeOven;
import blusunrize.immersiveengineering.common.blocks.wooden.TileEntityWatermill;
import blusunrize.immersiveengineering.common.blocks.wooden.TileEntityWindmill;
import blusunrize.immersiveengineering.common.blocks.wooden.TileEntityWindmillAdvanced;
import blusunrize.immersiveengineering.common.blocks.wooden.TileEntityWoodenPost;
import blusunrize.immersiveengineering.common.entities.EntityRevolvershot;
import blusunrize.immersiveengineering.common.items.ItemRevolver;
import blusunrize.immersiveengineering.common.util.Lib;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{

	@Override
	public void init()
	{
		//METAL
		RenderingRegistry.registerBlockHandler(new BlockRenderMetalDevices());
		RenderingRegistry.registerBlockHandler(new BlockRenderMetalDecoration());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConnectorLV.class, new TileRenderConnectorLV());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConnectorMV.class, new TileRenderConnectorMV());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTransformer.class, new TileRenderTransformer(false));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRelayHV.class, new TileRenderRelayHV());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConnectorHV.class, new TileRenderConnectorHV());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTransformerHV.class, new TileRenderTransformer(true));
		//WOOD
		RenderingRegistry.registerBlockHandler(new BlockRenderWoodenDevices());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWoodenPost.class, new TileRenderPost());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWatermill.class, new TileRenderWatermill());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWindmill.class, new TileRenderWindmill());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWindmillAdvanced.class, new TileRenderWindmillAdvanced());

		RenderingRegistry.registerBlockHandler(new BlockRenderWoodenDecoration());
		//STONE
		RenderingRegistry.registerBlockHandler(new BlockRenderStoneDevices());

		//REVOLVER
		MinecraftForgeClient.registerItemRenderer(IEContent.itemRevolver, new ItemRenderRevolver());
		RenderingRegistry.registerEntityRenderingHandler(EntityRevolvershot.class, new EntityRenderRevolvershot());

		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	}

	@Override
	public void serverStart()
	{
		GuiManual.manualContents.clear();
		GuiManual.addEntry("introduction", new ManualPages.Text("introduction0"),new ManualPages.Text("introduction1"),new ManualPages.Crafting("introductionHammer", new ItemStack(IEContent.itemTool,1,0)));
		GuiManual.addEntry("ores", 
				new ManualPages.ItemDisplay("oresCopper", new ItemStack(IEContent.blockOres,1,0),new ItemStack(IEContent.itemMetal,1,0)),
				new ManualPages.ItemDisplay("oresBauxite", new ItemStack(IEContent.blockOres,1,1),new ItemStack(IEContent.itemMetal,1,1)),
				new ManualPages.ItemDisplay("oresLead", new ItemStack(IEContent.blockOres,1,2),new ItemStack(IEContent.itemMetal,1,2)),
				new ManualPages.ItemDisplay("oresSilver", new ItemStack(IEContent.blockOres,1,3),new ItemStack(IEContent.itemMetal,1,3)),
				new ManualPages.ItemDisplay("oresNickel", new ItemStack(IEContent.blockOres,1,4),new ItemStack(IEContent.itemMetal,1,4)));
		ArrayList<IManualPage> pages = new ArrayList();
//		if(Config.getBoolean("crushingOreRecipe"))
		{
			PositionedItemStack[][] recipes = new PositionedItemStack[16][3];
			for(int i=0; i<7; i++)
			{
				ItemStack ore = i==0?new ItemStack(Blocks.iron_ore): i==1?new ItemStack(Blocks.gold_ore): new ItemStack(IEContent.blockOres,1,i-2);
				ItemStack ingot = i==0?new ItemStack(Items.iron_ingot): i==1?new ItemStack(Items.gold_ingot): new ItemStack(IEContent.itemMetal,1,i-2);
				recipes[i*2][0] = new PositionedItemStack(ore, 24, 0);
				recipes[i*2][1] = new PositionedItemStack(new ItemStack(IEContent.itemTool,1,0), 42, 0);
				recipes[i*2][2] = new PositionedItemStack(new ItemStack(IEContent.itemMetal,2,8+i), 78, 0);
				recipes[i*2+1][0] = new PositionedItemStack(ingot, 24, 0);
				recipes[i*2+1][1] = new PositionedItemStack(new ItemStack(IEContent.itemTool,1,0), 42, 0);
				recipes[i*2+1][2] = new PositionedItemStack(new ItemStack(IEContent.itemMetal,1,8+i), 78, 0);
				//				"ingotCopper"
				//				"ingotAluminum"
				//				"ingotLead"
				//				"ingotSilver"
				//				"ingotNickel"
				//				"ingotConstantan"
				//				"ingotElectrum"
				//				"ingotSteel"
				//
				//				"dustIron"
				//				"dustGold"
				//				"dustCopper"
				//				"dustAluminum"
				//				"dustLead"
				//				"dustSilver"
				//				"dustNickel"
				//				"dustConstantan"
				//				"dustElectrum"
						
			}
			pages.add(new ManualPages.CraftingMulti("oreProcessing", (Object[])recipes));
		}
		pages.add(new ManualPages.CraftingMulti("oreProcessing_blend", (Object[])new PositionedItemStack[][]{
				new PositionedItemStack[]{new PositionedItemStack(OreDictionary.getOres("dustCopper"),24,0), new PositionedItemStack(OreDictionary.getOres("dustNickel"),42,0), new PositionedItemStack(new ItemStack(IEContent.itemMetal,2,15),78,0)},
				new PositionedItemStack[]{new PositionedItemStack(OreDictionary.getOres("dustGold"),24,0), new PositionedItemStack(OreDictionary.getOres("dustSilver"),42,0), new PositionedItemStack(new ItemStack(IEContent.itemMetal,2,16),78,0)}}));
		GuiManual.addEntry("oreProcessing", pages.toArray(new IManualPage[0]));
		GuiManual.addEntry("cokeoven", new ManualPages.Text("cokeoven0"), new ManualPages.Crafting("cokeovenBlock", new ItemStack(IEContent.blockStoneDevice,1,1)));
		GuiManual.addEntry("treatedwood", new ManualPages.Text("treatedwood0"), 
				new ManualPages.Crafting("", new ItemStack(IEContent.blockWoodenDecoration,1,0),new ItemStack(IEContent.blockWoodenDecoration,1,2),new ItemStack(IEContent.blockWoodenStair)),
				new ManualPages.Crafting("", new ItemStack(IEContent.itemMaterial,1,0),new ItemStack(IEContent.blockWoodenDecoration,1,1)),
				new ManualPages.Crafting("treatedwoodPost0", new ItemStack(IEContent.blockWoodenDevice,1,0)),
				new ManualPages.Text("treatedwoodPost1"));
		GuiManual.addEntry("wiring", new ManualPages.Text("wiring0"), 
				new ManualPages.Crafting("wiring1", new ItemStack(IEContent.itemWireCoil,1,OreDictionary.WILDCARD_VALUE)),
				new ManualPages.Image("wiring2", "immersiveengineering:textures/misc/wiring.png;0;0;110;40", "immersiveengineering:textures/misc/wiring.png;0;40;110;30"),
				new ManualPages.Image("wiring3", "immersiveengineering:textures/misc/wiring.png;0;70;110;60", "immersiveengineering:textures/misc/wiring.png;0;130;110;60"),
				new ManualPages.CraftingMulti("wiringConnector", new ItemStack(IEContent.blockMetalDevice,1,BlockMetalDevices.META_connectorLV),new ItemStack(IEContent.blockMetalDevice,1,BlockMetalDevices.META_connectorMV),new ItemStack(IEContent.blockMetalDevice,1,BlockMetalDevices.META_relayHV),new ItemStack(IEContent.blockMetalDevice,1,BlockMetalDevices.META_connectorHV)),
				new ManualPages.CraftingMulti("wiringCapacitor", new ItemStack(IEContent.blockMetalDevice,1,BlockMetalDevices.META_capacitorLV),new ItemStack(IEContent.blockMetalDevice,1,BlockMetalDevices.META_capacitorMV),new ItemStack(IEContent.blockMetalDevice,1,BlockMetalDevices.META_capacitorHV)),
				new ManualPages.CraftingMulti("wiringTransformer0", new ItemStack(IEContent.blockMetalDevice,1,BlockMetalDevices.META_transformer),new ItemStack(IEContent.blockMetalDevice,1,BlockMetalDevices.META_transformerHV)),
				new ManualPages.Text("wiringTransformer1"), 
				new ManualPages.Crafting("wiringCutters", new ItemStack(IEContent.itemTool,1,1)),
				new ManualPages.Crafting("wiringVoltmeter", new ItemStack(IEContent.itemTool,1,2)));
		GuiManual.addEntry("generator",
				new ManualPages.Text("generator0"),
				new ManualPages.Crafting("", new ItemStack(IEContent.blockMetalDevice,1,BlockMetalDevices.META_dynamo),new ItemStack(IEContent.blockMetalDevice,1,BlockMetalDevices.META_thermoelectricGen)),
				new ManualPages.CraftingMulti("generatorWindmill", new ItemStack(IEContent.itemMaterial,1,2),new ItemStack(IEContent.blockWoodenDevice,1,2)),
				new ManualPages.CraftingMulti("generatorWatermill", new ItemStack(IEContent.itemMaterial,1,1),new ItemStack(IEContent.blockWoodenDevice,1,1)),
				new ManualPages.CraftingMulti("generatorWindmillImproved", new ItemStack(IEContent.itemMaterial,1,4),new ItemStack(IEContent.itemMaterial,1,5),new ItemStack(IEContent.blockWoodenDevice,1,3))
				);


		GuiManual.addEntry("blastfurnace", new ManualPages.Text("blastfurnace0"), new ManualPages.Crafting("blastfurnaceBlock", new ItemStack(IEContent.blockStoneDevice,1,2)));

		GuiManual.addEntry("highvoltage", new ManualPages.Text("highvoltage0"),
				new ManualPages.Crafting("", new ItemStack(IEContent.blockMetalDevice,1,8),new ItemStack(IEContent.blockMetalDevice,1,4)),
				new ManualPages.Crafting("", new ItemStack(IEContent.blockMetalDevice,1,5),new ItemStack(IEContent.blockMetalDevice,1,7)));
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID==Lib.GUIID_CokeOven && world.getTileEntity(x, y, z) instanceof TileEntityCokeOven)
			return new GuiCokeOven(player.inventory, (TileEntityCokeOven) world.getTileEntity(x, y, z));
		if(ID==Lib.GUIID_BlastFurnace && world.getTileEntity(x, y, z) instanceof TileEntityBlastFurnace)
			return new GuiBlastFurnace(player.inventory, (TileEntityBlastFurnace) world.getTileEntity(x, y, z));
		if(ID==Lib.GUIID_Revolver && player.getCurrentEquippedItem()!=null && player.getCurrentEquippedItem().getItem() instanceof ItemRevolver)
			return new GuiRevolver(player.inventory, world);
		if(ID==Lib.GUIID_Manual && player.getCurrentEquippedItem()!=null && OreDictionary.itemMatches(new ItemStack(IEContent.itemTool,1,3), player.getCurrentEquippedItem(), false))
			return new GuiManual(player);
		return null;
	}

}