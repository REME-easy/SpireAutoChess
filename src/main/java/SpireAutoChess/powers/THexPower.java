package SpireAutoChess.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import SpireAutoChess.cards.Enchanting.Hex;
import SpireAutoChess.helper.GenericHelper;
import SpireAutoChess.modcore.ChessPlayerModCore;

public class THexPower extends AbstractChessPower {
    private static final String id = ChessPlayerModCore.MakePath(THexPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(id);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public THexPower(AbstractCreature owner, int amt) {
        super(owner, amt, id, NAME);
        this.loadRegion("hex");
    }

    @Override
    public String getDescription() {
        return DESCRIPTIONS[0];

    }

    @Override
    public void onVictory() {
        super.onVictory();
        for (int i = 0; i < this.amount; i++) {
            GenericHelper
                    .addEffect(new ShowCardAndObtainEffect(new Hex(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
        }
    }
}
