package blusunrize.immersiveengineering.common.blocks;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import blusunrize.immersiveengineering.api.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.WireType;
import blusunrize.immersiveengineering.common.util.Utils;

public abstract class TileEntityImmersiveConnectable extends TileEntityIEBase implements IImmersiveConnectable
{
	protected WireType limitType = null;

	protected boolean canTakeLV()
	{
		return false;
	}
	protected boolean canTakeMV()
	{
		return false;
	}
	protected boolean canTakeHV()
	{
		return false;
	}

	@Override
	public void invalidate()
	{
		super.invalidate();
		if(worldObj!=null && !worldObj.isRemote)
			ImmersiveNetHandler.clearAllConnectionsFor(Utils.toCC(this),worldObj);
	}

	@Override
	public boolean canConnect()
	{
		return true;
	}
	@Override
	public boolean isEnergyOutput()
	{
		return false;
	}
	@Override
	public int outputEnergy(int amount, boolean simulate, int energyType)
	{
		return 0;
	}
	@Override
	public boolean canConnectCable(WireType cableType, TargetingInfo target)
	{
		if(cableType==WireType.STEEL&&!canTakeHV())
			return false;
		if(cableType==WireType.ELECTRUM&&!canTakeMV())
			return false;
		if(cableType==WireType.COPPER&&!canTakeLV())
			return false;
		return limitType==null||limitType==cableType;
	}
	@Override
	public void connectCable(WireType cableType, TargetingInfo target)
	{
		this.limitType = cableType;
	}
	@Override
	public WireType getCableLimiter(TargetingInfo target)
	{
		return this.limitType;
	}
	@Override
	public void removeCable(WireType type)
	{
		if(ImmersiveNetHandler.getConnections(worldObj,Utils.toCC(this)).isEmpty())
		{
			if(type==limitType || type==null)
				this.limitType = null;
			this.markDirty();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		if(worldObj!=null && !worldObj.isRemote)
		{
			NBTTagList connectionList = new NBTTagList();
			List<Connection> conL = ImmersiveNetHandler.getConnections(worldObj, Utils.toCC(this));
			for(Connection con : conL)
				connectionList.appendTag(con.writeToNBT());
			nbttagcompound.setTag("connectionList", connectionList);
		}
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 3, nbttagcompound);
	}
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		NBTTagCompound nbt = pkt.func_148857_g();
		this.readFromNBT(nbt);
		if(worldObj!=null && worldObj.isRemote)
		{
			NBTTagList connectionList = nbt.getTagList("connectionList", 10);
			ImmersiveNetHandler.clearConnectionsOriginatingFrom(Utils.toCC(this), worldObj);
			for(int i=0; i<connectionList.tagCount(); i++)
			{
				NBTTagCompound conTag = connectionList.getCompoundTagAt(i);
				Connection con = Connection.readFromNBT(conTag);
				if(con!=null)
				{
					ImmersiveNetHandler.addConnection(worldObj, Utils.toCC(this), con);
				}
				else
					System.out.println("CLIENT read connection as null");
			}
		}
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt)
	{
		try{
			if(nbt.hasKey("limitType"))
				limitType = WireType.getValue(nbt.getInteger("limitType"));

			//			int[] prevPos = nbt.getIntArray("prevPos");
			//			if(prevPos!=null && prevPos.length>3 && FMLCommonHandler.instance().getEffectiveSide()==Side.SERVER)
			//			{
			//				//				if(worldObj.provider.dimensionId!=prevPos[0])
			//				//				{
			//				//					ImmersiveNetHandler.clearAllConnectionsFor(Utils.toCC(this),worldObj);
			//				//				}
			//				//				else
			//				World worldTest = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(prevPos[0]);
			//				if(xCoord!=prevPos[1] || yCoord!=prevPos[2] || zCoord!=prevPos[3])
			//				{
			//					System.out.println("I was moved! Attmpting to update connections...");
			//					
			//					if(worldTest.getTileEntity(prevPos[1],prevPos[2],prevPos[3]) instanceof IImmersiveConnectable)
			//					{
			//						System.out.println("Someone else has taken my place");
			//					}
			//					
			//					Iterator<Connection> it = ImmersiveNetHandler.getAllConnections(worldTest).iterator();
			//					ChunkCoordinates node = new ChunkCoordinates(prevPos[1],prevPos[2],prevPos[3]);
			//					while(it.hasNext())
			//					{
			//						Connection con = it.next();
			//						if(node.equals(con.start))
			//							con.start = Utils.toCC(this);
			//						if(node.equals(con.end))
			//							con.end = Utils.toCC(this);
			//						//						if(node.equals(con.start) || node.equals(con.end))
			//						//						{
			//						////							it.remove();
			//						//							//if(node.equals(con.start) && toIIC(con.end, world)!=null && getConnections(world,con.end).isEmpty())
			//						////							iic = toIIC(con.end, worldObj);
			//						////							if(iic!=null)
			//						////								iic.removeCable(con.cableType);
			//						//							//if(node.equals(con.end) && toIIC(con.start, world)!=null && getConnections(world,con.start).isEmpty())
			//						////							iic = toIIC(con.start, worldObj);
			//						////							if(iic!=null)
			//						////								iic.removeCable(con.cableType);
			//						//
			//						//							if(node.equals(con.end))
			//						//							{
			//						//								double dx = node.posX+.5+Math.signum(con.start.posX-con.end.posX);
			//						//								double dy = node.posY+.5+Math.signum(con.start.posY-con.end.posY);
			//						//								double dz = node.posZ+.5+Math.signum(con.start.posZ-con.end.posZ);
			//						//								worldObj.spawnEntityInWorld(new EntityItem(worldObj, dx,dy,dz, new ItemStack(IEContent.itemWireCoil,1,con.cableType.ordinal())));
			//						//							}
			//						//						}
			//					}
			//					IESaveData.setDirty(worldTest.provider.dimensionId);
			//					//					ImmersiveNetHandler.indirectConnections.clear();
			//				}
			//			}
		}catch(Exception e)
		{
			System.out.println("MASSIVE ERROR. DOES NOT COMPUTE WRITE.");
		}
	}
	@Override
	public void writeCustomNBT(NBTTagCompound nbt)
	{
		try{

			if(limitType!=null)
				nbt.setInteger("limitType", limitType.ordinal());

			if(this.worldObj!=null)
			{
				nbt.setIntArray("prevPos", new int[]{this.worldObj.provider.dimensionId,xCoord,yCoord,zCoord});
			}
		}catch(Exception e)
		{
			System.out.println("MASSIVE ERROR. DOES NOT COMPUTE WRITE.");
		}
	}
}