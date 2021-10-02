package SpireAutoChess.cards.Enchanting;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import SpireAutoChess.cards.AbstractEnchantingCard;
import SpireAutoChess.helper.EventHelper.BattleStartSubscriber;
import SpireAutoChess.helper.GenericHelper;
import SpireAutoChess.modcore.ChessPlayerModCore;

public class Barricade extends AbstractEnchantingCard implements BattleStartSubscriber {
    private static final String ID = ChessPlayerModCore.MakePath(Barricade.class.getSimpleName());
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public Barricade() {
        super(ID, true, cardStrings, -1, CardType.POWER, CardRarity.RARE, CardTarget.NONE);
    }

    @Override
    public void OnBattleStart(AbstractRoom room) {
        if (room.phase == RoomPhase.COMBAT) {
            GenericHelper.applyPowerToSelf(GetEquippedMonster(), (m) -> new BarricadePower(m));
        }
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void limitedUpgrade() {
        super.limitedUpgrade();
    }

}
