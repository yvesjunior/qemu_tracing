package org.eclipse.tracecompass.analysis.os.linux.qemu.ui;

import static org.eclipse.tracecompass.common.core.NonNullUtils.checkNotNull;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;
import org.eclipse.tracecompass.tmf.core.event.TmfEvent;
import org.eclipse.tracecompass.tmf.core.request.ITmfEventRequest;
import org.eclipse.tracecompass.tmf.core.request.TmfEventRequest;
import org.eclipse.tracecompass.tmf.core.signal.TmfSignalHandler;
import org.eclipse.tracecompass.tmf.core.signal.TmfTimestampFormatUpdateSignal;
import org.eclipse.tracecompass.tmf.core.signal.TmfTraceSelectedSignal;
import org.eclipse.tracecompass.tmf.core.timestamp.TmfTimeRange;
import org.eclipse.tracecompass.tmf.core.timestamp.TmfTimestampFormat;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.ui.views.TmfView;
import org.swtchart.Chart;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.Range;

/**
 * @since 2.0
 */
@SuppressWarnings("javadoc")
public class QemuDirtyPageView extends TmfView {
    //private static final String SERIES_NAME = "TimeStamp";
    ArrayList<String> VMs_list=new ArrayList<>();
    Map<String, ArrayList<Double>>VM_MAP_X= new HashMap<>();
    Map<String, ArrayList<Double>>VM_MAP_Y= new HashMap<>();
    private static final String Y_AXIS_TITLE = "Dirty Pages";
    private static final String X_AXIS_TITLE = "Time";
    private static final String FIELD = "dirty_pages"; // The name of the field that we want to display on the Y axis
    private static final String VIEW_ID = "org.eclipse.tracecompass.analysis.os.linux.qemu.ui.dirty";
    private Chart chart;
    private ITmfTrace currentTrace;
    public QemuDirtyPageView() {
        super(VIEW_ID);
        // TODO Auto-generated constructor stub
    }
    @Override
    public void createPartControl(Composite parent) {
        chart = new Chart(parent, SWT.BORDER);
        chart.getTitle().setVisible(false);
        chart.getAxisSet().getXAxis(0).getTitle().setText(X_AXIS_TITLE);
        chart.getAxisSet().getYAxis(0).getTitle().setText(Y_AXIS_TITLE);

        //chart.getSeriesSet().createSeries(SeriesType.LINE, "serie2");
        //chart.getSeriesSet().

        chart.getLegend().setVisible(true);
        chart.getAxisSet().getXAxis(0).getTick().setFormat(new TmfChartTimeStampFormat());

    }
    public class TmfChartTimeStampFormat extends SimpleDateFormat {
        private static final long serialVersionUID = 1L;
        @Override
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            long time = date.getTime();
            toAppendTo.append(TmfTimestampFormat.getDefaulTimeFormat().format(time));
            return toAppendTo;
        }
    }

    /**
     * @param signal
     */
    @TmfSignalHandler
    public void timestampFormatUpdated(TmfTimestampFormatUpdateSignal signal) {
        // Called when the time stamp preference is changed
        chart.getAxisSet().getXAxis(0).getTick().setFormat(new TmfChartTimeStampFormat());
        chart.redraw();
    }
    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
        chart.setFocus();
    }
    @TmfSignalHandler
    public void traceSelected(final TmfTraceSelectedSignal signal) {
        // Don't populate the view again if we're already showing this trace
        if (currentTrace == signal.getTrace()) {
            return;
        }
        currentTrace = signal.getTrace();

        // Create the request to get data from the trace

        TmfEventRequest req = new TmfEventRequest(TmfEvent.class,
                TmfTimeRange.ETERNITY, 0, ITmfEventRequest.ALL_DATA,
                ITmfEventRequest.ExecutionType.BACKGROUND) {
            ArrayList<Double> xValues = new ArrayList<>();
            ArrayList<Double> yValues = new ArrayList<>();
            private double maxY = -Double.MAX_VALUE;
            private double minY = Double.MAX_VALUE;
            private double maxX = -Double.MAX_VALUE;
            private double minX = Double.MAX_VALUE;
            @Override
            public void handleData(ITmfEvent data) {
                // Called for each event

                super.handleData(data);
                @NonNull String type = data.getType().getName().toString();
                if(type.equals("qemu:migration_bitmap_sync_end")) {
                    String procname= data.getContent().getField("context._procname").getValue().toString();
                    if(!VMs_list.contains(procname)){
                        VMs_list.add(procname);
                        System.out.println(procname);
                    }
                    ITmfEventField field = data.getContent().getField(FIELD);
                    if (field != null) {
                        Double yValue=Double.parseDouble(field.getValue().toString());
                        //yValues.add(yValue);
                        minY = Math.min(minY, yValue);
                        maxY = Math.max(maxY, yValue);
                        yValues.add(yValue);
                        double xValue = data.getTimestamp().getValue();
                        xValues.add(xValue);
                        minX = Math.min(minX, xValue);
                        maxX = Math.max(maxX, xValue);

                        if(VM_MAP_Y.get(procname)==null || VM_MAP_X.get(procname)==null){
                            VM_MAP_Y.put(procname, new ArrayList<>());
                            VM_MAP_X.put(procname, new ArrayList<>());
                            checkNotNull(VM_MAP_Y.get(procname)).add(yValue);
                            checkNotNull(VM_MAP_X.get(procname)).add(xValue);
                        }else{
                            checkNotNull(VM_MAP_Y.get(procname)).add(yValue);
                            checkNotNull(VM_MAP_X.get(procname)).add(xValue);
                        }
                    }
                }

            }
            @Override
            public void handleFailure() {
                // Request failed, not more data available
                super.handleFailure();
            }

            @Override
            public void handleSuccess() {
                // Request successful, not more data available
                super.handleSuccess();



                // This part needs to run on the UI thread since it updates the chart SWT control
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        for(int i=0;i<VMs_list.size();i++){
                            chart.getSeriesSet().createSeries(SeriesType.LINE,VMs_list.get(i));
                            double x[] = toArray(VM_MAP_X.get(VMs_list.get(i)));
                            double y[] = toArray(VM_MAP_Y.get(VMs_list.get(i)));
                            chart.getSeriesSet().getSeries()[i].setXSeries(x);
                            chart.getSeriesSet().getSeries()[i].setYSeries(y);
                            // Color color = new Color(Display.getDefault(), 255, 0, 0);

                            // Set the new range
                            if (!xValues.isEmpty() && !yValues.isEmpty()) {
                                chart.getAxisSet().getXAxis(0).setRange(new Range(0, x[x.length - 1]));
                                chart.getAxisSet().getYAxis(0).setRange(new Range(minY, maxY));
                            } else {
                                chart.getAxisSet().getXAxis(0).setRange(new Range(0, 1));
                                chart.getAxisSet().getYAxis(0).setRange(new Range(0, 1));
                            }
                        }
                        chart.getAxisSet().adjustRange();
                        chart.redraw();
                    }

                });

            }

            /**
             * Convert List<Double> to double[]
             */
            private double[] toArray(List<Double> list) {
                double[] d = new double[list.size()];
                for (int i = 0; i < list.size(); ++i) {
                    d[i] = list.get(i);
                }

                return d;
            }
        };
        ITmfTrace trace = signal.getTrace();
        trace.sendRequest(req);
    }


}
