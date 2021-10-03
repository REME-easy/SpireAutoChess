package SpireAutoChess.cards.Enchanting;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;

import SpireAutoChess.cards.AbstractEnchantingCard;
import SpireAutoChess.helper.EventHelper.TeamMonsterSpawnSubscriber;
import SpireAutoChess.helper.GenericHelper;
import SpireAutoChess.modcore.ChessPlayerModCore;
import SpireAutoChess.monsters.AbstractTeamMonster;
import SpireAutoChess.monsters.AbstractTeamMonster.MonsterRace;

public class LivabilityOne extends AbstractEnchantingCard implements TeamMonsterSpawnSubscriber {
    private static final String ID = ChessPlayerModCore.MakePath(LivabilityOne.class.getSimpleName());
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public LivabilityOne() {
        super(ID, true, cardStrings, CardType.SKILL, CardRarity.COMMON, CardTarget.NONE);
        this.setupMagicNumber(1);
    }

    @Override
    public void OnTeamMonsterSpawn(AbstractTeamMonster monster) {
        if (monster.race == MonsterRace.ECOLOGY) {
            GenericHelper.applyPowerToSelf(monster, (m) -> new StrengthPower(m, this.magicNumber));
        }
    }

    @Override
    public void limitedUpgrade() {
        super.limitedUpgrade();
        this.upgradeMagicNumber(1);
    }
}
