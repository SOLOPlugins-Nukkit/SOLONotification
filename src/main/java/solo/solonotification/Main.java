package solo.solonotification;

import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerRespawnEvent;
import cn.nukkit.plugin.PluginBase;
import solo.solobasepackage.event.notification.NotificationEvent;
import solo.solobasepackage.util.Message;
import solo.solobasepackage.util.Notification;

public class Main extends PluginBase implements Listener{

	@Override
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable(){
		Notification.save();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onNotification(NotificationEvent event){
		Player player = this.getServer().getPlayerExact(event.getPlayerName());
		if(player != null){
			String message = event.getMessage();
			if(message.length() > 18){
				message = message.substring(0, 18) + "...";
			}
			Message.raw(player, "§b알림이 도착하였습니다 : " + message);
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event){
		if(Notification.hasNotification(event.getPlayer())){
			Message.raw(event.getPlayer(), "§b" + Integer.toString(Notification.getNotificationCount(event.getPlayer())) + "개의 알림이 있습니다. /알림 확인 명령어로 확인해주세요. ");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(command.getName().equals("알림")){
			if(args.length == 0){
				args = new String[]{"x"};
			}
			switch(args[0]){
				case "확인":
				case "목록":
					if(! Notification.hasNotification(sender)){
						Message.normal(sender, "알림이 없습니다.");
						return true;
					}
					int page = 1;
					try{
						page = Integer.parseInt(args[1]);
					}catch(Exception e){
						
					}
					String[] received = Notification.getNotifications(sender);
					for(int i = 0; i < received.length; i++){
						received[i] = "[" + Integer.toString(i) + "] " + received[i];
					}
					Message.page(sender, sender.getName() + "님의 알림 목록", received, page);
					return true;
					
				case "삭제":
				case "제거":
					if(! Notification.hasNotification(sender)){
						Message.normal(sender, "삭제할 알림이 없습니다.");
						return true;
					}
					try{
						int index = Integer.parseInt(args[1]);
						if(Notification.removeNotification(sender, index)){
							Message.normal(sender, "알림을 삭제하였습니다.");
							return true;
						}
						Message.normal(sender, "해당 인덱스는 존재하지 않습니다.");
						return true;
					}catch(Exception e){
					}
					Message.usage(sender, "/알림 삭제 [인덱스]");
					return true;
					
				case "모두삭제":
				case "모두제거":
				case "전체삭제":
				case "전체제거":
					if(! Notification.hasNotification(sender)){
						Message.normal(sender, "삭제할 알림이 없습니다.");
						return true;
					}
					Notification.removeAllNotifications(sender);
					Message.normal(sender, "모든 알림을 삭제하였습니다.");
					return true;
		
				default:
					LinkedHashMap<String, String> help = new LinkedHashMap<String, String>();
					help.put("/알림 확인", "알림을 확인합니다.");
					help.put("/알림 삭제 [인덱스]", "알림을 삭제합니다.");
					help.put("/알림 전체삭제", "알림을 모두 삭제합니다.");
					Message.commandHelp(sender, "알림 명령어 목록", help);
					
			}
		}
		return true;
	}
	
}