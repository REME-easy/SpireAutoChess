package SpireAutoChess.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.CardTrailEffect;

import java.util.ArrayList;

public class PackEffect extends AbstractGameEffect {
    private AbstractCard card;
    private AbstractCard target;
    private CatmullRomSpline<Vector2> crs = new CatmullRomSpline<>();
    private ArrayList<Vector2> controlPoints = new ArrayList<>();
    private Vector2[] points = new Vector2[20];
    private Vector2 pos;
    private float backUpTimer;
    private float vfxTimer = 0.015F;
    private float spawnStutterTimer = 0.0F;
    private static final Pool<CardTrailEffect> trailEffectPool = new Pool<CardTrailEffect>() {
        protected CardTrailEffect newObject() {
            return new CardTrailEffect();
        }
    };
    private float currentSpeed;
    private static final float START_VELOCITY;
    private static final float MAX_VELOCITY;
    private static final float VELOCITY_RAMP_RATE;
    private static final float DST_THRESHOLD;
    private float rotation;
    private boolean rotateClockwise;
    private boolean stopRotating;
    private float rotationRate;
    private static final float ROTATION_RATE;
    private Vector2 tmp = new Vector2();

    public PackEffect(AbstractCard card, AbstractCard target) {
        this.card = card;
        this.card.targetDrawScale = 0.12F;
        this.pos = new Vector2(card.current_x, card.current_y);
        this.target = target;
        this.crs.controlPoints = new Vector2[1];
        this.rotationRate = ROTATION_RATE * MathUtils.random(4.0F, 6.0F);
        this.currentSpeed = START_VELOCITY * MathUtils.random(1.0F, 1.5F);
        this.backUpTimer = 0.5F;
        this.stopRotating = false;
        this.rotateClockwise = MathUtils.randomBoolean();
        this.rotation = (float) MathUtils.random(0, 359);
        this.duration = 0.5F;
    }

    @Override
    public void update() {
        this.card.update();
        this.card.targetAngle = this.rotation + 90.0F;
        this.card.current_x = this.pos.x;
        this.card.current_y = this.pos.y;
        this.card.target_x = this.card.current_x;
        this.card.target_y = this.card.current_y;
        if (this.spawnStutterTimer > 0.0F) {
            this.spawnStutterTimer -= Gdx.graphics.getDeltaTime();
            return;
        }
        this.updateMovement();
        this.updateBackUpTimer();
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration <= 0.0F) {
            this.isDone = true;
        }
    }

    private void updateMovement() {
        Vector2 t = new Vector2(target.current_x, target.current_y);
        this.tmp.x = this.pos.x - t.x;
        this.tmp.y = this.pos.y - t.y;
        this.tmp.nor();
        float targetAngle = this.tmp.angle();
        this.rotationRate += Gdx.graphics.getDeltaTime() * 800.0F;
        if (!this.stopRotating) {
            if (this.rotateClockwise) {
                this.rotation += Gdx.graphics.getDeltaTime() * this.rotationRate;
            } else {
                this.rotation -= Gdx.graphics.getDeltaTime() * this.rotationRate;
                if (this.rotation < 0.0F) {
                    this.rotation += 360.0F;
                }
            }

            this.rotation %= 360.0F;
            if (!this.stopRotating) {
                if (Math.abs(this.rotation - targetAngle) < Gdx.graphics.getDeltaTime() * this.rotationRate) {
                    this.rotation = targetAngle;
                    this.stopRotating = true;
                }
            }
        }

        this.tmp.setAngle(this.rotation);
        Vector2 var10000 = this.tmp;
        var10000.x *= Gdx.graphics.getDeltaTime() * this.currentSpeed;
        var10000 = this.tmp;
        var10000.y *= Gdx.graphics.getDeltaTime() * this.currentSpeed;
        this.pos.sub(this.tmp);
        if (this.stopRotating && this.backUpTimer < 1.3499999F) {
            this.currentSpeed += Gdx.graphics.getDeltaTime() * VELOCITY_RAMP_RATE * 3.0F;
        } else {
            this.currentSpeed += Gdx.graphics.getDeltaTime() * VELOCITY_RAMP_RATE * 1.5F;
        }

        if (this.currentSpeed > MAX_VELOCITY) {
            this.currentSpeed = MAX_VELOCITY;
        }

        if (t.x < (float) Settings.WIDTH / 2.0F && this.pos.x < 0.0F) {
            this.isDone = true;
        } else if (t.x > (float) Settings.WIDTH / 2.0F && this.pos.x > (float) Settings.WIDTH) {
            this.isDone = true;
        }

        if (stopRotating && t.dst(this.pos) < DST_THRESHOLD) {
            this.isDone = true;
        }

        this.vfxTimer -= Gdx.graphics.getDeltaTime();
        if (!this.isDone && this.vfxTimer < 0.0F) {
            this.vfxTimer = 0.015F;
            if (!this.controlPoints.isEmpty()) {
                if (!(this.controlPoints.get(0)).equals(this.pos)) {
                    this.controlPoints.add(this.pos.cpy());
                }
            } else {
                this.controlPoints.add(this.pos.cpy());
            }

            if (this.controlPoints.size() > 10) {
                this.controlPoints.remove(0);
            }

            if (this.controlPoints.size() > 3) {
                Vector2[] vec2Array = new Vector2[0];
                this.crs.set(this.controlPoints.toArray(vec2Array), false);

                for (int i = 0; i < 20; ++i) {
                    if (this.points[i] == null) {
                        this.points[i] = new Vector2();
                    }

                    Vector2 derp = this.crs.valueAt(this.points[i], (float) i / 19.0F);
                    CardTrailEffect effect = trailEffectPool.obtain();
                    effect.init(derp.x, derp.y);
                    AbstractDungeon.topLevelEffects.add(effect);
                }
            }
        }

    }

    private void updateBackUpTimer() {
        this.backUpTimer -= Gdx.graphics.getDeltaTime();
        if (this.backUpTimer < 0.0F) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        this.card.renderOuterGlow(sb);
        this.card.render(sb);
    }

    @Override
    public void dispose() {

    }

    static {
        START_VELOCITY = 500.0F * Settings.scale;
        MAX_VELOCITY = 8000.0F * Settings.scale;
        VELOCITY_RAMP_RATE = 5000.0F * Settings.scale;
        DST_THRESHOLD = 4.0F * Settings.scale;
        ROTATION_RATE = 200.0F * Settings.scale;
    }
}
