package com.todense.view;

import com.todense.viewmodel.AnalysisViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class AnalysisView implements FxmlView<AnalysisViewModel> {

    @FXML private BarChart<String, Integer> barChart;
    @FXML private Label minDegreeLabel, maxDegreeLabel, avgDegreeLabel, radiusLabel, diameterLabel,
    densityLabel;
    @FXML private ProgressBar minDegreeProgressBar, maxDegreeProgressBar,
            avgDegreeProgressBar, radiusProgressBar, diameterProgressBar, densityProgressBar;

    @InjectViewModel
    AnalysisViewModel viewModel;

    public void initialize(){
        viewModel.calculateAll();
        var distribution = viewModel.getDegreeDistribution();
        XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
        for(int i : distribution.keySet()){
            series1.getData().add(new XYChart.Data<>(String.valueOf(i), distribution.get(i)));
        }
        barChart.getData().clear();
        barChart.getData().add(series1);


        minDegreeLabel.setText(String.valueOf(viewModel.getMinDegree()));
        maxDegreeLabel.setText(String.valueOf(viewModel.getMaxDegree()));
        avgDegreeLabel.setText(String.format("%.2f", viewModel.getAvgDegree()));
        radiusLabel.setText(String.valueOf(viewModel.getRadius()));
        diameterLabel.setText(String.valueOf(viewModel.getDiameter()));
        densityLabel.setText(String.format("%.3f", viewModel.getDensity()));
        minDegreeProgressBar.setProgress(viewModel.getMinDegreePerc());
        maxDegreeProgressBar.setProgress(viewModel.getMaxDegreePerc());
        avgDegreeProgressBar.setProgress(viewModel.getAvgDegreePerc());
        radiusProgressBar.setProgress(viewModel.getRadiusPerc());
        diameterProgressBar.setProgress(viewModel.getDiameterPerc());
        densityProgressBar.setProgress(viewModel.getDensity());
    }

}
