package SpireAutoChess.cards.Attack;

import static com.megacrit.cardcrawl.cards.AbstractCard.CardType.ATTACK;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import SpireAutoChess.cards.AbstractChessCard;
import SpireAutoChess.modcore.ChessPlayerModCore;

public class BlackKey extends AbstractChessCard {
    private static final String ID = ChessPlayerModCore.MakePath(BlackKey.class.getSimpleName());
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public BlackKey() {
        super(ID, cardStrings, 0, ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.setupDamage(11);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.LIGHTNING));
    }

    @Override
    public void limitedUpgrade() {
        super.limitedUpgrade();
        this.upgradeDamage(3);
    }
}
