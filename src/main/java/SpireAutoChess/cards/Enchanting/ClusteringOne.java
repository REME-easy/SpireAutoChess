package SpireAutoChess.cards.Enchanting;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;

import SpireAutoChess.cards.AbstractEnchantingCard;
import SpireAutoChess.helper.EventHelper.TeamMonsterSpawnSubscriber;
import SpireAutoChess.helper.GenericHelper;
import SpireAutoChess.modcore.ChessPlayerModCore;
import SpireAutoChess.monsters.AbstractTeamMonster;

public class ClusteringOne extends AbstractEnchantingCard implements TeamMonsterSpawnSubscriber {
    private static final String ID = ChessPlayerModCore.MakePath(ClusteringOne.class.getSimpleName());
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public ClusteringOne() {
        super(ID, true, cardStrings, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.NONE);
        this.setupMagicNumber(1);
        this.setupSecondMagicNumber(20);
    }

    @Override
    public void OnTeamMonsterSpawn(AbstractTeamMonster monster) {
        if (monster.maxHealth <= this.SecondaryMagicNum)
            GenericHelper.applyPowerToSelf(monster, (m) -> new StrengthPower(m, this.magicNumber));

    }

    @Override
    public void limitedUpgrade() {
        super.limitedUpgrade();
        this.upgradeMagicNumber(1);
        this.upgradeSecondMagicNumber(5);
    }

}
