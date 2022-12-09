package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network;

import com.nukkitx.protocol.bedrock.BedrockPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.*;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity.*;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world.*;

public class BedrockPacketTranslatorManager extends PacketTranslatorManager<BedrockPacket> {

	public BedrockPacketTranslatorManager() {
		this.addTranslator(new StartGameTranslator());
		this.addTranslator(new ChunkRadiusUpdatedTranslator());
		this.addTranslator(new LevelChunkTranslator());
		this.addTranslator(new NetworkStackLatencyPacketTranslator());
		this.addTranslator(new ResourcePacksInfoPacketTranslator());
		this.addTranslator(new ResourcePackStackPacketTranslator());
		this.addTranslator(new AddPlayerTranslator());
		this.addTranslator(new PlayerListPacketTranslator());
		this.addTranslator(new TextTranslator());
		this.addTranslator(new AddEntityPacketTranslator());
		this.addTranslator(new SetTimePacketTranslator());
		this.addTranslator(new RemoveEntityPacketTranslator());
//		this.addTranslator(new InventorySlotPacketTranslator()); // TODO: REDO INVENTORIES/CONTAINERS
		this.addTranslator(new AddItemEntityPacketTranslator());
		this.addTranslator(new MovePlayerPacketTranslator());
		this.addTranslator(new MoveEntityAbsolutePacketTranslator());
		this.addTranslator(new ServerToClientHandshakePacketTranslator());
		this.addTranslator(new UpdateBlockTranslator());
		this.addTranslator(new SetEntityMotionTranslator());
		this.addTranslator(new TakeItemEntityPacketTranslator());
		this.addTranslator(new NetworkChunkPublisherUpdateTranslator());
		this.addTranslator(new SetEntityDataPacketTranslator());
//		this.addTranslator(new ContainerOpenPacketTranslator()); // TODO: REDO INVENTORIES/CONTAINERS
//		this.addTranslator(new InventoryContentPacketTranslator()); // TODO: REDO INVENTORIES/CONTAINERS
		this.addTranslator(new DisconnectTranslator());
		this.addTranslator(new SetPlayerGameTypeTranslator());
		this.addTranslator(new AdventureSettingsTranslator());
		this.addTranslator(new AnimateTranslator());
		this.addTranslator(new MobEquipmentTranslator());
		this.addTranslator(new MobArmorEquipmentTranslator());
		this.addTranslator(new BlockEntityDataTranslator());
		this.addTranslator(new GameRulesChangedTranslator());
		this.addTranslator(new UpdatePlayerGameTypeTranslator());
		this.addTranslator(new LevelEventTranslator());
		this.addTranslator(new LevelSoundEvent2Translator());
		this.addTranslator(new LevelSoundEventTranslator());
		this.addTranslator(new BlockEntityDataPacketTranslator());
		this.addTranslator(new RespawnPacketTranslator());
		this.addTranslator(new PlayStatusTranslator());
		this.addTranslator(new EntityEventPacketTranslator());
	}
}