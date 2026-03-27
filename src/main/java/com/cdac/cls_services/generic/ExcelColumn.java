package com.cdac.cls_services.generic;

import java.util.function.Function;

/**
 * Describes one column in a generated Excel sheet.
 *
 * @param <T> the row-data type (e.g. CallLogResponseDto)
 */
public class ExcelColumn<T> {

    public enum CellType { TEXT, DATE, TIME, NUMBER }

    private final String header;
    private final int widthInChars;          // e.g. 20  → setColumnWidth(i, 20 * 256)
    private final CellType cellType;
    private final Function<T, Object> valueExtractor;

    private ExcelColumn(Builder<T> b) {
        this.header         = b.header;
        this.widthInChars   = b.widthInChars;
        this.cellType       = b.cellType;
        this.valueExtractor = b.valueExtractor;
    }

    public String getHeader()                    { return header; }
    public int getWidthInChars()                 { return widthInChars; }
    public CellType getCellType()                { return cellType; }
    public Function<T, Object> getValueExtractor() { return valueExtractor; }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static <T> Builder<T> builder() { return new Builder<>(); }

    public static final class Builder<T> {
        private String header;
        private int widthInChars = 20;
        private CellType cellType = CellType.TEXT;
        private Function<T, Object> valueExtractor = row -> "";

        public Builder<T> header(String header)               { this.header = header;               return this; }
        public Builder<T> width(int widthInChars)             { this.widthInChars = widthInChars;   return this; }
        public Builder<T> type(CellType cellType)             { this.cellType = cellType;           return this; }
        public Builder<T> value(Function<T, Object> extractor){ this.valueExtractor = extractor;    return this; }

        public ExcelColumn<T> build() {
            if (header == null) throw new IllegalStateException("header is required");
            return new ExcelColumn<>(this);
        }
    }
}