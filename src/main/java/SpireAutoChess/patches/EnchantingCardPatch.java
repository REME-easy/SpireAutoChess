package SpireAutoChess.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;

import SpireAutoChess.cards.AbstractChessCard;
import javassist.CtBehavior;

public class EnchantingCardPatch {
    @SpirePatch(clz = CardGroup.class, method = "initializeDeck")
    public static class DontInitPatch {

        @SpireInsertPatch(locator = Locator.class, localvars = { "copy" })
        public static void Insert(CardGroup _inst, CardGroup masterDeck, @ByRef CardGroup[] copy) {
            CardGroup tmp = copy[0];
            AbstractCard c;
            for (int i = tmp.size() - 1; i > 0; i--) {
                c = tmp.group.get(i);
                if (c instanceof AbstractChessCard && ((AbstractChessCard) c).isEnchanting) {
                    tmp.removeCard(c);
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            private Locator() {
            }

            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "shuffle");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }
}