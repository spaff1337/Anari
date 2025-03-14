package me.spaff.anari.nms;

import me.spaff.anari.utils.ReflectionUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.net.SocketAddress;

public class EmptyConnection extends Connection {
    public EmptyConnection(PacketFlow flag) throws IOException {
        super(flag);
        channel = new EmptyChannel(null);
        address = new SocketAddress() {
            private static final long serialVersionUID = 8207338859896320185L;
        };
    }

    @Override
    public void flushChannel() {
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void send(Packet packet) {
    }

    @Override
    public void send(Packet packet, PacketSendListener genericfuturelistener) {
    }

    @Override
    public void send(Packet packet, PacketSendListener genericfuturelistener, boolean flag) {
    }

    @Override
    public void setListenerForServerboundHandshake(PacketListener pl) {
        MethodHandle CONNECTION_PACKET_LISTENER = (MethodHandle) ReflectionUtils.getField(Connection.class, "q", null);
        MethodHandle CONNECTION_DISCONNECT_LISTENER = (MethodHandle) ReflectionUtils.getField(Connection.class, "p", null);
        try {
            CONNECTION_PACKET_LISTENER.invoke(this, pl);
            CONNECTION_DISCONNECT_LISTENER.invoke(this, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
