package fun.kaituo.tagchurch.item;

import fun.kaituo.tagchurch.TagChurch;
import fun.kaituo.tagchurch.state.HuntState;
import fun.kaituo.tagchurch.util.ActiveItem;
import fun.kaituo.tagchurch.util.Corpse;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import static fun.kaituo.gameutils.util.ItemUtils.removeItem;

@SuppressWarnings("unused")
public class Revival extends ActiveItem {
    @Override
    public Rarity getRarity() {
        return Rarity.LEGENDARY;
    }

    @EventHandler
    public void onUse(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        if (!TagChurch.inst().playerIds.contains(p.getUniqueId())) {
            return;
        }
        if (!p.getInventory().getItemInMainHand().isSimilar(item)) {
            return;
        }
        Entity entity = e.getRightClicked();
        if (!(entity instanceof ArmorStand stand)) {
            return;
        }
        for (Corpse corpse: HuntState.INST.corpses) {
            if (!corpse.getMainPart().equals(stand)) {
                continue;
            }
            if (corpse.revive()) {
                removeItem(p.getInventory(), item);
                return;
            }
        }
    }

    @Override
    public boolean use(Player p) {
        return false;
    }
}
