package org.eclipse.tracecompass.analysis.os.linux.qemu.ui;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.analysis.os.linux.qemu.ui.QemuStateEntry.Type;
import org.eclipse.tracecompass.internal.lttng2.ust.qemu.core.analyse.QemuAnalysisModule;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystem;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.statesystem.core.interval.ITmfStateInterval;
import org.eclipse.tracecompass.tmf.core.statesystem.TmfStateSystemAnalysisModule;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.ui.views.timegraph.AbstractStateSystemTimeGraphView;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ITimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ITimeGraphEntry;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.NullTimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.TimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.TimeGraphEntry;
/**
 * @since 2.0
 */
@SuppressWarnings("javadoc")
public class QemuStateView extends AbstractStateSystemTimeGraphView {
    private static final long BUILD_UPDATE_TIMEOUT = 500;
    public static final String ID = "org.eclipse.tracecompass.analysis.os.linux.qemu.ui.view1"; //$NON-NLS-1$
    private static final String[] COLUMN_NAMES = new String[] {
            "Qemu"
    };
    private static final String[] FILTER_COLUMN_NAMES = new String[] {
            "Qemu"
    };
    static QemuStatePresentationProvider sp=new QemuStatePresentationProvider();
    public QemuStateView() {
        super(ID,sp);
        setTreeColumns(COLUMN_NAMES);
        setFilterColumns(FILTER_COLUMN_NAMES);
        setFilterLabelProvider(new QemuFilterLabelProvider());
        setTreeLabelProvider(new QemuTreeLabelProvider());
    }
    private static class QemuFilterLabelProvider extends TreeLabelProvider {
        @Override
        public String getColumnText(Object element, int columnIndex) {
            QemuStateEntry entry = (QemuStateEntry) element;
            if (columnIndex == 0) {
                return entry.getName();
            }
            return "test";
        }
    }
    /**
     * @author yves
     *
     */
    protected static class QemuTreeLabelProvider extends TreeLabelProvider {

