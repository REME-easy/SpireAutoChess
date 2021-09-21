package SpireAutoChess.cards.Attack;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import SpireAutoChess.cards.AbstractChessCard;
import SpireAutoChess.helper.EventHelper.BattleStartSubscriber;
import SpireAutoChess.modcore.ChessPlayerModCore;

public class HealthUpOne extends AbstractChessCard implements BattleStartSubscriber {
    private static final String ID = ChessPlayerModCore.MakePath(HealthUpOne.class.getSimpleName());
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public HealthUpOne() {
        super(ID, cardStrings, -1, CardType.SKILL, CardRarity.BASIC, CardTarget.NONE);
        this.setupMagicNumber(4);
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
        this.upgradeMagicNumber(2);
    }

}
