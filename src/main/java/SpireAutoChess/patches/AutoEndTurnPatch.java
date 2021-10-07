package SpireAutoChess.patches;

import SpireAutoChess.character.ChessPlayer;
import SpireAutoChess.ui.AutoEndTurnOption;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.EndTurnAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.EnemyTurnEffect;

public class AutoEndTurnPatch {
    
    @SpirePatch(clz = OverlayMenu.class, method = SpirePatch.CLASS)
    public static class AutoEndField {
        public static SpireField<AutoEndTurnOption> Option = new SpireField<>(() -> null);
    }
    
    public static void initOption(OverlayMenu _inst) {
        if (AutoEndField.Option.get(_inst) == null)
            AutoEndField.Option.set(_inst, new AutoEndTurnOption());
    }
    
    @SpirePatch(clz = OverlayMenu.class, method = SpirePatch.CONSTRUCTOR)
    public static class InitAutoEndOption {
        @SpirePostfixPatch
        public static void Postfix(OverlayMenu _inst, AbstractPlayer p) {
            if (p instanceof ChessPlayer) {
                initOption(_inst);
            }
        }
    }
    
    @SpirePatch(clz = OverlayMenu.class, method = "update")
    public static class UpdateOption {
        @SpireInsertPatch(rloc = 11)
        public static void Insert(OverlayMenu _inst) {
            if (AutoEndField.Option.get(_inst) != null) {
                AutoEndField.Option.get(_inst).update();
            }
        }
    }

    @SpirePatch(clz = OverlayMenu.class, method = "render")
    public static class RenderOption {
        public static void Prefix(OverlayMenu _inst, SpriteBatch sb) {
            if (AutoEndField.Option.get(_inst) != null) {
                AutoEndField.Option.get(_inst).render(sb);
            }
        }
    }
}