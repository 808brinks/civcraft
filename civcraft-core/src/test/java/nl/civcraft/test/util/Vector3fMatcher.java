package nl.civcraft.test.util;


import org.joml.Vector3f;
import org.mockito.ArgumentMatcher;

import static org.mockito.ArgumentMatchers.argThat;

/**
 * Created by Bob on 17-2-2017.
 * <p>
 * This is probably not worth documenting
 */
public class Vector3fMatcher implements ArgumentMatcher<Vector3f> {

    public static float DEFAULT_RANGE = 0.0001f;

    private final Vector3f value;
    private final float range;

    public Vector3fMatcher(Vector3f value,
                           float range) {
        this.value = value;
        this.range = range;
    }

    public static Vector3f isInRange(Vector3f value) {
        return isInRange(value, DEFAULT_RANGE);
    }

    public static Vector3f isInRange(Vector3f value,
                                     float range) {
        return argThat(new Vector3fMatcher(value, range));
    }

    @Override
    public boolean matches(Vector3f argument) {
        return argument instanceof Vector3f && value.distance(argument) < range;
    }

    @Override
    public Class<?> type() {
        return Vector3f.class;
    }
}
