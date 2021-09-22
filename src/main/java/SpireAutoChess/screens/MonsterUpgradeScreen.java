package SpireAutoChess.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.ui.buttons.ConfirmButton;

import SpireAutoChess.helper.CustomTipRenderer;
import SpireAutoChess.helper.GenericHelper;
import SpireAutoChess.helper.MonsterManager;
import SpireAutoChess.monsters.AbstractTeamMonster;

public class MonsterUpgradeScreen {
    public boolean isOpen = false;

    private float arrowScale1;
    private float arrowScale2;
    private float arrowScale3;
    private float arrowTimer;
    public ConfirmButton confirmButton;
    public EmptyCancelButton cancelButton;

    private AbstractTeamMonster monster;
    private AbstractTeamMonster upgradeMonster;

    private static final float START_Y = (float) Settings.HEIGHT / 2.0F - 32.0F;
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString("ChessPlayer_OrganizationScreen").TEXT;

    public MonsterUpgradeScreen() {
        this.confirmButton = new ConfirmButton();
        this.cancelButton = new EmptyCancelButton();
    }

    public void open(AbstractTeamMonster monster) {
        this.monster = monster;
        GenericHelper.MoveMonster(monster, Settings.WIDTH * 0.1F, AbstractDungeon.floorY);
        this.upgradeMonster = MonsterManager.GetMonsterInstance(monster.id);
        for (int i = 0; i < this.monster.upgradedTimes + 1; i++) {
            this.upgradeMonster.upgrade(i);
        }
        this.isOpen = true;
        confirmButton.isDisabled = false;
        confirmButton.hideInstantly();
        confirmButton.show();
        cancelButton.hideInstantly();
        cancelButton.show(TEXT[5]);
    }

    public void close() {
        this.isOpen = false;
        confirmButton.hide();
        cancelButton.hide();
    }

    public void update() {
        this.confirmButton.update();
        if (this.confirmButton.hb.clicked) {
            this.confirmButton.hb.clicked = false;
            this.confirmButton.hb.clickStarted = false;
            this.confirmButton.isDisabled = true;
            this.confirmButton.hide();
            this.cancelButton.hide();
            this.close();
        }

        this.cancelButton.update();
        if (this.cancelButton.hb.clicked) {
            this.cancelButton.hb.clicked = false;
            this.cancelButton.hb.clickStarted = false;
            this.confirmButton.hide();
            this.cancelButton.hide();
            this.close();
        }
    }

    public void updateInput() {

    }

    public void render(SpriteBatch sb) {
        CustomTipRenderer.renderGenericTip(Settings.WIDTH * 0.25F, Settings.HEIGHT * 0.6F, monster.name,
                monster.getDescription());
        renderArrows(sb);
        TipHelper.renderGenericTip(Settings.WIDTH * 0.6F, Settings.HEIGHT * 0.6F, upgradeMonster.name,
                upgradeMonster.getDescription());
        this.confirmButton.render(sb);
        this.cancelButton.render(sb);
        this.monster.render(sb);
    }

    private void renderArrows(SpriteBatch sb) {
        float x = (float) Settings.WIDTH / 2.0F - 73.0F * Settings.scale - 32.0F;
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.UPGRADE_ARROW, x, START_Y, 32.0F, 32.0F, 64.0F, 64.0F, this.arrowScale1 * Settings.scale,
                this.arrowScale1 * Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        x += 64.0F * Settings.scale;
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.UPGRADE_ARROW, x, START_Y, 32.0F, 32.0F, 64.0F, 64.0F, this.arrowScale2 * Settings.scale,
                this.arrowScale2 * Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        x += 64.0F * Settings.scale;
        sb.draw(ImageMaster.UPGRADE_ARROW, x, START_Y, 32.0F, 32.0F, 64.0F, 64.0F, this.arrowScale3 * Settings.scale,
                this.arrowScale3 * Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        this.arrowTimer += Gdx.graphics.getDeltaTime() * 2.0F;
        this.arrowScale1 = 0.8F + (MathUtils.cos(this.arrowTimer) + 1.0F) / 8.0F;
        this.arrowScale2 = 0.8F + (MathUtils.cos(this.arrowTimer - 0.8F) + 1.0F) / 8.0F;
        this.arrowScale3 = 0.8F + (MathUtils.cos(this.arrowTimer - 1.6F) + 1.0F) / 8.0F;
    }
}