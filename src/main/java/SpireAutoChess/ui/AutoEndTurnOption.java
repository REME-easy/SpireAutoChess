package SpireAutoChess.ui;

import SpireAutoChess.helper.GenericHelper;
import SpireAutoChess.utils.GeneralUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.buttons.EndTurnButton;

import java.lang.reflect.Field;

import static com.megacrit.cardcrawl.core.Settings.*;
import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.actionManager;
import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.overlayMenu;

public final class AutoEndTurnOption implements GeneralUtils {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    private static final String LABEL;
    
    private static Vector2 position;
    private static boolean AutoEnding;
    
    private float bufferTime;
    private boolean buffered;
    private Hitbox hitbox;
    private Color textColor;
    private Texture img;
    
    static {
        uiStrings = CardCrawlGame.languagePack.getUIString("ChessPlayer_AutoEndTurnOption");
        TEXT  = uiStrings.TEXT;
        LABEL = TEXT[0];
        
        position = new Vector2(0F, 0F);
        AutoEnding = false;
    }
    
    public AutoEndTurnOption() {
        hitbox = new Hitbox(200F * scale, 60F * scale);
        textColor = CREAM_COLOR.cpy();
        img = ImageMaster.COLOR_TAB_BOX_UNTICKED;
        bufferTime = 0F;
        buffered = true;
        updatePosition();
    }
    
    public void update() {
        textColor = hitbox.hovered ? GOLD_COLOR.cpy() : CREAM_COLOR.cpy();
        img = isAutoEnding() ? ImageMaster.COLOR_TAB_BOX_TICKED : ImageMaster.COLOR_TAB_BOX_UNTICKED;
        updatePosition();
        updateHitbox();
        updateDisableEndTurnButtonLogic();
        updateBufferTime();
    }

    /**
     * 更新缓冲时间
     */
    private void updateBufferTime() {
        if (bufferTime >= 0F)
            bufferTime -= Gdx.graphics.getDeltaTime();
        buffered = bufferTime > 0F;
    }
    
    private void updateDisableEndTurnButtonLogic() {
        if (isAutoEnding()) {
            overlayMenu.endTurnButton.disable();
        } else {
            overlayMenu.endTurnButton.enable();
        }
    }
    
    private void updateHitbox() {
        hitbox.move(position.x, position.y);
        hitbox.update();
        if (hitbox.hovered && InputHelper.justClickedLeft && !buffered)
            hitbox.clickStarted = true;
        if (hitbox.clicked) {
            hitbox.clicked = false;
            bufferTime = 1F;
            setAutoEnding(!isAutoEnding());
        }
    }

    /**
     * 更新选项位置，与结束回合按钮保持一致
     */
    private void updatePosition() {
        try {
            Field etb = getField(EndTurnButton.class.getDeclaredField("current_x"));
            float cx = etb.getFloat(overlayMenu.endTurnButton);
            etb = getField(EndTurnButton.class.getDeclaredField("current_y"));
            float cy = etb.getFloat(overlayMenu.endTurnButton);
            etb = getField(EndTurnButton.class.getDeclaredField("hb"));
            float height = ((Hitbox) etb.get(overlayMenu.endTurnButton)).height * 0.75F;
            position.set(cx, cy + height);
        } catch (Exception e) {
            GenericHelper.info("Failed to set auto ending option's position");
            e.printStackTrace();
        }
    }
    
    public void render(SpriteBatch sb) {
        hitbox.render(sb);
        FontHelper.renderFontRightAligned(sb, FontHelper.topPanelInfoFont, LABEL, position.x + 90F * scale, 
                position.y, textColor);
        sb.setColor(textColor);
        sb.draw(img, (position.x + 80F * scale) - FontHelper.getSmartWidth(FontHelper.topPanelInfoFont, LABEL, 
                        9999F, 0F) - 24F, position.y - 24F, 24F, 24F, 
                48F, 48F, scale, scale, 0F, 0, 0, 48, 48, 
                false, false);
    }

    public static void setAutoEnding(boolean autoEnding) {
        AutoEnding = autoEnding;
        if (isAutoEnding() && !actionManager.turnHasEnded && actionManager.monsterQueue.isEmpty())
            actionManager.addToBottom(new PressEndTurnButtonAction());
    }

    public static boolean isAutoEnding() {
        return AutoEnding;
    }
}