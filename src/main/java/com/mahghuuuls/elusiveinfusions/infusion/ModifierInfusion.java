package com.mahghuuuls.elusiveinfusions.infusion;

import com.mahghuuuls.elusiveinfusions.roster.Channel;
import com.mahghuuuls.elusiveinfusions.roster.TierValues;
import com.mahghuuuls.everfillingflasks.api.FlaskBonuses;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.Map;

/**
 * An infusion that changes the hosting flask itself while placed. A negative value is the penalty
 * side of a trade-off; Everfilling Flasks combines and clamps the totals.
 *
 * <p>Everfilling Flasks asks for the contribution whenever it computes a flask or draws a tooltip,
 * so the values are unpacked once here and the call itself creates nothing.
 */
final class ModifierInfusion extends RosterInfusion {

    private final Channel[] channels;
    private final float[] percents;

    ModifierInfusion(TierValues values) {
        super(values);
        Map<Channel, Double> bonuses = values.bonuses();
        channels = new Channel[bonuses.size()];
        percents = new float[bonuses.size()];
        int i = 0;
        for (Map.Entry<Channel, Double> bonus : bonuses.entrySet()) {
            channels[i] = bonus.getKey();
            percents[i] = bonus.getValue().floatValue();
            i++;
        }
    }

    @Override
    public void contribute(ItemStack infusion, EntityPlayer player, FlaskBonuses bonuses) {
        for (int i = 0; i < channels.length; i++) {
            float percent = percents[i];
            switch (channels[i]) {
                case HEALING:
                    bonuses.healing(percent);
                    break;
                case DRINK_SPEED:
                    bonuses.drinkSpeed(percent);
                    break;
                case HIT_RESISTANCE:
                    bonuses.hitResistance(percent);
                    break;
                case RECHARGE_SPEED:
                    bonuses.rechargeSpeed(percent);
                    break;
                case EFFECT_POWER:
                    bonuses.effectPower(percent);
                    break;
                default:
                    throw new IllegalStateException("Unmapped channel " + channels[i]);
            }
        }
    }
}
