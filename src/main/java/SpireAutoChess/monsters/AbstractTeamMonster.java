package SpireAutoChess.monsters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;
import com.megacrit.cardcrawl.vfx.combat.HbBlockBrokenEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;

import SpireAutoChess.actions.DamageFrontAction;
import SpireAutoChess.character.TeamMonsterGroup;
import SpireAutoChess.helper.GenericHelper;
import basemod.ReflectionHacks;

public class AbstractTeamMonster extends AbstractMonster {
    protected Supplier<Boolean> NextTurnAction;

    public final String[] moves;
    public final String[] dialog;

    public final ArrayList<MoveInfo> moveInfos = new ArrayList<>();
    public final ArrayList<String> rawDescriptions = new ArrayList<>();
    public String description;
    public boolean isDirty = false;

    public MonsterRarity rarity = MonsterRarity.COMMON;
    public int upgradedTimes = 0;
    public int maxUpgradeTimes = 1;

    private static final int COMMON_PRICE = 50;
    private static final int UNCOMMON_PRICE = 125;
    private static final int RARE_PRICE = 250;

    public AbstractTeamMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h,
            String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
        this.isPlayer = true;
        MonsterStrings strings = CardCrawlGame.languagePack.getMonsterStrings(id);
        moves = strings.MOVES;
        dialog = strings.DIALOG;
    }

    protected void DamageFront(DamageInfo info, DamageType type, AttackEffect effect) {
        info.type = type;
        addToBot(new AnimateFastAttackAction(this));
        addToBot(new DamageFrontAction(info, effect));
    }

    protected void DamageFront(DamageInfo info, DamageType type) {
        DamageFront(info, type, AttackEffect.SLASH_DIAGONAL);
    }

    protected void DamageFront(DamageInfo info) {
        DamageFront(info, DamageType.NORMAL, AttackEffect.SLASH_DIAGONAL);
    }

    protected void ApplyPowerToSelf(AbstractPower power) {
        addToBot(new ApplyPowerAction(this, this, power));
    }

    protected void ApplyPowerToOther(Function<AbstractMonster, AbstractPower> fac, int... indices) {
        for (int index : indices) {
            AbstractMonster m = TeamMonsterGroup.Inst().GetMonsterByIndex(index);
            addToBot(new ApplyPowerAction(m, this, fac.apply(m)));
        }
    }

    public int getPurchasePrice() {
        return this.rarity.price;
    }

    public int getUpgradePrice(int level) {
        return level * 50;
    }

    public int getSellPrice() {
        return (getPurchasePrice() + getUpgradePrice(this.upgradedTimes)) / 2;
    }

    public boolean canUpgrade() {
        return this.upgradedTimes < this.maxUpgradeTimes;
    }

    public void upgrade(int level) {
        this.upgradedTimes++;
    }

    public void setDescriptionRange(int min, int max) {
        this.rawDescriptions.clear();
        for (int i = min; i <= max; i++) {
            this.rawDescriptions.add(this.moves[i]);
        }
        this.isDirty = true;
    }

    public void setDescriptionRange(int max) {
        setDescriptionRange(0, max);
    }

    public void setDescriptionRange() {
        setDescriptionRange(0, this.moves.length - 1);
    }

    public void addDescription(int index, int position) {
        this.rawDescriptions.add(position, this.moves[index]);
        this.isDirty = true;
    }

    public void addNextDescription(int position) {
        if (position < 0)
            position = this.rawDescriptions.size() + position;
        addDescription(this.rawDescriptions.size(), position);
    }

    public void addNextDescription() {
        addNextDescription(0);
    }

    public String getDescription() {
        if (isDirty) {
            StringBuilder builder = new StringBuilder();
            int index = 0;
            for (String str : this.rawDescriptions) {
                str = str.replace("D", String.valueOf(this.moveInfos.get(index).info.base))
                        .replace("B", String.valueOf(getBlock(index)))
                        .replace("M", String.valueOf(getMagicNumber(index)));
                index++;
                builder.append(str + " NL ");
            }
            this.description = builder.toString();
            this.isDirty = false;
        }
        return this.description;
    }

    @Override
    protected void getMove(int rnd) {

    }

    public void addMoveInfo(DamageInfo info, int block, int magic) {
        // this.damage.add(info);
        this.moveInfos.add(new MoveInfo(info, block, magic));
    }

    public void addMoveInfo(DamageInfo info) {
        addMoveInfo(info, 0, 0);
    }

    public void addMoveInfo(int block, int magic) {
        addMoveInfo(new DamageInfo(this, 0), block, magic);
        this.isDirty = true;
    }

    protected int getDamage(int index) {
        return this.moveInfos.get(index).info.base;
    }

    protected void changeDamage(int index, int delta) {
        this.moveInfos.get(index).info.base += delta;
        this.isDirty = true;
    }

    protected int getBlock(int index) {
        return this.moveInfos.get(index).block;
    }

    protected void changeBlock(int index, int delta) {
        this.moveInfos.get(index).block += delta;
        this.isDirty = true;
    }

    protected int getMagicNumber(int index) {
        return this.moveInfos.get(index).magic;
    }

    protected void changeMagicNumber(int index, int delta) {
        this.moveInfos.get(index).magic += delta;
        this.isDirty = true;
    }

    protected DamageInfo getDamageInfo(int index) {
        return this.moveInfos.get(index).info;
    }

    protected void setNextMove(byte move, Intent intent, int amount, Supplier<Boolean> func) {
        this.setMove(move, intent, amount);
        this.NextTurnAction = func;
    }

    public boolean takeTurnSimple(int index) {
        return this.NextTurnAction.get();
    }

    @Override
    public void takeTurn() {
        if (!takeTurnSimple(this.nextMove)) {
            addToBot(new RollMoveAction(this));
        }
    }

    @Override
    public void die() {
        this.die(false);
    }

    @Override
    public void die(boolean arg0) {
        if (!this.isDying) {
            this.isDying = true;
            if (this.currentHealth < 0) {
                this.currentHealth = 0;
            }

            if (!Settings.FAST_MODE) {
                ++this.deathTimer;
            } else {
                ++this.deathTimer;
            }
        }
    }

    @SpireOverride
    protected void calculateDamage(int dmg) {
        AbstractCreature target = GenericHelper.getFrontMonster();
        float tmp = (float) dmg;
        GenericHelper.info("raw damage:" + tmp);
        AbstractPower p;
        Iterator<AbstractPower> var6;
        for (var6 = this.powers.iterator(); var6.hasNext(); tmp = p.atDamageGive(tmp, DamageType.NORMAL)) {
            p = var6.next();
        }

        for (var6 = target.powers.iterator(); var6.hasNext(); tmp = p.atDamageReceive(tmp, DamageType.NORMAL)) {
            p = var6.next();
        }

        for (var6 = this.powers.iterator(); var6.hasNext(); tmp = p.atDamageFinalGive(tmp, DamageType.NORMAL)) {
            p = var6.next();
        }

        for (var6 = target.powers.iterator(); var6.hasNext(); tmp = p.atDamageFinalReceive(tmp, DamageType.NORMAL)) {
            p = var6.next();
        }

        dmg = MathUtils.floor(tmp);
        if (dmg < 0) {
            dmg = 0;
        }
        GenericHelper.info("final damage:" + dmg);

        ReflectionHacks.setPrivateInherited(this, AbstractTeamMonster.class, "intentDmg", dmg);
    }

    @Override
    public void damage(DamageInfo info) {
        float damageAmount = info.output;
        if (!this.isDying) {
            if (damageAmount < 0) {
                damageAmount = 0;
            }

            boolean hadBlock = true;
            if (this.currentBlock == 0) {
                hadBlock = false;
            }

            boolean weakenedToZero = damageAmount == 0;
            damageAmount = this.decrementBlock(info, (int) damageAmount);

            AbstractPower p;
            Iterator<AbstractPower> var6;

            for (var6 = this.powers.iterator(); var6
                    .hasNext(); damageAmount = p.atDamageFinalReceive(damageAmount, info.type)) {
                p = var6.next();
            }

            for (var6 = info.owner.powers.iterator(); var6
                    .hasNext(); damageAmount = p.atDamageFinalGive(damageAmount, info.type)) {
                p = var6.next();
            }

            Iterator<AbstractPower> var5;

            if (info.owner != null) {
                for (var5 = info.owner.powers.iterator(); var5
                        .hasNext(); damageAmount = p.onAttackToChangeDamage(info, (int) damageAmount)) {
                    p = var5.next();
                }
            }

            GenericHelper.info(name + "受到的伤害：" + damageAmount);
            for (var5 = this.powers.iterator(); var5
                    .hasNext(); damageAmount = p.onAttackedToChangeDamage(info, (int) damageAmount)) {
                p = (AbstractPower) var5.next();
            }

            var5 = this.powers.iterator();

            while (var5.hasNext()) {
                p = (AbstractPower) var5.next();
                p.wasHPLost(info, (int) damageAmount);
            }

            if (info.owner != null) {
                var5 = info.owner.powers.iterator();

                while (var5.hasNext()) {
                    p = (AbstractPower) var5.next();
                    p.onAttack(info, (int) damageAmount, this);
                }
            }

            for (var5 = this.powers.iterator(); var5.hasNext(); damageAmount = p.onAttacked(info, (int) damageAmount)) {
                p = (AbstractPower) var5.next();
            }

            this.lastDamageTaken = Math.min((int) damageAmount, this.currentHealth);
            boolean probablyInstantKill = this.currentHealth == 0;
            if (damageAmount > 0) {
                if (info.owner != this) {
                    this.useStaggerAnimation();
                }

                this.currentHealth -= damageAmount;
                if (!probablyInstantKill) {
                    AbstractDungeon.effectList.add(new StrikeEffect(this, this.hb.cX, this.hb.cY, (int) damageAmount));
                }

                if (this.currentHealth < 0) {
                    this.currentHealth = 0;
                }

                this.healthBarUpdatedEvent();
            } else if (!probablyInstantKill) {
                if (weakenedToZero && this.currentBlock == 0) {
                    if (hadBlock) {
                        AbstractDungeon.effectList.add(new BlockedWordEffect(this, this.hb.cX, this.hb.cY, TEXT[1]));
                    } else {
                        AbstractDungeon.effectList.add(new StrikeEffect(this, this.hb.cX, this.hb.cY, 0));
                    }
                } else if (Settings.SHOW_DMG_BLOCK) {
                    AbstractDungeon.effectList.add(new BlockedWordEffect(this, this.hb.cX, this.hb.cY, TEXT[1]));
                }
            }

            if (this.currentHealth <= 0) {
                this.die();

                if (this.currentBlock > 0) {
                    this.loseBlock();
                    AbstractDungeon.effectList
                            .add(new HbBlockBrokenEffect(this.hb.cX - this.hb.width / 2.0F + BLOCK_ICON_X,
                                    this.hb.cY - this.hb.height / 2.0F + BLOCK_ICON_Y));
                }
            }

        }
    }

    @Override
    public void applyPowers() {
        // for (DamageInfo info : this.damage) {
        // GenericHelper.info("before calculate:" + info.base + ",output:" +
        // info.output);
        // info.applyPowers(this, GenericHelper.getFrontMonster());
        // GenericHelper.info("after calculate:" + info.base + ",output:" +
        // info.output);
        // }
        for (MoveInfo move : this.moveInfos) {
            move.info.applyPowers(this, GenericHelper.getFrontMonster());
        }
        EnemyMoveInfo move = ReflectionHacks.getPrivateInherited(this, AbstractTeamMonster.class, "move");
        if (move.baseDamage > -1) {
            this.calculateDamage(move.baseDamage);
        }

        ReflectionHacks.setPrivateInherited(this, AbstractTeamMonster.class, "intentImg", this.getIntentImg());
        updateIntentTip();
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!this.isDead && !this.escaped) {
            if (this.atlas == null) {
                sb.setColor(this.tint.color);
                if (this.img != null) {
                    sb.draw(this.img, this.drawX - (float) this.img.getWidth() * Settings.scale / 2.0F + this.animX,
                            this.drawY + this.animY, (float) this.img.getWidth() * Settings.scale,
                            (float) this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(),
                            this.img.getHeight(), this.flipHorizontal, this.flipVertical);
                }
            } else {
                this.state.update(Gdx.graphics.getDeltaTime());
                this.state.apply(this.skeleton);
                this.skeleton.updateWorldTransform();
                this.skeleton.setPosition(this.drawX + this.animX, this.drawY + this.animY);
                this.skeleton.setColor(this.tint.color);
                this.skeleton.setFlip(!this.flipHorizontal, this.flipVertical);
                sb.end();
                CardCrawlGame.psb.begin();
                sr.draw(CardCrawlGame.psb, this.skeleton);
                CardCrawlGame.psb.end();
                sb.begin();
                sb.setBlendFunction(770, 771);
            }

            if (this.atlas == null) {
                sb.setBlendFunction(770, 1);
                sb.setColor(new Color(1.0F, 1.0F, 1.0F, 0.1F));
                if (this.img != null) {
                    sb.draw(this.img, this.drawX - (float) this.img.getWidth() * Settings.scale / 2.0F + this.animX,
                            this.drawY + this.animY, (float) this.img.getWidth() * Settings.scale,
                            (float) this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(),
                            this.img.getHeight(), this.flipHorizontal, this.flipVertical);
                    sb.setBlendFunction(770, 771);
                }
            }

            if (!this.isDying && !this.isEscaping && AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT
                    && !AbstractDungeon.player.isDead && !AbstractDungeon.player.hasRelic("Runic Dome")
                    && this.intent != Intent.NONE && !Settings.hideCombatElements) {
                this.renderIntentVfxBehind(sb);
                this.renderIntent(sb);
                this.renderIntentVfxAfter(sb);
                this.renderDamageRange(sb);
            }

            this.hb.render(sb);
            this.intentHb.render(sb);
            this.healthHb.render(sb);
        }

        if (!AbstractDungeon.player.isDead) {
            this.renderHealth(sb);
            this.renderName(sb);
        }
    }

    @SpireOverride
    protected void renderDamageRange(SpriteBatch sb) {
        SpireSuper.call(new Object[] { sb });
    }

    @SpireOverride
    protected void renderIntentVfxBehind(SpriteBatch sb) {
        SpireSuper.call(new Object[] { sb });
    }

    @SpireOverride
    protected void renderIntent(SpriteBatch sb) {
        SpireSuper.call(new Object[] { sb });
    }

    @SpireOverride
    protected void renderIntentVfxAfter(SpriteBatch sb) {
        SpireSuper.call(new Object[] { sb });
    }

    @SpireOverride
    protected void renderName(SpriteBatch sb) {
        SpireSuper.call(new Object[] { sb });
    }

    @SpireOverride
    protected void updateIntentTip() {
        SpireSuper.call(new Object[] {});
    }

    @SpireOverride
    protected Texture getIntentImg() {
        return SpireSuper.call(new Object[] {});
    }

    public enum MonsterRarity {
        COMMON(COMMON_PRICE), UNCOMMON(UNCOMMON_PRICE), RARE(RARE_PRICE);

        public int price;

        MonsterRarity(int price) {
            this.price = price;
        }
    }

    public class MoveInfo {
        private DamageInfo info;
        private int block;
        private int magic;

        MoveInfo(DamageInfo info, int block, int magic) {
            this.info = info;
            this.block = block;
            this.magic = magic;
        }
    }
}