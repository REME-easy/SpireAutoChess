package SpireAutoChess.screens;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.overlayMenu;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.HorizontalScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import com.megacrit.cardcrawl.ui.buttons.ConfirmButton;

import SpireAutoChess.character.TeamMonsterGroup;
import SpireAutoChess.helper.CustomTipRenderer;
import SpireAutoChess.helper.EventHelper;
import SpireAutoChess.helper.GenericHelper;
import SpireAutoChess.helper.MonsterManager;
import SpireAutoChess.monsters.AbstractTeamMonster;
import SpireAutoChess.patches.CustomScreenQueuePatch.ICustomScreen;

public class OrganizationScreen implements ScrollBarListener, ICustomScreen {
    private static OrganizationScreen _inst;

    public static OrganizationScreen Inst() {
        if (_inst == null)
            _inst = new OrganizationScreen();
        return _inst;
    }

    public ArrayList<AbstractTeamMonster> teamMonsters;
    public ArrayList<UpgradeButton> upgradeButtons;
    public ArrayList<SellButton> sellButtons;
    public AbstractTeamMonster selectedMonster;
    public int selectedIndex;

    public boolean isOpen = false;
    private boolean grabbedScreen = false;
    private float grabStartX = 0.0F;
    private float scrollX;
    private float targetX;
    private float currentWidth;
    private float scrollLowerBound;
    private float scrollUpperBound;
    private boolean shouldShowScrollBar;
    private HorizontalScrollBar scrollBar;
    public ConfirmButton confirmButton;
    public MonsterUpgradeScreen upgradeScreen;

    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString("ChessPlayer_OrganizationScreen").TEXT;

    public OrganizationScreen() {
        this.scrollX = Settings.WIDTH - 300.0F * Settings.xScale;
        this.targetX = this.scrollX;
        this.scrollLowerBound = 0.0F;
        this.scrollUpperBound = 2400.0F * Settings.scale;
        this.shouldShowScrollBar = false;
        this.confirmButton = new ConfirmButton();
        this.targetX = 0.0F;
        this.currentWidth = 0.0F;
        this.teamMonsters = new ArrayList<>();
        this.upgradeButtons = new ArrayList<>();
        this.sellButtons = new ArrayList<>();
        this.scrollBar = new HorizontalScrollBar(this, (float) Settings.WIDTH / 2.0F,
                50.0F * Settings.scale + HorizontalScrollBar.TRACK_H / 2.0F,
                (float) Settings.WIDTH - 256.0F * Settings.scale);
        this.upgradeScreen = new MonsterUpgradeScreen();
    }

    public void open() {
        for (int i = 0; i < TeamMonsterGroup.GetBattleMonsters().size(); i++) {
            AbstractTeamMonster m = MonsterManager.GetMonsterInstance(TeamMonsterGroup.GetBattleMonsters().get(i).id);
            m.showHealthBar();
            m.positionIndex = i;
            this.addMonsters(m);
        }
        for (int i = 0; i < TeamMonsterGroup.GetWaitingMonsters().size(); i++) {
            AbstractTeamMonster m = MonsterManager.GetMonsterInstance(TeamMonsterGroup.GetWaitingMonsters().get(i).id);
            m.showHealthBar();
            this.addMonsters(m);
        }
        currentWidth = 0.0F;
        this.isOpen = true;
        this.queueToFont();
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.isScreenUp = true;
        confirmButton.isDisabled = false;
        overlayMenu.proceedButton.hide();
        overlayMenu.cancelButton.hide();
        overlayMenu.hideCombatPanels();
        confirmButton.hideInstantly();
        confirmButton.show();
    }

    public void reopen() {
        this.isOpen = true;
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.overlayMenu.proceedButton.hide();
        AbstractDungeon.overlayMenu.endTurnButton.disable();
        GenericHelper.info("screen reopen");
    }

    public void close() {
        this.isOpen = false;
        this.teamMonsters.clear();
        this.upgradeButtons.clear();
        this.sellButtons.clear();
        confirmButton.hide();
        AbstractDungeon.isScreenUp = false;
        AbstractDungeon.dynamicBanner.hide();
        overlayMenu.showCombatPanels();
    }

    public void addMonsters(AbstractTeamMonster... m) {
        for (AbstractTeamMonster monster : m) {
            this.teamMonsters.add(monster);
            this.upgradeButtons.add(new UpgradeButton(this, this.teamMonsters.size() - 1));
            this.sellButtons.add(new SellButton(this, this.teamMonsters.size() - 1));
            currentWidth += monster.hb_w * 2.0F;
        }
        this.checkIfShowScrollBar();
    }

    public void selectToUpgrade(int index) {
        upgradeScreen.open(teamMonsters.get(index));
    }

    public void selectToSell(int index) {
        AbstractDungeon.player.gainGold(teamMonsters.get(index).getSellPrice());
        teamMonsters.remove(index);
        EventHelper.TeamMonsterRemoveSubscribers.forEach((m) -> {
            m.OnRemoveMonster(index);
        });
    }

    public void update() {
        if (!this.upgradeScreen.isOpen) {
            this.confirmButton.update();
            if (this.confirmButton.hb.clicked) {
                this.confirmButton.hb.clicked = false;
                this.confirmButton.hb.clickStarted = false;
                this.confirmButton.isDisabled = true;
                this.confirmButton.hide();
                this.close();
            }

            // this.updateControllerInput();
            if (!this.scrollBar.update()) {
                this.updateScrolling();
            }
            updateMonsters();
        } else {
            this.upgradeScreen.update();
        }
    }

