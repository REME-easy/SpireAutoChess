package SpireAutoChess.cards.Enchanting;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import SpireAutoChess.cards.AbstractEnchantingCard;
import SpireAutoChess.helper.EventHelper.BattleStartSubscriber;
import SpireAutoChess.helper.GenericHelper;
import SpireAutoChess.modcore.ChessPlayerModCore;

public class Hex extends AbstractEnchantingCard implements BattleStartSubscriber {
    private static final String ID = ChessPlayerModCore.MakePath(Hex.class.getSimpleName());
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public Hex() {
        super(ID, true, cardStrings, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.NONE);
        this.setupMagicNumber(1);
    }

    @Override
    public void OnBattleStart(AbstractRoom room) {
        if (room.phase == RoomPhase.COMBAT) {
            GenericHelper.applyPowerToSelf(GetEquippedMonster(), (m) -> new StrengthPower(m, magicNumber));
        }
    }

    @Override
    public void limitedUpgrade() {
        super.limitedUpgrade();
        this.upgradeMagicNumber(1);
    }

}
