package SpireAutoChess.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class NotEnoughMoneyEffect extends AbstractGameEffect {
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString("ChessPlayer_OrganizationScreen").TEXT;

    private float x;
    private float y;
    private Color color;

    public NotEnoughMoneyEffect() {
        this.color = Settings.RED_TEXT_COLOR.cpy();
        this.duration = 2.0F;
        this.color.a = 0.0F;
        this.x = (float) Settings.WIDTH / 2.0F;
        this.y = Settings.OPTION_Y + 460.0F * Settings.scale;
        this.scale = 1.3F;
    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        this.scale = MathHelper.popLerpSnap(this.scale, 1.0F);
        if (this.duration < 0.0F) {
            this.duration = 0.0F;
            this.isDone = true;
        }

        if (this.duration < 1.0F) {
            this.color.a = this.duration;
        } else {
            this.color.a = 1.0F;
        }

    }

    public void render(SpriteBatch sb) {
        sb.setColor(new Color(0.0F, 0.0F, 0.0F, this.duration / 2.0F));
        float w = FontHelper.getWidth(FontHelper.panelNameFont, TEXT[6], this.scale);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, this.x - w / 2.0F - 50.0F * Settings.scale,
                this.y - 25.0F * Settings.scale, w + 100.0F * Settings.scale, 50.0F * Settings.scale);
        FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, TEXT[6], this.x, this.y, this.color, this.scale);
    }

    public void dispose() {
    }
}
