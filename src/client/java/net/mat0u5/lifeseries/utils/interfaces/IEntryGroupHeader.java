package net.mat0u5.lifeseries.utils.interfaces;

public interface IEntryGroupHeader {
    void expand();
    boolean isExpanded();
    int expandTextX(int x, int width);
    default boolean showExpandIcon() {
        return true;
    }
    default boolean showExpandText() {
        return true;
    }
}
