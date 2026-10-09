package com.fiverules.network;

import com.fiverules.FiveRules;
import java.util.List;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

/** Sent by the server with the pages of the book the player picked up; the client can't close it before the last page. */
public record ReadBookPayload(List<Text> pages) implements CustomPayload {
	public static final CustomPayload.Id<ReadBookPayload> ID = new CustomPayload.Id<>(Identifier.of(FiveRules.MOD_ID, "read_book"));
	public static final PacketCodec<RegistryByteBuf, ReadBookPayload> CODEC = PacketCodec.tuple(
			TextCodecs.REGISTRY_PACKET_CODEC.collect(PacketCodecs.toList()), ReadBookPayload::pages,
			ReadBookPayload::new);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
