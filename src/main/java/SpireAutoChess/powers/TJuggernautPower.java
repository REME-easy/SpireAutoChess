package SpireAutoChess.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

import SpireAutoChess.actions.DamageFrontAction;
import SpireAutoChess.helper.EventHelper.TeamMonsterGainBlockSubscriber;
import SpireAutoChess.modcore.ChessPlayerModCore;
import SpireAutoChess.monsters.AbstractTeamMonster;

public class TJuggernautPower extends AbstractChessPower implements TeamMonsterGainBlockSubscriber {
    private static final String id = ChessPlayerModCore.MakePath(TJuggernautPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(id);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public TJuggernautPower(AbstractCreature owner, int amt) {
        super(owner, amt, id, NAME);
        this.loadRegion("juggernaut");
        this.subscribe();
    }

    @Override
    public String getDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public float OnGainBlock(AbstractTeamMonster monster, float amt) {
        addToBot(new DamageFrontAction(new DamageInfo(this.owner, this.amount, DamageType.THORNS),
                AttackEffect.LIGHTNING));
        return amt;
    }
}
