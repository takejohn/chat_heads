package dzwdz.chat_heads.mixin;

import com.mojang.authlib.GameProfile;
import dzwdz.chat_heads.ChatHeads;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(ChatListener.class)
public abstract class ChatListenerMixin {
    // called after message filtering
    @Inject(
        method = "showMessageToPlayer", // called from handlePlayerChatMessage
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V"
        )
    )
    public void chatheads$handleAddedPlayerMessage(ChatType.Bound bound, PlayerChatMessage playerChatMessage, Component component, GameProfile gameProfile, boolean bl, Instant instant, CallbackInfoReturnable<Boolean> cir) {
        // looks like gameProfile.getId() *could* be different from the sender UUID, so we set ChatHeads.lastSender in ClientPacketListenerMixin instead
        ChatHeads.handleAddedMessage(component);
    }

    @Inject(
        method = "handleDisguisedChatMessage",
        at = @At("HEAD")
    )
    public void chatheads$handleAddedDisguisedMessage(Component component, ChatType.Bound bound, CallbackInfo ci) {
        ChatHeads.lastSender = null;
        ChatHeads.handleAddedMessage(component);
    }

    // called for system messages
    @Inject(
            method = "handleSystemMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;)V"
            )
    )
    public void chatheads$handleAddedSystemMessage(Component message, boolean bl, CallbackInfo ci) {
        ChatHeads.lastSender = null;
        ChatHeads.handleAddedMessage(message);
    }
}