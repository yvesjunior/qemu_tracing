package org.eclipse.tracecompass.analysis.os.linux.core.qemu;
import static org.eclipse.tracecompass.common.core.NonNullUtils.checkNotNull;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.statesystem.core.exceptions.StateValueTypeException;
import org.eclipse.tracecompass.statesystem.core.statevalue.ITmfStateValue;
import org.eclipse.tracecompass.statesystem.core.statevalue.TmfStateValue;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.statesystem.AbstractTmfStateProvider;
import org.eclipse.tracecompass.tmf.core.statesystem.ITmfStateProvider;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
/**
 * @author yves
 * @since 2.0
 *
 */
/*
 * Host
 *     |--Qemu_instance
 *                      |--MIG_COPY
 *                      |--MIG_MIG_STATUS
 *
 */
public class QemuStateProvider extends AbstractTmfStateProvider{

    /* Version of this state provider */
    private static final int VERSION = 1;
    public static String ID="org.tracecompass.analyse.os.linux.core.qemu"; //$NON-NLS-1$


    int count_round=0;
    boolean STOP_COPY=false;
    long SECTION_END_time=0;
    public QemuStateProvider(ITmfTrace trace) {
        super(trace,ID);
    }
    @Override
    public int getVersion() {
        // TODO Auto-generated method stub
        return VERSION;
    }
    @Override
    public @NonNull ITmfStateProvider getNewInstance() {
        // TODO Auto-generated method stub
        return new QemuStateProvider( getTrace());
    }
    @Override
    protected void eventHandle(@NonNull ITmfEvent ev) {
        // TODO Auto-generated method stub
        ITmfStateSystemBuilder ss = checkNotNull(getStateSystemBuilder());
        ITmfEvent event=ev;
        final long ts = event.getTimestamp().getValue();
        String event_type= event.getType().getName();
        //int Qemuquark=ss.getQuarkAbsoluteAndAdd("QEMUs","qemu-instance","MIG_STATUS");

        try {

            ITmfStateValue value = null;
            if(event_type.equals(EventString.eventMigration_bitmap_sync_start)) {
                value=StateValues.MIGRATION_BITMAP_SYNC_START;
                String procname= event.getContent().getField("context._procname").getValue().toString();
                int Qemuquark=ss.getQuarkAbsoluteAndAdd("QEMUs",procname,"MIG_STATUS");
                ss.modifyAttribute(ts, value, Qemuquark);
            }
            else if(event_type.equals(EventString.eventMigration_bitmap_sync_end)) {
                value=TmfStateValue.nullValue();
                String procname= event.getContent().getField("context._procname").getValue().toString();
                int Qemuquark=ss.getQuarkAbsoluteAndAdd("QEMUs",procname,"MIG_STATUS");
                ss.modifyAttribute(ts, value, Qemuquark);
                //
                int copyquark=ss.getQuarkAbsoluteAndAdd("QEMU_COPY",procname,"MIG_COPY");
                ITmfStateValue state=findMigrationRound();
                ss.modifyAttribute(ts, state, copyquark);

            }
            else if(event_type.equals(EventString.eventSaveVM_section_start)){
                value=StateValues.SAVE_VMSTATE_SECTION_START;
                String procname= event.getContent().getField("context._procname").getValue().toString();
                int Qemuquark=ss.getQuarkAbsoluteAndAdd("QEMUs",procname,"MIG_STATUS");
                ss.modifyAttribute(ts, value, Qemuquark);
            }
            else if(event_type.equals(EventString.eventSaveVM_section_end)){
                value=TmfStateValue.nullValue();
                String procname= event.getContent().getField("context._procname").getValue().toString();
                int Qemuquark=ss.getQuarkAbsoluteAndAdd("QEMUs",procname,"MIG_STATUS");
                ss.modifyAttribute(ts, value, Qemuquark);
                //save time for, to modify attribute in transferred state
                SECTION_END_time=ts;
            }
            else if(event_type.equals(EventString.eventMigration_transferred)){
                value=StateValues.MIGRATION_TRANFERRED;
                String procname= event.getContent().getField("context._procname").getValue().toString();
                int Qemuquark=ss.getQuarkAbsoluteAndAdd("QEMUs",procname,"MIG_STATUS");
                ss.modifyAttribute(SECTION_END_time,value,Qemuquark);//modify section end state to transfered
                value=TmfStateValue.nullValue();
                ss.modifyAttribute(ts,value,Qemuquark);//stop transferred state b null value
            }
            else if(event_type.equals(EventString.eventMigrate_pending)){
                value=StateValues.MIGRATION_PENDING;
                String procname= event.getContent().getField("context._procname").getValue().toString();
                int Qemuquark=ss.getQuarkAbsoluteAndAdd("QEMUs",procname,"MIG_STATUS");
                ss.modifyAttribute(ts, value, Qemuquark);
            }else if(event_type.equals(EventString.eventSytem_wakeup_request)){
                value=TmfStateValue.nullValue();
                String procname= event.getContent().getField("context._procname").getValue().toString();
                int Qemuquark=ss.getQuarkAbsoluteAndAdd("QEMUs",procname,"MIG_STATUS");
                ss.modifyAttribute(ts, value, Qemuquark);
            }else if(event_type.equals(EventString.eventMigration_precopy)){
                value=StateValues.MIG_STOP_COPY;STOP_COPY=true;
                String procname= event.getContent().getField("context._procname").getValue().toString();
                int copyquark=ss.getQuarkAbsoluteAndAdd("QEMU_COPY",procname,"MIG_COPY");
                ss.modifyAttribute(ts, value, copyquark);
            }
        } catch (StateValueTypeException | AttributeNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    ITmfStateValue findMigrationRound(){
        ITmfStateValue state;
        if (count_round==0){
            //state =TmfStateValue.nullValue();
            state=StateValues.MIG_PRECOPY;
        }else if(count_round==1){
            state =TmfStateValue.newValueInt(100+count_round);
        }else{
            if(STOP_COPY) {
                state=StateValues.MIG_STOP_COPY;
                STOP_COPY=true;
            } else {
                state =TmfStateValue.newValueInt(100+count_round);
            }
        }
        count_round++;
        return state;
    }

}

