package nl.civcraft.desktop;

import com.jme3.app.Application;
import com.jme3.app.FlyCamAppState;
import nl.civcraft.core.conf.InitialConfigruation;
import nl.civcraft.desktop.conf.DesktopConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Bob on 25-11-2015.
 * <p>
 * This is probably not worth documenting
 */
public class Civcraft   {

    public static void main(String[] args){
        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext(InitialConfigruation.class, DesktopConfiguration.class);
        Application app = appContext.getBean(Application.class);
        app.start();
        app.getStateManager().getState(FlyCamAppState.class).getCamera().setMoveSpeed(100f);
    }
}