        @Override
        public String getColumnText(Object element, int columnIndex) {
            QemuStateEntry entry = (QemuStateEntry) element;
            if (columnIndex==0) {
                return entry.getName();
            }
            return "yy";
        }
    }
    @Override
    protected @Nullable List<ITimeEvent> getEventList(@NonNull TimeGraphEntry entry, ITmfStateSystem ssq, @NonNull List<List<ITmfStateInterval>> fullStates, @Nullable List<ITmfStateInterval> prevFullState, @NonNull IProgressMonitor monitor) {
        List<ITimeEvent> eventList = null;
        QemuStateEntry resourcesEntry = (QemuStateEntry) entry;
        try {
            if (resourcesEntry.getType().equals(Type.QEMU)) {
                int quark=resourcesEntry.getQuark();
                int statusQuark = ssq.getQuarkRelative(quark,"MIG_STATUS");
                //System.out.println(ssq.getAttributeName(statusQuark));

                eventList = new ArrayList<>(fullStates.size());
                ITmfStateInterval lastInterval = prevFullState == null || statusQuark >= prevFullState.size() ? null : prevFullState.get(statusQuark);
                long lastStartTime = lastInterval == null ? -1 : lastInterval.getStartTime();
                long lastEndTime = lastInterval == null ? -1 : lastInterval.getEndTime() + 1;
                for (List<ITmfStateInterval> fullState : fullStates) {

                    if (monitor.isCanceled()) {
                        return null;
                    }
                    if (statusQuark >= fullState.size()) {
                        // No information on this CPU (yet?), skip it for now
                        continue;
                    }

                    ITmfStateInterval statusInterval = fullState.get(statusQuark);
                    int status = statusInterval.getStateValue().unboxInt();
                    long time = statusInterval.getStartTime();
                    long duration = statusInterval.getEndTime() - time + 1;
                    if (time == lastStartTime) {
                        continue;
                    }
                    if (!statusInterval.getStateValue().isNull()) {
                        if (lastEndTime != time && lastEndTime != -1) {
                            eventList.add(new TimeEvent(entry, lastEndTime, time - lastEndTime));
                        }
                        eventList.add(new TimeEvent(entry, time, duration, status));
                    } else {
                        eventList.add(new NullTimeEvent(entry, time, duration));
                    }
                    lastStartTime = time;
                    lastEndTime = time + duration;
                }
            }else if(resourcesEntry.getType().equals(Type.QEMU_COPY)){
                int quark=resourcesEntry.getQuark();
                int statusQuark = ssq.getQuarkRelative(quark,"MIG_COPY");
                //System.out.println(ssq.getAttributeName(statusQuark));

                eventList = new ArrayList<>(fullStates.size());
                ITmfStateInterval lastInterval = prevFullState == null || statusQuark >= prevFullState.size() ? null : prevFullState.get(statusQuark);
                long lastStartTime = lastInterval == null ? -1 : lastInterval.getStartTime();
                long lastEndTime = lastInterval == null ? -1 : lastInterval.getEndTime() + 1;
                for (List<ITmfStateInterval> fullState : fullStates) {

                    if (monitor.isCanceled()) {
                        return null;
                    }
                    if (statusQuark >= fullState.size()) {
                        // No information on this CPU (yet?), skip it for now
                        continue;
                    }

                    ITmfStateInterval statusInterval = fullState.get(statusQuark);
                    int status = statusInterval.getStateValue().unboxInt();
                    long time = statusInterval.getStartTime();
                    long duration = statusInterval.getEndTime() - time + 1;
                    if (time == lastStartTime) {
                        continue;
                    }
                    if (!statusInterval.getStateValue().isNull()) {
                        if (lastEndTime != time && lastEndTime != -1) {
                            eventList.add(new TimeEvent(entry, lastEndTime, time - lastEndTime));
                        }
                        eventList.add(new TimeEvent(entry, time, duration, status));
                    } else {
                        eventList.add(new NullTimeEvent(entry, time, duration));
                    }
                    lastStartTime = time;
                    lastEndTime = time + duration;
                }
            }
        } catch (AttributeNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return eventList;
    }

    @Override
    protected void buildEventList(@NonNull ITmfTrace trace, @NonNull ITmfTrace parentTrace, @NonNull IProgressMonitor monitor) {
        final ITmfStateSystem ssq = TmfStateSystemAnalysisModule.getStateSystem(trace, QemuAnalysisModule.ID);
        if (ssq == null) {
            return;
        }
        Comparator<ITimeGraphEntry> comparator = new Comparator<ITimeGraphEntry>() {
            @Override
            public int compare(ITimeGraphEntry o1, ITimeGraphEntry o2) {
                return ((QemuStateEntry) o1).compareTo(o2);
            }
        };
        Map<Integer, QemuStateEntry> entryMap = new HashMap<>();
        TimeGraphEntry traceEntry = null;
        long startTime = ssq.getStartTime();
        long start = startTime;
        setStartTime(Math.min(getStartTime(), startTime));
        boolean complete = false;
        while (!complete) {
            if (monitor.isCanceled()) {
                return;
            }
            complete = ssq.waitUntilBuilt(BUILD_UPDATE_TIMEOUT);
            if (ssq.isCancelled()) {
                return;
            }
            long end = ssq.getCurrentEndTime();
            if (start == end && !complete) { // when complete execute one last time regardless of end time
                continue;
            }
            long endTime = end + 1;
            setEndTime(Math.max(getEndTime(), endTime));
            if (traceEntry == null) {
                traceEntry = new QemuStateEntry(trace, trace.getName(), startTime, endTime, "QEMUs");
                traceEntry.sortChildren(comparator);
                List<TimeGraphEntry> entryList = Collections.singletonList(traceEntry);
                addToEntryList(parentTrace, ssq, entryList);
            } else {
                traceEntry.updateEndTime(endTime);
            }//---------
            //----------
            List<Integer> sQuarks = ssq.getQuarks("QEMU_COPY", "*"); //$NON-NLS-1$
            for (Integer serviceQuark : sQuarks) {
                String serv = ssq.getAttributeName(serviceQuark).toString();
                @Nullable QemuStateEntry entry = entryMap.get(serviceQuark);
                if (entry == null) {
                    entry = new QemuStateEntry(serviceQuark, trace, startTime, endTime, Type.QEMU_COPY, serv);
                    entryMap.put(serviceQuark, entry);
                    traceEntry.addChild(entry);
                } else {
                    entry.updateEndTime(endTime);
                }
            }
            //----------
            List<Integer> servicesQuarks = ssq.getQuarks("QEMUs", "*"); //$NON-NLS-1$
            for (Integer serviceQuark : servicesQuarks) {
                String serv = ssq.getAttributeName(serviceQuark).toString();
                @Nullable QemuStateEntry entry = entryMap.get(serviceQuark);
                if (entry == null) {
                    entry = new QemuStateEntry(serviceQuark, trace, startTime, endTime, Type.QEMU, serv);
                    entryMap.put(serviceQuark, entry);
                    traceEntry.addChild(entry);
                } else {
                    entry.updateEndTime(endTime);
                }
            }



            if (parentTrace.equals(getTrace())) {
                refresh();
            }
            final List<? extends ITimeGraphEntry> traceEntryChildren = traceEntry.getChildren();
            final long resolution = Math.max(1, (endTime - ssq.getStartTime()) / getDisplayWidth());
            final long qStart = start;
            final long qEnd = end;
            queryFullStates(ssq, qStart, qEnd, resolution, monitor, new IQueryHandler() {
                @Override
                public void handle(List<List<ITmfStateInterval>> fullStates, List<ITmfStateInterval> prevFullState) {
                    for (ITimeGraphEntry child : traceEntryChildren) {
                        if (monitor.isCanceled()) {
                            return;
                        }
                        if (child instanceof TimeGraphEntry) {
                            TimeGraphEntry entry = (TimeGraphEntry) child;
                            List<ITimeEvent> eventList = getEventList(entry, ssq, fullStates, prevFullState, monitor);
                            if (eventList != null) {
                                for (ITimeEvent event : eventList) {
                                    entry.addEvent(event);
                                }
                            }
                        }
                    }
                }
            });

            start = end;
        }
    }

}
