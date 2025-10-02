package net.mat0u5.lifeseries.utils.interfaces;

public interface IEntryGroupHeader {
    void expand();
    boolean shouldExpand();
    int expandTextX(int x, int width);
}
