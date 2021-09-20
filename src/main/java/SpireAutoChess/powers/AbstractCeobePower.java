package SpireAutoChess.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

import static com.megacrit.cardcrawl.powers.AbstractPower.PowerType.BUFF;

public abstract class AbstractCeobePower extends AbstractPower {
    public ArrayList<Integer> counters = new ArrayList<>();

    public AbstractCeobePower(AbstractCreature owner, int amount, String id, String name) {
        this.ID = id;
        this.name = name;
        this.owner = owner;
        this.amount = amount;
        this.type = BUFF;
        String sid = id.replace("Ceobe_", "");
        Texture img84 = ImageMaster.loadImage(String.format("CeobeResources/img/powers/%s84.png", sid));
        Texture img32 = ImageMaster.loadImage(String.format("CeobeResources/img/powers/%s32.png", sid));
        if (img84 != null || img32 != null) {
            this.region128 = new AtlasRegion(img84, 0, 0, 84, 84);
            this.region48 = new AtlasRegion(img32, 0, 0, 32, 32);
        }
        this.updateDescription();
    }

    public abstract String getDescription();

    public void updateDescription() {
        super.updateDescription();
        String des = getDescription();
        for (int i = 0; i < counters.size(); i++) {
            des = des.replace("M" + i, String.valueOf(counters.get(i)));
        }
        des = des.replace("M", String.valueOf(this.amount));
        this.description = des;
    }

    public AbstractCeobePower makeCopy() {
        try {
            return this.getClass().newInstance();
        } catch (IllegalAccessException | InstantiationException var2) {
            throw new RuntimeException("cannot create instance of: " + this.ID);
        }
    }
}
