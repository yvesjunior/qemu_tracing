package org.eclipse.tracecompass.analysis.os.linux.qemu.ui;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.tracecompass.analysis.os.linux.qemu.ui.QemuStateEntry.Type;
import org.eclipse.tracecompass.analysis.os.linux.qemu.ui.StatesColor.State;
import org.eclipse.tracecompass.internal.analysis.os.linux.ui.Activator;
import org.eclipse.tracecompass.internal.lttng2.ust.qemu.core.analyse.QemuAnalysisModule;
import org.eclipse.tracecompass.internal.lttng2.ust.qemu.core.analyse.StateValues;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystem;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.statesystem.core.exceptions.StateSystemDisposedException;
import org.eclipse.tracecompass.statesystem.core.exceptions.TimeRangeException;
import org.eclipse.tracecompass.tmf.core.statesystem.TmfStateSystemAnalysisModule;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.StateItem;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.TimeGraphPresentationProvider;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ITimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ITimeGraphEntry;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.NullTimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.TimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.widgets.ITmfTimeGraphDrawingHelper;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.widgets.Utils;
/**
 * @since 2.0
 */
@SuppressWarnings("javadoc")
public class QemuStatePresentationProvider extends TimeGraphPresentationProvider{
    private Color fColorWhite;
    private Color fColorGray;
    private Integer fAverageCharWidth;

    public QemuStatePresentationProvider() {
        super();
    }
    private static StatesColor.State[] getStateValues() {
        return StatesColor.State.values();
    }
    private static StatesColor.State getEventState(TimeEvent event) {
        if (event.hasValue()) {
            QemuStateEntry entry = (QemuStateEntry) event.getEntry();
            int value = event.getValue();

            if (entry.getType() == Type.QEMU) {

                if (value == StateValues.int_MIGRATION_BITMAP_SYNC_START) {
                    return StatesColor.State.MIGRATION_BITMAP_SYNC_START;
                }
                else if(value == StateValues.int_MIGRATION_BITMAP_SYNC_END){
                    return StatesColor.State.END;
                }
                else if(value == StateValues.int_SAVE_VMSTATE_SECTION_START){
                    return StatesColor.State.SAVE_VMSTATE_SECTION;
                }else if(value==StateValues.int_SAVE_VMSTATE_SECTION_END){
                    return StatesColor.State.END;
                }
                else if(value == StateValues.int_MIGRATION_PENDING){
                    return StatesColor.State.MIGRATION_PENDING;
                }else if(value==StateValues.int_SYSTEM_WAKEUP_REQUEST){
                    return StatesColor.State.END;
                }else if(value==StateValues.int_MIGRATION_TRANFERRED){
                    return StatesColor.State.MIG_TRANSFERRED;
                }

            } else if (entry.getType() == Type.QEMU_COPY) {
                if(value==StateValues.int_MIG_PRECOPY){
                    return StatesColor.State.PRECOPY;
                }else if(value==StateValues.int_STOP_COPY){
                    return StatesColor.State.STOP_COPY;
                }
                else if(value>100){
                    return findIterationColor();
                }
            }
        }
        return null;
    }
    public static State findIterationColor(){
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        StatesColor.State.ITERATION.rgb=new RGB(r, g, b);
        //System.out.println("color");
        return StatesColor.State.ITERATION;
    }
    @Override
    public int getStateTableIndex(ITimeEvent event) {
        StatesColor.State state = getEventState((TimeEvent) event);
        if (state != null) {
            return state.ordinal();
        }
        if (event instanceof NullTimeEvent) {
            return INVISIBLE;
        }
        return TRANSPARENT;
    }

    @Override
    public StateItem[] getStateTable() {
        StatesColor.State[] states = getStateValues();
        StateItem[] stateTable = new StateItem[states.length];
        for (int i = 0; i < stateTable.length; i++) {
            StatesColor.State state = states[i];
            stateTable[i] = new StateItem(state.rgb, state.toString());
        }
        return stateTable;
    }

    @Override
    public String getEventName(ITimeEvent event) {
        StatesColor.State state = getEventState((TimeEvent) event);
        if (state != null) {
            return state.toString();
        }
        if (event instanceof NullTimeEvent) {
            return null;
        }
        return "Messages.ResourcesView_multipleStates";
    }
    @Override
    public Map<String, String> getEventHoverToolTipInfo(ITimeEvent event, long hoverTime) {
        Map<String, String> retMap = new LinkedHashMap<>();
        if (event instanceof TimeEvent && ((TimeEvent) event).hasValue()) {
            TimeEvent tcEvent = (TimeEvent) event;
            QemuStateEntry entry = (QemuStateEntry) event.getEntry();
            if (tcEvent.hasValue()) {
                @SuppressWarnings("null")
                ITmfStateSystem ss = TmfStateSystemAnalysisModule.getStateSystem(entry.getTrace(), QemuAnalysisModule.ID);
                if (ss == null) {
                    return retMap;
                }
                // Check for type CPU
                if (entry.getType().equals(Type.QEMU)) {
                    // int status = tcEvent.getValue();
                }
            }
        }

        return retMap;
    }
    @Override
    public void postDrawEvent(ITimeEvent event, Rectangle bounds, GC gc) {
        if (fColorGray == null) {
            fColorGray = gc.getDevice().getSystemColor(SWT.COLOR_GRAY);
        }
        if (fColorWhite == null) {
            fColorWhite = gc.getDevice().getSystemColor(SWT.COLOR_WHITE);
        }
        if (fAverageCharWidth == null) {
            fAverageCharWidth = gc.getFontMetrics().getAverageCharWidth();
        }
        ITmfTimeGraphDrawingHelper drawingHelper = getDrawingHelper();
        drawingHelper.getClass();
        if (bounds.width <= fAverageCharWidth) {
            return;
        }
        if (!(event instanceof TimeEvent)) {
            return;
        }
        TimeEvent tcEvent = (TimeEvent) event;
        if (!tcEvent.hasValue()) {
            return;
        }
        QemuStateEntry entry = (QemuStateEntry) event.getEntry();

        ITmfStateSystem ss = TmfStateSystemAnalysisModule.getStateSystem(entry.getTrace(),QemuAnalysisModule.ID);
        if (ss == null) {
            return;
        }
        try {
            String str=ss.querySingleState(event.getTime(), entry.getQuark()).getStateValue().unboxStr();
            if(str.equals("nullValue")) {
                str="";
            }else{str=":"+str;}
            gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
            String state= getEventName(event);
            Utils.drawText(gc, state+str, bounds.x, bounds.y, bounds.width, bounds.height, true, true);

        } catch (TimeRangeException | AttributeNotFoundException | StateSystemDisposedException e) {
            Activator.getDefault().logError("Error in ControlFlowPresentationProvider", e); //$NON-NLS-1$
        }
    }

    @Override
    public void postDrawEntry(ITimeGraphEntry entry, Rectangle bounds, GC gc) {
        //fLastThreadId = -1;
    }

}
