package SpireAutoChess.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;

import basemod.ReflectionHacks;

public class Patches {
    private static final float padX = AbstractCard.IMG_WIDTH * 0.75F + Settings.CARD_VIEW_PAD_X;

    @SpirePatch(clz = GridCardSelectScreen.class, method = "updateCardPositionsAndHoverLogic")
    public static class StupidGridCardSelectScreen {
        public static SpireReturn<Void> Prefix(GridCardSelectScreen _inst) {
            if (!_inst.isJustForConfirming && _inst.targetGroup.size() <= 4) {
                int size = _inst.targetGroup.size();
                float half = size / 2.0F;
                for (int i = 0; i < _inst.targetGroup.group.size(); i++) {
                    AbstractCard c = _inst.targetGroup.group.get(i);
                    c.target_x = Settings.WIDTH / 2.0F - (half - i) * padX;
                    c.target_y = Settings.HEIGHT / 2.0F;
                    c.fadingOut = false;
                    c.update();
                    c.updateHoverLogic();
                }
                ReflectionHacks.setPrivate(_inst, GridCardSelectScreen.class, "hoveredCard", null);
                for (AbstractCard c : _inst.targetGroup.group) {
                    if (c.hb.hovered) {
                        ReflectionHacks.setPrivate(_inst, GridCardSelectScreen.class, "hoveredCard", c);
                        break;
                    }
                }
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
