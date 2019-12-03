package org.eclipse.tracecompass.analysis.os.linux.core.qemu;

import org.eclipse.tracecompass.statesystem.core.statevalue.ITmfStateValue;
import org.eclipse.tracecompass.statesystem.core.statevalue.TmfStateValue;

/**
 * @author yves
 * @since 2.0
 *
 */
public interface  StateValues {
    int int_MIGRATION_BITMAP_SYNC_START=1;
    int int_MIGRATION_BITMAP_SYNC_END=11;
    int int_SAVE_VMSTATE_SECTION_START=2;
    int int_SAVE_VMSTATE_SECTION_END=21;
    int int_MIGRATION_PENDING=3;
    int int_SYSTEM_WAKEUP_REQUEST=31;
    int int_MIGRATION_TRANFERRED=41; //start with section_end, stop by mig_transferred
    int int_MIG_PRECOPY=50;
    int int_STOP_COPY=60;
    //public static ITmfStateValue =TmfStateValue.newValueInt(0);
    public static ITmfStateValue MIGRATION_BITMAP_SYNC_START=TmfStateValue.newValueInt(int_MIGRATION_BITMAP_SYNC_START);
    public static ITmfStateValue MIGRATION_BITMAP_SYNC_END=TmfStateValue.newValueInt(int_MIGRATION_BITMAP_SYNC_END);
    public static ITmfStateValue SAVE_VMSTATE_SECTION_START=TmfStateValue.newValueInt(int_SAVE_VMSTATE_SECTION_START);
    public static ITmfStateValue SAVE_VMSTATE_SECTION_END=TmfStateValue.newValueInt(int_SAVE_VMSTATE_SECTION_END);
    public static ITmfStateValue MIGRATION_PENDING=TmfStateValue.newValueInt(int_MIGRATION_PENDING);
    public static ITmfStateValue SYSTEM_WAKEUP_REQUEST=TmfStateValue.newValueInt(int_SYSTEM_WAKEUP_REQUEST);
    public static ITmfStateValue MIGRATION_TRANFERRED=TmfStateValue.newValueInt(int_MIGRATION_TRANFERRED);
    public static ITmfStateValue MIG_PRECOPY=TmfStateValue.newValueInt(int_MIG_PRECOPY);
    public static ITmfStateValue MIG_STOP_COPY=TmfStateValue.newValueInt(int_STOP_COPY);

}