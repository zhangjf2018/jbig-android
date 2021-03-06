package io.github.suzp1984.jbigandroid.controller;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.internal.Preconditions;
import io.github.suzp1984.jbigandroid.display.IDisplay;
import io.github.suzp1984.jbigandroid.states.ApplicationState;
import io.github.suzp1984.jbigandroid.states.JbigDbState;


/**
 * Created by moses on 9/2/15.
 */

@Singleton
public class MainController extends BaseUiController<MainController.MainControllerUi,
        MainController.MainControllerUiCallback> {

    public enum TabItem {
        PAINT_TAB, DECODER_TAB;
    }

    private final ApplicationState mApplicationState;
    private final JbigController mJbigController;

    @Inject
    public MainController(ApplicationState state, JbigController controller) {
        super();

        mApplicationState = Preconditions.checkNotNull(state, "Applicatin cannot be null.");
        mJbigController = Preconditions.checkNotNull(controller, "JbigController cannot be null.");
    }

    @Override
    MainControllerUiCallback createUiCallbacks(MainControllerUi ui) {
        return new MainControllerUiCallback() {
            @Override
            public void onTabItemSelected(TabItem item) {
                IDisplay display = getDisplay();

                if (display != null) {
                    showUiItem(display, item);
                    display.closeDrawerLayout();
                }
            }
        };
    }

    @Override
    void onUiAttached(MainControllerUi ui) {

    }

    @Override
    void onUiDetached(MainControllerUi ui) {

    }

    @Override
    void populateUi(MainControllerUi ui) {
        // do nothing?
    }

    @Override
    void onInited() {
        mApplicationState.registerForEvents(this);

        mJbigController.init();
    }

    @Override
    void onSuspended() {
        mJbigController.suspend();

        mApplicationState.unregisterForEvents(this);
    }

    @Override
    protected void setDisplay(IDisplay display) {
        super.setDisplay(display);

        mJbigController.setDisplay(display);
    }

    @Subscribe
    public void onJbigDataAdd(JbigDbState.JbigDbAddEvent event) {
        // populate Decoder Tab
        populateUis();
    }

    public void attachDisplay(IDisplay display) {
        Preconditions.checkNotNull(display, "display is null");

        setDisplay(display);
    }

    public void detachDisplay(IDisplay display) {
        Preconditions.checkNotNull(display, "display is null");
        if (getDisplay() != display) {
            throw new RuntimeException("display is not attached");
        }

        setDisplay(null);
    }

    public JbigController getJbigController() {
        return mJbigController;
    }

    private void showUiItem(IDisplay display, TabItem item) {
        Preconditions.checkNotNull(display, "IDisplay cannot be null");
        Preconditions.checkNotNull(item, "TabItem cannot be null.");

        switch (item) {
            case PAINT_TAB:
                display.showPaintUi();
                break;
            case DECODER_TAB:
                display.showDecoderUi();
                break;
            default:
                break;
        }

        // set selected TabItem
    }

    public interface MainControllerUi
            extends BaseUiController.Ui<MainControllerUiCallback> {

    }

    public interface MainControllerUiCallback {
        void onTabItemSelected(TabItem item);
    }
}
