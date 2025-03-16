package fun.kaituo.tagchurch.util;

import fun.kaituo.gameutils.util.AbstractSignListener;
import fun.kaituo.tagchurch.TagChurch;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import static fun.kaituo.tagchurch.util.Misc.getCharacterDisplayName;

public class ChooseCharacterSign extends AbstractSignListener {
    public static final String HUMAN_CHOOSE_PREFIX = "誓约了";
    public static final String HUNTER_CHOOSE_PREFIX = "选择成为";

    @Getter
    private final Class<? extends PlayerData> characterClass;
    private final String displayName;
    private final String chooseMessage;
    private final ChatColor color;
    private final boolean isHuman;
    @Getter
    private final Team team;

    public ChooseCharacterSign(JavaPlugin plugin, Location location, Class<? extends PlayerData> characterClass) {
        super(plugin, location);
        this.characterClass = characterClass;
        displayName = getCharacterDisplayName(characterClass);
        chooseMessage = Misc.getCharacterChooseMessage(characterClass);
        color = Misc.getCharacterColor(characterClass);
        isHuman = Misc.isCharacterHuman(characterClass);
        lines.set(1, color + displayName);

        team = TagChurch.inst().getTagBoard().registerNewTeam(characterClass.getSimpleName());
        team.setColor(color);
    }

    @Override
    public void onRightClick(PlayerInteractEvent e) {
        Player chooser = e.getPlayer();
        if (TagChurch.inst().playerCharacterChoices.get(chooser.getUniqueId()).equals(characterClass)) {
            return;
        }
        for (Player p : TagChurch.inst().getPlayers()) {
            p.sendMessage(
                    color + chooser.getName() + " " +
                    "§f" + (isHuman? HUMAN_CHOOSE_PREFIX : HUNTER_CHOOSE_PREFIX) + " " +
                    color + displayName);
        }
        chooser.sendMessage(
                color + displayName +
                "§f" + "：" + chooseMessage);
        TagChurch.inst().playerCharacterChoices.put(chooser.getUniqueId(), characterClass);
        team.addPlayer(chooser);
    }

    @Override
    public void onSneakingRightClick(PlayerInteractEvent e) {
        onRightClick(e);
    }
}