    private void updateScrolling() {
        int x = InputHelper.mX;
        if (!this.grabbedScreen) {
            if (InputHelper.scrolledDown) {
                this.targetX += Settings.SCROLL_SPEED;
            } else if (InputHelper.scrolledUp) {
                this.targetX -= Settings.SCROLL_SPEED;
            }

            if (InputHelper.justClickedLeft) {
                this.grabbedScreen = true;
                this.grabStartX = (float) (-x) - this.targetX;
            }
        } else if (InputHelper.isMouseDown) {
            this.targetX = (float) (-x) - this.grabStartX;
        } else {
            this.grabbedScreen = false;
        }

        this.scrollX = MathHelper.scrollSnapLerpSpeed(this.scrollX, this.targetX);
        this.resetScrolling();
        this.updateBarPosition();
    }

    public void updateMonsters() {
        int teamSize = this.teamMonsters.size();
        float preWidth = 0;

        for (int i = 0; i < teamSize; i++) {
            AbstractTeamMonster m = this.teamMonsters.get(i);
            float targetX = preWidth - scrollX;
            float targetY = AbstractDungeon.floorY;
            if (m != selectedMonster) {
                if (!m.isMovingToTarget)
                    GenericHelper.MoveMonster(m, preWidth - scrollX, m.drawY);
            } else {
                GenericHelper.MoveMonster(m, InputHelper.mX - m.hb_w / 2.0F, InputHelper.mY - m.hb_h / 2.0F);
            }

            preWidth += m.hb_w * 2.0F;

            m.update();
            m.hb.update();
            if (m.hb.hovered) {
                if (selectedMonster == null) {
                    if (m.drawX < Settings.WIDTH * 0.75F) {
                        CustomTipRenderer.renderGenericTip(m.drawX + m.hb_w, m.drawY + m.hb_h / 2.0F, m.name,
                                m.getDescription(), m.keywords);
                    } else {
                        CustomTipRenderer.renderGenericTip(m.drawX - m.hb_w - CustomTipRenderer.BOX_W,
                                m.drawY + m.hb_h / 2.0F, m.name, m.getDescription(), m.keywords);
                    }
                    if (InputHelper.justClickedLeft) {
                        CardCrawlGame.sound.play("UI_CLICK_1");
                        selectedMonster = m;
                        selectedIndex = i;
                    }
                }
            }

            UpgradeButton btn = this.upgradeButtons.get(i);
            btn.update();
            btn.current_x = targetX - 107.0F * Settings.scale;
            btn.current_y = targetY - 64.0F * Settings.scale;

            SellButton sell = this.sellButtons.get(i);
            sell.update();
            sell.current_x = targetX + 43.0F * Settings.scale;
            sell.current_y = targetY - 64.0F * Settings.scale;
        }

        if (selectedMonster != null && InputHelper.justReleasedClickLeft) {
            preWidth = -scrollX;
            float mX = InputHelper.mX;
            for (int i = 0; i < this.teamMonsters.size(); i++) {
                AbstractTeamMonster m = this.teamMonsters.get(i);
                preWidth += m.hb_w * 2.0F;
                if (mX < preWidth) {
                    Collections.swap(this.teamMonsters, i, selectedIndex);
                }
            }
            selectedMonster = null;
        }
    }

    private void resetScrolling() {
        if (this.targetX < this.scrollLowerBound) {
            this.targetX = MathHelper.scrollSnapLerpSpeed(this.targetX, this.scrollLowerBound);
        } else if (this.targetX > this.scrollUpperBound) {
            this.targetX = MathHelper.scrollSnapLerpSpeed(this.targetX, this.scrollUpperBound);
        }

    }

    public void render(SpriteBatch sb) {
        sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.95F));
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT - 64.0F * Settings.scale);
        sb.setColor(Color.WHITE);
        this.confirmButton.render(sb);
        this.renderMonsters(sb);
        if (this.shouldShowScrollBar) {
            this.scrollBar.render(sb);
        }

        if (this.upgradeScreen.isOpen) {
            sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.9F));
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float) Settings.HEIGHT);
            this.upgradeScreen.render(sb);
            sb.setColor(Color.WHITE);
        }
    }

    private void renderMonsters(SpriteBatch sb) {
        int teamSize = this.teamMonsters.size();
        for (int i = 0; i < teamSize; i++) {
            if (!this.upgradeScreen.isOpen) {
                this.upgradeButtons.get(i).render(sb);
                this.sellButtons.get(i).render(sb);
            }
            AbstractTeamMonster m = this.teamMonsters.get(i);
            m.render(sb);
            m.renderHealth(sb);
            if (m.positionIndex >= 0) {
                m.renderReticle(sb);
            }
        }
    }

    private void checkIfShowScrollBar() {
        // if (!(this.shouldShowScrollBar = this.currentWidth > Settings.WIDTH * 1.5F))
        // {
        this.scrollLowerBound = -500.0F;
        this.scrollUpperBound = Math.abs(Settings.WIDTH - this.currentWidth) + 300.0F;
        this.shouldShowScrollBar = true;
        this.resetScrolling();
        // }
    }

    @Override
    public void scrolledUsingBar(float newPercent) {
        this.scrollX = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);
        this.targetX = this.scrollX;
        this.updateBarPosition();
    }

    private void updateBarPosition() {
        float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.scrollX);
        this.scrollBar.parentScrolledToPercent(percent);
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

}