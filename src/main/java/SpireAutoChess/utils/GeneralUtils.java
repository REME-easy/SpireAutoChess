package SpireAutoChess.utils;

import java.lang.reflect.Field;

public interface GeneralUtils {
    default Field getField(Field target, boolean accessible) {
        target.setAccessible(accessible);
        return target;
    }
    
    default Field getField(Field target) {
        return getField(target, true);
    }
}