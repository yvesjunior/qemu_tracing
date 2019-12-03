package org.eclipse.tracecompass.analysis.os.linux.core.qemu;

/**
 * @since 2.0
 */
@SuppressWarnings("javadoc")
public interface EventString {

    String eventMigration_bitmap_sync_start="qemu:migration_bitmap_sync_start";//ok
    String eventMigration_bitmap_sync_end="qemu:migration_bitmap_sync_end";//ok
    String eventMigration_throttle="qemu:migration_throttle";
    String eventMigration_completion_file_err="qemu:migration_completion_file_err";
    String eventmigration_thread_low_pending="qemu:migration_thread_low_pending";
    String eventMigration_thread_setup_complete="qemu:migration_thread_setup_complete";
    String eventMigration_thread_file_err= "qemu:migration_thread_file_err";
    String eventMigration_thread_after_loop= "qemu:migration_thread_after_loop";
    String eventLoad_vm_tate= "qemu:vmstate_load_state_field";
    String eventSaveVM_section_start="qemu:savevm_section_start";//ok
    String eventSaveVM_section_end="qemu:savevm_section_end";//ok
    String eventMigration_transferred="qemu:migrate_transferred";//ok
    String eventMigration_precopy="qemu:savevm_state_complete_precopy";//ok
    //----------
    String eventMigrate_pending="qemu:migrate_pending";//ok
    String eventSytem_wakeup_request="qemu:system_wakeup_request";//ok
}