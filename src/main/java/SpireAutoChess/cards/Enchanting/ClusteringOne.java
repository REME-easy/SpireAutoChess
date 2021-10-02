package SpireAutoChess.cards.Enchanting;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import SpireAutoChess.cards.AbstractEnchantingCard;
import SpireAutoChess.helper.EventHelper.BattleStartSubscriber;
import SpireAutoChess.modcore.ChessPlayerModCore;

public class ClusteringOne extends AbstractEnchantingCard implements BattleStartSubscriber {
    private static final String ID = ChessPlayerModCore.MakePath(ClusteringOne.class.getSimpleName());
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public ClusteringOne() {
        super(ID, true, cardStrings, -1, CardType.SKILL, CardRarity.BASIC, CardTarget.NONE);
        this.setupSecondMagicNumber(20);
        this.setupMagicNumber(2);
    }

    @Override
    public void OnBattleStart(AbstractRoom room) {
        if (room.phase == RoomPhase.COMBAT) {
            // TODO
        }
    }

    @Override
    public void limitedUpgrade() {
        super.limitedUpgrade();
        this.upgradeMagicNumber(1);
        this.upgradeSecondMagicNumber(5);
    }

}
