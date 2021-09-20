package SpireAutoChess.actions;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import SpireAutoChess.helper.GenericHelper;

public class DamageFrontAction extends AbstractGameAction {

    private DamageInfo info;
    private AttackEffect effect;

    public DamageFrontAction(DamageInfo info, AttackEffect effect) {
        this.info = info;
        this.effect = effect;
    }

    @Override
    public void update() {
        ArrayList<AbstractMonster> list = (ArrayList<AbstractMonster>) GenericHelper.monsters().stream()
                .filter((m) -> GenericHelper.isAlive(m)).collect(Collectors.toList());
        GenericHelper.info(list.toString());
        if (list.size() > 0) {
            AbstractMonster target = list.get(0);
            for (AbstractMonster m : list) {
                if (m.hb.cX < target.hb.cX) {
                    target = m;
                }
            }
            addToTop(new DamageAction(target, info, effect));
        }
        this.isDone = true;
    }

}