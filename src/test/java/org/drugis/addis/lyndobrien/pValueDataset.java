package org.drugis.addis.lyndobrien;

import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.ProgressEvent.EventType;
import org.jfree.data.xy.AbstractXYDataset;

@SuppressWarnings("serial")
public class pValueDataset extends AbstractXYDataset implements
		ProgressListener {

	private final LyndOBrienModel d_model;
	private int d_itemCount = 400;
	private int d_seriesCount = 1;
	private double[] d_data;
	
	public pValueDataset(LyndOBrienModel model) {
		d_model = model;
		d_data = new double[d_itemCount];
		model.addProgressListener(this);
		if(model.isReady()) {
			calcPvalues();
		}
	}

	private void calcPvalues() {
		int i = 0;
		for(double mu = 0.01; mu < 4; mu += 0.01){
			d_data[i++] = d_model.getPValue(mu);
		}
	}
	
	@Override
	public int getSeriesCount() {
		return d_seriesCount;
	}

	@Override
	public Comparable<?> getSeriesKey(int arg0) {
		return 1;
	}

	public void update(MCMCModel mtc, ProgressEvent event) {
		if(event.getType() == EventType.SIMULATION_PROGRESS) {
			calcPvalues();
			fireDatasetChanged();
		} else if (event.getType() == EventType.SIMULATION_FINISHED) {
			calcPvalues();
			fireDatasetChanged();
		}
	}


	public int getItemCount(int arg0) {
		return d_itemCount;
	}

	public Number getX(int series, int i) {
		return 0.01 * i + 0.01;
	}

	public Number getY(int series, int i) {
		return d_data[i];
	}

}