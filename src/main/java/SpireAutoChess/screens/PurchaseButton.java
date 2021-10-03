package SpireAutoChess.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import SpireAutoChess.monsters.AbstractTeamMonster;
import SpireAutoChess.vfx.NotEnoughMoneyEffect;

public class PurchaseButton {
    private static final Color HOVER_BLEND_COLOR = new Color(1.0F, 1.0F, 1.0F, 0.4F);
    private static final Texture BUTTON_IMG = ImageMaster.loadImage("ChessPlayerResources/img/UI/sell_button.png");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString("ChessPlayer_MonsterShopScreen").TEXT;
    private static final float GOLD_IMG_OFFSET_X = -50.0F * Settings.scale;
    private static final float GOLD_IMG_OFFSET_Y = -120.0F * Settings.scale;
    private static final float PRICE_TEXT_OFFSET_X = 16.0F * Settings.scale;
    private static final float PRICE_TEXT_OFFSET_Y = -95.0F * Settings.scale;
    private static final float GOLD_IMG_WIDTH = ImageMaster.UI_GOLD.getWidth() * Settings.scale;

    public float current_x;
    public float current_y;
    public Hitbox hb;
    private MonsterShopScreen screen;
    private AbstractTeamMonster monster;
    private int purchasePrice;

    public PurchaseButton(MonsterShopScreen screen, AbstractTeamMonster monster) {
        this.screen = screen;
        this.monster = monster;
        this.purchasePrice = monster.getPurchasePrice();
        this.current_x = 0.0F;
        this.current_y = 0.0F;
        this.hb = new Hitbox(100.0F * Settings.scale, 100.0F * Settings.scale);
    }

    public void update() {
        this.hb.move(this.current_x, current_y);
        this.hb.update();
        if (InputHelper.justClickedLeft && this.hb.hovered) {
            this.hb.clickStarted = true;
            CardCrawlGame.sound.play("UI_CLICK_1");
        }

        if (this.hb.justHovered) {
            CardCrawlGame.sound.play("UI_HOVER");
            this.screen.moveHand(monster.drawX - monster.hb_w, monster.drawY + monster.hb_h / 2.0F);
        }

        if (this.hb.clicked) {
            this.hb.clicked = false;
            if (purchasePrice > AbstractDungeon.player.gold) {
                AbstractDungeon.topLevelEffects.add(new NotEnoughMoneyEffect());
                this.screen.CantBuySpeech();
            } else {
                AbstractDungeon.player.loseGold(purchasePrice);
                this.screen.purchaseMonster(monster);
                this.screen.BuySpeech();

            }
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
        if (this.hb.hovered) {
            TipHelper.renderGenericTip(this.current_x - 64.0F, this.current_y + 100.0F * Settings.scale, TEXT[0],
                    TEXT[1]);

            Color color;
            sb.setColor(Color.WHITE);
            sb.draw(ImageMaster.UI_GOLD, this.current_x + GOLD_IMG_OFFSET_X, this.current_y + GOLD_IMG_OFFSET_Y,
                    GOLD_IMG_WIDTH, GOLD_IMG_WIDTH);
            if (purchasePrice > AbstractDungeon.player.gold) {
                color = Color.SALMON.cpy();
            } else {
                color = Color.WHITE.cpy();
            }
            FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, Integer.toString(purchasePrice),
                    current_x + PRICE_TEXT_OFFSET_X, current_y + PRICE_TEXT_OFFSET_Y, color);
        }
        this.hb.render(sb);

    }
}