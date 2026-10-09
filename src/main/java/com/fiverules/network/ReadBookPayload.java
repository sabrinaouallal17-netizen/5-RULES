package com.fiverules.network;

import com.fiverules.FiveRules;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Sent by the server to make the client open the rules book it can't close until the last page. */
public record ReadBookPayload() implements CustomPayload {
	public static final ReadBookPayload INSTANCE = new ReadBookPayload();
	public static final CustomPayload.Id<ReadBookPayload> ID = new CustomPayload.Id<>(Identifier.of(FiveRules.MOD_ID, "read_book"));
	public static final PacketCodec<ByteBuf, ReadBookPayload> CODEC = PacketCodec.unit(INSTANCE);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
