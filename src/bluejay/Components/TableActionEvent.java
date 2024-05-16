package bluejay.Components;

public interface TableActionEvent {
    void onEdit(int row);
    void onDelete(int row);
    void onView(int row);
}