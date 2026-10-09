package com.fiverules.network;

import com.fiverules.FiveRules;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Sent by the client when the player opens the F3 debug screen. */
public record CheaterPayload() implements CustomPayload {
	public static final CheaterPayload INSTANCE = new CheaterPayload();
	public static final CustomPayload.Id<CheaterPayload> ID = new CustomPayload.Id<>(Identifier.of(FiveRules.MOD_ID, "cheater"));
	public static final PacketCodec<ByteBuf, CheaterPayload> CODEC = PacketCodec.unit(INSTANCE);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
