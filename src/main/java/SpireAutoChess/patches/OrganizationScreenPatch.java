package SpireAutoChess.patches;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import SpireAutoChess.screens.OrganizationScreen;
import javassist.CtBehavior;

public class OrganizationScreenPatch {
    public static class Enum {
        @SpireEnum
        public static AbstractDungeon.CurrentScreen ORGANIZATION_SCREEN;

        public Enum() {
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "openPreviousScreen")
    public static class OpenPreviousScreen {
        public OpenPreviousScreen() {
        }

        public static void Postfix(AbstractDungeon.CurrentScreen s) {
            if (s == Enum.ORGANIZATION_SCREEN) {
                OrganizationScreen.Inst().reopen();
            }

        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "render")
    public static class Render {
        public Render() {
        }

        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractDungeon __instance, SpriteBatch sb) {
            if (AbstractDungeon.screen == Enum.ORGANIZATION_SCREEN) {
                OrganizationScreen.Inst().render(sb);
            }
        }

        private static class Locator extends SpireInsertLocator {
            private Locator() {
            }

            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher("com.megacrit.cardcrawl.dungeons.AbstractDungeon",
                        "screen");
                return LineFinder.findInOrder(ctBehavior, new ArrayList<>(), finalMatcher);
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "update")
    public static class Update {
        public Update() {
        }

        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractDungeon __instance) {
            if (AbstractDungeon.screen == Enum.ORGANIZATION_SCREEN) {
                OrganizationScreen.Inst().update();
            }

        }

        private static class Locator extends SpireInsertLocator {
            private Locator() {
            }

            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher("com.megacrit.cardcrawl.dungeons.AbstractDungeon",
                        "screen");
                return LineFinder.findInOrder(ctBehavior, new ArrayList<>(), finalMatcher);
            }
        }
    }
}