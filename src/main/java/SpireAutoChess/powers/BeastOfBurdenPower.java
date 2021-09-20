package SpireAutoChess.powers;

import basemod.BaseMod;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

import SpireAutoChess.helper.GenericHelper;
import SpireAutoChess.modcore.ChessPlayerModCore;

public class BeastOfBurdenPower extends AbstractCeobePower {
    private static final String id = ChessPlayerModCore.MakePath(BeastOfBurdenPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(id);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BeastOfBurdenPower(AbstractCreature owner, int amt) {
        super(owner, amt, id, NAME);
        this.loadRegion("tools");
    }

    @Override
    public String getDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        BaseMod.MAX_HAND_SIZE += this.amount;
        GenericHelper.info(String.valueOf(BaseMod.MAX_HAND_SIZE));
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        BaseMod.MAX_HAND_SIZE += stackAmount;
        GenericHelper.info(String.valueOf(BaseMod.MAX_HAND_SIZE));
    }

    @Override
    public void onVictory() {
        super.onVictory();
        BaseMod.MAX_HAND_SIZE -= this.amount;
        GenericHelper.info(String.valueOf(BaseMod.MAX_HAND_SIZE));
    }
}
