package org.eclipse.tracecompass.analysis.os.linux.qemu.ui;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ITimeGraphEntry;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.TimeGraphEntry;
/**
 * @author yves
 * @since 2.0
 *
 */
public class QemuStateEntry extends TimeGraphEntry implements Comparable<ITimeGraphEntry>{
    public static enum Type {
        /** Null resources (filler rows, etc.) */
        NULL,
        /** Entries for QEMUs */
        QEMU_COPY,
        QEMU

    }
    private final String fId;
    private final @NonNull ITmfTrace fTrace;
    private final Type fType;
    private final int fQuark;
    /**
     * @param name
     * @param startTime
     * @param endTime
     */
    public QemuStateEntry(int quark, @NonNull ITmfTrace trace, String name,
            long startTime, long endTime, Type type, String id) {
        super(name, startTime, endTime);
        fId = id;
        fTrace = trace;
        fType = type;
        fQuark = quark;
    }
    /**
     * @param trace
     * @param name
     * @param startTime
     * @param endTime
     * @param id
     * @param p
     */
    public QemuStateEntry(@NonNull ITmfTrace trace, String name,
            long startTime, long endTime, String id) {
        this(-1, trace, name, startTime, endTime, Type.NULL, id);
    }
    public QemuStateEntry(int quark, @NonNull ITmfTrace trace,
            long startTime, long endTime, Type type, String id) {
        this(quark, trace, computeEntryName(type, id), startTime, endTime, type, id);
    }
    /**
     * @param type
     */
    private static String computeEntryName(Type type, String id) {
        //return type.toString() + ' ' + id;
        return id;
    }
    @Override
    public int compareTo(ITimeGraphEntry other) {
        // TODO Auto-generated method stub
        if (!(other instanceof QemuStateEntry)) {
            return -1;
        }
        QemuStateEntry o = (QemuStateEntry) other;
        int ret = this.getType().compareTo(o.getType());
        if (ret != 0) {
            return ret;
        }
        return this.getId().compareTo(o.getId());
    }

    public String getId() {
        return fId;
    }

    public ITmfTrace getTrace() {
        return fTrace;
    }

    public Type getType() {
        return fType;
    }

    public int getQuark() {
        return fQuark;
    }


}
