package mygame;

import com.jme3.app.Application;
import com.jme3.app.LegacyApplication;

public interface CustomCanvas extends Application {
    void selectView();

    void deselectView();
}
