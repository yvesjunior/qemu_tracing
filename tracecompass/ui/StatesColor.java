package org.eclipse.tracecompass.analysis.os.linux.qemu.ui;
import org.eclipse.swt.graphics.RGB;
/**
 * @author yves
 * @since 2.0
 *
 */
public interface StatesColor {

    enum State {
        MIGRATION_BITMAP_SYNC_START (new RGB(100, 10, 200)),
       // MIGRATION_BITMAP_SYNC_END (new RGB(100, 10, 200)),
        SAVE_VMSTATE_SECTION (new RGB(0, 150, 100)),
        MIG_TRANSFERRED (new RGB(153,153,0)),
        MIGRATION_PENDING (new RGB(255, 51, 51)),
        END (new RGB(255,255, 255)),
        PRECOPY (new RGB(51,255, 51)),
        ITERATION (new RGB(0,0, 0)),
        STOP_COPY(new RGB(255,102, 102)),
        DEFAULT (new RGB(10, 10, 10));
        public RGB rgb;
        private State(RGB rgb) {
            this.rgb = rgb;
        }
    }

}
