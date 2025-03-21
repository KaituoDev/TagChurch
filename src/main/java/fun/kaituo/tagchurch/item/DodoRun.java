package fun.kaituo.tagchurch.item;

import fun.kaituo.tagchurch.TagChurch;
import fun.kaituo.tagchurch.character.Dodo;
import fun.kaituo.tagchurch.util.ActiveItem;
import fun.kaituo.tagchurch.util.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class DodoRun extends ActiveItem {
    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    public boolean use(Player p) {
        ItemStack dodoRunFaster = TagChurch.inst().getItem("DodoRunFaster");
        assert dodoRunFaster != null;
        p.getInventory().addItem(dodoRunFaster);
        p.sendMessage("§b获得加速！");
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));

        PlayerData data = TagChurch.inst().idDataMap.get(p.getUniqueId());
        assert data != null;
        if (data.getClass().equals(Dodo.class)) {
            p.sendMessage("§a获得额外治疗！");
            p.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 0));
        }
        return true;
    }
}
