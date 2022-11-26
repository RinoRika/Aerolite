package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType

class NotificationCommand : Command("notification", arrayOf("ntest")) {
    override fun execute(args: Array<String>) {
        LiquidBounce.hud.addNotification(Notification("Test","This is a notification test", NotifyType.SUCCESS))
        LiquidBounce.hud.addNotification(Notification("Test","This is a notification test", NotifyType.INFO))
        LiquidBounce.hud.addNotification(Notification("Test","This is a notification test", NotifyType.WARNING))
        LiquidBounce.hud.addNotification(Notification("Test","This is a notification test", NotifyType.ERROR))
    }
}