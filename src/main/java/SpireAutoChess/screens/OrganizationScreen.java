package SpireAutoChess.screens;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.overlayMenu;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
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
import SpireAutoChess.monsters.AbstractTeamMonster;
import SpireAutoChess.patches.CustomScreenQueuePatch.ICustomScreen;

public class OrganizationScreen implements ICustomScreen {
    private static OrganizationScreen _inst;

    public static OrganizationScreen Inst() {
        if (_inst == null)
            _inst = new OrganizationScreen();
        return _inst;
    }

    public ArrayList<OrganizationNodeScreen> nodeScreens;
    public AbstractTeamMonster selectedMonster;
    public int selectedIndex;
    public int selectedScreenIndex;
    public int hoveredScreenIndex;

    public boolean isOpen = false;
    public ConfirmButton confirmButton;
    public MonsterUpgradeScreen upgradeScreen;

    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString("ChessPlayer_OrganizationScreen").TEXT;

    public OrganizationScreen() {
        this.confirmButton = new ConfirmButton();
        this.upgradeScreen = new MonsterUpgradeScreen();
        this.nodeScreens = new ArrayList<>();

    }

    public void open() {
        this.isOpen = true;
        this.nodeScreens.clear();
        this.nodeScreens.add(new OrganizationNodeScreen(this, 0));
        this.nodeScreens.add(new OrganizationNodeScreen(this, 1));
        this.nodeScreens.get(0).addMonsters(TeamMonsterGroup.GetWaitingMonsters());
        this.nodeScreens.get(1).addMonsters(TeamMonsterGroup.GetBattleMonsters());
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
        confirmButton.hide();
        AbstractDungeon.isScreenUp = false;
        AbstractDungeon.dynamicBanner.hide();
        overlayMenu.showCombatPanels();
    }

