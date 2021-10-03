package SpireAutoChess.patches;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import javassist.CtBehavior;

public class CustomScreenQueuePatch {

    public static final ArrayList<ICustomScreen> CustomScreens = new ArrayList<>();
    // @SpirePatch(clz = AbstractDungeon.class, method = "openPreviousScreen")
    // public static class OpenPreviousScreenPatch {
    // public static void Postfix(AbstractDungeon.CurrentScreen s) {
    // if (OrganizationScreen.Inst().isOpen) {
    // OrganizationScreen.Inst().reopen();
    // }
    // if (MonsterShopScreen.Inst().isOpen) {
    // MonsterShopScreen.Inst().reopen();
    // }

    // }
    // }

    // @SpirePatch(clz = AbstractDungeon.class, method = "closeCurrentScreen")
    // public static class CloseCurrentScreenPatch {
    // public static void Prefix() {
    // if (AbstractDungeon.screen == Enum.ORGANIZATION_SCREEN) {
    // OrganizationScreen.Inst().close();
    // } else if (AbstractDungeon.screen == Enum.MONSTER_SHOP_SCREEN) {
    // MonsterShopScreen.Inst().close();
    // }
    // }
    // }

    @SpirePatch(clz = AbstractDungeon.class, method = "render")
    public static class Render {
        public Render() {
        }

        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractDungeon __instance, SpriteBatch sb) {
            // if (OrganizationScreen.Inst().isOpen) {
            // OrganizationScreen.Inst().render(sb);
            // }
            // if (MonsterShopScreen.Inst().isOpen) {
            // MonsterShopScreen.Inst().render(sb);
            // }
            for (int i = CustomScreens.size() - 1; i >= 0; i--) {
                ICustomScreen screen = CustomScreens.get(i);
                if (screen.isOpen()) {
                    screen.render(sb);
                }
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
            // if (OrganizationScreen.Inst().isOpen) {
            // OrganizationScreen.Inst().update();
            // }
            // if (MonsterShopScreen.Inst().isOpen) {
            // MonsterShopScreen.Inst().update();
            // }
            if (CustomScreens.size() > 0) {
                ICustomScreen screen = CustomScreens.get(0);
                if (screen.isOpen()) {
                    screen.update();
                }
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

    public interface ICustomScreen {
        boolean isOpen();

        default void queueToFont() {
            CustomScreens.remove(this);
            CustomScreens.add(0, this);
        }

        void update();

        void render(SpriteBatch sb);
    }
}