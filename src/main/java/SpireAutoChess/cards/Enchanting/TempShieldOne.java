package SpireAutoChess.cards.Enchanting;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import SpireAutoChess.cards.AbstractEnchantingCard;
import SpireAutoChess.helper.EventHelper.BattleStartSubscriber;
import SpireAutoChess.modcore.ChessPlayerModCore;

public class TempShieldOne extends AbstractEnchantingCard implements BattleStartSubscriber {
    private static final String ID = ChessPlayerModCore.MakePath(TempShieldOne.class.getSimpleName());
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public TempShieldOne() {
        super(ID, true, cardStrings, -1, CardType.SKILL, CardRarity.COMMON, CardTarget.NONE);
        this.setupMagicNumber(6);
    }

    @Override
    public void OnBattleStart(AbstractRoom room) {
        if (room.phase == RoomPhase.COMBAT) {
            addToBot(new GainBlockAction(GetEquippedMonster(), GetEquippedMonster(), this.magicNumber));
        }
    }

    @Override
    public void limitedUpgrade() {
        super.limitedUpgrade();
        this.upgradeMagicNumber(3);
    }

}