    public void update() {
        if (!this.upgradeScreen.isOpen) {
            this.nodeScreens.forEach((sc) -> {
                sc.update();
            });

            if (selectedMonster != null && InputHelper.justReleasedClickLeft) {
                if (this.hoveredScreenIndex != this.selectedScreenIndex) {
                    nodeScreens.get(this.hoveredScreenIndex).addMonsters(this.selectedMonster);
                    nodeScreens.get(this.selectedScreenIndex).removeMonsters(this.selectedMonster);
                    if (selectedScreenIndex == 0) {
                        TeamMonsterGroup.GetBattleMonsters().add(this.selectedMonster);
                        TeamMonsterGroup.GetWaitingMonsters().remove(this.selectedMonster);
                    } else {
                        TeamMonsterGroup.GetWaitingMonsters().add(this.selectedMonster);
                        TeamMonsterGroup.GetBattleMonsters().remove(this.selectedMonster);
                    }
                }
                this.selectedMonster = null;
                this.selectedIndex = -1;
                this.selectedScreenIndex = -1;
            }

            this.confirmButton.update();
            if (this.confirmButton.hb.clicked) {
                this.confirmButton.hb.clicked = false;
                this.confirmButton.hb.clickStarted = false;
                this.confirmButton.isDisabled = true;
                this.confirmButton.hide();
                this.close();
            }
        } else {
            this.upgradeScreen.update();
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.95F));
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT - 64.0F * Settings.scale);
        sb.setColor(Color.WHITE);
        this.nodeScreens.forEach((sc) -> {
            sc.render(sb);
        });
        CustomTipRenderer.renderViewTip(sb, TEXT[7], 150.0F * Settings.scale,
                Settings.HEIGHT * 0.75F + 50.0F * Settings.scale, Color.WHITE);
        CustomTipRenderer.renderViewTip(sb, TEXT[8], 150.0F * Settings.scale,
                Settings.HEIGHT * 0.25F + 50.0F * Settings.scale, Color.WHITE);
        this.confirmButton.render(sb);
        if (this.upgradeScreen.isOpen) {
            sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.9F));
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float) Settings.HEIGHT);
            this.upgradeScreen.render(sb);
            sb.setColor(Color.WHITE);
        }
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    public class OrganizationNodeScreen implements ScrollBarListener, ICustomScreen {
        public ArrayList<AbstractTeamMonster> teamMonsters;
        public ArrayList<UpgradeButton> upgradeButtons;
        public ArrayList<SellButton> sellButtons;
        public boolean isOpen = false;
        private boolean grabbedScreen = false;
        private float grabStartX = 0.0F;
        private float scrollX;
        private float screenY;
        private float targetX;
        private float currentWidth;
        private float scrollLowerBound;
        private float scrollUpperBound;
        private boolean shouldShowScrollBar;
        private HorizontalScrollBar scrollBar;
        private Hitbox hb;
        private OrganizationScreen ParentScreen;
        private int index;

        public OrganizationNodeScreen(OrganizationScreen parent, int index) {
            this.teamMonsters = new ArrayList<>();
            this.upgradeButtons = new ArrayList<>();
            this.sellButtons = new ArrayList<>();
            this.ParentScreen = parent;
            this.index = index;
            this.screenY = AbstractDungeon.floorY / 2.5F + index * Settings.HEIGHT / 2.0F;
            this.scrollBar = new HorizontalScrollBar(this, (float) Settings.WIDTH / 2.0F,
                    HorizontalScrollBar.TRACK_H / 2.0F + index * Settings.HEIGHT / 2.0F,
                    (float) Settings.WIDTH - 256.0F * Settings.scale);
            this.hb = new Hitbox(0.0F, index * Settings.HEIGHT / 2.0F, Settings.WIDTH, Settings.HEIGHT / 2.0F);
        }

        public void addMonsters(AbstractTeamMonster... monsters) {
            for (AbstractTeamMonster m : monsters) {
                this.teamMonsters.add(m);
                this.upgradeButtons.add(new UpgradeButton(this, m));
                this.sellButtons.add(new SellButton(this, m));
                currentWidth += m.hb_w * 2.0F;
            }
            this.checkIfShowScrollBar();
        }

        public void addMonsters(ArrayList<AbstractTeamMonster> monsters) {
            for (AbstractTeamMonster m : monsters) {
                this.teamMonsters.add(m);
                this.upgradeButtons.add(new UpgradeButton(this, m));
                this.sellButtons.add(new SellButton(this, m));
                currentWidth += m.hb_w * 2.0F;
            }
            this.checkIfShowScrollBar();
        }

        public void removeMonsters(AbstractTeamMonster... monsters) {
            for (AbstractTeamMonster m : monsters) {
                if (this.teamMonsters.contains(m)) {
                    int ind = this.teamMonsters.indexOf(m);
                    this.teamMonsters.remove(m);
                    this.upgradeButtons.remove(ind);
                    this.sellButtons.remove(ind);
                    currentWidth -= m.hb_w * 2.0F;
                }
            }
            this.checkIfShowScrollBar();
        }

        public void selectToUpgrade(AbstractTeamMonster monster) {
            upgradeScreen.open(monster);
        }

        public void selectToSell(AbstractTeamMonster monster) {
            AbstractDungeon.player.gainGold(monster.getSellPrice());
            int ind = this.teamMonsters.indexOf(monster);
            teamMonsters.remove(monster);
            sellButtons.remove(ind);
            upgradeButtons.remove(ind);
            EventHelper.TeamMonsterRemoveSubscribers.forEach((m) -> {
                m.OnRemoveMonster(index);
            });
        }

        public void update() {
            this.hb.update();
            if (this.hb.hovered) {
                ParentScreen.hoveredScreenIndex = this.index;
            }
            if (!ParentScreen.upgradeScreen.isOpen) {

                // this.updateControllerInput();
                if (this.shouldShowScrollBar && !this.scrollBar.update() && this.hb.hovered
                        && ParentScreen.selectedMonster == null) {
                    this.updateScrolling();
                }
                updateMonsters();
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
                    this.grabStartX = -x - this.targetX;
                }
            } else if (InputHelper.isMouseDown) {
                this.targetX = -x - this.grabStartX;
            } else {
                this.grabbedScreen = false;
            }

            this.scrollX = MathHelper.scrollSnapLerpSpeed(this.scrollX, this.targetX);
            this.resetScrolling();
            this.updateBarPosition();
        }

        public void updateMonsters() {
            float preWidth = 0;
            AbstractTeamMonster m;
            float targetX;
            float targetY;
            for (int i = 0; i < this.teamMonsters.size(); i++) {
                m = this.teamMonsters.get(i);
                targetX = this.shouldShowScrollBar ? preWidth - scrollX : preWidth - scrollX + 300.0F * Settings.scale;
                targetY = screenY;
                if (m != ParentScreen.selectedMonster) {
                    if (!m.isMovingToTarget)
                        GenericHelper.MoveMonster(m, targetX, targetY);
                } else {
                    GenericHelper.MoveMonster(m, InputHelper.mX - m.hb_w / 2.0F, InputHelper.mY - m.hb_h / 2.0F);
                }

                preWidth += m.hb_w * 2.0F;

                m.update();
                m.hb.update();
                if (m.hb.hovered && ParentScreen.selectedMonster == null) {
                    if (m.drawX < Settings.WIDTH * 0.75F) {
                        CustomTipRenderer.renderGenericTip(m.drawX + m.hb_w, m.drawY + m.hb_h / 2.0F, m.name,
                                m.getDescription(), m.keywords);
                    } else {
                        CustomTipRenderer.renderGenericTip(m.drawX - m.hb_w - CustomTipRenderer.BOX_W,
                                m.drawY + m.hb_h / 2.0F, m.name, m.getDescription(), m.keywords);
                    }
                    if (InputHelper.justClickedLeft) {
                        CardCrawlGame.sound.play("UI_CLICK_1");
                        ParentScreen.selectedMonster = m;
                        ParentScreen.selectedIndex = i;
                        ParentScreen.selectedScreenIndex = this.index;
                    }
                }

                UpgradeButton btn = this.upgradeButtons.get(i);
                btn.update();
                btn.current_x = targetX + m.hb_w / 2.0F + 64.0F * Settings.scale;
                btn.current_y = targetY + Settings.HEIGHT / 6.0F + 64.0F * Settings.scale;

                SellButton sell = this.sellButtons.get(i);
                sell.update();
                sell.current_x = targetX + m.hb_w / 2.0F + 64.0F * Settings.scale;
                sell.current_y = targetY + Settings.HEIGHT / 6.0F - 64.0F * Settings.scale;
            }

            // if (ParentScreen.selectedMonster != null &&
            // InputHelper.justReleasedClickLeft) {
            // preWidth = -scrollX;
            // float mX = InputHelper.mX;
            // for (int i = 0; i < this.teamMonsters.size(); i++) {
            // AbstractTeamMonster tmp = this.teamMonsters.get(i);
            // preWidth += tmp.hb_w * 2.0F;
            // if (mX < preWidth) {
            // Collections.swap(this.teamMonsters, i, ParentScreen.selectedIndex);
            // }
            // }
            // ParentScreen.selectedMonster = null;
            // }
        }

        private void resetScrolling() {
            if (this.targetX < this.scrollLowerBound) {
                this.targetX = MathHelper.scrollSnapLerpSpeed(this.targetX, this.scrollLowerBound);
            } else if (this.targetX > this.scrollUpperBound) {
                this.targetX = MathHelper.scrollSnapLerpSpeed(this.targetX, this.scrollUpperBound);
            }

        }

        public void render(SpriteBatch sb) {
            sb.setColor(Color.WHITE);
            this.renderMonsters(sb);
            if (this.shouldShowScrollBar) {
                this.scrollBar.render(sb);
            }
            this.hb.render(sb);
        }

        private void renderMonsters(SpriteBatch sb) {
            int teamSize = this.teamMonsters.size();
            for (int i = 0; i < teamSize; i++) {
                if (!ParentScreen.upgradeScreen.isOpen) {
                    this.sellButtons.get(i).render(sb);
                    this.upgradeButtons.get(i).render(sb);
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
            GenericHelper.info(String.format("%f,%d", this.currentWidth, Settings.WIDTH));
            if (this.shouldShowScrollBar = this.currentWidth + 500.0F * Settings.scale > Settings.WIDTH) {
                this.scrollLowerBound = -500.0F;
                this.scrollUpperBound = Math.abs(Settings.WIDTH - this.currentWidth) + 300.0F;
                // this.shouldShowScrollBar = true;
                this.resetScrolling();
            }
        }

        @Override
        public void scrolledUsingBar(float newPercent) {
            this.scrollX = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);
            this.targetX = this.scrollX;
            this.updateBarPosition();
        }

        private void updateBarPosition() {
            float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound,
                    this.scrollX);
            this.scrollBar.parentScrolledToPercent(percent);
        }

        @Override
        public boolean isOpen() {
            return isOpen;
        }
    }

}