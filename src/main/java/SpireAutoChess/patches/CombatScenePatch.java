package SpireAutoChess.patches;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class CombatScenePatch {
    public static final OrthographicCamera ZoomCamera;

    public static Vector3 Position = new Vector3();
    public static int PrevX;
    public static int PrevY;

    static {
        ZoomCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ZoomCamera.zoom += 0.2F;
        ZoomCamera.position.set(Settings.WIDTH * 0.4F, Settings.HEIGHT * 0.5F, 0.0F);
        ZoomCamera.update();
    }

    @SpirePatch(clz = AbstractRoom.class, method = "render")
    public static class RoomRenderStartPatch {
        public static void Prefix(AbstractRoom _inst, SpriteBatch sb) {
            // PrevMatrix = sb.getProjectionMatrix();
            sb.end();
            sb.setProjectionMatrix(ZoomCamera.combined);
            CardCrawlGame.psb.setProjectionMatrix(ZoomCamera.combined);
            sb.begin();
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method = "render")
    public static class RoomRenderEndPatch {
        @SpireInsertPatch(rloc = 17)
        public static void Insert(AbstractRoom _inst, SpriteBatch sb) {
            sb.end();
            sb.setProjectionMatrix(CardCrawlGame.viewport.getCamera().combined);
            CardCrawlGame.psb.setProjectionMatrix(CardCrawlGame.viewport.getCamera().combined);
            sb.begin();
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method = "update")
    public static class RoomUpdateStartPatch {
        public static void Prefix(AbstractRoom _inst) {
            PrevX = InputHelper.mX;
            PrevY = InputHelper.mY;
            Position.set(InputHelper.mX, InputHelper.mY, 0.0F);
            ZoomCamera.unproject(Position);
            InputHelper.mX = (int) Position.x;
            InputHelper.mY = (int) Position.y;
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method = "update")
    public static class RoomUpdateEndPatch {
        public static void Postfix(AbstractRoom _inst) {
            InputHelper.mX = PrevX;
            InputHelper.mY = PrevY;
        }
    }
}