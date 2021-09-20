package SpireAutoChess.helper;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;

import SpireAutoChess.cards.AbstractChessCard;

public class SecondaryMagicVariable extends DynamicVariable {
    public SecondaryMagicVariable() {
    }

    public String key() {
        return "CM2";
    }

    public boolean isModified(AbstractCard card) {
        if (card instanceof AbstractChessCard) {
            AbstractChessCard c = (AbstractChessCard) card;
            return c.isSecondaryMagicNumModified;
        } else {
            return false;
        }
    }

    public int value(AbstractCard card) {
        if (card instanceof AbstractChessCard) {
            AbstractChessCard c = (AbstractChessCard) card;
            return c.SecondaryMagicNum;
        } else {
            return 0;
        }
    }

    public int baseValue(AbstractCard card) {
        if (card instanceof AbstractChessCard) {
            AbstractChessCard c = (AbstractChessCard) card;
            return c.baseSecondaryMagicNum;
        } else {
            return 0;
        }
    }

    public boolean upgraded(AbstractCard card) {
        if (card instanceof AbstractChessCard) {
            AbstractChessCard c = (AbstractChessCard) card;
            return c.upgradedSecondaryMagicNum;
        } else {
            return false;
        }
    }
}
