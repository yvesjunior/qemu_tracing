package org.eclipse.tracecompass.analysis.os.linux.core.qemu;

import static org.eclipse.tracecompass.common.core.NonNullUtils.checkNotNull;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.tmf.core.statesystem.ITmfStateProvider;
import org.eclipse.tracecompass.tmf.core.statesystem.TmfStateSystemAnalysisModule;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;

/**
 * @author yves
 * @since 2.0
 *
 */
public class QemuAnalysisModule extends TmfStateSystemAnalysisModule {
    public static final @NonNull String ID = "org.eclipse.tracecompass.analysis.os.linux.core.qemu.module"; //$NON-NLS-1$
    @Override
    protected @NonNull ITmfStateProvider createStateProvider() {
        // TODO Auto-generated method stub
        return new QemuStateProvider(checkNotNull(getTrace()));
    }
    @Override
    protected StateSystemBackendType getBackendType() {
        return StateSystemBackendType.FULL;
    }
    @Override
    protected @Nullable ITmfTrace getTrace() {
        return super.getTrace();
    }
}