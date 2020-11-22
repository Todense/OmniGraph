package com.todense.view;

import com.todense.viewmodel.BackgroundPopOverViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;

public class BackgroundPopOverView implements FxmlView<BackgroundPopOverViewModel> {

    @InjectViewModel
    BackgroundPopOverViewModel viewModel;

    public void initialize(){

    }

    public void pasteAction() {
        viewModel.pasteSubgraph();
    }

}
