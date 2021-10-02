package SpireAutoChess.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

import SpireAutoChess.actions.DamageFrontAction;
import SpireAutoChess.modcore.ChessPlayerModCore;

public class TExplosivePower extends AbstractChessPower {
    private static final String id = ChessPlayerModCore.MakePath(TExplosivePower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(id);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public TExplosivePower(AbstractCreature owner, int amt) {
        super(owner, amt, id, NAME);
        this.loadRegion("explosive");
    }

    @Override
    public String getDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onDeath() {
        super.onDeath();
        addToBot(new DamageFrontAction(new DamageInfo(this.owner, this.amount, DamageType.THORNS), AttackEffect.FIRE));
    }
}
