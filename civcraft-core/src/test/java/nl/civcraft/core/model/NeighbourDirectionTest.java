package nl.civcraft.core.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by Bob on 17-2-2017.
 * <p>
 * This is probably not worth documenting
 */
@ExtendWith(MockitoExtension.class)
public class NeighbourDirectionTest {

    @Test
    public void fromFace_top() throws Exception {
        NeighbourDirection neighbourDirection = NeighbourDirection.fromFace(Face.TOP);
        assertThat(neighbourDirection, is(NeighbourDirection.TOP));
    }
}