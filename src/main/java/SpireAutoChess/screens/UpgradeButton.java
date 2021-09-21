package SpireAutoChess.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class UpgradeButton {
    private static final Color HOVER_BLEND_COLOR = new Color(1.0F, 1.0F, 1.0F, 0.4F);
    private static final Texture BUTTON_IMG = ImageMaster.loadImage("ChessPlayerResources/img/UI/upgrade_button.png");

    public float current_x;
    public float current_y;
    public Hitbox hb;
    private OrganizationScreen screen;
    private int index;

    public UpgradeButton(OrganizationScreen screen, int index) {
        this.screen = screen;
        this.index = index;
        this.current_x = 0.0F;
        this.current_y = 0.0F;
        this.hb = new Hitbox(100.0F * Settings.scale, 100.0F * Settings.scale);
    }

    public void update() {
        this.hb.update();
        this.hb.move(this.current_x, current_y);
        if (InputHelper.justClickedLeft && this.hb.hovered) {
            this.hb.clickStarted = true;
            CardCrawlGame.sound.play("UI_CLICK_1");
        }

        if (this.hb.justHovered) {
            CardCrawlGame.sound.play("UI_HOVER");
        }

        if (this.hb.clicked) {
            this.hb.clicked = false;
            this.screen.selectToUpgrade(index);
        }
    }

    public void render(SpriteBatch sb) {
        if (this.hb.hovered) {
            sb.setColor(HOVER_BLEND_COLOR);
        } else {
            sb.setColor(Color.WHITE);
        }
        sb.draw(BUTTON_IMG, this.current_x - 64.0F, this.current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F,
                Settings.scale, Settings.scale, 0.0F, 0, 0, 128, 128, false, false);
        this.hb.render(sb);

    }
}