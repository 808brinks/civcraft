package nl.civcraft.core.input;

import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class GlobalInput implements ActionListener, AssetEventListener {
    public static final String WIREFRAME = "WIREFRAME";
    private static final String EXIT = "EXIT";
    private final Set<Material> materials;
    private boolean wireframe;

    @Autowired
    public GlobalInput(AssetManager assetManager, InputManager inputManager, ApplicationEventPublisher publisher) {
        assetManager.addAssetEventListener(this);
        materials = new HashSet<>();
        registerInput(inputManager);
    }

    private void registerInput(InputManager inputManager) {
        inputManager.addMapping(WIREFRAME, new KeyTrigger(KeyInput.KEY_F11));
        inputManager.addListener(this, WIREFRAME);
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed) {
            return;
        }
        if (name.equals(WIREFRAME)) {
            for (Material material : materials) {
                material.getAdditionalRenderState().setWireframe(!wireframe);
            }
            wireframe = !wireframe;
        }
    }

    @Override
    public void assetLoaded(AssetKey key) {
        String debug = "true";
    }

    @Override
    public void assetRequested(AssetKey key) {
        //No op
    }

    @Override
    public void assetDependencyNotFound(AssetKey parentKey, AssetKey dependentAssetKey) {
        //No op
    }
}
